package com.main.bean;

import com.main.utils.PropertiesUtils;

public class Constants {
    public static Boolean IGNORE_TABLE_PREFIX;
    public static String SUFFIX_BEAN_PARMA;
    
    // 数据库类型到Java类型的映射常量
    public static final String JAVA_TYPE_INT = "Integer";
    public static final String JAVA_TYPE_LONG = "Long";
    public static final String JAVA_TYPE_BYTE = "Byte";
    public static final String JAVA_TYPE_SHORT = "Short";
    public static final String JAVA_TYPE_DOUBLE = "Double";
    public static final String JAVA_TYPE_DECIMAL = "java.math.BigDecimal";
    public static final String JAVA_TYPE_BOOLEAN = "Boolean";
    public static final String JAVA_TYPE_DATE = "java.util.Date";
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
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARMA = PropertiesUtils.getString("suffix.bean.parma");
    }
}
