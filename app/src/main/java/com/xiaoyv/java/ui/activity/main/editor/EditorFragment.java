package com.xiaoyv.java.ui.activity.main.editor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.googlejavaformat.FormatterDiagnostic;
import com.google.googlejavaformat.java.FormatterException;
import com.lib.editor.code.SymbolView;
import com.lib.editor.code.TextEditor;
import com.xiaoyv.editor.common.Lexer;
import com.xiaoyv.java.R;
import com.xiaoyv.java.base.BaseFragment;
import com.xiaoyv.java.ui.activity.console.ConsoleActivity;
import com.xiaoyv.javaengine.JavaEngineSetting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 编辑器
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
@SuppressLint("InflateParams")
public class EditorFragment extends BaseFragment<EditorContract.Presenter> implements EditorContract.View {
    private TextEditor editorEditLayout;
    private TabLayout editorTabLayout;
    private SymbolView editorSymbolView;
    private View outputDialogLayout;

    // 包含main(String[] args)的类
    private List<String> keys = new ArrayList<>();
    private AlertDialog outputDialog;
    private AppCompatTextView outputConsole;
    private AppCompatImageView outputEditView;
    private AppCompatImageView outputCloseView;
    private ContentLoadingProgressBar outputProgressBar;


    public static EditorFragment newInstance() {
        return new EditorFragment();
    }

    @Override
    public void setPresenter(EditorContract.Presenter presenter) {
        super.setFragmentPresenter(presenter);
    }

    @Override
    public void showError(String failMsg) {
        SnackbarUtils.with(rootView)
                .setMessage(failMsg)
                .setMessageColor(Color.WHITE)
                .showError();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_editor;
    }

    @Override
    public void initView() {
        editorEditLayout = findView(R.id.editor_layout);
        editorTabLayout = findView(R.id.editor_tab_layout);
        editorSymbolView = findView(R.id.editor_tab_symbol);

        outputDialogLayout = LayoutInflater.from(activity).inflate(R.layout.fragment_editor_ouput, null);
        outputConsole = outputDialogLayout.findViewById(R.id.editor_output_view);
        outputProgressBar = outputDialogLayout.findViewById(R.id.editor_output_progress);
        outputEditView = outputDialogLayout.findViewById(R.id.editor_output_edit);
        outputCloseView = outputDialogLayout.findViewById(R.id.editor_output_close);
    }

    @Override
    public void initData() {
        editorEditLayout.loadAppInfoJavaFile();

        // 设置语法提示数据源
        ThreadUtils.getCachedPool().execute(() ->
                Lexer.getLanguage().addRtIdentifier(JavaEngineSetting.getRtPath()));

    }

    @Override
    public void initListener() {
        // 符号栏目监听
        editorSymbolView.setOnSymbolViewClick((view, text) -> {
            if (text.equals(SymbolView.TAB)) {
                // TAB键，两个空格
                editorEditLayout.insert(editorEditLayout.getCaretPosition(), "  ");
            } else {
                editorEditLayout.insert(editorEditLayout.getCaretPosition(), text);
            }
        });

        // 编辑器编辑监听
        editorEditLayout.setOnEditListener(() -> {
            // 默认信息不设置已修改
            if (editorTabLayout.getTabCount() == 0) {
                editorEditLayout.setEdited(false);
                return;
            }
            if (!editorEditLayout.isEdited()) {
                toolbar.setTitle(toolbar.getTitle() + "(未保存)");
            }
        });

        // 软键盘监听
        KeyboardUtils.registerSoftInputChangedListener(activity.getWindow(), height -> {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) editorSymbolView.getLayoutParams();
            // 要减去主页 BottomView 的高度
            params.bottomMargin = height - ConvertUtils.dp2px(56);
            editorSymbolView.setLayoutParams(params);
            editorSymbolView.setVisible(height > 0);
        });

        // 查询导包
        editorEditLayout.setImportBtnClickListener((selectedText, isHide) ->
                presenter.findPackage(selectedText, isHide));

        Menu menu = toolbar.getMenu();
        menu.add("对齐").setOnMenuItemClickListener(item -> {
            presenter.formatJavaCode(editorEditLayout.getText().toString(), false);
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("撤消").setOnMenuItemClickListener(item -> {
            editorEditLayout.undo();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("运行").setOnMenuItemClickListener(item -> {
            KeyboardUtils.hideSoftInput(editorEditLayout);
            if (editorEditLayout.isEmpty()) return false;
            boolean edited = editorEditLayout.isEdited();
            if (edited) {
                editorEditLayout.save();
            }
            editorEditLayout.save();
            presenter.runFile(editorEditLayout.getFile());
            //TODO 编译整个项目
            //compileCurrentProject(JavaProject.getCurrentSrcDir());
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("重做").setOnMenuItemClickListener(item -> {
            editorEditLayout.redo();
            return false;
        });
        menu.add("保存").setOnMenuItemClickListener(item -> {
            KeyboardUtils.hideSoftInput(editorEditLayout);
            if (editorEditLayout.isEmpty()) return false;

            if (editorEditLayout.isEdited()) {
                File saveFile = editorEditLayout.save();
                ToastUtils.showShort(saveFile.getName() + " 保存成功");
            }
            return false;
        });
        menu.add("关闭当前文件").setOnMenuItemClickListener(item -> {
            KeyboardUtils.hideSoftInput(editorEditLayout);
            if (editorEditLayout.isEmpty()) return false;

            boolean edited = editorEditLayout.isEdited();
            if (edited) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity)
                        .setTitle("文件未保存：" + editorEditLayout.getFile().getName())
                        .setMessage("是否保存对该文件的更改？")
                        .setPositiveButton("保存", (dialog, which) -> {
                            // 保存文件
                            editorEditLayout.save();
                            // 关闭文件
                            //removeTab(currentFile.getAbsolutePath());
                        })
                        .setNegativeButton("不保存", (dialog, which) -> {
                            // 放弃更改
                            editorEditLayout.setEdited(false);
                            // 关闭文件
                            //removeTab(currentFile.getAbsolutePath());
                        })
                        .create();
                alertDialog.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                return false;
            }
            // 关闭文件
            // removeTab(currentFile.getAbsolutePath());
            return false;
        });
    }

    @Override
    public void onDestroy() {
        KeyboardUtils.unregisterSoftInputChangedListener(activity.getWindow());
        super.onDestroy();
    }

    @Override
    public void sendImportText(String importText) {
        int oldCaretPosition = editorEditLayout.getCaretPosition();
        String code = editorEditLayout.getText().toString();

        if (code.contains(importText)) {
            return;
        }

        if (code.trim().contains("package")) {
            String tempPackage = code.substring(0, code.indexOf(";") + 1);
            editorEditLayout.insert(tempPackage.length() + 1, "\n" + importText);
            editorEditLayout.moveCaret(oldCaretPosition);
            editorEditLayout.moveCaretDown();
            return;
        }
        editorEditLayout.insert(0, importText + "\n");
        editorEditLayout.moveCaret(oldCaretPosition);
        editorEditLayout.moveCaretDown();
    }

    @Override
    public void formatCodeFail(FormatterException exception) {
        // 错误信息
        List<FormatterDiagnostic> diagnostics = exception.diagnostics();
        if (ObjectUtils.isNotEmpty(diagnostics)) {
            FormatterDiagnostic diagnostic = diagnostics.get(0);
            int line = diagnostic.line();
            int column = diagnostic.column();
            String message = diagnostic.message();
            message = "line:" + line + "\ncolumn:" + column + "\nerror:" + message;
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setTitle("对齐失败")
                    .setMessage(message)
                    .setPositiveButton("知道了", (dialog, which) -> {
                        editorEditLayout.gotoLine(line);
                        int caretPosition = editorEditLayout.getCaretPosition();
                        editorEditLayout.moveCaret(caretPosition + column);
                    })
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    @Override
    public void formatCodeSuccess(String sourceCode) {
        editorEditLayout.setText(sourceCode);
        editorEditLayout.save();
    }


    @Override
    public void showLog() {
        if (outputDialog == null) {
            outputCloseView.setOnClickListener(v ->
                    outputDialog.dismiss());
            outputEditView.setOnClickListener(v -> {
                outputConsole.setTextIsSelectable(true);
                outputConsole.requestFocus();
                ToastUtils.showShort("日志复制模式开启");
            });
            outputDialog = new AlertDialog.Builder(activity, R.style.console_dialog)
                    .setView(outputDialogLayout)
                    .create();
            outputDialog.setCanceledOnTouchOutside(false);
        }
        outputDialog.show();
        outputConsole.setText(null);
        outputConsole.setTextIsSelectable(false);
        outputCloseView.setEnabled(false);
        outputProgressBar.setProgress(0);
        outputProgressBar.setIndeterminate(true);
        outputDialog.setCancelable(false);

        Window window = outputDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.getDecorView().setPadding(0, 0, 0, 0);
            Display display = activity.getWindowManager().getDefaultDisplay();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = display.getWidth(); // 设置满屏宽度
            lp.height = display.getHeight() / 2;// 设置半屏宽度
            window.setAttributes(lp);
        }
    }

    @Override
    public void hideLog() {
        if (outputDialog.isShowing()) outputDialog.dismiss();
    }

    @Override
    public void showProgress(String task, int progress) {
        if (!outputDialog.isShowing()) outputDialog.show();
        outputProgressBar.setIndeterminate(false);
        outputProgressBar.setProgress(progress);
        outputConsole.append(String.format(">>> 编译进度：%s\n", FileUtils.getFileName(task)));
    }

    @Override
    public void showErrorInfo(String err) {
        if (!outputDialog.isShowing()) outputDialog.show();
        outputCloseView.setEnabled(true);
        outputDialog.setOnDismissListener(null);
        outputConsole.append(Html.fromHtml("<br><font color=\"#FF0000\">" + err.replace("\n", "<br>") + "</font>"));
    }

    @Override
    public void showNormalInfo(String out) {
        if (!outputDialog.isShowing()) outputDialog.show();
        outputConsole.append(out);
        outputConsole.append("\n");
    }

    @Override
    public void showLogDismissListener(String dexPath) {
        outputCloseView.setEnabled(true);
        outputDialog.setOnDismissListener(dialog -> {
            // 运行Dex文件
            ConsoleActivity.start(dexPath, null);
        });
    }
}