package com.xiaoyv.java.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.lib.utils.MyUtils;
import com.xiaoyv.java.R;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends BaseAdapter implements Filterable {
    private String docHost = "http://www.matools.com/file/manual/jdk_api_1.8_google/";
    private List<String> data = new ArrayList<>();
    private Context context;
    private Filter filter;

    public DocumentAdapter(Context context, Filter filter) {
        this.context = context;
        this.filter = filter;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_document_item, parent, false);
        } else {
            view = convertView;
        }
        TextView textView = view.findViewById(R.id.textView);
        String html = data.get(position);
        textView.setText(html);
        textView.setTextSize(15);
        textView.setPadding(20, 20, 20, 20);
        view.setOnClickListener(v -> {
            String url = docHost + html;
            MyUtils.openUrl(context, url);
        });
        return view;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }
}