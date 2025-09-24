package com.main.builder;

import com.main.bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * BuildBase类是一个用于构建基础代码文件的工具类
 * 它通过读取模板文件并添加相应的包声明，生成Java源代码文件
 */
public class BuildBase {
    // 创建一个日志记录器，用于记录该类的日志信息
    private static final Logger logger = LoggerFactory.getLogger(BuildBase.class);

    /**
     * execute方法用于执行代码构建过程
     * 它会为多个基础类（如枚举、工具类、映射器等）生成对应的Java文件
     */
    public static void execute () {
        // 用于存储文件头信息的列表，主要是包声明
        List<String> headerInfoList = new ArrayList<>();

        // 生成DateTimePatternEnum枚举类
        headerInfoList.add("package " + Constants.PACKAGE_ENUM + ";");
        build(headerInfoList, "DateTimePatternEnum", Constants.PATH_ENUM);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成DateUtils工具类
        headerInfoList.add("package " + Constants.PACKAGE_UTILS + ";");
        build(headerInfoList, "DateUtils", Constants.PATH_UTILS);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成BaseMapper接口
        headerInfoList.add("package " + Constants.PACKAGE_MAPPER + ";");
        build(headerInfoList, "BaseMapper", Constants.PATH_MAPPER);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成PageSize枚举类
        headerInfoList.add("package " + Constants.PACKAGE_ENUM + ";");
        build(headerInfoList, "PageSize", Constants.PATH_ENUM);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成SimplePage查询类，需要导入PageSize枚举
        headerInfoList.add("package " + Constants.PACKAGE_QUERY + ";");
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".PageSize;");
        build(headerInfoList, "SimplePage", Constants.PATH_QUERY);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成BaseQuery查询类
        headerInfoList.add("package " + Constants.PACKAGE_QUERY + ";");
        build(headerInfoList, "BaseQuery", Constants.PATH_QUERY);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成PaginationResultVO分页结果类，需要导入List和ArrayList
        headerInfoList.add("package " + Constants.PACKAGE_VO + ";");
        headerInfoList.add("import java.util.List;");
        headerInfoList.add("import java.util.ArrayList;");
        build(headerInfoList, "PaginationResultVO", Constants.PATH_VO);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成BaseException异常类
        headerInfoList.add("package " + Constants.PACKAGE_EXCEPTION + ";");
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".ResponseCodeEnum;");
        build(headerInfoList, "BusinessException", Constants.PATH_EXCEPTION);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成异常CODE枚举类
        headerInfoList.add("package " + Constants.PACKAGE_ENUM + ";");
        build(headerInfoList, "ResponseCodeEnum", Constants.PATH_ENUM);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成BaseController控制器类
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER + ";");
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".ResponseCodeEnum;");
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO;");
        build(headerInfoList, "ABaseController", Constants.PATH_CONTROLLER);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成ResponseVO响应类
        headerInfoList.add("package " + Constants.PACKAGE_VO + ";");
        build(headerInfoList, "ResponseVO", Constants.PATH_VO);

        // 清空列表，为下一个文件做准备
        headerInfoList.clear();
        // 生成AGlobalExceptionHandlerController全局异常处理器类
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER + ";");
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".ResponseCodeEnum;");
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO;");
        headerInfoList.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException;");
        build(headerInfoList, "AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER);
    }

    /**
     * build方法用于根据模板文件生成Java源代码文件
     * @param headerInfoList 包含文件头信息的列表（主要是包声明）
     * @param fileName 要生成的文件名
     * @param outPath 输出文件的路径
     */
    private static void build (List<String> headerInfoList, String fileName, String outPath){
        // 创建输出目录文件夹
        File folder = new File(outPath);
        if (!folder.exists()){
            // 如果目录不存在，则创建所有必要的父目录
            folder.mkdirs();
        }

        // 创建输出文件的完整路径
        File javaFile = new File(outPath, fileName + ".java");

        // 使用try-with-resources语句自动管理资源
        try (OutputStream out = new FileOutputStream(javaFile);
             OutputStreamWriter outw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(outw);
             // 从类路径中读取模板文件
             InputStream in = new FileInputStream(Objects.requireNonNull(BuildBase.class.getClassLoader().getResource("template/" + fileName + ".txt")).getPath());
             InputStreamReader inr = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(inr);
             ) {

            // 写入文件头信息（包声明等）
            for (String headerInfo : headerInfoList) {
                bw.write(headerInfo);
                bw.newLine();
                bw.newLine();
            }

            // 读取模板文件内容并写入目标文件
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            // 确保所有内容都被写入
            bw.flush();
        } catch (Exception e) {
            // 如果发生异常，抛出运行时异常
            throw new RuntimeException(e);
        }


    }
}
