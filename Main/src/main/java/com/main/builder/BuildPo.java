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

public class BuildPo {
    private static final Logger logger = LoggerFactory.getLogger(BuildPo.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_PO);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // File 对象本身不包含文件内容，它只是文件的路径表示。要实际读写文件内容，需要将 File 对象与 IO 流结合使用
        File file = new File(folder, tableInfo.getBeanName() + ".java");
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write("package " + Constants.PACKAGE_PO + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import java.io.Serializable;");
            bw.newLine();

            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write(Constants.BEAN_DATE_PARSE_CLASS);
                bw.newLine();
            }

            Boolean haveIgnoreBean = false;
            for (FieldInfo field : tableInfo.getFieldList()) {
                if (ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FILED.split(","), field.getPropertyName())) {
                    haveIgnoreBean = true;
                    break;
                }
            }
            if (haveIgnoreBean) {
                bw.write(Constants.IGNORE_BEAN_TOJSON_CLASS);
                bw.newLine();
            }

            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }

            BuildComment.createClassComment(bw, tableInfo.getComment());

            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();

            for(FieldInfo fieldInfo : tableInfo.getFieldList()){
                BuildComment.createFieldComment(bw, fieldInfo.getComment());

                if (ArrayUtils.contains(Constants.DATE_TIME_TYPES, fieldInfo.getSqlType())){
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.DATE_TIME_FORMAT));
                    bw.newLine();

                    bw.write("\t" + String.format(Constants.BEAN_DATE_PARSE_EXPRESSION, DateUtils.DATE_TIME_FORMAT));
                    bw.newLine();
                }

                if (ArrayUtils.contains(Constants.DATE_TYPES, fieldInfo.getSqlType())){
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.DATE_FORMAT));
                    bw.newLine();

                    bw.write("\t" + String.format(Constants.BEAN_DATE_PARSE_EXPRESSION, DateUtils.DATE_FORMAT));
                    bw.newLine();
                }

                if (ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FILED.split(","), fieldInfo.getPropertyName())){
                    bw.write("\t" + String.format(Constants.IGNORE_BEAN_TOJSON_EXPRESSION, fieldInfo.getPropertyName()));
                    bw.newLine();
                }

                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
            }

            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String methodName = StringUtils.uperCaseFirst(fieldInfo.getPropertyName());
                bw.write("\tpublic void set" + methodName + "(" + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ") {");
                bw.newLine();
                bw.write("\t\tthis." + fieldInfo.getPropertyName() + " = " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + methodName + "() {");
                bw.newLine();
                bw.write("\t\treturn " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
            }

            // 重写toString
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn \"" + tableInfo.getBeanName() + " [\" +");
            StringBuffer str = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                str.append("\n\t\t\t\"").append(fieldInfo.getPropertyName()).append("=\" + ").append("(").append(fieldInfo.getPropertyName()).append(" == null ? \"空\" : ").append(fieldInfo.getPropertyName()).append(")").append(" + \", \" +");
            }
            str.replace(str.lastIndexOf("\", \" +"), str.length(), "");
            str.append("\n\t\t\t\"]\"");
            str.append(";\n");
            bw.write(str.toString());
            bw.write("\t}");
            bw.newLine();

            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("生成PO文件失败", e);
        }
    }
}
