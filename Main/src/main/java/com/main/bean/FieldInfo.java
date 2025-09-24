package com.main.bean;

/**
 * FieldInfo类用于表示数据库表中的一个字段信息
 * 包含字段名、属性名、SQL类型、Java类型、注释和是否自增等信息
 */
public class FieldInfo {
    /**
     * 数据库中的字段名
     */
    private String fieldName;
    /**
     * 对应的Java属性名（通常是将字段名转换为驼峰命名）
     */
    private String propertyName;
    /**
     * 数据库中的字段类型（如VARCHAR, INT, DATETIME等）
     */
    private String sqlType;
    /**
     * 对应的Java类型（如String, Integer, Date等）
     */
    private String javaType;
    /**
     * 字段的注释说明
     */
    private String comment;
    /**
     * 是否为自增字段
     */
    private Boolean isAutoIncrement;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getIsAutoIncrement() {
        return isAutoIncrement;
    }

    public void setIsAutoIncrement(Boolean isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }
}
