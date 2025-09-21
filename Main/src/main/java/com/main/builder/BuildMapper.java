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
 * BuildMapper类用于生成MyBatis Mapper接口文件。
 * 该类根据数据库表结构信息自动生成对应的Mapper接口，
 * 包含基于表主键或唯一索引的CRUD操作方法。
 * 生成的Mapper接口继承自BaseMapper，提供通用的数据库操作能力。
 */
public class BuildMapper {
    // 使用Logger记录日志，记录类名为BuildMapper
    private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);

    /**
     * 执行生成MyBatis Mapper接口文件的方法
     * 根据数据库表结构信息自动生成对应的Mapper接口文件
     * @param tableInfo 包含表结构信息的对象，包含表名、字段列表、主键信息、索引信息等
     */
    public static void execute(TableInfo tableInfo) {
        // 创建Mapper文件存放的文件夹路径
        File folder = new File(Constants.PATH_MAPPER);
        // 如果文件夹不存在，则创建文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        // 构建Mapper接口的类名
        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
        // 创建Mapper接口文件对象
        File file = new File(folder, className + ".java");
        // 使用try-with-resources语句自动管理资源，确保文件流正确关闭
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            
            // 写入包名声明
            bw.write("package " + Constants.PACKAGE_MAPPER + ";");
            bw.newLine();
            bw.newLine();

            // 导入MyBatis的@Param注解，用于方法参数命名
            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.newLine();

            // 创建类的注释，添加"mapper"字样以表明这是一个Mapper接口
            BuildComment.createClassComment(bw, tableInfo.getComment() + "mapper");

            // 写入接口声明，使用泛型<T, P>，T表示实体类型，P表示主键类型
            // 继承BaseMapper以获取基础CRUD操作方法
            bw.write("public interface " + className + "<T, P> extends BaseMapper {");
            bw.newLine();

            // 获取表的主键和唯一索引信息，用于生成对应的查询、更新和删除方法
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            // 遍历每个主键或唯一索引，为每个索引生成对应的CRUD方法
            for(Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                // 获取当前索引包含的字段列表
                List<FieldInfo> fieldInfoList = entry.getValue();
                // 用于构建方法名的StringBuffer，如"selectByNameAndId"
                StringBuffer methodName = new StringBuffer();
                // 用于构建方法参数的StringBuffer，如"@Param("name") String name, @Param("id") Long id"
                StringBuffer methodParam = new StringBuffer();

                // 遍历索引中的每个字段，构建方法名和参数
                // 方法名格式为：字段名首字母大写，并用"And"连接，如"NameAndId"
                for (FieldInfo fieldInfo : fieldInfoList){
                    methodName.append(StringUtils.uperCaseFirst(fieldInfo.getPropertyName())).append("And");
                    methodParam.append("@Param(\"").append(fieldInfo.getPropertyName()).append("\") ").append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName()).append(", ");
                }
                bw.newLine();
                // 移除方法名末尾多余的"And"
                methodName.delete(methodName.length() - 3, methodName.length());
                // 移除参数末尾多余的逗号和空格
                methodParam.delete(methodParam.length() - 2, methodParam.length());

                // 生成根据指定条件查询记录的方法
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\tT selectBy" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();

                // 生成根据指定条件更新记录的方法，接收实体对象和条件参数
                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger updateBy" + methodName + "(@Param(\"bean\") T t, " + methodParam + ");");
                bw.newLine();
                bw.newLine();

                // 生成根据指定条件删除记录的方法
                // 注意：这里有个拼写错误，应该是"deleteBy"而不是"deletetBy"
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger deleteBy" + methodName + "(" + methodParam + ");");
                bw.newLine();
            }

            // 写入类的结束大括号
            bw.write("}");
            // 确保所有内容写入文件
            bw.flush();
        } catch (Exception e) {
            // 记录生成Mapper接口文件失败的错误日志
            logger.error("生成mapper文件失败", e);
            // 可以根据需要在这里添加更多的错误处理逻辑，如重试机制或通知机制
        }
    }
}
