package com.xiaoyv.java.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.lib.utils.GsonUtils;
import com.lib.utils.MyUtils;
import com.xiaoyv.http.OkHttp;
import com.xiaoyv.http.OnResultStringListener;
import com.xiaoyv.java.R;
import com.xiaoyv.java.bean.SearchJarBean;
import com.xiaoyv.java.ui.activity.base.BaseActivity;
import com.xiaoyv.java.ui.adapter.SearchJarAdapter;
import com.xiaoyv.java.url.Url;

import java.util.ArrayList;
import java.util.List;

public class SearchJarActivity extends BaseActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private SearchJarAdapter searchJarAdapter;
    private int page = 1;
    private String query;
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getIntent().getStringExtra("title");
        query = getIntent().getStringExtra("query");

        setContentView(R.layout.activity_search_jar);
        init();
        MyUtils.setToolbarBackToHome(this, toolbar);
    }

    public static void start(Context context, String title, String query) {
        Intent intent = new Intent(context, SearchJarActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("query", query);
        MyUtils.startActivity(intent);
    }

    @Override
    public void findViews() {
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        refresh = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.recyclerView);

    }

    @Override
    public void setEvents() {
        searchJarAdapter = new SearchJarAdapter(this, R.layout.activity_search_jar_item);
        recyclerView.setAdapter(searchJarAdapter);

        toolbar.setTitle(StringUtils.isEmpty(title) ? "Maven 依赖搜索" : title);
        if (StringUtils.isEmpty(title)) {
            searchJarAdapter.versionList = false;
            searchView.setVisibility(View.VISIBLE);
            searchView.setIconified(false);
        } else {
            searchJarAdapter.versionList = true;
            searchView.setVisibility(View.GONE);
            searchView.setEnabled(false);
        }

        refresh.setColorSchemeColors(ColorUtils.getColor(R.color.colorPrimary));
        refresh.setOnRefreshListener(() -> {
            page = 1;
            searchJarAdapter.setNewData(new ArrayList<>());
            queryMavenRepository();
        });

        searchJarAdapter.setEnableLoadMore(true);
        searchJarAdapter.setOnLoadMoreListener(() -> {
            page++;
            queryMavenRepository();
        });

        searchView.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (StringUtils.isEmpty(query)) return false;
                searchJarAdapter.setNewData(new ArrayList<>());

                KeyboardUtils.hideSoftInput(searchView);
                SearchJarActivity.this.query = query;
                refresh.setRefreshing(true);
                queryMavenRepository();

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if (!StringUtils.isEmpty(query)) {
            refresh.setRefreshing(true);
            queryMavenRepository();
        }
    }

    private void queryMavenRepository() {
        int rows = 20;
        String url = Url.App_Search_Jar + "?q=" + query + "&start=" + (page - 1) * rows + "&rows=" + rows;
        if (searchJarAdapter.versionList) url = url + "&core=gav";
        LogUtils.e(url);
        OkHttp.do_Get(url, new OnResultStringListener() {
            @Override
            public void onResponse(String response) {
                refresh.setRefreshing(false);
                SearchJarBean searchJarBean = GsonUtils.fromJson(response, SearchJarBean.class);
                List<SearchJarBean.ResponseBean.DocsBean> docs = searchJarBean.getResponse().getDocs();
                if (ObjectUtils.isEmpty(docs)) {
                    if (page == 1) {
                        searchJarAdapter.setEmptyView(SearchJarActivity.this, "未查询到相关依赖");
                        return;
                    }
                    searchJarAdapter.loadMoreEnd();
                    return;
                }
                searchJarAdapter.addData(docs);
                searchJarAdapter.loadMoreComplete();
            }

            @Override
            public void onFailure(String error) {
                refresh.setRefreshing(false);
                if (page != 1)
                    page--;
                searchJarAdapter.loadMoreFail();
            }
        });
    }


}
