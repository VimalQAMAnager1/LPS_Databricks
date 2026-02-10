package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import stepdefinitions.support.DatabricksTestContext;

import static org.junit.Assert.assertTrue;

public class DatabricksSeleniumSteps {
    private WebDriver driver;
    private DatabricksTestContext context = new DatabricksTestContext();

    @Given("I connect to Databricks")
    public void i_connect_to_databricks() {
        context.connect();
    }

    @Then("the connection should be successful")
    public void the_connection_should_be_successful() {
        Assert.assertTrue("Failed to connect to Databricks", context.isConnected());

    }

    @Given("I fetch user data from Databricks")
    public void i_fetch_user_data_from_databricks() {
        context.fetchUserData();
    }

    @When("I login with username from Databricks")
    public void i_login_with_username_from_databricks() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        context.performLogin(driver);
    }

    @Then("I should see the correct user information on the UI")
    public void i_should_see_the_correct_user_information_on_the_ui() {
        assertTrue(context.validateUserInfo(driver));
        driver.quit();
    }

    @Given("I have a test result")
    public void i_have_a_test_result() {
        context.prepareTestResult();
    }

    @When("I insert the test result into Databricks")
    public void i_insert_the_test_result_into_databricks() {
        context.insertTestResult();
    }

    @Then("the result should be stored successfully")
    public void the_result_should_be_stored_successfully() {
        Assert.assertTrue(context.isInsertSuccessful());
    }
}
