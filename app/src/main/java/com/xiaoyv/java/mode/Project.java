package com.xiaoyv.java.mode;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.File;
import java.util.List;

public class Project {
    public static final String rootDirName = "JavaProject";
    public static final String rootPtah = PathUtils.getExternalStoragePath() + "/A_Tool/" + rootDirName;
    public static final String zipPath = PathUtils.getExternalStoragePath() + "/A_Tool/JavaExport";
    public static final String srcDirName = "src";
    public static final String binDirName = "bin";
    public static final String libDirName = "lib";
    public static final String buildDirName = "build";
    public static final String projectIniFileName = ".project";
    public static final String projectDependencyFileName = ".dependency";
    public static String currentProjectName;
    public static String currentProjectPath;


    public enum FileType {
        DIR_PROJECT, DIR_FOLDER, DIR_PACKAGE, DIR_PACKAGE_EMPTY, FILE_JAVA, FILE_INI, FILE, NONE
    }


    public static FileType getFileType(File file) {
        if (file == null || !file.exists()) {
            return FileType.NONE;
        }
        // 文件夹
        if (FileUtils.isDir(file)) {
            File parentFile = file.getParentFile();
            if (parentFile == null || !parentFile.exists()) {
                return FileType.NONE;
            }

            // 项目文件夹
            if (StringUtils.equals(rootDirName, parentFile.getName())) {
                return FileType.DIR_PROJECT;
            }

            // 包文件夹
            if (file.getAbsolutePath().contains("/" + srcDirName + "/")
                    || file.getAbsolutePath().contains("/" + binDirName + "/")
                    || file.getAbsolutePath().contains("/" + buildDirName + "/")) {
                List<File> files = FileUtils.listFilesInDir(file);
                if (ObjectUtils.isEmpty(files)) {
                    return FileType.DIR_PACKAGE_EMPTY;
                } else {
                    return FileType.DIR_PACKAGE;
                }
            }
            // src文件夹和bin及其子文件夹
            return FileType.DIR_FOLDER;
        }
        // 文件
        else {
            if (file.getName().endsWith(".java")) {
                return FileType.FILE_JAVA;
            }

            if (StringUtils.equals(file.getName(), ".java")) {
                return FileType.FILE_JAVA;
            }

            if (StringUtils.equals(file.getName(), projectIniFileName)) {
                return FileType.FILE_INI;
            }
            return FileType.FILE;
        }
    }

    /**
     * 创建新的工程
     */
    public static void createProject(String projectName) {
        currentProjectName = projectName;
        File newJavaProject = new File(Project.rootPtah + "/" + projectName);
        currentProjectPath = newJavaProject.getAbsolutePath();
        FileUtils.createOrExistsDir(newJavaProject);
        FileIOUtils.writeFileFromString(newJavaProject.getAbsolutePath() + "/" + projectIniFileName,
                "项目名称：" + projectName
                        + "\n创建时间：" + TimeUtils.getNowString()
                        + "\n创建工具：Java编译器"
                        + "\n技术支持：1223414335@qq.com");
        FileUtils.createFileByDeleteOldFile(newJavaProject.getAbsolutePath() + "/" + projectDependencyFileName);
        FileUtils.createOrExistsDir(newJavaProject.getAbsolutePath() + "/" + srcDirName);
        FileUtils.createOrExistsDir(newJavaProject.getAbsolutePath() + "/" + buildDirName);
        FileUtils.createOrExistsDir(newJavaProject.getAbsolutePath() + "/" + binDirName);
        FileUtils.createOrExistsDir(newJavaProject.getAbsolutePath() + "/" + libDirName);
        String classTemplate = Project.Template.getClassTemplate(null, "Main", true);
        FileIOUtils.writeFileFromString(newJavaProject.getAbsolutePath() + "/" + srcDirName + "/Main.java", classTemplate);
    }

    /**
     * 设置当前的工程
     */
    public static void setCurrentProject(File projectDir) {
        currentProjectName = projectDir.getName();
        currentProjectPath = projectDir.getAbsolutePath();
    }

    /**
     * 获取当前工程名称
     */
    public static String getCurrentProjectNameByFile(File projectFile) {
        String projectFilePath = projectFile.getAbsolutePath();
        projectFilePath = projectFilePath.replace(Project.rootPtah + "/", "");
        String projectName;
        if (projectFilePath.contains("/")) {
            projectName = projectFilePath.substring(0, projectFilePath.indexOf("/"));
        } else {
            projectName = projectFilePath;
        }
        return projectName;
    }


    /**
     * 获取当前项目lib目录的jar依赖路径
     */
    public static String getUserLibClassPath() {
        String libDir = rootPtah + "/" + currentProjectName + "/" + libDirName;
        FileUtils.createOrExistsDir(libDir);

        List<File> javaLibraries = FileUtils.listFilesInDirWithFilter(libDir, pathname ->
                FileUtils.isFile(pathname) && FileUtils.getFileName(pathname).endsWith(".jar"));
        if (javaLibraries == null) {
            return "";
        }

        StringBuilder classpath = new StringBuilder(".");
        for (File javaLibrary : javaLibraries) {
            if (classpath.length() != 0) {
                classpath.append(File.pathSeparator);
            }
            classpath.append(javaLibrary.getAbsolutePath());
        }
        return classpath.toString();
    }

    public static String getCurrentSrcDirPath() {
        return rootPtah + "/" + currentProjectName + "/" + srcDirName;
    }

    public static File getCurrentSrcDir() {
        return new File(getCurrentSrcDirPath());
    }

    public static String getCurrentBinDirPath() {
        return rootPtah + "/" + currentProjectName + "/" + binDirName;
    }

    public static File getCurrentBinDir() {
        return new File(getCurrentBinDirPath());
    }

    public static String getCurrentBuildDirPath() {
        return rootPtah + "/" + currentProjectName + "/" + buildDirName;
    }

    public static File getCurrentBuildDir() {
        return new File(getCurrentBuildDirPath());
    }


    public static String getCurrentLibDirPath() {
        return rootPtah + "/" + currentProjectName + "/" + libDirName;
    }

    public static File getCurrentLibDir() {
        return new File(getCurrentLibDirPath());
    }

    public static String getCurrentDependencyFilePath() {
        return rootPtah + "/" + currentProjectName + "/" + projectDependencyFileName;
    }

    public static File getCurrentDependencyFile() {
        return new File(getCurrentDependencyFilePath());
    }

    /**
     * 编辑器默认文字
     */
    public static String getDefaultText() {
        return ResourceUtils.readAssets2String("info.java");
    }


    public static class Template {
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
}