package com.main.builder;

import com.main.bean.Constants;
import com.main.bean.FieldInfo;
import com.main.bean.TableInfo;
import com.main.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * BuildMapperXml类用于生成MyBatis的Mapper XML文件
 * 该类根据表结构信息自动生成标准的CRUD操作SQL语句
 * 包括实体映射、通用查询条件、CRUD操作等SQL语句
 */
public class BuildMapperXml {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);
    // 基础字段列表SQL片段ID
    private static final String BASE_COLUMN_LIST = "base_column_list";
    // 基础查询条件SQL片段ID
    private static final String BASE_QUERY_CONDITION = "base_query_condition";
    // 扩展查询条件SQL片段ID
    private static final String BASE_QUERY_CONDITION_EXTEND = "base_query_condition_extend";
    // 通用查询条件SQL片段ID
    private static final String QUERY_CONDITION = "query_condition";

    /**
     * 执行生成Mapper XML文件的方法
     * @param tableInfo 表结构信息对象，包含表名、字段信息等
     */
    public static void execute(TableInfo tableInfo) {
        // 创建Mapper XML文件所在目录
        File folder = new File(Constants.PATH_MAPPER_XML);
        if (!folder.exists()) {
            folder.mkdirs(); // 如果目录不存在，则创建目录
        }
        // 构建Mapper类名和文件路径
        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
        File file = new File(folder, className + ".xml");
        
        // 使用try-with-resources方式创建文件写入流，确保资源自动关闭
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            // 写入XML文件头
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPER + "." + className + "\">");
            bw.newLine();
            
            // 写入实体映射ResultMap
            bw.write("\t<!-- 实体映射 -->");
            bw.newLine();
            String poClass = Constants.PACKAGE_PO + "." + tableInfo.getBeanName();
            bw.write("\t<resultMap id=\"base_result_map\" type=\"" + poClass + "\">");
            bw.newLine();

            // 查找主键字段
            FieldInfo idField = null;
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                if ("PRIMARY".equals(entry.getKey())) {
                    List<FieldInfo> fieldInfoList = entry.getValue();
                    if (fieldInfoList.size() == 1) {
                        idField = fieldInfoList.get(0);
                        break;
                    }
                }
            }

        // 遍历所有字段，生成ResultMap映射
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t<!-- " + fieldInfo.getComment() + " -->");
                bw.newLine();
                String key = "";
                if (idField != null && idField.getPropertyName().equals(fieldInfo.getPropertyName())) {
                    key = "id"; // 主键字段使用id标签
                } else {
                    key = "result"; // 非主键字段使用result标签
                }
                bw.write("\t\t<" + key + " column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\"/>");
                bw.newLine();
            }
            bw.write("\t</resultMap>");
            bw.newLine();

        // 生成通用查询结果列SQL片段
            bw.newLine();
            bw.write("<!-- 通用查询结果列 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_COLUMN_LIST + "\">");
            bw.newLine();
            StringBuffer columnBuilder = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                columnBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            String columnBuilderStr = columnBuilder.substring(0, columnBuilder.length() - 1);
            bw.write("\t\t" + columnBuilderStr);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

        // 生成基础查询条件SQL片段
            bw.newLine();
            bw.write("<!-- 基础查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String stringQuery = "";
                if (ArrayUtils.contains(Constants.STRING_TYPES, fieldInfo.getSqlType())) {
                    stringQuery = "and query." + fieldInfo.getPropertyName() + "!=''";
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null " + stringQuery + "\">");
                bw.newLine();
                bw.write("\t\t\tand " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();

        // 生成扩展查询条件SQL片段
            bw.newLine();
            bw.write("\t<!-- 扩展查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION_EXTEND + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldExtendList()) {
                String andWhere = "";
                if (ArrayUtils.contains(Constants.STRING_TYPES, fieldInfo.getSqlType())) {
                    andWhere = "and " + fieldInfo.getFieldName() + " like concat('%',#{query." + fieldInfo.getPropertyName() + "},'%')";
                } else if (ArrayUtils.contains(Constants.DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)){
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " >= str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d')]]>";
                    } else if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)){
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " < date_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d'), interval -1 day)]]>";
                    }
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null and query." + fieldInfo.getPropertyName() + " !=''\">");
                bw.newLine();
                bw.write("\t\t\t" + andWhere );
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();

        // 生成通用查询条件SQL片段
            bw.newLine();
            bw.write("\t<!-- 通用查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + QUERY_CONDITION + "\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION_EXTEND + "\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

        // 生成查询列表SQL
            bw.newLine();
            bw.write("\t<!-- 查询列表 -->");
            bw.newLine();
            bw.write("\t<select id=\"selectList\" resultMap=\"base_result_map\">");
            bw.newLine();
            bw.write("\t\tSELECT <include refid=\"" + BASE_COLUMN_LIST + "\"/>");
            bw.newLine();
            bw.write("\t\tFROM " + tableInfo.getTableName() + " ");
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.orderBy != null and query.orderBy != ''\">");
            bw.newLine();
            bw.write("\t\t\torder by ${query.orderBy}");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.simplePage != null\">");
            bw.newLine();
            bw.write("\t\t\tlimit #{query.simplePage.start}, #{query.simplePage.end}");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();

        // 生成查询数量SQL
            bw.newLine();
            bw.write("\t<!-- 查询数量 -->");
            bw.newLine();
            bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Integer\">");
            bw.newLine();
            bw.write("\t\tSELECT count(1) FROM " + tableInfo.getTableName() + " ");
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();

        // 生成插入SQL
            bw.newLine();
            bw.write("\t<!-- 插入(匹配有值的字段) -->");
            bw.newLine();
            bw.write("\t<insert id=\"insert\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
            bw.newLine();
            FieldInfo autoIncrementField = null;
        // 查找自增字段
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getIsAutoIncrement() != null && fieldInfo.getIsAutoIncrement()) {
                    autoIncrementField = fieldInfo;
                    break;
                }
            }
        // 如果有自增字段，添加获取自增ID的selectKey
            if (autoIncrementField != null) {
                bw.write("\t\t<selectKey keyProperty=\"bean." + autoIncrementField.getFieldName() + "\" order=\"AFTER\" resultType=\"" + autoIncrementField.getJavaType() + "\">");
                bw.newLine();
                bw.write("\t\t\tSELECT LAST_INSERT_ID()");
                bw.newLine();
                bw.write("\t\t</selectKey>");
                bw.newLine();
            }
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成插入字段
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ", ");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"values(\" suffix=\")\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成插入值
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "}, ");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

        // 生成插入或更新SQL
            bw.newLine();
            bw.write("\t<!-- 插入或更新 -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成插入字段
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ", ");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"values(\" suffix=\")\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成插入值
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "}, ");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"on duplicate key update\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成更新字段
            Map<String, String> keyTempMap = new HashMap();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()){
                List<FieldInfo> fieldInfoList = entry.getValue();
                for (FieldInfo fieldInfo : fieldInfoList) {
                    keyTempMap.put(fieldInfo.getFieldName(), fieldInfo.getPropertyName());
                }
            }
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (keyTempMap.containsKey(fieldInfo.getFieldName())) {
                    continue; // 跳过主键字段
                }
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "), ");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

        // 生成批量插入SQL
            bw.newLine();
            bw.write("\t<!-- 批量插入 -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertBatch\" parameterType=\""+ poClass + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成插入字段（跳过自增字段）
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getIsAutoIncrement()) continue;
                bw.write("\t\t\t" + fieldInfo.getFieldName() + ", ");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\tvalues");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\" >");
            bw.newLine();
            bw.write("\t\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成插入值（跳过自增字段）
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getIsAutoIncrement()) continue;
                bw.write("\t\t\t\t#{item." + fieldInfo.getPropertyName() + "}, ");
                bw.newLine();
            }
            bw.write("\t\t\t</trim>");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

        // 生成批量插入或更新SQL
            bw.newLine();
            bw.write("\t<!-- 批量插入或更新 -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\""+ poClass + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成插入字段（跳过自增字段）
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getIsAutoIncrement()) continue;
                bw.write("\t\t\t" + fieldInfo.getFieldName() + ", ");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\tvalues");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\" >");
            bw.newLine();
            bw.write("\t\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成插入值（跳过自增字段）
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getIsAutoIncrement()) continue;
                bw.write("\t\t\t\t#{item." + fieldInfo.getPropertyName() + "}, ");
                bw.newLine();
            }
            bw.write("\t\t\t</trim>");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"on duplicate key update\" suffixOverrides=\",\" >");
            bw.newLine();
        // 生成更新字段（跳过自增字段）
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getIsAutoIncrement()) continue;
                bw.write("\t\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "), ");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

            // 生成多条件更新SQL
            bw.write("\t<!-- 多条件更新 -->");
            bw.newLine();
            bw.write("\t<update id=\"updateByParam\">");
            bw.newLine();
            bw.write("\t\tupdate " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<set>");
            bw.newLine();
            // 生成更新字段（跳过自增字段）
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getIsAutoIncrement()) continue;
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "}, ");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</set>");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            // 生成条件字段
            // 检查主键和唯一索引
            Set<String> keyFieldSet = new HashSet<>();
            StringBuilder anyKeyExist = new StringBuilder();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();
                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    if (keyFieldSet.contains(fieldInfo.getPropertyName())) continue;
                    keyFieldSet.add(fieldInfo.getPropertyName());
                    anyKeyExist.append("query.").append(fieldInfo.getPropertyName()).append(" == null and ");
                }
            }
            anyKeyExist.delete(anyKeyExist.length() - 4, anyKeyExist.length());
            bw.write("\t\t\t<if test=\"" + anyKeyExist + "\">");
            bw.newLine();
            bw.write("\t\t\t\t1 = 2");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
            // 引用通用查询条件
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</update>");
            bw.newLine();
            bw.newLine();

            // 生成多条件删除SQL
            bw.write("\t<!-- 多条件删除 -->");
            bw.newLine();
            bw.write("\t<delete id=\"deleteByParam\">");
            bw.newLine();
            bw.write("\t\tdelete from " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            // 生成条件字段
            bw.write("\t\t\t<if test=\"" + anyKeyExist + "\">");
            bw.newLine();
            bw.write("\t\t\t\t1 = 2");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
            // 引用通用查询条件
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</delete>");
            bw.newLine();
            bw.newLine();

        // 根据主键生成对应的查询、删除、更新SQL
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                StringBuffer methodName = new StringBuffer();
                StringBuffer methodParam = new StringBuffer();

                for (FieldInfo fieldInfo : keyFieldInfoList){
                    methodName.append(StringUtils.uperCaseFirst(fieldInfo.getPropertyName())).append("And");
                    methodParam.append(fieldInfo.getFieldName()).append(" = #{").append(fieldInfo.getPropertyName()).append("}").append(" and ");
                }
                bw.newLine();
                methodName.delete(methodName.length() - 3, methodName.length());
                methodParam.delete(methodParam.length() - 4, methodParam.length());

                bw.write("\t<!-- 根据" + methodName + "查询 -->");
                bw.newLine();
                bw.write("\t<select id=\"selectBy" + methodName + "\" resultMap=\"base_result_map\">");
                bw.newLine();
                bw.write("\t\tselect <include refid=\"" + BASE_COLUMN_LIST + "\"/>");
                bw.newLine();
                bw.write("\t\tfrom " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\twhere " + methodParam);
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();

                bw.newLine();
                bw.write("\t<!-- 根据" + methodName + "删除 -->");
                bw.newLine();
                bw.write("\t<delete id=\"deleteBy" + methodName + "\">");
                bw.newLine();
                bw.write("\t\tdelete from " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\twhere " + methodParam);
                bw.newLine();
                bw.write("\t</delete>");
                bw.newLine();

                bw.newLine();
                bw.write("\t<!-- 根据" + methodName + "更新 -->");
                bw.newLine();
                bw.write("\t<update id=\"updateBy" + methodName + "\">");
                bw.newLine();
                bw.write("\t\tupdate " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\t<set>");
                bw.newLine();
                for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                    bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "}, ");
                    bw.newLine();
                    bw.write("\t\t\t</if>");
                    bw.newLine();
                }
                bw.write("\t\t</set>");
                bw.newLine();
                bw.write("\t\twhere " + methodParam);
                bw.newLine();
                bw.write("\t</update>");
                bw.newLine();
            }

            bw.write("</mapper>");
            bw.flush();
        } catch (Exception e) {
            logger.error("生成mapper xml文件失败", e);
        }
    }
}
