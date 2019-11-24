package com.xiaoyv.java.bean;

import java.util.List;

public class SampleBean {
    /**
     * root : {"category":[{"name":"Java语言基础","description":"While, if, for, switch... statement","path":"sample/JavaLanguageFundamentals","project":[{"name":"Basic Sample","path":"sample/JavaLanguageFundamentals/BasicSample"},{"name":"Break Statement","path":"sample/JavaLanguageFundamentals/BreakStatment"},{"name":"Continue Statement","path":"sample/JavaLanguageFundamentals/ContinueStatment"},{"name":"Data Types","path":"sample/JavaLanguageFundamentals/DataTypes"},{"name":"Do While Loop","path":"sample/JavaLanguageFundamentals/DoWhileLoop"},{"name":"Final","path":"sample/JavaLanguageFundamentals/Final"},{"name":"For Loop","path":"sample/JavaLanguageFundamentals/ForLoop"},{"name":"While Loop","path":"sample/JavaLanguageFundamentals/IfElse"},{"name":"If Else","path":"sample/JavaLanguageFundamentals/Operators"},{"name":"Operators","path":"sample/JavaLanguageFundamentals/SortingAlgorithms"},{"name":"Sorting Algorithms","path":"sample/JavaLanguageFundamentals/Static"},{"name":"Static","path":"sample/JavaLanguageFundamentals/SwitchStatement"},{"name":"Switch Statement","path":"sample/JavaLanguageFundamentals/Thread"},{"name":"Thread","path":"sample/JavaLanguageFundamentals/WhileLoop"}]},{"name":"常用Java类","path":"sample/CommonlyUsedJavaClasses","description":"Calender、Java Date、Java Date Formatting、Java String、Java String Buffer、String Tokenizer","project":[{"name":"Calender","path":"sample/CommonlyUsedJavaClasses/Calender"},{"name":"Java Date","path":"sample/CommonlyUsedJavaClasses/JavaDate"},{"name":"Java Date Formatting","path":"sample/CommonlyUsedJavaClasses/JavaDateFormatting"},{"name":"Java String","path":"sample/CommonlyUsedJavaClasses/JavaString"},{"name":"Java String Buffer","path":"sample/CommonlyUsedJavaClasses/JavaStringBuffer"},{"name":"String Tokenizer","path":"sample/CommonlyUsedJavaClasses/StringTokenizer"}]},{"name":"java.lang包的包装类和其他类","path":"sample/WrapperClassesAndJavaLangPackage","description":"BigInteger、MathClass、...WrapperClass","project":[{"name":"BigInteger","path":"sample/WrapperClassesAndJavaLangPackage/BigInteger"},{"name":"BooleanWrapperClass","path":"sample/WrapperClassesAndJavaLangPackage/BooleanWrapperClass"},{"name":"ByteWrapperClass","path":"sample/WrapperClassesAndJavaLangPackage/ByteWrapperClass"},{"name":"DoubleWrapperClass","path":"sample/WrapperClassesAndJavaLangPackage/DoubleWrapperClass"},{"name":"FloatWrapperClass","path":"sample/WrapperClassesAndJavaLangPackage/FloatWrapperClass"},{"name":"IntegerWrapperClass","path":"sample/WrapperClassesAndJavaLangPackage/IntegerWrapperClass"},{"name":"LongWrapperClass","path":"sample/WrapperClassesAndJavaLangPackage/LongWrapperClass"},{"name":"MathClass","path":"sample/WrapperClassesAndJavaLangPackage/MathClass"},{"name":"ShortWrapperClass","path":"sample/WrapperClassesAndJavaLangPackage/ShortWrapperClass"}]},{"name":"Java集合和数据结构（java.util包）","path":"sample/JavaCollectionsAndDataStructures","description":"Arrays、Collections、Comparator、Enumeration、HashMap、Iterator、Set","project":[{"name":"Arrays","path":"sample/JavaCollectionsAndDataStructures/Arrays"},{"name":"Array List","path":"sample/JavaCollectionsAndDataStructures/ArrayList"},{"name":"Collections","path":"sample/JavaCollectionsAndDataStructures/Collections"},{"name":"Comparator","path":"sample/JavaCollectionsAndDataStructures/Comparator"},{"name":"Enumeration","path":"sample/JavaCollectionsAndDataStructures/Enumeration"},{"name":"Hash Map","path":"sample/JavaCollectionsAndDataStructures/Hashmap"},{"name":"Hash Set","path":"sample/JavaCollectionsAndDataStructures/HashSet"},{"name":"Hash Table","path":"sample/JavaCollectionsAndDataStructures/Hashtable"},{"name":"Iterator","path":"sample/JavaCollectionsAndDataStructures/Iterator"},{"name":"Linked Has hMap","path":"sample/JavaCollectionsAndDataStructures/LinkedHashMap"},{"name":"Linked Hash Set","path":"sample/JavaCollectionsAndDataStructures/LinkedHashSet"}]}]}
     */

    private RootBean root;

    public RootBean getRoot() {
        return root;
    }

    public void setRoot(RootBean root) {
        this.root = root;
    }

    public static class RootBean {
        private List<CategoryBean> category;

        public List<CategoryBean> getCategory() {
            return category;
        }

        public void setCategory(List<CategoryBean> category) {
            this.category = category;
        }

        public static class CategoryBean {
            /**
             * name : Java语言基础
             * description : While, if, for, switch... statement
             * path : sample/JavaLanguageFundamentals
             * project : [{"name":"Basic Sample","path":"sample/JavaLanguageFundamentals/BasicSample"},{"name":"Break Statement","path":"sample/JavaLanguageFundamentals/BreakStatment"},{"name":"Continue Statement","path":"sample/JavaLanguageFundamentals/ContinueStatment"},{"name":"Data Types","path":"sample/JavaLanguageFundamentals/DataTypes"},{"name":"Do While Loop","path":"sample/JavaLanguageFundamentals/DoWhileLoop"},{"name":"Final","path":"sample/JavaLanguageFundamentals/Final"},{"name":"For Loop","path":"sample/JavaLanguageFundamentals/ForLoop"},{"name":"While Loop","path":"sample/JavaLanguageFundamentals/IfElse"},{"name":"If Else","path":"sample/JavaLanguageFundamentals/Operators"},{"name":"Operators","path":"sample/JavaLanguageFundamentals/SortingAlgorithms"},{"name":"Sorting Algorithms","path":"sample/JavaLanguageFundamentals/Static"},{"name":"Static","path":"sample/JavaLanguageFundamentals/SwitchStatement"},{"name":"Switch Statement","path":"sample/JavaLanguageFundamentals/Thread"},{"name":"Thread","path":"sample/JavaLanguageFundamentals/WhileLoop"}]
             */

            private String name;
            private String description;
            private String path;
            private List<ProjectBean> project;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public List<ProjectBean> getProject() {
                return project;
            }

            public void setProject(List<ProjectBean> project) {
                this.project = project;
            }

            public static class ProjectBean {
                /**
                 * name : Basic Sample
                 * path : sample/JavaLanguageFundamentals/BasicSample
                 */

                private String name;
                private String path;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPath() {
                    return path;
                }

                public void setPath(String path) {
                    this.path = path;
                }
            }
        }
    }
}
