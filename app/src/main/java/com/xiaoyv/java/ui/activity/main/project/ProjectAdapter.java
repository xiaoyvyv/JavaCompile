package com.xiaoyv.java.ui.activity.main.project;

import com.blankj.utilcode.util.PathUtils;
import com.lib.adapter.base.BaseQuickAdapter;
import com.lib.adapter.base.BaseViewHolder;
import com.xiaoyv.java.R;

import java.io.File;

public class ProjectAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    public ProjectAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder holder, File itemBean) {
        holder.setText(R.id.project_item_name, itemBean.getName());
        holder.setText(R.id.project_item_path, itemBean.getAbsolutePath().replace(PathUtils.getExternalAppFilesPath(), ""));
        holder.addOnClickListener(R.id.project_item_layout, R.id.project_item_delete, R.id.project_item_export);
    }
}
