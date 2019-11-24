package com.xiaoyv.java.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.googlejavaformat.FormatterDiagnostic;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.lib.textwarrior.code.SymbolView;
import com.lib.textwarrior.code.TextEditor;
import com.lib.textwarrior.common.Lexer;
import com.xiaoyv.java.JavaApplication;
import com.xiaoyv.java.R;
import com.xiaoyv.java.compile.ClassCompiler;
import com.xiaoyv.java.compile.DexCompiler;
import com.xiaoyv.java.compile.listener.CompilerListener;
import com.xiaoyv.java.compile.view.console.ConsoleEditText;
import com.xiaoyv.java.mode.Project;
import com.xiaoyv.java.mode.Setting;
import com.xiaoyv.java.ui.activity.ConsoleActivity;
import com.xiaoyv.java.ui.activity.MainActivity;
import com.xiaoyv.java.ui.activity.base.BaseFragment;
import com.xiaoyv.java.ui.listener.SimpleTabSelectListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EditorFragment extends BaseFragment {
    private AlertDialog outputDialog;
    private ConsoleEditText outputConsole;
    private ContentLoadingProgressBar outputProgressBar;
    private View outputCloseView;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private TextEditor editorLayout;
    private SymbolView symbolView;

    private MainActivity activity;
    private File currentFile;
    // 包含main(String[] args)的类
    List<String> keys = new ArrayList<>();

    // 键盘状态监听
    private KeyboardUtils.OnSoftInputChangedListener softInputChangedListener = height -> {
        if (height > 0) {
            tabLayout.setVisibility(View.GONE);
            symbolView.setVisible(true);
        } else {
            if (tabLayout.getTabCount() != 0)
                tabLayout.setVisibility(View.VISIBLE);
            symbolView.setVisible(false);
        }
    };

    @Override
    public int setContentView() {
        return R.layout.fragment_cdoe;
    }

    @Override
    public void findViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        editorLayout = findViewById(R.id.editorLayout);
        symbolView = findViewById(R.id.symbolView);

    }

    @Override
    public void setEvents() {

        activity = (MainActivity) getActivity();
        editorLayout.setText(Project.getDefaultText());
        symbolView.setTextBackgroundColor(Color.WHITE);


        // 符号栏目监听
        symbolView.setOnSymbolViewClick((view, text) -> {
            if (text.equals(SymbolView.TAB)) {
                // TAB键，两个空格
                editorLayout.insert(editorLayout.getCaretPosition(), "  ");
            } else {
                editorLayout.insert(editorLayout.getCaretPosition(), text);
            }
        });

        tabLayout.setVisibility(View.GONE);
        tabLayout.addOnTabSelectedListener(new SimpleTabSelectListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String path = String.valueOf(tab.getTag());
                editorLayout.open(path);
                currentFile = new File(path);
                setToolbarInfo(currentFile);

            }
        });

        editorLayout.setOnEditListener(() -> {
            // 默认信息不设置已修改
            if (tabLayout.getTabCount() == 0) {
                editorLayout.setEdited(false);
                return;
            }
            if (!editorLayout.isEdited()) {
                toolbar.setTitle(toolbar.getTitle() + "(未保存)");
            }
        });

        // 检测ClassPath文件
        if (ClassCompiler.checkRtFile()) {
            ThreadUtils.getCachedPool().execute(() ->
                    Lexer.getLanguage().addRtIdentifier(Setting.getRtPath()));
        }


        // 查询导包
        editorLayout.setImportBtnClickListener(EditorFragment.this::findPackage);

        Menu menu = toolbar.getMenu();
        menu.add("对齐").setOnMenuItemClickListener(item -> {
            formatJavaCode(false);
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("撤消").setOnMenuItemClickListener(item -> {
            editorLayout.undo();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("运行").setOnMenuItemClickListener(item -> {
            KeyboardUtils.hideSoftInput(editorLayout);
            if (isEmptyEditor()) return false;
            boolean edited = editorLayout.isEdited();
            if (edited) {
                saveFile(false);
            }
            // 编译整个项目
            compileCurrentProject(Project.getCurrentSrcDir());
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("重做").setOnMenuItemClickListener(item -> {
            editorLayout.redo();
            return false;
        });
        menu.add("保存").setOnMenuItemClickListener(item -> {
            KeyboardUtils.hideSoftInput(editorLayout);
            if (isEmptyEditor()) return false;

            boolean edited = editorLayout.isEdited();
            if (edited) {
                saveFile(false);
            }
            ToastUtils.showShort(currentFile.getName() + " 保存成功");
            return false;
        });
        menu.add("关闭当前文件").setOnMenuItemClickListener(item -> {
            KeyboardUtils.hideSoftInput(editorLayout);
            if (isEmptyEditor()) return false;

            boolean edited = editorLayout.isEdited();
            if (edited) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity)
                        .setTitle("文件未保存：" + currentFile.getName())
                        .setMessage("是否保存对该文件的更改？")
                        .setPositiveButton("保存", (dialog, which) -> {
                            // 保存文件
                            saveFile(false);
                            // 关闭文件
                            removeTab(currentFile.getAbsolutePath());
                        })
                        .setNegativeButton("不保存", (dialog, which) -> {
                            // 放弃更改
                            editorLayout.setEdited(false);
                            // 关闭文件
                            removeTab(currentFile.getAbsolutePath());
                        })
                        .create();
                alertDialog.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                return false;
            }
            // 关闭文件
            removeTab(currentFile.getAbsolutePath());
            return false;
        });

        KeyboardUtils.registerSoftInputChangedListener(activity, softInputChangedListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkFiles();
        // 是否夜间模式
        editorLayout.setDark(Setting.isDarkMode());
        if (Setting.isDarkMode()) {
            ((ViewGroup) editorLayout.getParent()).setBackgroundColor(Color.BLACK);
        } else {
            ((ViewGroup) editorLayout.getParent()).setBackgroundColor(Color.WHITE);
            // 设置背景
            editorLayout.setBackgroundColor(Color.WHITE);
        }

        // 是否代码提示
        editorLayout.setAutoCompete(Setting.isAutoCompete());
        // 设置是否显示行号
        editorLayout.setShowLineNumbers(Setting.isShowRow());

        String formatter = SPUtils.getInstance(Setting.KEY).getString("formatter", "2");
        if (StringUtils.equals(formatter, "2")) {
            editorLayout.setAutoIndentWidth(2);
        } else {
            editorLayout.setAutoIndentWidth(4);
        }

        editorLayout.invalidate();
        editorLayout.requestFocus();
    }


    @SuppressLint("InflateParams")
    private void showOutputDialog() {
        if (outputDialog == null) {
            View view = LayoutInflater.from(activity).inflate(R.layout.fragment_cdoe_ouput, null);
            outputConsole = view.findViewById(R.id.consoleView);
            outputProgressBar = view.findViewById(R.id.progressBar);
            ScrollView scrollView = view.findViewById(R.id.scrollView);
            AppCompatImageView editView = view.findViewById(R.id.editView);
            outputCloseView = view.findViewById(R.id.closeView);
            outputConsole.setScrollView(scrollView);
            outputCloseView.setOnClickListener(v -> outputDialog.dismiss());
            editView.setOnClickListener(v -> {
                outputConsole.setEnabled(true);
                outputConsole.requestFocus();
                ToastUtils.showShort("日志复制模式开启");
            });
            outputDialog = new AlertDialog.Builder(activity, R.style.console_dialog)
                    .setView(view)
                    .create();
        }
        outputCloseView.setEnabled(false);
        outputConsole.setEnabled(false);
        outputConsole.setText(null);
        outputProgressBar.setProgress(0);
        outputDialog.setCanceledOnTouchOutside(false);
        outputDialog.setCancelable(false);
        outputDialog.show();

        outputDialog.setCanceledOnTouchOutside(false);
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
        initInOutStream();
    }


    /**
     * 格式化Java代码
     */
    private void formatJavaCode(boolean ignoreError) {
        String source = editorLayout.getText().toString().trim();
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                JavaFormatterOptions javaFormatterOptions = Setting.getJavaFormatterOptions();
                Formatter formatter = new Formatter(javaFormatterOptions);
                return formatter.formatSource(source);
            }

            @Override
            public void onFail(Throwable t) {
                if (ignoreError) return;
                if (t instanceof FormatterException) {
                    FormatterException exception = (FormatterException) t;
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
                                    editorLayout.gotoLine(line);
                                    int caretPosition = editorLayout.getCaretPosition();
                                    editorLayout.moveCaret(caretPosition + column);
                                })
                                .create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }

                }
            }

            @Override
            public void onSuccess(Object result) {
                editorLayout.setText(String.valueOf(result));
                if (!FileUtils.isFileExists(currentFile)) return;
                // 保存文件
                saveFile(false);
            }
        });
    }


    /**
     * 导包
     */
    private void findPackage(String selectedText, boolean isHide) {
        // 格式代码
        // formatJavaCode(true);

        // 若选择的非单词，则跳过
        if (selectedText.trim().contains(" ")) {
            return;
        }

        HashMap<String, String> identifier = Lexer.getLanguage().getIdentifier();
        String className;
        if (identifier.containsKey(selectedText)) {
            className = identifier.get(selectedText);

            if (!StringUtils.isEmpty(className) && className.startsWith("java.lang")) {
                if (!isHide)
                    ToastUtils.showShort("java.lang 所含类无需导包");
                return;
            }
            LogUtils.e("找到:" + className);
        } else {
            if (!isHide)
                ToastUtils.showShort("JDK 中未找到该类:" + selectedText);
            return;
        }

        int oldCaretPosition = editorLayout.getCaretPosition();
        String importText = "import " + className + ";\n";
        String code = editorLayout.getText().toString();

        if (code.contains(importText)) {
            return;
        }

        if (code.trim().startsWith("package")) {
            String tempPackage = code.substring(0, code.indexOf(";") + 1);
            editorLayout.gotoLine(2);
            editorLayout.insert(tempPackage.length() + 1, importText);
            editorLayout.moveCaret(oldCaretPosition);
            editorLayout.moveCaretDown();
            return;
        }
        editorLayout.gotoLine(1);
        editorLayout.insert(0, importText);
        editorLayout.moveCaret(oldCaretPosition);
        editorLayout.moveCaretDown();
    }


    /**
     * 编译项目或者单个文件
     */
    private void compileCurrentProject(File sourceFileOrDir) {
        // 检测代码是否有System.exit
        if (editorLayout.getText().toString().contains("System.exit")) {
            ToastUtils.showShort("请移除 System.exit(); 相关代码");
            return;
        }

        boolean fileExists = FileUtils.isFileExists(sourceFileOrDir);
        if (!fileExists) {
            SnackbarUtils.with(getRootView()).setMessage("请先打开一个项目文件").show();
            return;
        }

        activity.projectFragment.readProjectList();
        // 显示编译日志框
        showOutputDialog();

        System.out.println("Class编译开始 Tips：初次编译需要加载环境");

        // 开始运行，第一步编译
        ClassCompiler.getInstance().compile(sourceFileOrDir.getAbsolutePath(), Project.getCurrentBinDirPath(), new CompilerListener() {
            @Override
            public void onSuccess(String path) {
                System.out.println("Class编译完成");
                System.out.println("Class输出路径 " + path);
                jarToDex(path);
            }

            @Override
            public void onError(Throwable error) {
                // 移除IO拦截
                removeInOutStream();
                outputCloseView.setEnabled(true);
                outputDialog.setOnDismissListener(null);
                outputConsole.getErrorStream().println(error.toString());
            }

            @Override
            public void onProgress(String task, int progress) {
                task = task.replace(Project.rootPtah, "");
                System.out.println(">>" + task);
                outputProgressBar.setProgress(progress);
            }
        });
    }

    /**
     * jar或class文件转为Dex
     *
     * @param path iar或class文件路径
     */
    private void jarToDex(String path) {
        System.out.println("Dex转换开始");
        File classFile = new File(path);
        // 读取项目lib目录是否有第三方jar库
        File currentLibDir = Project.getCurrentLibDir();
        List<File> jarList = FileUtils.listFilesInDirWithFilter(currentLibDir, pathname ->
                FileUtils.getFileName(pathname).endsWith(".jar"));
        jarList.add(classFile);

        // 所有编译后dex文件的存放目录
        File dexBuildDir = new File(Project.getCurrentBuildDirPath() + "/dex");

        DexCompiler.getInstance().compile(jarList, dexBuildDir, new CompilerListener() {
            @Override
            public void onSuccess(String dexFilesDir) {
                List<File> dexFileList = FileUtils.listFilesInDirWithFilter(dexFilesDir, pathname -> {
                    String fileName = FileUtils.getFileName(pathname);
                    return fileName.endsWith(".dex");
                });

                if (ObjectUtils.isEmpty(dexFileList)) {
                    System.err.println("错误:未找到可执行的dex文件");
                    return;
                }
                System.out.println("Dex转换完成");

                // 检测是否为多个文件转换
                if (dexFileList.size() == 1) {
                    System.out.println("请关闭日志以运行主程序");
                    outputCloseView.setEnabled(true);
                    // 移除IO拦截
                    removeInOutStream();

                    outputDialog.setOnDismissListener(dialog -> {
                        // 编译完成，刷新文件目录
                        activity.projectFragment.readProjectList();
                        // 运行
                        ConsoleActivity.start(activity, dexFileList.get(0).getAbsolutePath());
                    });
                    if (Setting.isAutoRunWhenCompiled()) {
                        outputDialog.dismiss();
                    }
                    return;
                }

                System.out.println("Dex合并开始");

                String mergerDexFile = Project.getCurrentBuildDirPath() + "/" + Project.currentProjectName + ".dex";

                LogUtils.e(dexFileList.toArray());
                DexCompiler.getInstance().mergerDex(dexFileList, new File(mergerDexFile), new CompilerListener() {
                    @Override
                    public void onSuccess(String path) {
                        System.out.println("Dex合并完成");
                        System.out.println("请关闭日志以运行主程序");
                        outputCloseView.setEnabled(true);

                        // 移除IO拦截
                        removeInOutStream();
                        outputDialog.setOnDismissListener(dialog -> {
                            // 编译完成，刷新文件目录
                            activity.projectFragment.readProjectList();
                            // 运行
                            ConsoleActivity.start(activity, path);
                        });

                        if (Setting.isAutoRunWhenCompiled()) {
                            outputDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        // 移除IO拦截
                        removeInOutStream();
                        outputCloseView.setEnabled(true);
                        outputDialog.setOnDismissListener(null);
                        error.printStackTrace(outputConsole.getErrorStream());
                    }
                });
            }

            @Override
            public void onError(Throwable error) {
                // 移除IO拦截
                removeInOutStream();
                outputCloseView.setEnabled(true);
                outputDialog.setOnDismissListener(null);
                outputConsole.getErrorStream().println(error.toString());
            }
        });
    }


    private boolean isEmptyEditor() {
        boolean fileExists = FileUtils.isFileExists(currentFile);
        if (!fileExists) {
            SnackbarUtils.with(getRootView()).setMessage("请先打开一个项目文件").show();
            return true;
        }
        return false;
    }

    /**
     * 检查已打开的文件是否存在
     */
    void checkFiles() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab == null) continue;
            String path = String.valueOf(tab.getTag());
            boolean fileExists = FileUtils.isFileExists(path);
            if (!fileExists) {
                removeTab(tab, path);
            }
        }
    }

    private void removeTab(String tag) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab == null) continue;
            String path = String.valueOf(tab.getTag());
            if (StringUtils.equals(tag, path)) {
                removeTab(tab, path);
            }
        }
    }

    private void removeTab(TabLayout.Tab tab, String path) {
        keys.remove(path);
        tabLayout.removeTab(tab);
        if (tabLayout.getTabCount() == 0) {
            tabLayout.setVisibility(View.GONE);
            toolbar.setTitle("Java编译器");
            toolbar.setSubtitle("软件信息");
            editorLayout.setText(Project.getDefaultText());
            currentFile = null;
        }
    }

    @SuppressLint({"InflateParams", "SetTextI18n", "ClickableViewAccessibility"})
    void openFile(File file) {
        tabLayout.setVisibility(View.VISIBLE);
        editorLayout.setEdited(false);
        this.currentFile = file;
        setToolbarInfo(file);


        String path = file.getAbsolutePath();
        // 如果文件已经打开,则找到该Tab并且选中
        if (keys.contains(path)) {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab == null) continue;
                String tabPath = String.valueOf(tab.getTag());
                if (StringUtils.equals(tabPath, path)) {
                    if (tab.isSelected()) {
                        editorLayout.open(path);
                    } else {
                        tab.select();
                    }
                }
            }
            return;
        }

        keys.add(path);
        TabLayout.Tab tab = tabLayout.newTab();
        // 将文件路径设置到TAG
        tab.setTag(path);

        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_cdoe_tab, null);

        // 移除旧的Tab布局
        View oldCustomView = tab.getCustomView();
        if (oldCustomView != null) {
            ViewGroup customParent = (ViewGroup) oldCustomView.getParent();
            if (customParent != null) {
                customParent.removeView(oldCustomView);
            }
        }
        // 设置新的Tab布局
        tab.setCustomView(view);
        ViewGroup viewGroup = (ViewGroup) view.getParent();

        TextView nameView = view.findViewById(R.id.fileName);
        TextView projectView = view.findViewById(R.id.project);
        // 根据文件获取工程名
        String projectName = Project.getCurrentProjectNameByFile(file);
        projectView.setText("工程：" + projectName);
        nameView.setText(file.getName());


        viewGroup.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                boolean edited = editorLayout.isEdited();
                if (edited) {
                    saveFile(true);
                    return true;
                }
            }
            return false;
        });

        viewGroup.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(activity, tab.view);
            popupMenu.getMenu().add("关闭").setOnMenuItemClickListener(item -> {
                popupMenu.show();
                String path1 = String.valueOf(tab.getTag());
                removeTab(tab, path1);
                return false;
            });
            popupMenu.getMenu().add("运行该文件").setOnMenuItemClickListener(item -> {
                KeyboardUtils.hideSoftInput(editorLayout);
                if (isEmptyEditor()) return false;
                boolean edited = editorLayout.isEdited();
                if (edited) {
                    saveFile(false);
                }
                // 设置当前项目
                activity.projectFragment.setCurrentProjectByFile(currentFile);
                // 编译单个文件
                String path1 = String.valueOf(tab.getTag());
                compileCurrentProject(new File(path1));
                return false;
            });
            popupMenu.show();
            return true;
        });
        tabLayout.addTab(tab);

        Utils.runOnUiThreadDelayed(tab::select, 100);
    }

    private void saveFile(boolean isShowTips) {
        KeyboardUtils.hideSoftInput(editorLayout);

        if (!isShowTips) {
            editorLayout.save(currentFile.getAbsolutePath());
            editorLayout.setEdited(false);
            String title = String.valueOf(toolbar.getTitle());
            title = title.replace("(未保存)", "");
            toolbar.setTitle(title);
            return;
        }

        Snackbar.make(getRootView(), currentFile.getName() + " 是否保存当前文件？", Snackbar.LENGTH_INDEFINITE)
                .setAction("保存", v -> {
                    editorLayout.save(currentFile.getAbsolutePath());
                    editorLayout.setEdited(false);
                    String title = String.valueOf(toolbar.getTitle());
                    title = title.replace("(未保存)", "");
                    toolbar.setTitle(title);
                })
                .show();
    }

    public void saveWhenExit() {
        if (Setting.isAutoSaveWhenExit()) {
            if (editorLayout != null & FileUtils.isFileExists(currentFile)) {
                ThreadUtils.getCachedPool().execute(() ->
                        editorLayout.save(currentFile.getAbsolutePath()));
            }
        }
    }

    private void setToolbarInfo(File file) {
        String absolutePath = file.getAbsolutePath();
        String currentRelativePath = absolutePath.replace(Project.rootPtah + "/", "");
        toolbar.setTitle(FileUtils.getFileName(file));
        toolbar.setSubtitle(currentRelativePath);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }


    private void initInOutStream() {
        ThreadUtils.getCachedPool().execute(() -> {
            JavaApplication application = (JavaApplication) Utils.getApp();
            application.addStdErr(outputConsole.getErrorStream());
            application.addStdOut(outputConsole.getOutputStream());
        });
    }

    private void removeInOutStream() {
        ThreadUtils.getCachedPool().execute(() -> {
            JavaApplication application = (JavaApplication) Utils.getApp();
            application.removeErrStream(outputConsole.getErrorStream());
            application.removeOutStream(outputConsole.getOutputStream());
        });
    }

}
