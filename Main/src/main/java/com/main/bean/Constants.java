package com.main.bean;

import com.main.utils.PropertiesUtils;

public class Constants {
    public static String AUTHOR_COMMENT;
    public static Boolean IGNORE_TABLE_PREFIX;
    public static String SUFFIX_BEAN_PARMA;

    // 需要忽略的属性
    public static String IGNORE_BEAN_TOJSON_FILED;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;

    // 日期格式序列化，反序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;
    public static String BEAN_DATE_PARSE_EXPRESSION;
    public static String BEAN_DATE_PARSE_CLASS;

    public static String PACKAGE_ENUM;
    public static String PATH_ENUM;
    public static String PACKAGE_UTILS;
    public static String PATH_UTILS;
    public static String PATH_BASE;
    public static String PACKAGE_BASE;
    public static String PACKAGE_PO;
    public static String PATH_PO;
    private static String PATH_JAVA = "java";
    private String PATH_RESOURCE = "resources";
    
    // 数据库类型到Java类型的映射常量
    public static final String JAVA_TYPE_INT = "Integer";
    public static final String JAVA_TYPE_LONG = "Long";
    public static final String JAVA_TYPE_BYTE = "Byte";
    public static final String JAVA_TYPE_SHORT = "Short";
    public static final String JAVA_TYPE_DOUBLE = "Double";
    public static final String JAVA_TYPE_DECIMAL = "BigDecimal";
    public static final String JAVA_TYPE_BOOLEAN = "Boolean";
    public static final String JAVA_TYPE_DATE = "Date";
    public static final String JAVA_TYPE_STRING = "String";
    
    // 整数类型数组
    public static final String[] INTEGER_TYPES = { "INT", "INTEGER" };
    // 长整型数组
    public static final String[] LONG_TYPES = { "BIGINT" };
    // 小整数类型数组
    public static final String[] BYTE_TYPES = { "TINYINT" };
    public static final String[] SHORT_TYPES = { "SMALLINT" };
    // 浮点类型数组
    public static final String[] FLOAT_TYPES = { "FLOAT", "DOUBLE" };
    // 精确数值类型数组
    public static final String[] DECIMAL_TYPES = { "DECIMAL", "NUMERIC" };
    // 布尔类型数组
    public static final String[] BOOLEAN_TYPES = { "BOOLEAN", "BIT" };
    // 日期类型数组
    public static final String[] DATE_TYPES = { "DATE" };
    // 日期事件类型数组
    public static final String[] DATE_TIME_TYPES = { "TIMESTAMP", "TIME", "DATETIME" };
    // 字符串类型数组
    public static final String[] STRING_TYPES = { "TEXT", "CHAR", "VARCHAR" };

    static {
        AUTHOR_COMMENT = PropertiesUtils.getString("author.comment");
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARMA = PropertiesUtils.getString("suffix.bean.parma");

        IGNORE_BEAN_TOJSON_FILED = PropertiesUtils.getString("ignore.bean.tojson.filed");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.tojson.class");
        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        BEAN_DATE_PARSE_EXPRESSION = PropertiesUtils.getString("bean.date.parse.expression");
        BEAN_DATE_PARSE_CLASS = PropertiesUtils.getString("bean.date.parse.class");

        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.po");
        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.utils");
        PACKAGE_ENUM = PACKAGE_BASE + "." + PropertiesUtils.getString("package.enum");

        PATH_BASE = PropertiesUtils.getString("path.base");
        PATH_BASE = PATH_BASE + PATH_JAVA;

        PATH_PO = PATH_BASE + "/" + PACKAGE_PO.replace(".", "/");
        PATH_UTILS = PATH_BASE + "/" + PACKAGE_UTILS.replace(".", "/");
        PATH_ENUM = PATH_BASE + "/" + PACKAGE_ENUM.replace(".", "/");
    }
}
