package com.main.builder;

import com.main.utils.PropertiesUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * BuildTable类用于管理数据库连接和获取表信息
 * 提供了数据库连接的创建、关闭以及获取表信息的功能
 */
public class BuildTable {
    // 使用Logger记录日志信息
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    // 数据库连接对象
    private static Connection conn = null;

    // SQL查询语句，用于获取表状态信息
    private static final String SQL_SHOW_TABLE_STATUS = "show table status";

    // 静态代码块，用于初始化数据库连接
    static {
        // 从配置文件中获取数据库连接信息
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String username = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            // 加载数据库驱动
            Class.forName(driverName);
            // 建立数据库连接
            conn = DriverManager.getConnection(url, username, password);
            
            // 添加关闭钩子，确保程序退出时关闭连接
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                closeConnection();
            }));
            
            // 记录连接成功的日志
            logger.info("数据库连接成功");
        } catch (Exception e) {
            // 记录连接失败的日志
            logger.error("数据库连接失败", e);
        }
    }

    /**
     * 关闭数据库连接的方法
     * 确保连接被正确关闭，并记录相应的日志信息
     */
    public static void closeConnection() {
        if (conn != null) {
            try {
                // 关闭数据库连接
                conn.close();
                // 记录连接已关闭的日志
                logger.info("数据库连接已关闭");
            } catch (SQLException e) {
                // 记录关闭连接失败的日志
                logger.error("关闭数据库连接失败", e);
            } finally {
                // 将连接对象置为null
                conn = null;
            }
        }
    }

    /**
     * 获取数据库中所有表的信息
     * 包括表名和表的注释
     */
    public static void getTables() {
        // 检查数据库连接是否存在
        if (conn == null) {
            logger.warn("数据库连接未建立，无法获取表信息");
            return;
        }
        
        // 使用try-with-resources确保资源被正确关闭
        try (PreparedStatement ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
             ResultSet tableResult = ps.executeQuery()) {
            
            // 遍历结果集，输出表名和注释
            while (tableResult.next()) {
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");
                logger.info("表名: {}, 注释: {}", tableName, comment);
            }
        } catch (SQLException e) {
            // 记录查询失败的日志
            logger.error("查询表信息失败", e);
        }
    }
}
