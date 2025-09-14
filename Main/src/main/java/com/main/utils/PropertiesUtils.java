package com.main.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PropertiesUtils类
 * 用于读取并管理application.properties配置文件的工具类
 * 提供了获取配置项的方法，使用ConcurrentHashMap保证线程安全
 */
public class PropertiesUtils {
    // 使用Properties类加载配置文件
    private static Properties props = new Properties();
    // 使用ConcurrentHashMap存储配置项，保证线程安全
    private static Map<String, String> propMap = new ConcurrentHashMap<>();

    // 静态代码块，在类加载时执行，用于初始化配置
    static {
        // 定义输入流变量
        //InputStream is = null;
        try (InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
             InputStreamReader isr = new InputStreamReader(is, "gbk")
        ) {
            // 通过类加载器获取配置文件输入流
           // is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            // 加载配置文件
            props.load(isr);

            // 遍历Properties中的所有键
            Iterator<Object> iterator = props.keySet().iterator();
            while (iterator.hasNext()) {
                // 将键转换为String类型
                String key = (String) iterator.next();
                // 将配置项存入Map中
                propMap.put(key, props.getProperty(key));
            }
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
//        } finally {
//            // 确保输入流被关闭
//            if (is != null) {
//                try {
//                    // 关闭输入流
//                    is.close();
//                } catch (Exception e) {
//                    // 打印异常信息
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    /**
     * 根据键获取配置值的静态方法
     * @param key 配置项的键
     * @return 配置项的值，如果不存在则返回null
     */
    public static String getString(String key){
        return propMap.get(key);
    }

    /**
     * 主方法，用于测试
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 打印指定键的配置值
        System.out.println(getString("db.driver.name"));
    }

}
