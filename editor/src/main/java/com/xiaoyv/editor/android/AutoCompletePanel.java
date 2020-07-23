package com.xiaoyv.editor.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.xiaoyv.editor.R;
import com.xiaoyv.editor.common.Flag;
import com.xiaoyv.editor.common.Language;
import com.xiaoyv.editor.common.Lexer;
import com.xiaoyv.editor.common.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码自动提示
 */
public class AutoCompletePanel {
    private static Language language = Lexer.getLanguage();
    private FreeScrollingTextField textField;
    private ListPopupWindow listPopupWindow;
    private AutoPanelAdapter autoPanelAdapter;
    private String inputStr;
    private Filter filter;
    private Context context;
    private int padding = 80;

    AutoCompletePanel(FreeScrollingTextField textField) {
        this.textField = textField;
        context = textField.getContext();
        initAutoCompletePanel();
    }

    private void initAutoCompletePanel() {
        listPopupWindow = new ListPopupWindow(context);
        autoPanelAdapter = new AutoPanelAdapter(context);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(0XFFEEEEEE);

        listPopupWindow.setBackgroundDrawable(drawable);
        listPopupWindow.setContentWidth(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setAnchorView(textField);
        listPopupWindow.setAdapter(autoPanelAdapter);
        listPopupWindow.setOnItemClickListener((parent, view, position, id) ->
                select(position));

        // 取消阴影
        ListView listView = listPopupWindow.getListView();
        if (listView != null) {
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        filter = autoPanelAdapter.getFilter();
    }

    /**
     * 选择第一个提示
     */
    public void selectFirst() {
        select(0);
    }

    /**
     * 选择第几个提示
     *
     * @param position 第 position 个
     */
    public void select(int position) {
        View adapterView = autoPanelAdapter.getView(position, null, null);
        TextView textView = adapterView.findViewById(R.id.auto_panel_text);

        // 条目的文字内容
        String itemText = String.valueOf(textView.getText());
        // 点击后提交到输入法的文本
        String commitText;

        // 是否为方法
        boolean isMethod = itemText.contains("(");
        // 是否为函数
        boolean isFunc = itemText.contains("[");
        if (isMethod) {
            commitText = itemText.substring(0, itemText.indexOf('(')) + "()";
        } else if (isFunc) {
            commitText = itemText.substring(0, itemText.indexOf('\t'));
        } else {
            commitText = itemText;
            textField.postDelayed(() -> {
                // 检测是否导包
                if (textField.onImportBtnClickListener != null)
                    textField.onImportBtnClickListener.onSelectText(commitText, true);
            }, 100);
        }
        // 将未输入完整的文本自动替换
        textField.replaceText(textField.getCaretPosition() - inputStr.length(), inputStr.length(), commitText);
        autoPanelAdapter.abort();

        // 如果是方法，则自动向左移动一个光标到括号中间
        if (isMethod) textField.moveCaretLeft();

        // 关闭提示框
        dismiss();
    }

    /**
     * 设置提示框宽度
     *
     * @param width 宽度
     */
    public void setWidth(int width) {
        listPopupWindow.setWidth(width);
    }

    /**
     * 设置提示框高度
     *
     * @param height 高度
     */
    private void setHeight(int height) {
        listPopupWindow.setHeight(height);
    }

    /**
     * 设置水平偏移
     *
     * @param horizontal 水平偏移
     */
    private void setHorizontalOffset(int horizontal) {
        horizontal = Math.min(horizontal, textField.getWidth() / 2);
        listPopupWindow.setHorizontalOffset(horizontal);
    }


    /**
     * 设置垂直偏移
     *
     * @param verticalOffset 垂直偏移
     */
    private void setVerticalOffset(int verticalOffset) {
        int max = -listPopupWindow.getHeight();
        if (verticalOffset > max) {
            textField.scrollBy(0, verticalOffset - max);
            verticalOffset = max;
        }
        listPopupWindow.setVerticalOffset(verticalOffset);
    }

    /**
     * 更新自动完成匹配词语
     *
     * @param constraint 匹配词前部分
     */
    public void update(CharSequence constraint) {
        autoPanelAdapter.restart();
        filter.filter(constraint);
    }

    /**
     * 显示提示框
     */
    public void show() {
        if (!listPopupWindow.isShowing())
            listPopupWindow.show();
    }

    /**
     * 关闭提示框
     */
    public void dismiss() {
        if (listPopupWindow.isShowing()) {
            listPopupWindow.dismiss();
        }
    }

    /**
     * 提示框是否显示
     *
     * @return 提示框是否显示
     */
    public boolean isShow() {
        return listPopupWindow.isShowing();
    }

    /**
     * 设置语法分析
     *
     * @param lang 语法分析
     */
    synchronized public void setLanguage(Language lang) {
        language = lang;
    }

    /**
     * 获取语法分析
     *
     * @return 语法分析
     */
    synchronized public Language getLanguage() {
        return language;
    }

    /**
     * 自动提示条目数据
     */
    public static class ListItem {
        private Bitmap bitmap;
        private String text;

        ListItem(Bitmap bitmap, String text) {
            this.bitmap = bitmap;
            this.text = text;
        }

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
     * 自动提示适配器
     */
    @SuppressLint("InflateParams")
    public class AutoPanelAdapter extends BaseAdapter implements Filterable {
        private List<ListItem> listItems = new ArrayList<>();
        private int lineHeight;
        private Flag abort;
        private Bitmap bitmap;
        public int keywordColor = 0xFF000099;
        public int functionColor = 0xFFaa55aa;
        public int otherColor = 0xFF555555;
        public int methodColor = 0xFF569CEE;
        public int minLine = 3;

        AutoPanelAdapter(Context context) {
            abort = new Flag();
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_function);

            // 设置单行高度 30dp
            float scale = Resources.getSystem().getDisplayMetrics().density;
            lineHeight = (int) (30 * scale + 0.5f);
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
         * 实现自动完成的过滤算法
         */
        @Override
        public Filter getFilter() {
            return new Filter() {
                /**
                 * 本方法在后台线程执行，定义过滤算法
                 */
                @Override
                synchronized protected FilterResults performFiltering(CharSequence constraint) {
                    // 过滤后的数据集合
                    List<String> resultList = new ArrayList<>();

                    // 当前用户输入文本
                    inputStr = String.valueOf(constraint);

                    // 判断输入有几个点
                    int length = inputStr.length();
                    int newLength = inputStr.replace(".", "").length();
                    int count = length - newLength;

                    // 包含一个点，例如输入：System.out 或 scan.next
                    if (count == 1) {
                        //LogUtils.e("输入关键字包含一个点：" + inputStr);

                        // System.out
                        String className = inputStr.substring(0, inputStr.indexOf("."));
                        List<String> methods = findApiMethod(className, inputStr);
                        resultList.addAll(methods);
                    }

                    // 包含两个点，例如输入：System.out.print
                    if (count == 2) {
                        //LogUtils.e("输入关键字包含两个点：" + inputStr);

                        // System.out.print
                        String className = inputStr.substring(0, inputStr.indexOf("."));
                        String classMethod = inputStr.substring(inputStr.indexOf(".") + 1, inputStr.lastIndexOf("."));
                        String inputMethod = inputStr.substring(inputStr.lastIndexOf(".") + 1);
                        // 获取静态方法
                        List<String> methods = findApiMethod(className, className + "." + classMethod);
                        int i = 0;
                        while (i < methods.size()) {
                            String method = methods.get(i);
                            if (method.contains(classMethod)) {
                                String typeClass = method.substring(method.indexOf(":") + 1, method.lastIndexOf("]"));
                                List<String> jdkClassMethod = findApiJdkClassMethod(typeClass, inputMethod);
                                for (int j = 0; j < jdkClassMethod.size(); j++) {
                                    String JdkMethod = jdkClassMethod.get(j);
                                    resultList.add(className + "." + classMethod + "." + JdkMethod);
                                }
                                break;
                            }
                            i++;
                        }
                    }

                    // 添加用户所有自定义标识符的提示数据源
                    List<String> defendMap = language.getDefendMapKey();
                    for (int i = 0; i < defendMap.size(); i++) {
                        String key = defendMap.get(i);
                        if (StringUtils.isEmpty(key)) continue;
                        if (key.startsWith(inputStr) && !resultList.contains(key))
                            resultList.add(key);
                    }

                    // 添加JDK所有关键词的提示数据源
                    List<String> keywords = language.getKeywords();
                    for (int i = 0; i < keywords.size(); i++) {
                        String key = keywords.get(i);
                        if (StringUtils.isEmpty(key)) continue;
                        if (key.indexOf(inputStr) == 0 && !resultList.contains(key))
                            resultList.add(key);
                    }

                    // 添加JDK所有类名的提示数据源
                    HashMap<String, String> identifier = language.getIdentifier();
                    for (Map.Entry<String, String> maps : identifier.entrySet()) {
                        String key = maps.getKey();

                        if (key.startsWith(inputStr) && !resultList.contains(key))
                            resultList.add(key);
                    }

                    // 数据源结果
                    FilterResults filterResults = new FilterResults();
                    // 过滤结果
                    filterResults.values = resultList;
                    // 结果数量
                    filterResults.count = resultList.size();
                    return filterResults;
                }

                /**
                 * 本方法在UI线程执行，用于更新自动完成列表
                 */
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    // 有过滤结果，显示自动完成列表
                    if (results != null && results.count > 0 && !abort.isSet()) {
                        // 清空旧列表
                        listItems.clear();
                        List<?> stringArrayList = (List<?>) results.values;
                        for (int i = 0; i < stringArrayList.size(); i++) {
                            String itemText = String.valueOf(stringArrayList.get(i));
                            if (itemText.contains("(")) {
                                listItems.add(new ListItem(bitmap, itemText));
                            } else {
                                listItems.add(new ListItem(null, itemText));
                            }
                        }
                        int y = textField.getCaretY() + textField.rowHeight() / 2 - textField.getScrollY();

                        // 设置提示框高度
                        setHeight(lineHeight * Math.min(minLine, results.count));
                        // 设置提示框宽度
                        setWidth(textField.getWidth() - padding * 2);

                        setHorizontalOffset(padding);
                        setVerticalOffset(y - textField.getHeight());
                        notifyDataSetChanged();
                        show();
                    } else {
                        // 无过滤结果，关闭列表
                        notifyDataSetInvalidated();
                    }
                }

            };
        }


    }

    synchronized public static List<String> findApiStaticMethod(String className, String input) {
        List<String> temp = new ArrayList<>();
        HashMap<String, String> identifier = language.getIdentifier();
        for (Map.Entry<String, String> maps : identifier.entrySet()) {
            String simpleName = maps.getKey().trim();
            String fullName = maps.getValue().trim();
            // jdk 内部的类标识
            if (StringUtils.equals(simpleName, className.trim())) {
                List<String> method = getMethod(fullName, true);
                int i = 0;
                while (i < method.size()) {
                    String s = method.get(i);
                    String methodApi = className + "." + s;
                    if (methodApi.startsWith(input) && !temp.contains(methodApi)) {
                        temp.add(methodApi);
                    }
                    i++;
                }
                break;
            }
        }
        return temp;
    }

    synchronized public static List<String> findApiIdentifierMethod(String className, String input) {
        List<String> temp = new ArrayList<>();
        HashMap<String, String> identifier = language.getIdentifier();

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
        //LogUtils.e("自定义标识：" + className, "类型：" + typeClass);
        for (Map.Entry<String, String> maps : identifier.entrySet()) {
            String simpleName = maps.getKey().trim();
            String fullName = maps.getValue().trim();

            // jdk 内部的类标识
            if (StringUtils.equals(simpleName, typeClass)) {
                List<String> method = getMethod(fullName, false);
                int i = 0;
                while (i < method.size()) {
                    String s = method.get(i);
                    String methodApi = className + "." + s;
                    if (methodApi.startsWith(input) && !temp.contains(methodApi)) {
                        temp.add(methodApi);
                    }
                    i++;
                }
                break;
            }
        }
        return temp;
    }

    synchronized public static List<String> findApiJdkClassMethod(String className, String inputMethod) {
        List<String> temp = new ArrayList<>();
        HashMap<String, String> identifier = language.getIdentifier();
        for (Map.Entry<String, String> maps : identifier.entrySet()) {
            String simpleName = maps.getKey().trim();
            String fullName = maps.getValue().trim();
            // jdk 内部的类标识
            if (StringUtils.equals(simpleName, className.trim())) {
                List<String> method = getMethod(fullName, false);
                int i = 0;
                while (i < method.size()) {
                    String methodApi = method.get(i);
                    if (methodApi.startsWith(inputMethod) && !temp.contains(methodApi)) {
                        temp.add(methodApi);
                    }
                    i++;
                }
                break;
            }
        }
        return temp;
    }

    /**
     * 查询提示数据
     * <p>
     * 例子一
     * System.xxx
     * <p>
     * 例子二
     * Scanner scanner = new Scanner(System.in);
     * scanner.xxx
     *
     * @param identifier JDK类名或自定义的标识符。例子一为：System，例子二为：scanner
     * @param input      输入的匹配前缀。例子一为：scanner.xxx，例子二为：scanner.xxx
     * @return 匹配集合
     */
    synchronized public static List<String> findApiMethod(String identifier, String input) {
        // 缓存集合
        List<String> temp = new ArrayList<>();

        // JDK所有类名集合
        HashMap<String, String> jdkIdentifier = language.getIdentifier();
        // 是否为JDK里面包含的类名
        boolean isJdkClassName = false;
        for (Map.Entry<String, String> maps : jdkIdentifier.entrySet()) {
            String simpleName = maps.getKey().trim();
            // jdk 内部的类标识
            if (StringUtils.equals(simpleName, identifier.trim())) {
                isJdkClassName = true;
                break;
            }
        }

        if (isJdkClassName) {
            temp.addAll(findApiStaticMethod(identifier, input));
        } else {
            temp.addAll(findApiIdentifierMethod(identifier, input));
        }

        return temp;
    }

    synchronized public static List<String> getMethod(String className, boolean isStatic) {
        List<String> temp = new ArrayList<>();
        try {
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            // 获取静态方法
            Class<?> aClass = systemClassLoader.loadClass(className);

            Method[] methods = aClass.getDeclaredMethods();
            int j = 0;
            while (j < methods.length) {
                Method mi = methods[j];
                //LogUtils.i(mi.getName(), mi.getModifiers());

                if (isStatic) {
                    // 只查询静态公共方法
                    if (!Modifier.isStatic(mi.getModifiers()) || !Modifier.isPublic(mi.getModifiers())) {
                        j++;
                        continue;
                    }
                } else {
                    // 只查询公共方法
                    if (!Modifier.isPublic(mi.getModifiers())) {
                        j++;
                        continue;
                    }
                }

                StringBuilder method = new StringBuilder(mi.getName() + "(");
                Class<?>[] parameterTypes = mi.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> p = parameterTypes[i];
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
                temp.add(method.toString());
                j++;
            }

            Field[] declaredFields = aClass.getDeclaredFields();
            int i = 0;
            while (i < declaredFields.length) {
                Field declaredField = declaredFields[i];
                // 只查询静态公共方法
                if (!Modifier.isStatic(declaredField.getModifiers()) || !Modifier.isPublic(declaredField.getModifiers())) {
                    i++;
                    continue;
                }
                String name = declaredField.getName();
                String fieldClassName = declaredField.getType().getSimpleName();
                String field = name + "\t\t\t[type:" + fieldClassName + "]";
                temp.add(field);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }
}
