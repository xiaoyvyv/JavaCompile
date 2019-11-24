package com.xiaoyv.java.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.ZipUtils;
import com.lib.utils.GsonUtils;
import com.lib.utils.MyUtils;
import com.xiaoyv.java.R;
import com.xiaoyv.java.bean.SampleBean;
import com.xiaoyv.java.mode.Project;
import com.xiaoyv.java.ui.activity.base.BaseActivity;

import java.io.IOException;
import java.util.List;

public class SampleActivity extends BaseActivity {

    private Toolbar toolbar;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        init();
        MyUtils.setToolbarBackToHome(this, toolbar);
    }

    @Override
    public void findViews() {
        toolbar = findViewById(R.id.toolbar);
        container = findViewById(R.id.container);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setEvents() {
        checkSampleFiles(() -> {
            String packages = getFilesDir() + "/sample/package.json";
            String json = FileIOUtils.readFile2String(packages);
            SampleBean sampleBean = GsonUtils.fromJson(json, SampleBean.class);
            List<SampleBean.RootBean.CategoryBean> category = sampleBean.getRoot().getCategory();
            container.removeAllViews();
            for (SampleBean.RootBean.CategoryBean categoryBean : category) {
                View view = LayoutInflater.from(this).inflate(R.layout.activity_sample_item, container, false);
                TextView titleView = view.findViewById(R.id.title);
                TextView messageView = view.findViewById(R.id.message);
                titleView.setText("示例集合：" + categoryBean.getName());
                messageView.setText(categoryBean.getDescription());
                messageView.setOnClickListener(v -> {
                    List<SampleBean.RootBean.CategoryBean.ProjectBean> project = categoryBean.getProject();

                    String[] names = new String[project.size()];
                    String[] paths = new String[project.size()];
                    for (int i = 0; i < project.size(); i++) {
                        names[i] = project.get(i).getName();
                        paths[i] = project.get(i).getPath();
                    }

                    AlertDialog dialog = new AlertDialog.Builder(SampleActivity.this)
                            .setTitle("示例集合：" + categoryBean.getName())
                            .setItems(names, (dialog12, which) -> {
                                String name = names[which];
                                String path = paths[which];
                                AlertDialog alert = MyUtils.getAlert(SampleActivity.this,
                                        "是否导入该该示例项目？",
                                        (dialog1, which1) -> {
                                            FileUtils.copyDir(getFilesDir() + "/" + path + "/src/main/java",
                                                    Project.rootPtah + "/" + name + "/" + Project.srcDirName);
                                            ToastUtils.showShort("项目导入成功，请回到文件树页面查看");
                                            SampleActivity.this.finish();
                                        });
                                alert.setTitle("示例项目：" + name);
                                alert.show();
                            })
                            .setPositiveButton("关闭", null)
                            .create();
                    dialog.show();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);

                });
                container.addView(view);
            }
        });
    }

    /**
     * 检查sample文件
     */
    void checkSampleFiles(@NonNull Callback callback) {
        ThreadUtils.getCachedPool().execute(() -> {
            String sampleDir = getFilesDir() + "/sample";
            FileUtils.createOrExistsDir(sampleDir);
            long size = FileUtils.getDirLength(sampleDir);
            if (size < 1024) {
                ResourceUtils.copyFileFromAssets("code/sample.zip", sampleDir + "/sample.zip");
                try {
                    ZipUtils.unzipFile(sampleDir + "/sample.zip", getFilesDir().getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Utils.runOnUiThread(callback::onCall);
        });
    }

    public interface Callback {
        void onCall();
    }
}
