package de.htwsaar.vs.msgr.client.helper;


import java.sql.*;

public class JavaDatabase {

    /** A new connection to JavaDB. */
    private Connection conn;

    /** Represents the currently used operating system. */
    private String os = System.getProperty("os.name").toLowerCase();

    /** Connects to database, if no database exists then create the database. */
    public Connection connectionToDerby() {
        try {
            if (os.contains("win")) {
                // Windows
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                //Path for Windows
                conn = DriverManager.getConnection("jdbc:derby:C:\\Users\\"
                        + System.getProperty("user.name")
                        + "\\db\\clientdb;create=true");
                return conn;
            } else if (os.contains("osx") || os.contains("nix") ||
                    os.contains("aix") || os.contains("nux")) {
                // Unix
                String userHome = System.getProperty("user.home");
                //Path for Unix
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                conn = DriverManager.getConnection("jdbc:derby:" + userHome
                        + "/.config/pigeon;create=true");
                return conn;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Connects to database, if a table does not exist then create the table.
     */
    public void createTable() throws SQLException {
        if (connectionToDerby() != null) {
            //if Users Table Exists then do nothing
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, "APP", "USERS", null);

            Statement stmt;
            if (!rs.next()) {
                stmt = conn.createStatement();
                //Create Table
                stmt.executeUpdate("Create table users (UID varchar(256) " +
                        "primary key not null, name varchar(256) not null)");
            }

            rs = dmd.getTables(null, "APP", "GROUPS", null);
            if (!rs.next()) {
                stmt = conn.createStatement();
                stmt.executeUpdate("Create table groups (GID varchar(256) " +
                        "primary key not null, timeCreat bigint not null, " +
                        "timeMod bigint not null, name varchar(256) not null," +
                        " " +
                        "adminUID varchar(256) not null)");
            }

            rs = dmd.getTables(null, "APP", "MESSAGES", null);
            if (!rs.next()) {
                stmt = conn.createStatement();
                //Create Table
                stmt.executeUpdate("Create table messages (MID varchar(256), " +
                        "timeCreat bigint not null, timeMod bigint not null, " +
                        "text varchar(2048) not null, IDsend varchar(256) not" +
                        " null, IDrecv varchar(256) not null)");
                stmt.close();
            }

            rs = dmd.getTables(null, "APP", "ISIN", null);
            if (!rs.next()) {
                stmt = conn.createStatement();
                //Create Table
                stmt.executeUpdate("Create table isIn (UID varchar(256) not " +
                        "null references users (UID),GID varchar(256) not " +
                        "null references groups (GID))");
                stmt.close();
            }

            rs.close();
            conn.close();
        }
    }
}

