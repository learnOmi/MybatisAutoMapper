package com.main.bean;

import com.main.utils.PropertiesUtils;

/**
 * 常量配置类，用于存储系统中使用的各种常量配置
 */
public class Constants {
    // 作者注释
    public static String AUTHOR_COMMENT;
    // 是否忽略表前缀
    public static Boolean IGNORE_TABLE_PREFIX;
    // Bean查询后缀
    public static String SUFFIX_BEAN_QUERY;
    // Bean模糊查询后缀
    public static String SUFFIX_BEAN_QUERY_FUZZY;
    // Bean查询时间开始后缀
    public static String SUFFIX_BEAN_QUERY_TIME_START;
    // Bean查询时间结束后缀
    public static String SUFFIX_BEAN_QUERY_TIME_END;
    // Mapper后缀
    public static String SUFFIX_MAPPER;
    // Service后缀
    public static String SUFFIX_SERVICE;
    // Service实现类后缀
    public static String SUFFIX_SERVICE_IMPL;

    // 需要忽略的属性
    public static String IGNORE_BEAN_TOJSON_FILED;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;

    // 日期格式序列化，反序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;
    public static String BEAN_DATE_PARSE_EXPRESSION;
    public static String BEAN_DATE_PARSE_CLASS;

    // VO相关路径和包名
    public static String PATH_VO;
    public static String PACKAGE_VO;
    // Service实现类相关路径和包名
    public static String PATH_SERVICE_IMPL;
    public static String PACKAGE_SERVICE_IMPL;
    // Service相关路径和包名
    public static String PATH_SERVICE;
    public static String PACKAGE_SERVICE;
    // Mapper XML相关路径和包名
    public static String PATH_MAPPER_XML;
    public static String PACKAGE_MAPPER;
    // Mapper相关路径和包名
    public static String PATH_MAPPER;
    public static String PACKAGE_QUERY;
    // Query相关路径和包名
    public static String PATH_QUERY;
    // 枚举相关路径和包名
    public static String PACKAGE_ENUM;
    public static String PATH_ENUM;
    // 工具类相关路径和包名
    public static String PACKAGE_UTILS;
    public static String PATH_UTILS;
    // 基础类相关路径和包名
    public static String PATH_BASE;
    public static String PACKAGE_BASE;
    // PO相关路径和包名
    public static String PACKAGE_PO;
    public static String PATH_PO;
    // Java文件路径
    private static String PATH_JAVA = "java";
    // 资源文件路径
    private static String PATH_RESOURCE = "resources";
    
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

    /**
     * 静态初始化块，用于加载配置文件中的常量值
     */
    static {
        // 加载基本配置
        AUTHOR_COMMENT = PropertiesUtils.getString("author.comment");
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_QUERY = PropertiesUtils.getString("suffix.bean.parma");
        SUFFIX_BEAN_QUERY_FUZZY = PropertiesUtils.getString("suffix.bean.query.fuzzy");
        SUFFIX_BEAN_QUERY_TIME_START = PropertiesUtils.getString("suffix.bean.query.time.start");
        SUFFIX_BEAN_QUERY_TIME_END = PropertiesUtils.getString("suffix.bean.query.time.end");
        SUFFIX_MAPPER = PropertiesUtils.getString("suffix.mapper");
        SUFFIX_SERVICE = PropertiesUtils.getString("suffix.service");
        SUFFIX_SERVICE_IMPL = PropertiesUtils.getString("suffix.service.impl");

        // 加载JSON忽略配置
        IGNORE_BEAN_TOJSON_FILED = PropertiesUtils.getString("ignore.bean.tojson.filed");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.tojson.class");
        // 加载日期格式配置
        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        BEAN_DATE_PARSE_EXPRESSION = PropertiesUtils.getString("bean.date.parse.expression");
        BEAN_DATE_PARSE_CLASS = PropertiesUtils.getString("bean.date.parse.class");

        // 加载包名配置
        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.po");
        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getString("package.query");
        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.utils");
        PACKAGE_ENUM = PACKAGE_BASE + "." + PropertiesUtils.getString("package.enum");
        PACKAGE_MAPPER = PACKAGE_BASE + "." + PropertiesUtils.getString("package.mapper");
        PACKAGE_SERVICE = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service");
        PACKAGE_SERVICE_IMPL = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service.impl");
        PACKAGE_VO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.vo");

        // 加载路径配置
        PATH_BASE = PropertiesUtils.getString("path.base");
        PATH_BASE = PATH_BASE + PATH_JAVA;

        PATH_PO = PATH_BASE + "/" + PACKAGE_PO.replace(".", "/");
        PATH_QUERY = PATH_BASE + "/" + PACKAGE_QUERY.replace(".", "/");
        PATH_UTILS = PATH_BASE + "/" + PACKAGE_UTILS.replace(".", "/");
        PATH_ENUM = PATH_BASE + "/" + PACKAGE_ENUM.replace(".", "/");
        PATH_MAPPER = PATH_BASE + "/" + PACKAGE_MAPPER.replace(".", "/");
        PATH_MAPPER_XML = PATH_BASE + "/" + PACKAGE_MAPPER.replace(".", "/") + "/" + PATH_RESOURCE;
        PATH_SERVICE = PATH_BASE + "/" + PACKAGE_SERVICE.replace(".", "/");
        PATH_SERVICE_IMPL = PATH_BASE + "/" + PACKAGE_SERVICE_IMPL.replace(".", "/");
        PATH_VO = PATH_BASE + "/" + PACKAGE_VO.replace(".", "/");
    }
}
