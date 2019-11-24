package com.lib.textwarrior.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.lib.textwarrior.common.DocumentProvider;

public class ClipboardPanel {
    private FreeScrollingTextField freeScrollingTextField;
    private Context context;
    private ActionMode actionMode;

    ClipboardPanel(FreeScrollingTextField textField) {
        freeScrollingTextField = textField;
        context = textField.getContext();

    }

    public Context getContext() {
        return context;
    }

    public void show() {
        startClipboardAction();
    }

    public void hide() {
        stopClipboardAction();
    }

    @SuppressWarnings("ResourceType")
    public void startClipboardAction() {
        if (actionMode == null)
            freeScrollingTextField.startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    actionMode = mode;
                    mode.setTitle(android.R.string.selectTextMode);
                    TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{
                            android.R.attr.actionModeSelectAllDrawable,
                            android.R.attr.actionModeCutDrawable,
                            android.R.attr.actionModeCopyDrawable,
                            android.R.attr.actionModePasteDrawable,
                    });
                    menu.add(0, 0, 0, context.getString(android.R.string.selectAll))
                            .setShowAsActionFlags(2)
                            .setAlphabeticShortcut('a');
                    //    .setIcon(array.getDrawable(0));

                    menu.add(0, 1, 0, context.getString(android.R.string.cut))
                            .setShowAsActionFlags(2)
                            .setAlphabeticShortcut('x');
                    // .setIcon(array.getDrawable(1));

                    menu.add(0, 2, 0, context.getString(android.R.string.copy))
                            .setShowAsActionFlags(2)
                            .setAlphabeticShortcut('c');
                    //.setIcon(array.getDrawable(2));

                    menu.add(0, 3, 0, context.getString(android.R.string.paste))
                            .setShowAsActionFlags(2)
                            .setAlphabeticShortcut('v');
                    //.setIcon(array.getDrawable(3));
                    menu.add(0, 4, 0, "注释")
                            .setShowAsActionFlags(2)
                            .setAlphabeticShortcut('v');
                    //.setIcon(getContext().getResources().getDrawable(R.mipmap.slash));
                    menu.add(0, 5, 0, "导包")
                            .setShowAsActionFlags(2)
                            .setAlphabeticShortcut('v');
                    array.recycle();
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case 0:
                            freeScrollingTextField.selectAll();
                            break;
                        case 1:
                            freeScrollingTextField.cut();
                            mode.finish();
                            break;
                        case 2:
                            freeScrollingTextField.copy();
                            mode.finish();
                            break;
                        case 3:
                            freeScrollingTextField.paste();
                            mode.finish();
                            break;
                        case 4:
                            dealComment();
                            mode.finish();
                            break;
                        case 5:
                            freeScrollingTextField.sendSelectText();
                            mode.finish();
                            break;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode p1) {
                    // TODO: Implement this method
                    freeScrollingTextField.selectText(false);
                    actionMode = null;
                }
            });
    }

    /**
     * 处理注释
     */
    private void dealComment() {
        DocumentProvider documentProvider = freeScrollingTextField.createDocumentProvider();
        int startRowNum = documentProvider.findRowNumber(freeScrollingTextField.getSelectionStart());
        int endRowNum = documentProvider.findRowNumber(freeScrollingTextField.getSelectionEnd());

        for (int i = startRowNum; i <= endRowNum; i++) {
            if (isLineComment(i)) {
                unCommentRow(i);
            } else {
                commentRow(i);
            }
        }
    }

    /*
     *判断是否为行注释
     */
    public boolean isLineComment(int row) {
        DocumentProvider documentProvider = freeScrollingTextField.createDocumentProvider();
        int rowStart = documentProvider.getRowOffset(row);
        documentProvider.seekChar(rowStart);//调到该行的开始
        int offset = 0;
        while (documentProvider.hasNext()) {
            char ch = documentProvider.next();
            if (ch != '/' && ch != ' ')
                return false;
            char nextCh = documentProvider.charAt(rowStart + offset + 1);
            if (ch == '/' && nextCh == '/') {
                return true;
            }
            ++offset;
        }
        return false;
    }

    /**
     * 取消注释
     */
    public void unCommentRow(int row) {
        DocumentProvider documentProvider = freeScrollingTextField.createDocumentProvider();
        int rowStart = documentProvider.getRowOffset(row);
        documentProvider.seekChar(rowStart);//调到该行的开始
        int offset = 0;
        while (documentProvider.hasNext()) {

            char ch = documentProvider.next();
            char nextCh = documentProvider.charAt(rowStart + offset + 1);
            if (ch == '/' && nextCh == '/') {
                documentProvider.deleteAt(rowStart + offset, System.nanoTime());
                documentProvider.deleteAt(rowStart + offset, System.nanoTime());//删除一个‘/’后，第二个'/'的位置变成了原来第一个的位置
                freeScrollingTextField.respan();
                return;
            }
            ++offset;
        }


    }

    /**
     * 注释
     *
     * @param row
     */
    public void commentRow(int row) {
        DocumentProvider documentProvider = freeScrollingTextField.createDocumentProvider();
        documentProvider.insert(documentProvider.getRowOffset(row), "/");
        documentProvider.insert(documentProvider.getRowOffset(row), "/");
        freeScrollingTextField.respan();
    }

    public void stopClipboardAction() {
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }

}
