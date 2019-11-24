package com.xiaoyv.java.ui.fragment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.lib.mimo.MimoUtils;
import com.lib.mimo.OnAdListener;
import com.lib.utils.MyUtils;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.xiaoyv.java.R;
import com.xiaoyv.java.ui.activity.MainActivity;
import com.xiaoyv.java.ui.activity.SampleActivity;
import com.xiaoyv.java.ui.activity.SearchJarActivity;
import com.xiaoyv.java.ui.activity.SettingsActivity;
import com.xiaoyv.java.ui.activity.base.BaseFragment;
import com.xiaoyv.java.ui.adapter.DocumentAdapter;

import java.util.ArrayList;
import java.util.List;

public class DocumentFragment extends BaseFragment {
    private Toolbar toolbar;
    private AutoCompleteTextView keyWordView;
    private String[] apiStrArr;
    private DocumentAdapter adapter;
    private MainActivity activity;
    private ViewGroup adContainer;
    private CardView adCardView;
    private IAdWorker adWorker;
    private TextView sampleView;
    private TextView jsoupView;
    private TextView jarSearch;


    @Override
    public int setContentView() {
        return R.layout.fragment_document;
    }

    @Override
    public void findViews() {
        toolbar = findViewById(R.id.toolbar);
        adCardView = findViewById(R.id.adCardView);
        adContainer = findViewById(R.id.adContainer);
        keyWordView = findViewById(R.id.keyWordView);
        sampleView = findViewById(R.id.sampleLayout);
        jsoupView = findViewById(R.id.jsoupLayout);
        jarSearch = findViewById(R.id.jarSearch);

    }

    @Override
    public void setEvents() {
        activity = (MainActivity) getActivity();
        initSearchView();

        Menu menu = toolbar.getMenu();
        menu.add("设置")
                .setIcon(R.drawable.ic_settings)
                .setOnMenuItemClickListener(item -> {
                    MyUtils.startActivity(SettingsActivity.class);
                    return false;
                }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        sampleView.setOnClickListener(v ->
                MyUtils.startActivity(SampleActivity.class));
        jsoupView.setOnClickListener(v ->
                MyUtils.openUrl(activity, "https://www.open-open.com/jsoup/"));
        jarSearch.setOnClickListener(v -> MyUtils.startActivity(SearchJarActivity.class));
        loadAd();
    }

    private void loadAd() {
        MimoUtils.FeedView.loadSmallPicture(getActivity(), 1, new OnAdListener() {

            @Override
            public void onResultListener(IAdWorker iAdWorker, int state, Object info) {
                LogUtils.e(state, info);
                switch (state) {
                    case OnAdListener.AdLoaded:
                        adCardView.setVisibility(View.VISIBLE);
                        View ad = MimoUtils.Action.updateAdView(iAdWorker, 0);
                        adContainer.addView(ad);
                        DocumentFragment.this.adWorker = iAdWorker;
                        break;
                    case OnAdListener.AdFailed:
                    case OnAdListener.AdDismissed:
                        adCardView.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    /**
     * Java文档搜索
     */
    private void initSearchView() {
        String apiStr = ResourceUtils.readAssets2String("code/api.txt");
        apiStrArr = apiStr.split("\n");

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<String> newData = new ArrayList<>();
                for (String data : apiStrArr) {
                    if (constraint == null) break;
                    String keyword = String.valueOf(constraint);
                    if (data.contains(keyword)) {
                        newData.add(data);
                    }
                }
                results.values = newData;
                results.count = newData.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                adapter.setData((List<String>) results.values);
                adapter.notifyDataSetChanged();
            }
        };

        adapter = new DocumentAdapter(activity, filter);

        // 设置一个适配器
        keyWordView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MimoUtils.Action.recyclerView(DocumentFragment.this.adWorker);
    }
}
