package com.lib.textwarrior.code;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.lib.textwarrior.android.AutoCompletePanel;
import com.lib.textwarrior.android.FreeScrollingTextField;
import com.lib.textwarrior.android.YoyoNavigationMethod;
import com.lib.textwarrior.common.ColorScheme;
import com.lib.textwarrior.common.ColorSchemeDark;
import com.lib.textwarrior.common.ColorSchemeLight;
import com.lib.textwarrior.common.Document;
import com.lib.textwarrior.common.DocumentProvider;
import com.lib.textwarrior.common.LanguageJava;
import com.lib.textwarrior.common.Lexer;

import java.io.File;


public class TextEditor extends FreeScrollingTextField {
    // 当前打开的文件
    private Document currentDocument;
    // 自动换行
    private boolean _isWordWrap;
    private Context mContext;
    private String _lastSelectFile;
    private int _index;

    public TextEditor(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TextEditor(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init() {
        // 设置字体
        setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Consolas.ttf"));
        // 设置字体大小
        setTextSize(ConvertUtils.sp2px(BASE_TEXT_SIZE_PIXELS));
        // 显示行号
        setShowLineNumbers(true);
        // 设置自动提示
        setAutoCompete(true);
        // 当前行高亮
        setHighlightCurrentRow(true);
        // 设置是否自动换行
        setWordWrap(false);
        // 设置自动缩进宽度
        setAutoIndentWidth(2);

        // 设置语法分析
        Lexer.setLanguage(LanguageJava.getInstance());
        respan();

        AutoCompletePanel.setLanguage(LanguageJava.getInstance());
        // 设置选择文字时，水滴光标操作
        setNavigationMethod(new YoyoNavigationMethod(this));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (_index != 0 && right > 0) {
            moveCaret(_index);
            _index = 0;
        }
    }

    /**
     * 设置是否夜间模式
     */
    public void setDark(boolean isDark) {
        if (isDark)
            setColorScheme(new ColorSchemeDark());
        else
            setColorScheme(new ColorSchemeLight());
    }


    public void setPanelBackgroundColor(int color) {
        autoCompletePanel.setBackgroundColor(color);
    }

    public void setPanelTextColor(int color) {
        autoCompletePanel.setTextColor(color);
    }

    public void setKeywordColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.KEYWORD, color);
    }

    public void setCommentColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.COMMENT, color);
    }

    public void setBackgroundColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.BACKGROUND, color);
    }

    public void setTextColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.FOREGROUND, color);
    }

    public void setTextHighlightColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.SELECTION_BACKGROUND, color);
    }

    public void setLineHighlightError(boolean isError) {
        if (isError) {
            // 出错行颜色
            getColorScheme().setColor(ColorScheme.Colorable.LINE_HIGHLIGHT, Color.parseColor("#50ff0000"));
        } else {
            // 默认颜色
            getColorScheme().setColor(ColorScheme.Colorable.LINE_HIGHLIGHT, Color.parseColor("#dddddd"));
        }
    }


    public String getSelectedText() {
        return documentProvider.subSequence(getSelectionStart(), getSelectionEnd() - getSelectionStart()).toString();
    }

    public void gotoLine(int line) {
        if (line > documentProvider.getRowCount()) {
            line = documentProvider.getRowCount();

        }
        int i = getText().getLineOffset(line - 1);
        setSelection(i);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        final int filteredMetaState = event.getMetaState() & ~KeyEvent.META_CTRL_MASK;
        if (KeyEvent.metaStateHasNoModifiers(filteredMetaState)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_A:
                    selectAll();
                    return true;
                case KeyEvent.KEYCODE_X:
                    cut();
                    return true;
                case KeyEvent.KEYCODE_C:
                    copy();
                    return true;
                case KeyEvent.KEYCODE_V:
                    paste();
                    return true;
            }
        }
        return super.onKeyShortcut(keyCode, event);
    }


    @Override
    public void setWordWrap(boolean enable) {
        _isWordWrap = enable;
        super.setWordWrap(enable);
    }

    public DocumentProvider getText() {
        return createDocumentProvider();
    }

    public File getOpenedFile() {
        if (_lastSelectFile != null)
            return new File(_lastSelectFile);
        return null;
    }

    public void setOpenedFile(String file) {
        _lastSelectFile = file;
    }

    public void insert(int idx, String text) {
        selectText(false);
        moveCaret(idx);
        paste(text);
    }


    public void replaceAll(CharSequence c) {
        replaceText(0, getLength() - 1, c.toString());
    }

    public void setText(CharSequence c) {
        if (currentDocument == null)
            currentDocument = new Document(this);
        currentDocument.setWordWrap(_isWordWrap);
        currentDocument.setText(c);
        setDocumentProvider(new DocumentProvider(currentDocument));
    }

    public void setSelection(int index) {
        selectText(false);
        if (!hasLayout())
            moveCaret(index);
        else
            _index = index;
    }


    /**
     * 撤销
     */
    public void undo() {
        DocumentProvider doc = createDocumentProvider();
        int newPosition = doc.undo();
        if (newPosition >= 0) {
            setEdited(true);
            respan();
            selectText(false);
            moveCaret(newPosition);
            invalidate();
        }

    }

    /**
     * 重做
     */
    public void redo() {
        DocumentProvider doc = createDocumentProvider();
        int newPosition = doc.redo();
        if (newPosition >= 0) {
            setEdited(true);
            respan();
            selectText(false);
            moveCaret(newPosition);
            invalidate();
        }
    }


    /**
     * 打开文件
     */
    public void open(String file) {
        _lastSelectFile = file;

        File inputFile = new File(file);

        currentDocument = new Document(this);
        currentDocument.setWordWrap(_isWordWrap);

        ThreadUtils.getCachedPool().execute(() -> {
            String string = FileIOUtils.readFile2String(inputFile.getAbsolutePath());
            Utils.runOnUiThread(() -> setText(string));
        });
    }

    /**
     * 保存文件
     */
    public void save(String file) {
        FileIOUtils.writeFileFromString(file, getText().toString());
    }

    /**
     * 加载默认文件
     */
    public void loadTempFile() {
       /* File file = new File(CodeUtils.ConstantPool.FILE_PATH + "/" + CodeUtils.ConstantPool.TEMP_FILE_NAME);
        if (!file.exists()) {
            FileIOUtils.writeFileFromString(file, CodeUtils.ConstantPool.HELLO_WORLD);
        }
        open(file.getAbsolutePath());*/
    }
}
