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
 * 构建Service实现类
 * 该类负责根据表信息生成Service实现类的Java文件
 */
public class BuildServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);

    /**
     * 执行生成Service实现类的方法
     * @param tableInfo 表信息对象，包含表名、字段信息等
     */
    public static void execute(TableInfo tableInfo) {
        // 创建Service实现类的目录
        File folder = new File(Constants.PATH_SERVICE_IMPL);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 生成类名和接口名
        String interfaceName = tableInfo.getBeanName() + Constants.SUFFIX_SERVICE;
        String className = tableInfo.getBeanName() + Constants.SUFFIX_SERVICE_IMPL;
        File file = new File(folder, className + ".java");
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            // 写入包名
            bw.write("package " + Constants.PACKAGE_SERVICE_IMPL + ";");
            bw.newLine();
            bw.newLine();

            // 生成Mapper相关名称
            String mapperName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
            String mapperBeanName = StringUtils.lowerCaseFirst(mapperName);
            // 写入导入的类
            bw.write("import " + Constants.PACKAGE_ENUM + ".PageSize;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + ".SimplePage;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_MAPPER + "." + mapperName + ";");
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + interfaceName + ";");
            bw.newLine();
            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            bw.write("import jakarta.annotation.Resource;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();

            // 创建类注释
            BuildComment.createClassComment(bw, tableInfo.getComment() + Constants.SUFFIX_SERVICE_IMPL);
            // 写入@Service注解
            bw.write("@Service(\"" + StringUtils.lowerCaseFirst(interfaceName) + "\")");
            bw.newLine();
            // 写入类声明
            bw.write("public class " + className + " implements " + interfaceName + " {");
            bw.newLine();

            // 注入Mapper
            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate " + mapperName + "<" + tableInfo.getBeanName() + ", " + tableInfo.getBeanParamName() + "> " + mapperBeanName + ";");
            bw.newLine();
            bw.newLine();

            // 查询相关方法
            BuildComment.createFieldComment(bw, "根据条件查询列表");
            bw.write("\tpublic List<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".selectList(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "根据条件查询总数");
            bw.write("\tpublic Integer findCountByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".selectCount(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "分页查询");
            bw.write("\tpublic PaginationResultVO<" + tableInfo.getBeanName() + "> findPageByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\tInteger count = this.findCountByParam(query);");
            bw.newLine();
            bw.write("\t\tInteger pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();");
            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(), pageSize, count);");
            bw.newLine();
            bw.write("\t\tquery.setSimplePage(page);");
            bw.newLine();
            bw.write("\t\tList<" + tableInfo.getBeanName() + "> list = this.findListByParam(query);");
            bw.newLine();
            bw.write("\t\treturn new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            // 新增相关方法
            BuildComment.createFieldComment(bw, "新增");
            bw.write("\tpublic Integer add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insert(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\tpublic Integer addBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tif (listBean == null || listBean.size() == 0) {");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insertBatch(listBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("\tpublic Integer addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tif (listBean == null || listBean.size() == 0) {");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insertOrUpdateBatch(listBean);");
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
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\tpublic " + tableInfo.getBeanName() + " get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".selectBy" + methodName + "(" + params + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                // 根据主键更新
                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tpublic Integer update" + tableInfo.getBeanName() + "By" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParam + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".updateBy" + methodName + "(bean, " + params + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                // 根据主键删除
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tpublic Integer delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".deleteBy" + methodName + "(" + params + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
            }

            // 结束类定义
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("生成ServiceImpl文件失败", e);
        }
    }
}
