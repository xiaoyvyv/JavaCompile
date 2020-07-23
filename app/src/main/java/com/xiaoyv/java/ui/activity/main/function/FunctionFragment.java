package com.xiaoyv.java.ui.activity.main.function;

import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.xiaoyv.java.R;
import com.xiaoyv.java.base.BaseFragment;


/**
 * 功能
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class FunctionFragment extends BaseFragment<FunctionContract.Presenter> implements FunctionContract.View {

    public static FunctionFragment newInstance() {
        return new FunctionFragment();
    }

    @Override
    public void setPresenter(FunctionContract.Presenter presenter) {
        super.setFragmentPresenter(presenter);
    }

    @Override
    public void showError(String failMsg) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_document;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

}