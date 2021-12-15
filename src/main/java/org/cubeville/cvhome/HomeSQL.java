package org.cubeville.cvhome;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class HomeSQL {

    private final CVHome cvHome;
    private Connection connection;
    private Statement statement;

    public HomeSQL(CVHome cvHome) {
        this.cvHome = cvHome;
    }

    public void connect() {
        connection = null;
        try {
            File dbFile = new File(cvHome.getDataFolder(), "homes.db");
            if(!dbFile.exists()) {
                dbFile.createNewFile();
            }
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getResult(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
