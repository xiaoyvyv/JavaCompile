package com.xiaoyv.java.bean;

import java.util.List;

public class SearchJarBean {

    private ResponseBean response;


    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }


    public static class ResponseBean {
        /**
         * numFound : 56
         * start : 0
         * docs : [{"id":"org.jsoup:jsoup","g":"org.jsoup","a":"jsoup","latestVersion":"1.12.1","repositoryId":"central","p":"jar","timestamp":1557705333000,"versionCount":32,"text":["org.jsoup","jsoup","-javadoc.jar","-sources.jar",".jar",".pom"],"ec":["-javadoc.jar","-sources.jar",".jar",".pom"]}]
         */

        private int numFound;
        private int start;
        private List<DocsBean> docs;

        public int getNumFound() {
            return numFound;
        }

        public void setNumFound(int numFound) {
            this.numFound = numFound;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public List<DocsBean> getDocs() {
            return docs;
        }

        public void setDocs(List<DocsBean> docs) {
            this.docs = docs;
        }

        public static class DocsBean {
            /**
             * id : org.jsoup:jsoup
             * g : org.jsoup
             * a : jsoup
             * latestVersion : 1.12.1
             * repositoryId : central
             * p : jar
             * timestamp : 1557705333000
             * versionCount : 32
             * text : ["org.jsoup","jsoup","-javadoc.jar","-sources.jar",".jar",".pom"]
             * ec : ["-javadoc.jar","-sources.jar",".jar",".pom"]
             */

            private String id;
            private String g;
            private String a;
            private String v;
            private String latestVersion;
            private String repositoryId;
            private String p;
            private long timestamp;
            private int versionCount;
            private List<String> text;
            private List<String> ec;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getG() {
                return g;
            }

            public void setG(String g) {
                this.g = g;
            }

            public String getA() {
                return a;
            }

            public String getV() {
                return v;
            }

            public void setV(String v) {
                this.v = v;
            }

            public void setA(String a) {
                this.a = a;
            }

            public String getLatestVersion() {
                return latestVersion;
            }

            public void setLatestVersion(String latestVersion) {
                this.latestVersion = latestVersion;
            }

            public String getRepositoryId() {
                return repositoryId;
            }

            public void setRepositoryId(String repositoryId) {
                this.repositoryId = repositoryId;
            }

            public String getP() {
                return p;
            }

            public void setP(String p) {
                this.p = p;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public int getVersionCount() {
                return versionCount;
            }

            public void setVersionCount(int versionCount) {
                this.versionCount = versionCount;
            }

            public List<String> getText() {
                return text;
            }

            public void setText(List<String> text) {
                this.text = text;
            }

            public List<String> getEc() {
                return ec;
            }

            public void setEc(List<String> ec) {
                this.ec = ec;
            }
        }
    }
}
