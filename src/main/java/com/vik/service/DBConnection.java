package com.vik.service;

import com.vik.common.LoggerImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class represents a connection to DB,
 * use boolean initialized to check if DB have been initialized
 */
public class DBConnection implements AutoCloseable {

    private static Connection connection;
    public static boolean initialized = false;

    /**
     * For DB initialization use DBInitType.DEV or DBInitType.TEST according to needed scripts,
     * also set initialized arg to true
     *
     * @param initType initialization type for database dev or test
     */
    public static void init(InitType initType) {

        final String URL_INIT_DEV = "jdbc:h2:mem:default;INIT=runscript from " +
                "'classpath:create.sql'\\;" +
                "runscript from " +
                "'classpath:init.sql';DB_CLOSE_DELAY=1000";
        final String URL_INIT_TEST = "jdbc:h2:mem:default;INIT=runscript from " +
                "'classpath:createTest.sql'\\;" +
                "runscript from " +
                "'classpath:initTest.sql';DB_CLOSE_DELAY=1000";
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (initType.equals(InitType.DEV)) {
                DriverManager.getConnection(URL_INIT_DEV, "sa", "");
                initialized = true;
            } else if (initType.equals(InitType.TEST)) {
                DriverManager.getConnection(URL_INIT_TEST, "sa", "");
                initialized = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new LoggerImpl().write("DB init error");
        }
    }

    /**
     * Private method to start a connection
     *
     * @throws SQLException
     */
    private void startConnection() throws SQLException {

        final String URL = "jdbc:h2:mem:default";
        final String USERNAME = "sa";
        final String PASSWORD = "";

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Returns connection if there is no one yet
     * @return Connection
     * @throws SQLException should be checked on used method
     */
    public Connection getConnection() throws SQLException {
        startConnection();
        return connection;
    }

    /**
     * Auto close method l
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
