package com.codecool.marsexploration.mapexplorer.database;

import com.codecool.marsexploration.mapexplorer.logger.Logger;

import java.sql.*;

public class DatabaseManager {
    private final String dbFile;
    private final Logger logger;

    public DatabaseManager(String dbFile, Logger logger) {
        this.dbFile = dbFile;
        this.logger = logger;
        createTables();
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

    private void createTables() {
        String roversTableSQL = "CREATE TABLE IF NOT EXISTS Rovers (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "extractedResources INTEGER NOT NULL)";

        String commandCentersTableSQL = "CREATE TABLE IF NOT EXISTS CommandCenters (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "deliveredResources INTEGER NOT NULL," +
                "currentStock INTEGER NOT NULL)";

        String constructionsTableSQL = "CREATE TABLE IF NOT EXISTS Constructions (" +
                "name TEXT NOT NULL," +
                "progress TEXT NOT NULL," +
                "responsibleUnit TEXT NOT NULL," +
                "step INTEGER NOT NULL)";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(roversTableSQL);
            stmt.execute(commandCentersTableSQL);
            stmt.execute(constructionsTableSQL);
        } catch (SQLException e) {
            logger.logError("Error creating tables: " + e.getMessage());
        }
    }

    public void addRover(String name, int steps, String resources, String outcome) {
        String sql = "INSERT INTO Rovers(name, steps, resources, outcome) VALUES(?,?,?,?)";
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, steps);
                pstmt.setString(3, resources);
                pstmt.setString(4, outcome);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.logError(e.getMessage());
        }
    }
    public void addResourceExtraction(String roverId, int resource1Count, int resource2Count) {
        String sql = "UPDATE Rovers SET extracted_resource_1 = extracted_resource_1 + ?, extracted_resource_2 = extracted_resource_2 + ? WHERE id = ?";
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, resource1Count);
                pstmt.setInt(2, resource2Count);
                pstmt.setString(3, roverId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addResourceDelivery(Integer centerId, int resource1Count, int resource2Count) {
        String sql = "UPDATE CommandCenters SET delivered_resource_1 = delivered_resource_1 + ?, delivered_resource_2 = delivered_resource_2 + ? WHERE id = ?";
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, resource1Count);
                pstmt.setInt(2, resource2Count);
                pstmt.setInt(3, centerId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateCommandCenterStock(Integer centerId, int resource1Stock, int resource2Stock) {
        String sql = "UPDATE CommandCenters SET stock_resource_1 = ?, stock_resource_2 = ? WHERE id = ?";
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, resource1Stock);
                pstmt.setInt(2, resource2Stock);
                pstmt.setInt(3, centerId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addConstructionEvent( String name, String progress, String responsibleUnit, int step) {
        String sql = "INSERT INTO Constructions (name, progress, responsibleUnit, step) VALUES(?,?,?,?)";
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, progress);
                pstmt.setString(3, responsibleUnit); // Set responsibleUnit here
                pstmt.setInt(4, step);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void deleteAllRovers() {
        String sql = "DELETE FROM Rovers";
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteAllCommandCenters() {
        String sql = "DELETE FROM CommandCenters";
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteAllConstructions() {
        String sql = "DELETE FROM Constructions";
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
