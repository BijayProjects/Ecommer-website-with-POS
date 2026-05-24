package com.curator.pos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbPath = findDatabase();
            System.out.println("[DatabaseManager] Connecting to: " + dbPath);
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found: " + e.getMessage());
        } catch (IOException e) {
            throw new SQLException("Failed to resolve database path: " + e.getMessage());
        }
    }

    public static void initDatabase() {
        String createLogTable = "CREATE TABLE IF NOT EXISTS pos_user_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "action TEXT NOT NULL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String createTimesheetTable = "CREATE TABLE IF NOT EXISTS pos_staff_timesheet (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "date DATE DEFAULT CURRENT_DATE, " +
                "punch_in_time DATETIME, " +
                "punch_out_time DATETIME)";
        try (Connection conn = getConnection();
                java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(createLogTable);
            stmt.execute(createTimesheetTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logUserAction(String username, String action) {
        String insertLog = "INSERT INTO pos_user_log (username, action) VALUES (?, ?)";
        try (Connection conn = getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(insertLog)) {
            pstmt.setString(1, username);
            pstmt.setString(2, action);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String findDatabase() throws IOException, SQLException {
        // 1. Check config.properties next to the JAR/EXE for a user-defined path
        String jarDir = getJarDirectory();
        File configFile = new File(jarDir, "config.properties");
        if (configFile.exists()) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            }
            String customPath = props.getProperty("db.path");
            if (customPath != null && !customPath.isEmpty()) {
                File customDb = new File(customPath.trim());
                if (customDb.exists())
                    return customDb.getAbsolutePath();
            }
        }

        // 2. Try classic relative path from working dir (mvn javafx:run scenario)
        File candidate = new File(System.getProperty("user.dir"), "../db.sqlite3");
        if (candidate.exists())
            return candidate.getCanonicalPath();

        // 3. Try relative to the JAR directory (EXE launched from CuratorPOS\)
        candidate = new File(jarDir, "../Electronic-Store-Ecommerce/db.sqlite3");
        if (candidate.exists())
            return candidate.getCanonicalPath();

        // 4. Search Desktop for the project folder
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        File desktop = new File(desktopPath);
        if (desktop.exists()) {
            for (File dir : desktop.listFiles(File::isDirectory)) {
                File dbFile = new File(dir, "db.sqlite3");
                if (dbFile.exists())
                    return dbFile.getAbsolutePath();
            }
        }

        throw new SQLException(
                "Could not find db.sqlite3!\n" +
                        "Create a 'config.properties' file next to CuratorPOS.exe with:\n" +
                        "  db.path=C:\\path\\to\\Electronic-Store-Ecommerce\\db.sqlite3");
    }

    /** Returns the directory containing the running JAR or class root */
    private static String getJarDirectory() {
        try {
            File jarFile = new File(
                    DatabaseManager.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI());
            // If it's the JAR itself, return its parent
            if (jarFile.isFile())
                return jarFile.getParent();
            // In dev mode (class files) go up to project root
            return jarFile.getAbsolutePath();
        } catch (Exception e) {
            return System.getProperty("user.dir");
        }
    }
}
