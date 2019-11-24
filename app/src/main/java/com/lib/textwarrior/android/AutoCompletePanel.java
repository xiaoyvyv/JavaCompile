package com.lib.textwarrior.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.lib.textwarrior.common.Flag;
import com.lib.textwarrior.common.Language;
import com.lib.textwarrior.common.Lexer;
import com.xiaoyv.java.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码自动提示
 */
public class AutoCompletePanel {
    private FreeScrollingTextField freeScrollingTextField;
    private Context context;
    private static Language language = Lexer.getLanguage();
    private ListPopupWindow listPopupWindow;
    private AutoPanelAdapter autoPanelAdapter;
    private Filter filter;
    private int verticalOffset;
    private int height;
    private int horizontal;
    private CharSequence sequence;
    private GradientDrawable gradientDrawable;
    private final int PADDING = 20;

    AutoCompletePanel(FreeScrollingTextField textField) {
        freeScrollingTextField = textField;
        context = textField.getContext();
        initAutoCompletePanel();
    }

    public void setTextColor(int color) {
        gradientDrawable.setStroke(1, color);
        listPopupWindow.setBackgroundDrawable(gradientDrawable);
    }


    public void setBackgroundColor(int color) {
        gradientDrawable.setColor(color);
        listPopupWindow.setBackgroundDrawable(gradientDrawable);
    }

    public void setBackground(Drawable color) {
        listPopupWindow.setBackgroundDrawable(color);
    }

    @SuppressWarnings("ResourceType")
    private void initAutoCompletePanel() {
        listPopupWindow = new ListPopupWindow(context);
        listPopupWindow.setAnchorView(freeScrollingTextField);
        autoPanelAdapter = new AutoPanelAdapter(context);
        listPopupWindow.setAdapter(autoPanelAdapter);
        filter = autoPanelAdapter.getFilter();
        listPopupWindow.setContentWidth(ListPopupWindow.WRAP_CONTENT);

        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
        int textColor = array.getColor(1, 0xFF00FF);
        array.recycle();

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(backgroundColor);
        gradientDrawable.setCornerRadius(10);
        gradientDrawable.setStroke(1, Color.BLUE);

        setTextColor(textColor);
        listPopupWindow.setBackgroundDrawable(gradientDrawable);
        listPopupWindow.setOnItemClickListener((p1, p2, p3, p4) ->
                select(p3));
    }


    public void selectFirst() {
        select(0);
    }

    public void select(int pos) {
        View view = autoPanelAdapter.getView(pos, null, null);
        TextView textView = view.findViewById(R.id.auto_panel_text);
        String text = textView.getText().toString();
        String commitText;
        boolean isMethod = text.contains("(");
        boolean isFunc = text.contains("[");
        if (isMethod) {
            commitText = text.substring(0, text.indexOf('(')) + "()";
        } else if (isFunc) {
            commitText = text.substring(0, text.indexOf('\t'));
        } else {
            commitText = text;
            Utils.runOnUiThreadDelayed(() -> {
                // 检测是否导包
                freeScrollingTextField.onImportBtnClickListener.onSelectText(commitText, true);
            }, 100);
        }
        freeScrollingTextField.replaceText(freeScrollingTextField.getCaretPosition() - sequence.length(), sequence.length(), commitText);
        autoPanelAdapter.abort();
        dismiss();
        if (isMethod) {
            freeScrollingTextField.moveCaretLeft();
        }
    }

    public void setWidth(int width) {
        listPopupWindow.setWidth(width);
    }

    private void setHeight(int height) {
        if (this.height != height) {
            this.height = height;
            listPopupWindow.setHeight(height);
        }
    }

    private void setHorizontalOffset(int horizontal) {
        horizontal = Math.min(horizontal, freeScrollingTextField.getWidth() / 2);
        if (this.horizontal != horizontal) {
            this.horizontal = horizontal;
            listPopupWindow.setHorizontalOffset(horizontal);
        }
    }


    private void setVerticalOffset(int verticalOffset) {
        int max = 0 - listPopupWindow.getHeight();
        if (verticalOffset > max) {
            freeScrollingTextField.scrollBy(0, verticalOffset - max);
            verticalOffset = max;
        }
        if (this.verticalOffset != verticalOffset) {
            this.verticalOffset = verticalOffset;
            listPopupWindow.setVerticalOffset(verticalOffset);
        }
    }

    public void update(CharSequence constraint) {
        autoPanelAdapter.restart();
        filter.filter(constraint);
    }

    public void show() {
        if (!listPopupWindow.isShowing())
            listPopupWindow.show();
        ListView listView = listPopupWindow.getListView();
        if (listView != null) {
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    public void dismiss() {
        if (listPopupWindow.isShowing()) {
            listPopupWindow.dismiss();
        }

    }

    synchronized public static void setLanguage(Language lang) {
        language = lang;
    }

    synchronized public static Language getLanguage() {
        return language;
    }

    public boolean isShow() {
        return listPopupWindow.isShowing();
    }


    class ListItem {
        ListItem(Bitmap bitmap, String text) {
            this.bitmap = bitmap;
            this.text = text;
        }

        private Bitmap bitmap;
        private String text;

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

    /**
     * Adapter定义
     */
    class AutoPanelAdapter extends BaseAdapter implements Filterable {
        private int h;
        private Flag abort;
        private Bitmap bitmap;
        public int keywordColor = 0xFF000099;
        public int functionColor = 0xFFaa55aa;
        public int otherColor = 0xFF555555;
        public int methodColor = 0xFF569CEE;
        public int minLine = 3;
        private List<ListItem> listItems = new ArrayList<>();

        AutoPanelAdapter(Context context) {
            abort = new Flag();
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_function);
        }

        void abort() {
            abort.set();
        }


        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public ListItem getItem(int i) {
            return listItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View tempView;
            if (view == null) {
                tempView = LayoutInflater.from(context).inflate(R.layout.view_auto_code_item, null);
            } else {
                tempView = view;
            }
            TextView textView = tempView.findViewById(R.id.auto_panel_text);
            ImageView imageView = tempView.findViewById(R.id.auto_panel_icon);

            String text = getItem(i).getText().trim();

            SpannableString spannableString;
            ForegroundColorSpan foregroundColorSpan;
            if (text.contains("(")) {
                // 方法
                spannableString = new SpannableString(text);
                foregroundColorSpan = new ForegroundColorSpan(methodColor);
                spannableString.setSpan(foregroundColorSpan, 0, text.indexOf('('), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (text.contains("[keywords]")) {
                // 关键字
                foregroundColorSpan = new ForegroundColorSpan(keywordColor);
                int idx = text.indexOf("[keywords]");
                text = text.substring(0, idx);
                spannableString = new SpannableString(text);
                spannableString.setSpan(foregroundColorSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (text.contains("[")) {
                LogUtils.e(text);
                // 函数
                spannableString = new SpannableString(text);
                foregroundColorSpan = new ForegroundColorSpan(functionColor);
                spannableString.setSpan(foregroundColorSpan, 0, text.indexOf('['), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                // 其他
                spannableString = new SpannableString(text);
                foregroundColorSpan = new ForegroundColorSpan(otherColor);
                spannableString.setSpan(foregroundColorSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(spannableString);
            imageView.setImageBitmap(getItem(i).getBitmap());
            return tempView;
        }

        void restart() {
            abort.clear();
        }

        /**
         * 计算列表高
         */
        int getItemHeight() {
            if (h != 0)
                return h;
            LayoutInflater inflater = LayoutInflater.from(context);
            @SuppressLint("InflateParams")
            View rootView = inflater.inflate(R.layout.view_auto_code_item, null);
            rootView.measure(0, 0);
            h = rootView.getMeasuredHeight();

            return h;
        }

        /**
         * 实现自动完成的过滤算法
         */
        @Override
        public Filter getFilter() {
            return new Filter() {
                /**
                 * 本方法在后台线程执行，定义过滤算法
                 */
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    // 此处实现过滤
                    // 过滤后利用FilterResults将过滤结果返回
                    ArrayList<String> buf = new ArrayList<>();

                    // 检测是否有输入静态方法的意图 如输入了 System.xxx
                    String keyword = String.valueOf(constraint);
                    if (keyword.contains(".")) {
                        String className = keyword.substring(0, keyword.indexOf("."));
                        // LogUtils.e("静态类名：" + className);
                        String[] method = findApiMethod(className, keyword);
                        buf.addAll(Arrays.asList(method));
                    }

                    // 用户输入
                    String input = String.valueOf(constraint);
                    List<String> defendMap = language.getDefendMapKey();

                    for (String key : defendMap) {
                        if (key.startsWith(input))
                            buf.add(key);
                    }

                    List<String> keywords = language.getKeywords();
                    for (String k : keywords) {
                        if (k.indexOf(input) == 0)
                            buf.add(k);
                    }

                    HashMap<String, String> identifier = language.getIdentifier();
                    for (Map.Entry<String, String> maps : identifier.entrySet()) {
                        String key = maps.getKey();

                        if (key.startsWith(input))
                            buf.add(key);
                    }

                    sequence = input;
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = buf;   // results是上面的过滤结果
                    filterResults.count = buf.size();  // 结果数量
                    return filterResults;
                }

                /**
                 * 本方法在UI线程执行，用于更新自动完成列表
                 */
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0 && !abort.isSet()) {
                        // 有过滤结果，显示自动完成列表
                        listItems.clear();   // 清空旧列表
                        ArrayList<String> stringArrayList = (ArrayList<String>) results.values;
                        for (int i = 0; i < stringArrayList.size(); i++) {
                            String itemText = stringArrayList.get(i);
                            if (itemText.contains("(")) {
                                listItems.add(new ListItem(bitmap, itemText));
                            } else {
                                listItems.add(new ListItem(null, itemText));
                            }
                        }
                        int y = freeScrollingTextField.getCaretY() + freeScrollingTextField.rowHeight() / 2 - freeScrollingTextField.getScrollY();
                        setHeight(getItemHeight() * Math.min(minLine, results.count));

                        setHorizontalOffset(PADDING);
                        setWidth(freeScrollingTextField.getWidth() - PADDING * 2);
                        setVerticalOffset(y - freeScrollingTextField.getHeight());
                        notifyDataSetChanged();
                        show();
                    } else {
                        // 无过滤结果，关闭列表
                        notifyDataSetInvalidated();
                    }
                }

            };
        }


        /**
         * 根据静态类名查询所有静态方法
         */
        public String[] findApiMethod(String className, String input) {
            List<String> temp = new ArrayList<>();

            HashMap<String, String> identifier = language.getIdentifier();

            boolean isJdkClass = false;
            for (Map.Entry<String, String> maps : identifier.entrySet()) {
                String simpleName = maps.getKey().trim();
                String name = maps.getValue().trim();
                // jdk 内部的类标识
                if (StringUtils.equals(simpleName, className.trim())) {
                    String[] method = getMethod(name, true);
                    for (String s : method) {
                        String methodApi = className + "." + s;
                        if (methodApi.startsWith(input)) {
                            temp.add(methodApi);
                        }
                    }
                    isJdkClass = true;
                    break;
                }
            }

            if (!isJdkClass) {
                List<String> defendMap = Lexer.getLanguage().getDefendMapKey();
                // 用户自定义标识的类型索引
                int typeIndex = 0;
                for (int i = 0; i < defendMap.size(); i++) {
                    String text = defendMap.get(i);
                    if (StringUtils.equals(text, className)) {
                        typeIndex = i - 1;
                        break;
                    }
                }
                String typeClass = defendMap.get(typeIndex);
                LogUtils.e("自定义标识：" + className, "类型：" + typeClass);
                for (Map.Entry<String, String> maps : identifier.entrySet()) {
                    String simpleName = maps.getKey().trim();
                    String name = maps.getValue().trim();

                    // jdk 内部的类标识
                    if (StringUtils.equals(simpleName, typeClass)) {
                        String[] method = getMethod(name, false);
                        for (String s : method) {
                            String methodApi = className + "." + s;
                            if (methodApi.startsWith(input)) {
                                temp.add(methodApi);
                            }
                        }
                        break;
                    }
                }
            }

            String[] api = new String[temp.size()];
            return temp.toArray(api);
        }


        public String[] getMethod(String className, boolean isStatic) {
            List<String> s = new ArrayList<>();

            try {
                ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                // 获取静态方法
                Class<?> aClass = systemClassLoader.loadClass(className);

                Method[] methods = aClass.getDeclaredMethods();
                for (Method mi : methods) {
                    //LogUtils.i(mi.getName(), mi.getModifiers());

                    if (isStatic) {
                        // 只查询静态公共方法
                        if (!Modifier.isStatic(mi.getModifiers()) || !Modifier.isPublic(mi.getModifiers())) {
                            continue;
                        }
                    } else {
                        // 只查询公共方法
                        if (!Modifier.isPublic(mi.getModifiers())) {
                            continue;
                        }
                    }

                    StringBuilder method = new StringBuilder(mi.getName() + "(");
                    Class<?>[] parameterTypes = mi.getParameterTypes();
                    for (int i = 0; i < parameterTypes.length; i++) {
                        Class p = parameterTypes[i];
                        method.append(p.getSimpleName())
                                .append(" param");
                        if (i != parameterTypes.length - 1) {
                            method.append(", ");
                        }
                    }
                    method.append(")");
                    String simpleName = mi.getReturnType().getSimpleName();
                    if (!StringUtils.isEmpty(simpleName)) {
                        method.append("\t\t\t");
                        method.append("[return:");
                        method.append(simpleName);
                        method.append("]");
                    }
                    s.add(method.toString());
                }

                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    // 只查询静态公共方法
                    if (!Modifier.isStatic(declaredField.getModifiers()) || !Modifier.isPublic(declaredField.getModifiers())) {
                        continue;
                    }
                    String name = declaredField.getName();
                    String fieldClassName = declaredField.getType().getSimpleName();
                    String field = name + "\t\t\t[type:" + fieldClassName + "]";
                    s.add(field);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            String[] array = new String[s.size()];
            for (int i = 0; i < s.size(); i++) {
                array[i] = s.get(i);
            }
            return array;
        }

    }
}
