package stepdefinitions.support;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabricksConnectionManager {
    private Connection connection;
    private Properties properties;
    private String databricksHost;
    private String databricksHttpPath;
    private String databricksToken;
    private String port;
    private String database;

    public DatabricksConnectionManager(String propertiesFilePath) {
        loadConfiguration(propertiesFilePath);
    }

    public DatabricksConnectionManager() {
        this("src/test/resources/databricks.properties");
    }

    private void loadConfiguration(String filePath) {
        properties = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
            databricksHost = properties.getProperty("databricks.host");
            databricksHttpPath = properties.getProperty("databricks.httpPath");
            databricksToken = properties.getProperty("databricks.token");
            port = properties.getProperty("databricks.port", "443");
            database = properties.getProperty("databricks.database", "default");
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Connection connect() {
        try {
            if (databricksHost == null || databricksHttpPath == null || databricksToken == null) {
                throw new IllegalStateException("Databricks configuration is incomplete. Please check databricks.properties file.");
            }
            Class.forName("com.databricks.client.jdbc.Driver");
            String jdbcUrl = String.format(
                "jdbc:databricks://%s:%s/%s;transportMode=http;ssl=1;httpPath=%s;AuthMech=3;UID=token;PWD=%s",
                databricksHost,
                port,
                database,
                databricksHttpPath,
                databricksToken
            );
            connection = DriverManager.getConnection(jdbcUrl);
            return connection;
        } catch (ClassNotFoundException | SQLException | IllegalStateException e) {
            System.err.println("Failed to connect to Databricks: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                return connect();
            }
            return connection;
        } catch (SQLException e) {
            return connect();
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                Statement statement = conn.createStatement();
                return statement.executeQuery(query);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int executeUpdate(String query) {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                Statement statement = conn.createStatement();
                int rowsAffected = statement.executeUpdate(query);
                return rowsAffected;
            }
        } catch (SQLException e) {
            System.err.println("Error executing update: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
        }
        return false;
    }
}
