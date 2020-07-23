package com.xiaoyv.java.ui.activity.console;

import android.text.Html;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.xiaoyv.java.R;
import com.xiaoyv.java.base.BaseFragment;
import com.xiaoyv.javaengine.console.JavaConsole;


/**
 * 控制台
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class ConsoleFragment extends BaseFragment<ConsoleContract.Presenter> implements ConsoleContract.View {

    private AppCompatTextView consoleOutput;

    public static ConsoleFragment newInstance() {
        return new ConsoleFragment();
    }

    @Override
    public void setPresenter(ConsoleContract.Presenter presenter) {
        super.setFragmentPresenter(presenter);
    }

    @Override
    public void showError(String failMsg) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_console;
    }

    @Override
    public void initView() {
         consoleOutput = findView(R.id.console_output);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void showStderr(CharSequence err) {
        consoleOutput.append(Html.fromHtml("<font color=\"#F00\">" + err.toString() + "</font>"));

    }

    @Override
    public void showStdout(CharSequence out) {
        consoleOutput.append(out);
    }
}