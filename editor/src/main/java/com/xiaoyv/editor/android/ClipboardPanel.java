package com.xiaoyv.editor.android;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.xiaoyv.editor.R;


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
        if (actionMode == null)
            freeScrollingTextField.startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    actionMode = mode;
                    menu.add(0, 0, 0, context.getString(android.R.string.selectAll))
                            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                            .setAlphabeticShortcut('a');

                    menu.add(0, 1, 0, context.getString(android.R.string.copy))
                            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                            .setAlphabeticShortcut('c');

                    menu.add(0, 2, 0, context.getString(R.string.editor_comment))
                            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                            .setAlphabeticShortcut('m');

                    menu.add(0, 3, 0, context.getString(R.string.editor_import))
                            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                            .setAlphabeticShortcut('i');

                    menu.add(0, 4, 0, context.getString(android.R.string.cut))
                            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                            .setAlphabeticShortcut('x');


                    menu.add(0, 5, 0, context.getString(android.R.string.paste))
                            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                            .setAlphabeticShortcut('v');

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
                            freeScrollingTextField.copy();
                            mode.finish();
                            break;
                        case 2:
                            freeScrollingTextField.dealComment();
                            mode.finish();
                            break;
                        case 3:
                            freeScrollingTextField.sendSelectText();
                            mode.finish();
                            break;
                        case 4:
                            freeScrollingTextField.cut();
                            mode.finish();
                            break;
                        case 5:
                            freeScrollingTextField.paste();
                            mode.finish();
                            break;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode p1) {
                    freeScrollingTextField.selectText(false);
                    actionMode = null;
                }
            });
    }

    public void hide() {
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }
}
