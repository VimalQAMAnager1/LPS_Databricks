package stepdefinitions.support;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.sql.ResultSet;
import java.sql.SQLException;
import stepdefinitions.support.DatabricksConnectionManager;

public class DatabricksTestContext {
    private DatabricksConnectionManager dbManager;
    private boolean connected = false;
    private ResultSet userData;
    private boolean insertSuccess = false;

    public void connect() {
        dbManager = new DatabricksConnectionManager();
        connected = dbManager.testConnection();
    }

    public boolean isConnected() {
        return connected;
    }

    public void fetchUserData() {
        String query = "SELECT user_id, username, email FROM users LIMIT 1";
        userData = dbManager.executeQuery(query);
    }

    public void performLogin(WebDriver driver) {
        try {
            if (userData != null && userData.next()) {
                String username = userData.getString("username");
                driver.get("https://your-application-url.com/login");
                driver.findElement(By.id("username")).sendKeys(username);
                // Add password and login logic as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validateUserInfo(WebDriver driver) {
        // Implement UI validation logic here
        // Example: return driver.findElement(By.id("user-info")).getText().contains("expected value");
        return true;
    }

    public void prepareTestResult() {
        // Prepare test result data
    }

    public void insertTestResult() {
        String testName = "Login_Test";
        String status = "PASSED";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String insertQuery = String.format(
            "INSERT INTO test_results (test_name, status, timestamp) VALUES ('%s', '%s', '%s')",
            testName, status, timestamp
        );
        int rowsInserted = dbManager.executeUpdate(insertQuery);
        insertSuccess = rowsInserted > 0;
    }

    public boolean isInsertSuccessful() {
        return insertSuccess;
    }
}
