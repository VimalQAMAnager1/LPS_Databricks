Feature: Databricks and Selenium Integration
  As a QA engineer
  I want to validate data and UI using Databricks and Selenium

  Scenario: Validate Databricks connection
    Given I connect to Databricks
    Then the connection should be successful

  Scenario: Validate user data from Databricks in the UI
    Given I fetch user data from Databricks
    When I login with username from Databricks
    Then I should see the correct user information on the UI

  Scenario: Store test results in Databricks
    Given I have a test result
    When I insert the test result into Databricks
    Then the result should be stored successfully
