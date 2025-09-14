package com.main.builder;

import com.main.bean.Constants;
import com.main.bean.FieldInfo;
import com.main.bean.TableInfo;
import com.main.utils.BooleanUtils;
import com.main.utils.JsonUtils;
import com.main.utils.PropertiesUtils;
import com.main.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BuildTable类用于管理数据库连接和获取表信息
 * 提供了数据库连接的创建、关闭以及获取表信息的功能
 */
public class BuildTable {
    // 使用Logger记录日志信息
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    // 数据库连接对象
    private static Connection conn = null;

    // SQL查询语句，用于获取表状态信息
    private static final String SQL_SHOW_TABLE_STATUS = "show table status";
    private static final String SQL_SHOW_TABLE_FIELD = "show full fields from %s";
    private static final String SQL_SHOW_TABLE_INDEX= "show index from %s";

    // 静态代码块，用于初始化数据库连接
    static {
        // 从配置文件中获取数据库连接信息
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String username = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            // 加载数据库驱动
            Class.forName(driverName);
            // 建立数据库连接
            conn = DriverManager.getConnection(url, username, password);
            
            // 添加关闭钩子，确保程序退出时关闭连接
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                closeConnection();
            }));
            
            // 记录连接成功的日志
            logger.info("数据库连接成功");
        } catch (Exception e) {
            // 记录连接失败的日志
            logger.error("数据库连接失败", e);
        }
    }

    /**
     * 关闭数据库连接的方法
     * 确保连接被正确关闭，并记录相应的日志信息
     */
    public static void closeConnection() {
        if (conn != null) {
            try {
                // 关闭数据库连接
                conn.close();
                // 记录连接已关闭的日志
                logger.info("数据库连接已关闭");
            } catch (SQLException e) {
                // 记录关闭连接失败的日志
                logger.error("关闭数据库连接失败", e);
            } finally {
                // 将连接对象置为null
                conn = null;
            }
        }
    }

    /**
     * 获取数据库中所有表的信息
     * 包括表名和表的注释
     */
    public static List<TableInfo> getTables() {
        // 检查数据库连接是否存在
        if (conn == null) {
            logger.warn("数据库连接未建立，无法获取表信息");
            return new ArrayList<>();
        }

    // 创建一个用于存储表信息的列表
        List<TableInfo> tableList = new ArrayList<>();
        // 使用try-with-resources确保资源被正确关闭
        try (PreparedStatement ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
             ResultSet tableResult = ps.executeQuery()) {
            
            // 遍历结果集，输出表名和注释
            while (tableResult.next()) {
            // 获取表名和注释
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");

            // 处理bean名称，如果需要忽略表前缀
                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    beanName = tableName.substring(tableName.indexOf("_") + 1);
                }
            // 处理字段名称
                beanName = processField(tableName, true);

            // 创建表信息对象并设置基本信息
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
            // 设置bean参数名称，添加后缀
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_PARMA);

            // 读取字段信息和字段对应索引信息并设置到表信息对象中
                readFieldInfo(tableInfo);
                getIndexInfos(tableInfo);
                logger.info("表{}", JsonUtils.toJson(tableInfo));
            // 将表信息添加到列表中
                tableList.add(tableInfo);
            }
        } catch (SQLException e) {
            // 记录查询失败的日志
            logger.error("查询表信息失败", e);
        }

        return tableList;
    }

    public static void readFieldInfo(TableInfo tableInfo) {
        // 检查数据库连接是否存在
        if (conn == null) {
            logger.warn("数据库连接未建立，无法获取表信息");
            return;
        }

        // 首先检查表名是否合法，防止SQL注入
        if (!isValidTableName(tableInfo.getTableName())) {
            logger.error("表名不合法: {}", tableInfo.getTableName());
            return;
        }

        List<FieldInfo> fieldList = new ArrayList<>();
        // 使用try-with-resources确保资源被正确关闭
        try (PreparedStatement ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELD, tableInfo.getTableName()));
             ResultSet fieldResult = ps.executeQuery()) {

            // 遍历结果集，获取字段信息
            while (fieldResult.next()) {
                FieldInfo fieldInfo = new FieldInfo();
                
                // 获取字段名并转换为驼峰命名
                String fieldName = fieldResult.getString("field");
                String fieldNameCamel = processField(fieldName, false);
                fieldInfo.setPropertyName(fieldNameCamel);
                fieldInfo.setFieldName(fieldName);

                // 获取字段注释
                String comment = fieldResult.getString("comment");
                fieldInfo.setComment(comment);
                
                // 获取字段类型并映射到Java类型
                String fieldType = fieldResult.getString("type");
                if (fieldType.indexOf("(") > 0){
                    fieldType = fieldType.substring(0, fieldType.indexOf("("));
                }
                fieldInfo.setJavaType(getJavaType(fieldType));
                fieldInfo.setSqlType(fieldType.toUpperCase());
                
                // 获取是否自增
                String extra = fieldResult.getString("extra");
                fieldInfo.setIsAutoIncrement("auto_increment".equalsIgnoreCase(extra));

                // 获取是否包含日期时间
                if (tableInfo.getHaveDateTime() == null || BooleanUtils.compare(tableInfo.getHaveDateTime(), Boolean.FALSE)) tableInfo.setHaveDateTime(ArrayUtils.contains(Constants.DATE_TIME_TYPES, fieldType.toUpperCase()));
                // 获取是否包含日期
                if (tableInfo.getHaveDate() == null || BooleanUtils.compare(tableInfo.getHaveDate(), Boolean.FALSE)) tableInfo.setHaveDate(ArrayUtils.contains(Constants.DATE_TYPES, fieldType.toUpperCase()));
                // 获取是否包含BigDecimal
                if (tableInfo.getHaveBigDecimal() == null || BooleanUtils.compare(tableInfo.getHaveBigDecimal(), Boolean.FALSE)) tableInfo.setHaveBigDecimal(ArrayUtils.contains(Constants.DECIMAL_TYPES, fieldType.toUpperCase()) ||
                        ArrayUtils.contains(Constants.FLOAT_TYPES, fieldType.toUpperCase()));

                fieldList.add(fieldInfo);
            }

            tableInfo.setFieldList(fieldList);
        } catch (SQLException e) {
            logger.error("查询表 {} 的字段信息失败", tableInfo.getTableName(), e);
        }
    }

/**
 * 获取指定表的索引信息
 * @param tableInfo 包含表名和表结构信息的对象
 */
    public static void getIndexInfos(TableInfo tableInfo) {
        // 检查数据库连接是否存在
        if (conn == null) {
            logger.warn("数据库连接未建立，无法获取表信息");
            return;
        }

        // 首先检查表名是否合法，防止SQL注入
        if (!isValidTableName(tableInfo.getTableName())) {
            logger.error("表名不合法: {}", tableInfo.getTableName());
            return;
        }

        // 用于存储索引信息的列表
        List<FieldInfo> indexList = new ArrayList<>();
        // 使用try-with-resources确保资源被正确关闭
        try (PreparedStatement ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX, tableInfo.getTableName()));
             ResultSet indexResult = ps.executeQuery()) {

            Map<String, FieldInfo> tempMap = new HashMap<>();
            // 遍历表字段列表，找到匹配的字段并添加到索引字段列表中
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                tempMap.put(fieldInfo.getFieldName(), fieldInfo);
            }
            // 遍历结果集，获取索引信息
            while (indexResult.next()) {
                // 获取索引名
                String keyName = indexResult.getString("key_name");
                // 获取索引是否唯一标识(0表示唯一索引，1表示非唯一索引)
                Integer nonUnique = indexResult.getInt("non_unique");
                // 获取索引对应的列名
                String columnName = indexResult.getString("column_name");
                // 如果是非唯一索引则跳过
                if (nonUnique == 1) {
                    continue;
                }
                // 获取当前索引名对应的字段列表
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                // 如果列表不存在，则创建新列表
                if (keyFieldList == null) {
                    keyFieldList = new ArrayList<>();
                    tableInfo.getKeyIndexMap().put(keyName, keyFieldList);
                }
                keyFieldList.add(tempMap.get(columnName));
            }
        } catch (SQLException e) {
            // 记录错误日志
            logger.error("查询表 {} 的字段信息失败", tableInfo.getTableName(), e);
        }
    }

/**
     * 验证表名是否合法，防止SQL注入
     * @param tableName 待验证的表名
     * @return 如果表名合法返回true，否则返回false
     */
    private static boolean isValidTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return false;
        }
        
        // 检查表名是否只包含字母、数字和下划线
        if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            return false;
        }
        
        // 检查表名是否包含可能的SQL注入关键词
        String[] sqlKeywords = {"SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "UNION", "EXEC", 
                               "EXECUTE", "TRUNCATE", "GRANT", "REVOKE", "xp_"};
        String upperTableName = tableName.toUpperCase();
        
        for (String keyword : sqlKeywords) {
            if (upperTableName.contains(keyword)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 将数据库字段类型转换为Java类型
     * @param fieldType 数据库字段类型
     * @return 对应的Java类型
     */
    private static String getJavaType(String fieldType) {
        if (fieldType == null) {
            return Constants.JAVA_TYPE_STRING;
        }
        
        String upperFieldType = fieldType.toUpperCase();
        
        // 整数类型
        if (ArrayUtils.contains(Constants.INTEGER_TYPES, upperFieldType)) {
            return Constants.JAVA_TYPE_INT;
        }
        // 长整型
        else if (ArrayUtils.contains(Constants.LONG_TYPES, upperFieldType)) {
            return Constants.JAVA_TYPE_LONG;
        }
        // 小整数类型
        else if (ArrayUtils.contains(Constants.BYTE_TYPES, upperFieldType)) {
            return Constants.JAVA_TYPE_BYTE;
        }
        else if (ArrayUtils.contains(Constants.SHORT_TYPES, upperFieldType)) {
            return Constants.JAVA_TYPE_SHORT;
        }
        // 浮点类型
        else if (ArrayUtils.contains(Constants.FLOAT_TYPES, upperFieldType)) {
            return Constants.JAVA_TYPE_DOUBLE;
        }
        // 精确数值类型
        else if (ArrayUtils.contains(Constants.DECIMAL_TYPES, upperFieldType)) {
            return Constants.JAVA_TYPE_DECIMAL;
        }
        // 布尔类型
        else if (ArrayUtils.contains(Constants.BOOLEAN_TYPES, upperFieldType)) {
            return Constants.JAVA_TYPE_BOOLEAN;
        }
        // 日期时间类型
        else if (ArrayUtils.contains(Constants.DATE_TYPES, upperFieldType) || ArrayUtils.contains(Constants.DATE_TIME_TYPES, upperFieldType)) {
            return Constants.JAVA_TYPE_DATE;
        }
        // 字符串类型
        else if (ArrayUtils.contains(Constants.STRING_TYPES, upperFieldType)) {
            return Constants.JAVA_TYPE_STRING;
        }
        // 默认返回字符串类型
        else {
            return Constants.JAVA_TYPE_STRING;
        }
    }

    /**
 * 处理字段字符串，将其转换为驼峰命名格式
 * @param field 待处理的字段字符串，可能包含下划线
 * @param isuperCasefirst 是否将首字母大写
 * @return 处理后的驼峰格式字符串
 */
    private static String processField (String field, Boolean isuperCasefirst) {
    // 使用StringBuffer来构建结果字符串
        StringBuffer strBuffer = new StringBuffer();
    // 按下划线分割字符串为单词数组
        String[] words = field.split("_");
    // 根据uperCaseFirst参数决定是否将第一个单词首字母大写
        strBuffer.append(isuperCasefirst ? StringUtils.uperCaseFirst(words[0]):words[0]);
    // 循环处理剩余单词，将每个单词首字母大写后追加到结果中
        for (int i = 1; i < words.length; i++) {
            strBuffer.append(StringUtils.uperCaseFirst(words[i]));
        }
    // 返回处理后的字符串
        return strBuffer.toString();
    }
}
