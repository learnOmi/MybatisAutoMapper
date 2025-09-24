package com.main.builder;

import com.main.bean.Constants;
import com.main.utils.DateUtils;
import com.main.utils.PropertiesUtils;

import java.io.BufferedWriter;
import java.util.Date;

/**
 * BuildComment类，用于构建Java代码中的注释
 * 提供了创建类注释、方法注释和字段注释的静态方法
 */
public class BuildComment {
    /**
     * 创建类注释
     * @param bw BufferedWriter对象，用于写入生成的注释
     * @param classComment 类的注释内容
     * @throws Exception 可能抛出的IO异常
     */
    public static void createClassComment (BufferedWriter bw, String classComment) throws Exception {
        bw.write("/**\n");
        bw.write(" * " + classComment + "\n");
        bw.write(" * @author " + Constants.AUTHOR_COMMENT + "\n");
        bw.write(" * @since " + DateUtils.formatDate(new Date(), DateUtils.DATE_FORMAT_SLASH) + "\n");
        bw.write(" */\n");
    }

    /**
     * 创建方法注释
     * @param bw BufferedWriter对象，用于写入生成的注释
     * @param methodComment 方法的注释内容
     * @throws Exception 可能抛出的IO异常
     */
    public static void createMethodComment (BufferedWriter bw, String methodComment) throws Exception {
    }

    /**
     * 创建字段注释
     * @param bw BufferedWriter对象，用于写入生成的注释
     * @param fieldComment 字段的注释内容
     * @throws Exception 可能抛出的IO异常
     */
    public static void createFieldComment (BufferedWriter bw, String fieldComment) throws Exception {
        //bw.write("\t/**\n");        // 被注释的多行注释开始标记
        //bw.write("\t * " + (fieldComment == null ? "" : fieldComment) + "\n");  // 被注释的多行注释内容
        bw.write("\t// " + (fieldComment == null ? "" : fieldComment) + "\n");    // 单行注释，使用//而非/** */
        //bw.write("\t */\n");        // 被注释的多行注释结束标记
    }
}
