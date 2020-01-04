package com.revolut.moneytransfer.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class H2DataServer {


    static final String DB_URL = "jdbc:h2:mem:revolut-test";

    //Credentials
    static final String USERNAME = "revolut";
    static final String PASSWORD = "revolut";

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl(DB_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10);

        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    private H2DataServer() {
    }

}
