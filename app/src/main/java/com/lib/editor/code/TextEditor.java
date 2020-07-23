package com.lib.editor.code;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.xiaoyv.editor.android.FreeScrollingTextField;
import com.xiaoyv.editor.android.YoyoNavigationMethod;
import com.xiaoyv.editor.common.ColorScheme;
import com.xiaoyv.editor.common.ColorSchemeDark;
import com.xiaoyv.editor.common.ColorSchemeLight;
import com.xiaoyv.editor.common.Document;
import com.xiaoyv.editor.common.DocumentProvider;
import com.xiaoyv.editor.common.LanguageJava;
import com.xiaoyv.editor.common.Lexer;
import com.xiaoyv.java.bean.SampleBean;

import java.io.File;

import javax.tools.FileObject;


public class TextEditor extends FreeScrollingTextField {
    private Context mContext;
    // 当前打开的文本
    private Document currentDocument;
    // 自动换行
    private boolean isWordWrap;
    // 打开的文件
    private File currentFile;
    private int index;

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
        setTextSize(ConvertUtils.sp2px(FreeScrollingTextField.BASE_TEXT_SIZE_PIXELS));
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
        // 设置选择文字时，水滴光标操作
        setNavigationMethod(new YoyoNavigationMethod(this));
        // 设置语法分析
        Lexer.setLanguage(LanguageJava.getInstance());
        // 设置自动提示的语法分析
        autoCompletePanel.setLanguage(LanguageJava.getInstance());

        reSpan();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (index != 0 && right > 0) {
            moveCaret(index);
            index = 0;
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

    /**
     * 设置关键词颜色
     *
     * @param color 关键词颜色
     */
    public void setKeywordColor(int color) {
        getColorScheme().setColor(ColorScheme.ColorType.KEYWORD, color);
    }

    /**
     * 设置注释颜色
     *
     * @param color 注释颜色
     */
    public void setCommentColor(int color) {
        getColorScheme().setColor(ColorScheme.ColorType.COMMENT, color);
    }

    /**
     * 设置背景色
     *
     * @param color 背景色
     */
    public void setBackgroundColor(int color) {
        getColorScheme().setColor(ColorScheme.ColorType.BACKGROUND, color);
    }

    /**
     * 设置文字颜色
     *
     * @param color 文字颜色
     */
    public void setTextColor(int color) {
        getColorScheme().setColor(ColorScheme.ColorType.FOREGROUND, color);
    }

    /**
     * 设置选中背景色
     *
     * @param color 选中背景色
     */
    public void setSelectedTextBackgroundColor(int color) {
        getColorScheme().setColor(ColorScheme.ColorType.SELECTION_BACKGROUND, color);
    }

    /**
     * 设置错误行颜色高亮
     *
     * @param isHighlight 是否高亮错误颜色
     */
    public void setLineHighlightError(boolean isHighlight) {
        getColorScheme().setColor(ColorScheme.ColorType.LINE_HIGHLIGHT, isHighlight ? 0X50FF0000 : ColorScheme.COLOR_LINE_HIGHLIGHT);
    }


    /**
     * 设置开始选择文本
     *
     * @param index 光标位置
     */
    public void setSelection(int index) {
        selectText(false);
        if (!hasLayout())
            moveCaret(index);
        else
            this.index = index;
    }


    /**
     * 获取选中部分的文本
     *
     * @return 选中部分的文本
     */
    public String getSelectedText() {
        return documentProvider.subSequence(getSelectionStart(), getSelectionEnd() - getSelectionStart()).toString();
    }

    /**
     * 打开文件
     *
     * @param fileName 文件路径
     */
    public void open(String fileName) {
        open(new File(fileName));
    }

    /**
     * 打开文件
     *
     * @param inputFile 文件
     */
    public void open(File inputFile) {
        currentFile = inputFile;
        // 读取文件内容
        ThreadUtils.getCachedPool().execute(() -> {
            String string = FileIOUtils.readFile2String(inputFile.getAbsolutePath());
            ThreadUtils.runOnUiThread(() -> setText(string));
        });
    }

    /**
     * 保存文件
     */
    public File save() {
        if (currentFile != null)
            FileIOUtils.writeFileFromString(currentFile, getText().toString());
        return currentFile;
    }

    /**
     * 获取当前文件
     * @return 当前文件
     */
    public File getFile() {
        return currentFile;
    }

    /**
     * 关闭文件
     */
    public void close() {
        save();
        setText(null);
        currentFile = null;
    }

    /**
     * 判断编辑器打开文件是否为空
     *
     * @return 开文件是否为空
     */
    public boolean isEmpty() {
        return currentFile == null;
    }

    /**
     * 跳转到指定行
     *
     * @param line 指定行
     */
    public void gotoLine(int line) {
        if (line > documentProvider.getRowCount()) {
            line = documentProvider.getRowCount();
        }
        int i = getText().getLineOffset(line - 1);
        setSelection(i);
    }

    /**
     * 快捷键操作
     */
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
        isWordWrap = enable;
        super.setWordWrap(enable);
    }

    /**
     * 设置文本
     *
     * @param text 文本
     */
    public void setText(CharSequence text) {
        documentProvider.setWordWrap(isWordWrap);
        documentProvider.setText(text);
        setDocumentProvider(documentProvider);
    }

    /**
     * 获取文本
     *
     * @return 文本
     */
    public DocumentProvider getText() {
        return createDocumentProvider();
    }

    /**
     * 插入
     *
     * @param idx  插入位置
     * @param text 插入文本
     */
    public void insert(int idx, String text) {
        selectText(false);
        moveCaret(idx);
        paste(text);
    }


    /**
     * 撤销
     */
    public void undo() {
        DocumentProvider doc = createDocumentProvider();
        int newPosition = doc.undo();
        if (newPosition >= 0) {
            setEdited(true);
            reSpan();
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
            reSpan();
            selectText(false);
            moveCaret(newPosition);
            invalidate();
        }
    }

    /**
     * 加载默认文件
     */
    public void loadAppInfoJavaFile() {
        String infoJavaFile = PathUtils.getExternalAppCachePath() + "/JavaStudio.java";
        ResourceUtils.copyFileFromAssets("JavaStudio.java", infoJavaFile);
        open(infoJavaFile);
    }
}
