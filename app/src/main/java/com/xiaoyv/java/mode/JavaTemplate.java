package com.xiaoyv.java.mode;

import com.blankj.utilcode.util.StringUtils;

/**
 * 模板
 */
public class JavaTemplate {
    /**
     * 类模板
     *
     * @param packageName        包名
     * @param className          类名
     * @param isCreateMainMethod 是否创建 Main
     * @return 类
     */
    public static String getClassTemplate(String packageName, String className, boolean isCreateMainMethod) {
        String header = "";
        if (!StringUtils.isEmpty(packageName)) {
            header = "package " + packageName + ";\n" +
                    "\n";
        }
        return header +
                "import java.util.*;\n\n" +
                "public class " + className + " {\n" + (isCreateMainMethod ?
                "  public static void main(String[] args) {\n" +
                        "    System.out.println(\"Hello World!\");\n" +
                        "  }" : "") +
                "\n" +
                "}\n";
    }

    /**
     * 接口模板
     *
     * @param packageName 包名
     * @param className   接口名
     * @return 接口
     */
    public static String getInterfaceTemplate(String packageName, String className) {
        String header = "";
        if (!StringUtils.isEmpty(packageName)) {
            header = "package " + packageName + ";\n" +
                    "\n";
        }
        return header +
                "public interface " + className + " {\n\n" +
                "}\n";
    }
}