package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class SimpleConnectionPool {
    // 连接池配置参数
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/db03";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "lwnznxf5555";
    private static final int INITIAL_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;

    // 连接存储队列
    private final Queue<Connection> availableConnections = new LinkedList<>();
    // 使用信号量控制并发
    private final Semaphore semaphore;

    // 单例模式
    private static volatile SimpleConnectionPool instance;

    private SimpleConnectionPool() {
        try {
            // 1. 加载数据库驱动
            Class.forName(DRIVER_CLASS);

            // 2. 初始化连接池
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                availableConnections.add(createNewConnection());
            }

            // 3. 初始化信号量
            semaphore = new Semaphore(MAX_POOL_SIZE);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("初始化连接池失败", e);
        }
    }

    public static SimpleConnectionPool getInstance() {
        if (instance == null) {
            synchronized (SimpleConnectionPool.class) {
                if (instance == null) {
                    instance = new SimpleConnectionPool();
                }
            }
        }
        return instance;
    }
    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    public Connection getConnection() {
        try {
            semaphore.acquire(); // 获取许可

            synchronized (this) {
                if (!availableConnections.isEmpty()) {
                    return availableConnections.poll();
                }
                if (availableConnections.size() < MAX_POOL_SIZE) {
                    return createNewConnection();
                }
            }
            return getConnection();
        } catch (InterruptedException | SQLException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取连接失败", e);
        }
    }
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (this) {
                availableConnections.offer(connection);
            }
            semaphore.release(); // 释放许可
        }
    }
    public void shutdown() {
        synchronized (this) {
            for (Connection conn : availableConnections) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("关闭连接时出错: " + e.getMessage());
                }
            }
            availableConnections.clear();
        }
    }

    // 测试连接池
    public static void main(String[] args) {
        SimpleConnectionPool pool = SimpleConnectionPool.getInstance();
        Connection conn = null;

        try {
            conn = pool.getConnection();
            System.out.println("成功获取连接: " + conn);
            // 执行数据库操作...
        } finally {
            if (conn != null) {
                pool.releaseConnection(conn);
                System.out.println("连接已释放");
            }
        }

        pool.shutdown();
        System.out.println("连接池已关闭");
    }
}