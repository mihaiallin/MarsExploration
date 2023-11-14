package com.codecool.marsexploration.mapexplorer.database;

import com.codecool.marsexploration.mapexplorer.logger.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResourcesImpl implements Resources {
    private final String dbFile;
    private final Logger logger;

    public ResourcesImpl(String dbFile, Logger logger) {
        this.dbFile = dbFile;
        this.logger = logger;
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + dbFile;
            conn = DriverManager.getConnection(url);
            return conn;

        } catch (SQLException e) {
            logger.logError(e.getMessage());
        }
        return null;
    }

    @Override
    public void add(String name, int steps, int amountOfResources, String outcome) {
        String sql = "INSERT INTO ResourcesMars(id, steps, amountOfResources, outcome) VALUES(?,?,?,?)";
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, steps);
                pstmt.setInt(3, amountOfResources);
                pstmt.setString(4, outcome);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM ResourcesMars";
        try (Connection conn = this.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
