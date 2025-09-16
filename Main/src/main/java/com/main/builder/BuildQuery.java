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
 * BuildPo类用于生成PO(Persistent Object)文件，即Java实体类。
 * 该类根据数据库表结构信息自动生成对应的Java实体类文件。
 */
public class BuildQuery {
    // 使用Logger记录日志，记录类名为BuildPo
    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);

    /**
     * 执行生成PO文件的方法
     * @param tableInfo 包含表结构信息的对象，如字段名、类型、注释等
     */
    public static void execute(TableInfo tableInfo) {
        // 创建PO文件存放的文件夹路径
        File folder = new File(Constants.PATH_QUERY);
        // 如果文件夹不存在，则创建文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
        // File 对象本身不包含文件内容，它只是文件的路径表示。要实际读写文件内容，需要将 File 对象与 IO 流结合使用
        File file = new File(folder, className + ".java");
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            // 写入包名
            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();

            // 检查是否有日期或日期时间类型的字段，如果有则导入Date类
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
            }

            // 检查是否有BigDecimal类型的字段，如果有则导入BigDecimal类
            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }

            // 创建类的注释
            BuildComment.createClassComment(bw, tableInfo.getComment() + "查询");

            // 写入类的声明
            bw.write("public class " + className + " {");
            bw.newLine();

            List<FieldInfo> extendList = new ArrayList();
            // 遍历所有字段，生成字段声明和注释
            for(FieldInfo fieldInfo : tableInfo.getFieldList()){
                // 创建字段注释
                BuildComment.createFieldComment(bw, fieldInfo.getComment());
                // 写入字段声明
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                //bw.newLine();

                if (ArrayUtils.contains(Constants.STRING_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY + ";");
                    bw.newLine();
                    bw.newLine();

                    FieldInfo fuzzyFieldInfo = new FieldInfo();
                    fuzzyFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    fuzzyFieldInfo.setJavaType(fieldInfo.getJavaType());
                    extendList.add(fuzzyFieldInfo);
                }

                if (ArrayUtils.contains(Constants.DATE_TIME_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.DATE_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START + ";");
                    bw.newLine();
                    bw.newLine();

                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END + ";");
                    bw.newLine();
                    bw.newLine();

                    FieldInfo FieldInfo = new FieldInfo();
                    FieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    FieldInfo.setJavaType("String");
                    extendList.add(FieldInfo);

                    FieldInfo FieldInfo2 = new FieldInfo();
                    FieldInfo2.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    FieldInfo2.setJavaType("String");
                    extendList.add(FieldInfo2);
                }
            }

            tableInfo.getFieldList().addAll(extendList);

            // 生成所有字段的getter和setter方法
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
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

            // 类结束大括号
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            // 记录生成PO文件失败的错误日志
            logger.error("生成PO文件失败", e);
        }
    }
}
