package com.main.builder;

import com.main.bean.Constants;
import com.main.bean.FieldInfo;
import com.main.bean.TableInfo;
import com.main.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * BuildService类用于生成Service接口文件
 * 该类负责创建Service接口文件，包含基本的CRUD操作以及根据主键生成的查询、更新和删除方法
 */
public class BuildService {
    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    /**
     * 执行生成Service接口文件的方法
     * @param tableInfo 包含表结构信息的对象，用于生成Service接口
     */
    public static void execute(TableInfo tableInfo) {
        // 创建Service文件所在目录
        File folder = new File(Constants.PATH_SERVICE);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 生成Service接口文件名
        String className = tableInfo.getBeanName() + Constants.SUFFIX_SERVICE;
        File file = new File(folder, className + ".java");
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            // 写入包名
            bw.write("package " + Constants.PACKAGE_SERVICE + ";");
            bw.newLine();
            bw.newLine();

            // 写入import语句
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();

            // 创建类注释
            BuildComment.createClassComment(bw, tableInfo.getComment() + Constants.SUFFIX_SERVICE);
            // 写入接口声明
            bw.write("public interface " + className + " {");
            bw.newLine();

            // 基本CRUD方法
            // 查询列表方法
            BuildComment.createFieldComment(bw, "根据条件查询列表");
            bw.write("\tList<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            // 查询总数方法
            BuildComment.createFieldComment(bw, "根据条件查询总数");
            bw.write("\tInteger findCountByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            // 分页查询方法
            BuildComment.createFieldComment(bw, "分页查询");
            bw.write("\tPaginationResultVO<" + tableInfo.getBeanName() + "> findPageByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            // 新增方法
            BuildComment.createFieldComment(bw, "新增");
            bw.write("\tInteger add(" + tableInfo.getBeanName() + " bean);");
            bw.newLine();
            bw.newLine();

            // 批量新增方法
            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\tInteger addBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            // 批量新增或修改方法
            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("\tInteger addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            // 多条件更新
            BuildComment.createFieldComment(bw, "多条件更新");
            bw.write("\tInteger updateByParam(" + tableInfo.getBeanName() + " bean, " + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            // 多条件删除
            BuildComment.createFieldComment(bw, "多条件更新");
            bw.write("\tInteger deleteByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            // 根据主键生成的方法
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for(Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                StringBuffer methodName = new StringBuffer();
                StringBuffer methodParam = new StringBuffer();

                // 构建方法名和方法参数
                for (FieldInfo fieldInfo : fieldInfoList){
                    methodName.append(StringUtils.uperCaseFirst(fieldInfo.getPropertyName())).append("And");
                    methodParam.append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName()).append(", ");
                }
                methodName.delete(methodName.length() - 3, methodName.length());
                methodParam.delete(methodParam.length() - 2, methodParam.length());

                // 根据主键查询方法
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\t" + tableInfo.getBeanName() + " get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();

                // 根据主键更新方法
                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger update" + tableInfo.getBeanName() + "By" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParam + ");");
                bw.newLine();
                bw.newLine();

                // 根据主键删除方法
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParam + ");");
                bw.newLine();
            }

            // 结束接口声明
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("生成Service文件失败", e);
        }
    }
}
