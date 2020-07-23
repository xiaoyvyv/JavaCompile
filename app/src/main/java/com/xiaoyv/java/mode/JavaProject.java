package com.xiaoyv.java.mode;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.xiaoyv.javaengine.JavaEngineSetting;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class JavaProject implements Serializable {
    public static final long serialVersionUID = 2572333609309709157L;
    public static final String rootDirName = "JavaProject";
    public static final String rootDirPtah = PathUtils.getExternalAppFilesPath() + "/" + rootDirName;
    public static final String zipDirPath = PathUtils.getExternalAppFilesPath() + "/JavaExport";
    public static final String srcDirName = "src";
    public static final String binDirName = "bin";
    public static final String libDirName = "lib";
    public static final String buildDirName = "build";
    public String projectDependencyName = ".dependency";
    public String projectIniName = ".project";
    public String projectName;
    public String projectDirPath;
    public String projectZipPath;

    /**
     * 项目构造器
     *
     * @param projectName 项目名称
     */
    private JavaProject(String projectName) {
        this.projectName = projectName;
        this.projectDirPath = JavaProject.rootDirPtah + "/" + projectName;
        this.projectZipPath = JavaProject.zipDirPath + "/" + projectName + ".zip";
    }

    /**
     * 创建项目
     *
     * @param projectName 项目名称
     * @return 项目对象
     */
    public static JavaProject newProject(String projectName) {
        JavaProject javaProject = new JavaProject(projectName);
        javaProject.init();
        return javaProject;
    }

    /**
     * 打开项目
     *
     * @param projectName 项目名称
     * @return 项目对象
     */
    public static JavaProject openProject(String projectName) {
        return new JavaProject(projectName);
    }


    /**
     * 项目初始化
     */
    public void init() {
        // 创建项目文件夹
        FileUtils.createOrExistsDir(projectDirPath);

        // 创建项目文件夹
        FileUtils.createOrExistsDir(getSrcDirPath());
        FileUtils.createOrExistsDir(getBuildDirPath());
        FileUtils.createOrExistsDir(getBinDirPath());
        FileUtils.createOrExistsDir(getLibDirPath());


        // 创建项目信息文件
        FileIOUtils.writeFileFromString(getProjectIniPath(),
                "项目名称：" + projectName
                        + "\n创建时间：" + TimeUtils.getNowString()
                        + "\n创建工具：" + AppUtils.getAppName()
                        + "\n系统版本：" + DeviceUtils.getSDKVersionName()
                        + "\n技术支持：1223414335@qq.com");

        // 创建依赖文件
        FileUtils.createFileByDeleteOldFile(getProjectDependencyPath());

        // 创建模板文件
        String classTemplate = JavaTemplate.getClassTemplate(null, "Main", true);
        FileIOUtils.writeFileFromString(getSrcDirPath() + "/Main.java", classTemplate);
    }

    /**
     * 删除该项目
     */
    public void delete() {
        FileUtils.deleteAllInDir(projectDirPath);
        FileUtils.delete(projectDirPath);
    }

    /**
     * 清理该项目
     */
    public void clean() {
        FileUtils.deleteAllInDir(getBuildDirPath());
        FileUtils.deleteAllInDir(getBinDirPath());
    }

    /**
     * 导出该项目
     */
    public File export() {
        File shareFile = new File(getProjectZipPath());
        try {
            FileUtils.createFileByDeleteOldFile(shareFile);
            ZipUtils.zipFile(getProjectDirPath(), shareFile.getAbsolutePath());
        } catch (IOException e) {
            LogUtils.e(e.toString());
        }
        return shareFile;
    }

    /**
     * 获取项目名称
     *
     * @return 项目名称
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * 获取项目根路径
     *
     * @return 项目根路径
     */
    public String getProjectDirPath() {
        return projectDirPath;
    }

    /**
     * 获取项目压缩文件
     *
     * @return 项目压缩文件
     */
    public String getProjectZipPath() {
        return projectZipPath;
    }

    /**
     * 获取项目依赖目录文件
     *
     * @return 依赖目录文件
     */
    public String getProjectDependencyPath() {
        return projectDirPath + "/" + projectDependencyName;
    }

    /**
     * 获取项目信息文件
     *
     * @return 信息文件
     */
    public String getProjectIniPath() {
        return projectDirPath + "/" + projectIniName;
    }


    /**
     * 获取项目 src 路径
     *
     * @return src 路径
     */
    public String getSrcDirPath() {
        return projectDirPath + "/" + srcDirName;
    }

    /**
     * 获取项目 bin 路径
     *
     * @return bin 路径
     */
    public String getBinDirPath() {
        return projectDirPath + "/" + binDirName;
    }

    /**
     * 获取项目 lib 路径
     *
     * @return lib 路径
     */
    public String getLibDirPath() {
        return projectDirPath + "/" + libDirName;
    }

    /**
     * 获取项目 build 路径
     *
     * @return build 路径
     */
    public String getBuildDirPath() {
        return projectDirPath + "/" + buildDirName;
    }


    /**
     * 获取源文件数目
     *
     * @return 代码文件数
     */
    public int getSrcFileSize() {
      return FileUtils.listFilesInDir(getSrcDirPath(), true).size();
    }

    /**
     * 获取当前项目 lib 目录的 jar 依赖路径
     */
    public String getLibClassPath() {
        List<File> javaLibraries = FileUtils.listFilesInDirWithFilter(getLibDirPath(), pathname ->
                FileUtils.isFile(pathname) && FileUtils.getFileName(pathname).endsWith(".jar"));
        if (ObjectUtils.isEmpty(javaLibraries)) {
            return "";
        }

        StringBuilder classpath = new StringBuilder();
        for (File javaLibrary : javaLibraries) {
            if (classpath.length() != 0) {
                classpath.append(File.pathSeparator);
            }
            classpath.append(javaLibrary.getAbsolutePath());
        }
        return classpath.toString();
    }


    /**
     * 路径转文件
     *
     * @param path 路径
     * @return 文件
     */
    public File getFileByPath(String path) {
        return new File(path);
    }


    /**
     * 获取项目文件的类型
     *
     * @param file 项目文件
     * @return 文件的类型
     */
    public static JavaFileType getFileType(File file) {
        if (file == null || !file.exists()) {
            return JavaFileType.NONE;
        }
        // 文件夹
        if (FileUtils.isDir(file)) {
            File parentFile = file.getParentFile();
            if (parentFile == null || !parentFile.exists()) {
                return JavaFileType.NONE;
            }

            // 项目文件夹
            if (StringUtils.equals(rootDirName, parentFile.getName())) {
                return JavaFileType.DIR_PROJECT;
            }

            // 包文件夹
            if (file.getAbsolutePath().contains("/" + srcDirName + "/")
                    || file.getAbsolutePath().contains("/" + binDirName + "/")
                    || file.getAbsolutePath().contains("/" + buildDirName + "/")) {
                List<File> files = FileUtils.listFilesInDir(file);
                if (ObjectUtils.isEmpty(files)) {
                    return JavaFileType.DIR_PACKAGE_EMPTY;
                } else {
                    return JavaFileType.DIR_PACKAGE;
                }
            }
            // src文件夹和bin及其子文件夹
            return JavaFileType.DIR_FOLDER;
        }
        // 文件
        else {
            if (file.getName().endsWith(".java")) {
                return JavaFileType.FILE_JAVA;
            }

            if (StringUtils.equals(file.getName(), ".java")) {
                return JavaFileType.FILE_JAVA;
            }

            if (StringUtils.equals(file.getName(), ".project")) {
                return JavaFileType.FILE_INI;
            }
            return JavaFileType.FILE;
        }
    }
}