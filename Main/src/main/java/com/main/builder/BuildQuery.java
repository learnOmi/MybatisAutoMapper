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
import java.util.ArrayList;
import java.util.List;

/**
 * BuildQuery类用于生成查询条件(Query)类文件。
 * 该类根据数据库表结构信息自动生成对应的查询条件类，用于构建查询条件。
 * 生成的查询类包含表的所有字段，并为字符串类型字段添加模糊查询属性，
 * 为日期/日期时间类型字段添加开始时间和结束时间查询属性。
 */
public class BuildQuery {
    // 使用Logger记录日志，记录类名为BuildPo
    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);

    /**
     * 执行生成查询条件(Query)类的方法
     * 根据数据库表结构信息自动生成对应的查询条件类
     * @param tableInfo 包含表结构信息的对象，包含表名、字段列表、字段类型、字段注释等信息
     */
    public static void execute(TableInfo tableInfo) {
        // 创建查询条件类文件存放的文件夹路径
        File folder = new File(Constants.PATH_QUERY);
        // 如果文件夹不存在，则创建文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 构建查询条件类的类名
        String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
        // 创建查询条件类文件对象
        // File 对象本身不包含文件内容，它只是文件的路径表示。要实际读写文件内容，需要将 File 对象与 IO 流结合使用
        File file = new File(folder, className + ".java");
        // 使用try-with-resources语句自动管理资源，确保文件流正确关闭
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            
            // 写入包名声明
            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();

            // 检查是否有日期或日期时间类型的字段，如果有则导入Date类
            // 这些字段在查询条件中可能需要用于范围查询
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
            }

            // 检查是否有BigDecimal类型的字段，如果有则导入BigDecimal类
            // 这些字段在查询条件中可能需要进行数值比较
            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }

            // 创建类的注释，添加"查询"字样以表明这是一个查询条件类
            BuildComment.createClassComment(bw, tableInfo.getComment() + "查询");

            // 写入类的声明
            bw.write("public class " + className + " {");
            bw.newLine();

            // 用于存储扩展字段的列表，包括模糊查询字段和时间范围查询字段
            List<FieldInfo> extendList = new ArrayList();
            
            // 遍历所有字段，生成字段声明和注释
            // 同时根据字段类型生成额外的查询条件字段
            for(FieldInfo fieldInfo : tableInfo.getFieldList()){
                // 创建字段注释
                BuildComment.createFieldComment(bw, fieldInfo.getComment());
                // 写入字段声明
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                //bw.newLine();

                // 处理字符串类型字段，添加模糊查询属性
                if (ArrayUtils.contains(Constants.STRING_TYPES, fieldInfo.getSqlType())) {
                    // 为字符串类型字段添加模糊查询属性，属性名后添加Fuzzy后缀
                    bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY + ";");
                    bw.newLine();
                    bw.newLine();

                    // 创建模糊查询字段的FieldInfo对象并添加到扩展字段列表
                    FieldInfo fuzzyFieldInfo = new FieldInfo();
                    fuzzyFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    fuzzyFieldInfo.setJavaType(fieldInfo.getJavaType());
                    extendList.add(fuzzyFieldInfo);
                }

                // 处理日期和日期时间类型字段，添加时间范围查询属性
                if (ArrayUtils.contains(Constants.DATE_TIME_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.DATE_TYPES, fieldInfo.getSqlType())) {
                    // 为日期类型字段添加开始时间查询属性，属性名后添加TimeStart后缀
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START + ";");
                    bw.newLine();
                    bw.newLine();

                    // 为日期类型字段添加结束时间查询属性，属性名后添加TimeEnd后缀
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END + ");");
                    bw.newLine();
                    bw.newLine();

                    // 创建开始时间查询字段的FieldInfo对象并添加到扩展字段列表
                    FieldInfo startTimeFieldInfo = new FieldInfo();
                    startTimeFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    startTimeFieldInfo.setJavaType("String");
                    extendList.add(startTimeFieldInfo);

                    // 创建结束时间查询字段的FieldInfo对象并添加到扩展字段列表
                    FieldInfo endTimeFieldInfo = new FieldInfo();
                    endTimeFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    endTimeFieldInfo.setJavaType("String");
                    extendList.add(endTimeFieldInfo);
                }
            }

            // 创建一个合并后的字段列表，包含原始字段和扩展字段，但不修改原始tableInfo中的FieldList
            List<FieldInfo> allFieldList = new ArrayList<>(tableInfo.getFieldList());
            allFieldList.addAll(extendList);

            // 生成所有字段的getter和setter方法，包括原始字段和扩展字段
            for (FieldInfo fieldInfo : allFieldList) {
                // 将属性名首字母大写，用于构造getter和setter方法名
                String methodName = StringUtils.uperCaseFirst(fieldInfo.getPropertyName());
                
                // 生成setter方法
                bw.write("\tpublic void set" + methodName + "(" + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ") {");
                bw.newLine();
                bw.write("\t\tthis." + fieldInfo.getPropertyName() + " = " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                // 生成getter方法
                bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + methodName + "() {");
                bw.newLine();
                bw.write("\t\treturn " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }

            // 写入类的结束大括号
            bw.write("}");
            // 确保所有内容写入文件
            bw.flush();
        } catch (Exception e) {
            // 记录生成查询条件类文件失败的错误日志
            logger.error("生成Query文件失败", e);
            // 可以根据需要在这里添加更多的错误处理逻辑，如重试机制或通知机制
        }
    }
}
