package com.dzero.mysqljdbc;

import java.sql.*;

/**
 * MysqlDemo
 *
 * @author DZero
 * @date 2020/3/20 20:57
 */
public class MysqlDemo {
//    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//    static final String DB_URL = "jdbc:mysql://localhost:3306/RUNOOB";

    /**
     * MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
     */
    static final String JDBC_DRIVER = "com.mysql.cj2.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://192.168.13.108:3306/test?useSSL=false&serverTimezone=UTC";


    static final String USER = "root";
    static final String PASS = "123456";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try{
            System.out.println("加载驱动程序...");
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("1.getConnection()方法，连接MySQL数据库！！");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println("2.创建statement类对象，用来执行SQL语句！！");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, name, url FROM user";
            //3.ResultSet类，用来存放获取的结果集！！
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                int id  = rs.getInt("id");
                String name = rs.getString("name");
                String url = rs.getString("url");

                // 输出数据
                System.out.print("ID: " + id);
                System.out.print(", 站点名称: " + name);
                System.out.print(", 站点 URL: " + url);
                System.out.print("\n");
            }
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        }catch(ClassNotFoundException e) {
            //数据库驱动类异常处理
            e.printStackTrace();
        }catch(SQLException se){
            //数据库连接失败异常处理
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) {
                    stmt.close();
                }
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) {
                    conn.close();
                }
            }catch(SQLException se){
                se.printStackTrace();
            }
            System.out.println("finally!");
        }
        System.out.println("Goodbye!");
    }
}
