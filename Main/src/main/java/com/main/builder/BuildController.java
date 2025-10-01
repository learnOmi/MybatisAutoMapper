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
 * BuildController类，用于生成Controller层的代码
 * 该类负责创建Controller文件，包括基本的CRUD操作方法
 */
public class BuildController {
    // 使用SLF4J的Logger记录日志
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    /**
     * 执行生成Controller代码的方法
     * @param tableInfo 表信息对象，包含生成Controller所需的各种信息
     */
    public static void execute(TableInfo tableInfo) {
        // 创建Service实现类的目录
        File folder = new File(Constants.PATH_CONTROLLER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 生成类名，添加Controller后缀
        String className = tableInfo.getBeanName() + Constants.SUFFIX_CONTROLLER;
        File file = new File(folder, className + ".java");
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            // 写入包名
            bw.write("package " + Constants.PACKAGE_CONTROLLER + ";");
            bw.newLine();
            bw.newLine();

            // 生成Service相关的名称
            String serviceName = tableInfo.getBeanName() + Constants.SUFFIX_SERVICE;
            String serviceBeanName = StringUtils.lowerCaseFirst(serviceName);
            // 写入导入的类
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".ResponseVO;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName + ";");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RestController;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestBody;");
            bw.newLine();
            bw.write("import jakarta.annotation.Resource;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();

            // 创建类注释
            BuildComment.createClassComment(bw, tableInfo.getComment() + Constants.SUFFIX_CONTROLLER);
            // 写入@RestController注解
            bw.write("@RestController");
            bw.newLine();
            // 写入@RequestMapping注解
            bw.write("@RequestMapping(\"/" + StringUtils.lowerCaseFirst(tableInfo.getBeanName()) + "\")");
            bw.newLine();
            // 写入类声明
            bw.write("public class " + className + " extends ABaseController" + " {");
            bw.newLine();

            // 注入Service
            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate " + serviceName + " " + serviceBeanName + ";");
            bw.newLine();
            bw.newLine();

            // 加载数据列表方法
            BuildComment.createFieldComment(bw, "加载数据列表");
            bw.write("\t@RequestMapping(\"loadDataList\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO loadDataList(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponse(" + serviceBeanName + ".findPageByParam(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            // 新增相关方法
            BuildComment.createFieldComment(bw, "新增");
            bw.write("\t@RequestMapping(\"add\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceBeanName + ".add(bean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponse(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\t@RequestMapping(\"addBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceBeanName + ".addBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponse(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("\t@RequestMapping(\"addOrUpdateBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addOrUpdateBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceBeanName + ".addOrUpdateBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponse(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            // 根据主键生成查询、更新、删除方法
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for(Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                StringBuffer methodName = new StringBuffer();
                StringBuffer methodParam = new StringBuffer();
                StringBuffer params = new StringBuffer();

                // 构建方法名和参数
                for (FieldInfo fieldInfo : fieldInfoList){
                    methodName.append(StringUtils.uperCaseFirst(fieldInfo.getPropertyName())).append("And");
                    methodParam.append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName()).append(", ");
                    params.append(fieldInfo.getPropertyName()).append(", ");
                }
                methodName.delete(methodName.length() - 3, methodName.length());
                methodParam.delete(methodParam.length() - 2, methodParam.length());
                params.delete(params.length() - 2, params.length());

                // 根据主键查询
                String fullMethodName = tableInfo.getBeanName() + "By" + methodName;
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\t@RequestMapping(\"get" + fullMethodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO get" + fullMethodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponse(" + "this." + serviceBeanName + ".get" + fullMethodName + "(" + params + "));");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                // 根据主键更新
                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\t@RequestMapping(\"update" + fullMethodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO update" + fullMethodName + "(" + tableInfo.getBeanName() + " bean, " + methodParam + ") {");
                bw.newLine();
                bw.write("\t\tthis." + serviceBeanName + ".update" + fullMethodName + "(bean, " + params + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponse(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                // 根据主键删除
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\t@RequestMapping(\"delete" + fullMethodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO delete" + fullMethodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\tthis." + serviceBeanName + ".delete" + fullMethodName + "(" + params + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponse(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
            }

            // 结束类定义
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            // 记录生成Controller文件失败的错误日志
            logger.error("生成ServiceImpl文件失败", e);
        }
    }
}
