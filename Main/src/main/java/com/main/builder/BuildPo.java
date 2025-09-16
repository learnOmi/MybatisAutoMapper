package com.main.builder;

import com.main.bean.Constants;
import com.main.bean.FieldInfo;
import com.main.bean.TableInfo;
import com.main.utils.DateUtils;
import com.main.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * BuildPo类用于生成PO(Persistent Object)文件，即Java实体类。
 * 该类根据数据库表结构信息自动生成对应的Java实体类文件。
 */
public class BuildPo {
    // 使用Logger记录日志，记录类名为BuildPo
    private static final Logger logger = LoggerFactory.getLogger(BuildPo.class);

    /**
     * 执行生成PO文件的方法
     * @param tableInfo 包含表结构信息的对象，如字段名、类型、注释等
     */
    public static void execute(TableInfo tableInfo) {
        // 创建PO文件存放的文件夹路径
        File folder = new File(Constants.PATH_PO);
        // 如果文件夹不存在，则创建文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // File 对象本身不包含文件内容，它只是文件的路径表示。要实际读写文件内容，需要将 File 对象与 IO 流结合使用
        File file = new File(folder, tableInfo.getBeanName() + ".java");
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            // 写入包名
            bw.write("package " + Constants.PACKAGE_PO + ";");
            bw.newLine();
            bw.newLine();
            // 写入Serializable接口的导入语句
            bw.write("import java.io.Serializable;");
            bw.newLine();

            // 检查是否有日期或日期时间类型的字段，如果有则导入Date类
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
                // 导入日期格式化相关类
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                // 导入日期解析相关类
                bw.write(Constants.BEAN_DATE_PARSE_CLASS);
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_UTILS + ".DateUtils;");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_ENUM + ".DateTimePatternEnum;");
                bw.newLine();
            }

            // 检查是否有需要忽略JSON序列化的字段
            Boolean haveIgnoreBean = false;
            for (FieldInfo field : tableInfo.getFieldList()) {
                if (ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FILED.split(","), field.getPropertyName())) {
                    haveIgnoreBean = true;
                    break;
                }
            }
            // 如果有需要忽略JSON序列化的字段，则导入相关注解类
            if (haveIgnoreBean) {
                bw.write(Constants.IGNORE_BEAN_TOJSON_CLASS);
                bw.newLine();
            }

            // 检查是否有BigDecimal类型的字段，如果有则导入BigDecimal类
            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }

            // 创建类的注释
            BuildComment.createClassComment(bw, tableInfo.getComment());

            // 写入类的声明，实现Serializable接口
            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();

            // 遍历所有字段，生成字段声明和注释
            for(FieldInfo fieldInfo : tableInfo.getFieldList()){
                // 创建字段注释
                BuildComment.createFieldComment(bw, fieldInfo.getComment());

                // 如果是日期时间类型，添加日期格式化注解
                if (ArrayUtils.contains(Constants.DATE_TIME_TYPES, fieldInfo.getSqlType())){
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.DATE_TIME_FORMAT));
                    bw.newLine();

                    bw.write("\t" + String.format(Constants.BEAN_DATE_PARSE_EXPRESSION, DateUtils.DATE_TIME_FORMAT));
                    bw.newLine();
                }

                // 如果是日期类型，添加日期格式化注解
                if (ArrayUtils.contains(Constants.DATE_TYPES, fieldInfo.getSqlType())){
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.DATE_FORMAT));
                    bw.newLine();

                    bw.write("\t" + String.format(Constants.BEAN_DATE_PARSE_EXPRESSION, DateUtils.DATE_FORMAT));
                    bw.newLine();
                }

                // 如果是需要忽略JSON序列化的字段，添加相关注解
                if (ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FILED.split(","), fieldInfo.getPropertyName())){
                    bw.write("\t" + String.format(Constants.IGNORE_BEAN_TOJSON_EXPRESSION, fieldInfo.getPropertyName()));
                    bw.newLine();
                }

                // 写入字段声明
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                //bw.newLine();
            }
            bw.newLine();

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

            // 重写toString方法
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn \"" + tableInfo.getBeanName() + " [\" +");
            // 使用StringBuffer构建toString方法的内容
            StringBuffer str = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String properName = fieldInfo.getPropertyName();
                if (ArrayUtils.contains(Constants.DATE_TIME_TYPES, fieldInfo.getSqlType())){
                    properName = "DateUtils.format(" + properName + ", DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())";
                } else if (ArrayUtils.contains(Constants.DATE_TYPES, fieldInfo.getSqlType())){
                    properName = "DateUtils.format(" + properName + ", DateTimePatternEnum.YYYY_MM_DD.getPattern())";
                }
                str.append("\n\t\t\t\"").append(fieldInfo.getPropertyName()).append("=\" + ").append("(").append(fieldInfo.getPropertyName()).append(" == null ? \"空\" : ").append(properName).append(")").append(" + \", \" +");
            }
            // 移除最后一个", "并添加结束括号
            str.replace(str.lastIndexOf("\", \" +"), str.length(), "");
            str.append("\n\t\t\t\"]\"");
            str.append(";\n");
            bw.write(str.toString());
            bw.write("\t}");
            bw.newLine();

            // 类结束大括号
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            // 记录生成PO文件失败的错误日志
            logger.error("生成PO文件失败", e);
        }
    }
}
