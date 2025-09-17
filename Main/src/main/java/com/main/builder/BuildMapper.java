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
import java.util.List;
import java.util.Map;

/**
 * BuildPo类用于生成PO(Persistent Object)文件，即Java实体类。
 * 该类根据数据库表结构信息自动生成对应的Java实体类文件。
 */
public class BuildMapper {
    // 使用Logger记录日志，记录类名为BuildPo
    private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);

    /**
     * 执行生成PO文件的方法
     * @param tableInfo 包含表结构信息的对象，如字段名、类型、注释等
     */
    public static void execute(TableInfo tableInfo) {
        // 创建PO文件存放的文件夹路径
        File folder = new File(Constants.PATH_MAPPER);
        // 如果文件夹不存在，则创建文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
        File file = new File(folder, className + ".java");
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            // 写入包名
            bw.write("package " + Constants.PACKAGE_MAPPER + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.newLine();

            // 创建类的注释
            BuildComment.createClassComment(bw, tableInfo.getComment() + "mapper");

            bw.write("public interface " + className + "<T, P> extends BaseMapper {");
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for(Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                StringBuffer methodName = new StringBuffer();
                StringBuffer methodParam = new StringBuffer();

                for (FieldInfo fieldInfo : fieldInfoList){
                    methodName.append(StringUtils.uperCaseFirst(fieldInfo.getPropertyName())).append("And");
                    methodParam.append("@Param(\"").append(fieldInfo.getPropertyName()).append("\") ").append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName()).append(", ");
                }
                bw.newLine();
                methodName.delete(methodName.length() - 3, methodName.length());
                methodParam.delete(methodParam.length() - 2, methodParam.length());

                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\tT selectBy" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger updateBy" + methodName + "(@Param(\"bean\") T t, " + methodParam + ");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger deletetBy" + methodName + "(" + methodParam + ");");
                bw.newLine();
            }

            // 类结束大括号
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            // 记录生成PO文件失败的错误日志
            logger.error("生成mapper文件失败", e);
        }
    }
}
