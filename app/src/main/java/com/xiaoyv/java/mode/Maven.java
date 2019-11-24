package com.xiaoyv.java.mode;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.lib.utils.DownloadUtils;
import com.xiaoyv.java.bean.Dependency;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Maven {
    private static Maven maven;
    private static DocumentBuilder builder;

    public static final String MAVEN_JCENTER = "https://jcenter.bintray.com";

    private Maven() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            builder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Maven getInstance() {
        if (maven == null) {
            synchronized (Maven.class) {
                if (maven == null) {
                    maven = new Maven();
                }
            }
        }
        return maven;
    }

    public List<Dependency> getDependence(String dependXml) {
        try {
            InputStream inputStream = ConvertUtils.string2InputStream(dependXml, "utf-8");
            Document document = builder.parse(inputStream);
            List<Dependency> dependencies = new ArrayList<>();
            NodeList elements = document.getElementsByTagName("dependency");
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                String groupId = element.getElementsByTagName("groupId").item(0).getTextContent();
                String artifactId = element.getElementsByTagName("artifactId").item(0).getTextContent();
                String version = element.getElementsByTagName("version").item(0).getTextContent();
                Dependency dependency = new Dependency(groupId, artifactId, version);
                dependencies.add(dependency);
            }
            return dependencies;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return new ArrayList<>();
    }

    public void downloadDependence(@NonNull Dependency dependency, String downloadDir, DownloadUtils.OnDownloadListener downloadListener) {
        List<Dependency> dependencies = new ArrayList<>();
        dependencies.add(dependency);
        downloadDependence(dependencies, downloadDir, downloadListener);
    }

    public void downloadDependence(@NonNull List<Dependency> dependencies, String downloadDir, DownloadUtils.OnDownloadListener downloadListener) {
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dependency = dependencies.get(i);
            String groupId = dependency.getGroupId();
            groupId = groupId.replace(".", "/");
            String artifactId = dependency.getArtifactId();
            artifactId = artifactId.replace(".", "/");
            String version = dependency.getVersion();
            String maven = Setting.getMaven();
            String path = "/" + groupId + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar";
            String fileUrl = maven + path;
            String fileName = artifactId + "-" + version + ".jar";
            DownloadUtils.getInstance().download(fileUrl, downloadDir, fileName, downloadListener);
        }
    }


}
