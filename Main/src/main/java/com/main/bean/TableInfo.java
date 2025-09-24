package com.main.bean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 表信息类，用于存储数据库表的完整信息
 * 包括表名、实体类名称、字段列表、索引信息等
 */
public class TableInfo {
    /** 数据库表名 */
    private String tableName;
    
    /** 对应的实体类名称（如User、Order等） */
    private String beanName;
    
    /** 实体类参数名称（通常用于方法参数，如user、order等） */
    private String beanParamName;
    
    /** 表的注释说明 */
    private String comment;
    
    /** 表的字段信息列表 */
    private List<FieldInfo> fieldList;
    
    /** 扩展字段信息列表（通常用于查询条件等） */
    private List<FieldInfo> fieldExtendList;
    
    /** 索引信息映射，使用LinkedHashMap保持插入顺序 */
    private Map<String, List<FieldInfo>> keyIndexMap = new LinkedHashMap<>();
    
    /** 表中是否包含日期类型字段 */
    private Boolean haveDate;
    
    /** 表中是否包含日期时间类型字段 */
    private Boolean haveDateTime;
    
    /** 表中是否包含BigDecimal类型字段 */
    private Boolean haveBigDecimal;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanParamName() {
        return beanParamName;
    }

    public void setBeanParamName(String beanParamName) {
        this.beanParamName = beanParamName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public List<FieldInfo> getFieldExtendList() {
        return fieldExtendList;
    }

    public void setFieldExtendList(List<FieldInfo> fieldExtendList) {
        this.fieldExtendList = fieldExtendList;
    }

    public Map<String, List<FieldInfo>> getKeyIndexMap() {
        return keyIndexMap;
    }

    public void setKeyIndexMap(Map<String, List<FieldInfo>> keyIndexMap) {
        this.keyIndexMap = keyIndexMap;
    }

    public Boolean getHaveDate() {
        return haveDate;
    }
    
    public void setHaveDate(Boolean haveDate) {
        this.haveDate = haveDate;
    }
    
    public Boolean getHaveDateTime() {
        return haveDateTime;
    }
    
    public void setHaveDateTime(Boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }
    
    public Boolean getHaveBigDecimal() {
        return haveBigDecimal;
    }
    
    public void setHaveBigDecimal(Boolean haveBigDecimal) {
        this.haveBigDecimal = haveBigDecimal;
    }
}
