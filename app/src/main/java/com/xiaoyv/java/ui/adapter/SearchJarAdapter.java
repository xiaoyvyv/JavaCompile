package com.xiaoyv.java.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.adapter.base.BaseQuickAdapter;
import com.lib.adapter.base.BaseViewHolder;
import com.lib.utils.ClipboardUtils;
import com.xiaoyv.java.R;
import com.xiaoyv.java.bean.SearchJarBean;
import com.xiaoyv.java.ui.activity.SearchJarActivity;

public class SearchJarAdapter extends BaseQuickAdapter<SearchJarBean.ResponseBean.DocsBean, BaseViewHolder> implements BaseQuickAdapter.OnItemChildClickListener {
    private final Context context;
    public boolean versionList = false;

    public SearchJarAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
        setOnItemChildClickListener(this);
    }

    @Override
    protected void convert(BaseViewHolder holder, SearchJarBean.ResponseBean.DocsBean itemBean) {
        String g = itemBean.getG();
        String a = itemBean.getA();
        String v = itemBean.getV();
        String latestVersion = itemBean.getLatestVersion();
        String maven = "<dependency>\n" +
                "\t<groupId>" + g + "</groupId>\n" +
                "\t<artifactId>" + a + "</artifactId>\n" +
                "\t<version>" + (versionList ? v : latestVersion) + "</version>\n" +
                "</dependency>";
        holder.setText(R.id.title, itemBean.getId())
                .setText(R.id.group, "版本：" + itemBean.getP() + (versionList ? "" : "\t迭代次数：" + itemBean.getVersionCount()))
                .setText(R.id.desc, maven)
                .setText(R.id.time, TimeUtils.millis2String(itemBean.getTimestamp()))
                .setText(R.id.usage, (versionList ? itemBean.getP() : "仓库：" + itemBean.getRepositoryId()))
                .addOnClickListener(R.id.copy);

        TextView textview = holder.getView(R.id.desc);
        textview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        if (!versionList) {
            holder.addOnClickListener(R.id.clickView);
        } else {
            holder.setBackgroundColor(R.id.clickView, Color.WHITE);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        SearchJarBean.ResponseBean.DocsBean itemBean = getData().get(position);
        String id = itemBean.getId();
        String g = itemBean.getG();
        String a = itemBean.getA();
        String latestVersion = itemBean.getLatestVersion();
        if (view.getId() == R.id.clickView) {
            SearchJarActivity.start(context, "版本查询：" + id, "g:" + g + "+AND+a:" + a);
        }
        if (view.getId() == R.id.copy) {
            String maven = "<dependency>\n" +
                    "\t<groupId>" + g + "</groupId>\n" +
                    "\t<artifactId>" + a + "</artifactId>\n" +
                    "\t<version>" + latestVersion + "</version>\n" +
                    "</dependency>";
            ClipboardUtils.copyText(maven);
            ToastUtils.showShort(R.string.copy_right);
        }
    }
}
