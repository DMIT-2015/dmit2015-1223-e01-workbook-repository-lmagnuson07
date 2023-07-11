package dmit2015.faces;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExpenseSeleniumIT {

    private static WebDriver driver;

    static Long sharedEditId;

    @BeforeAll
    static void beforeAllTests() {
        //Launching the URL
        WebDriverManager.chromedriver().setup();
        var chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("ignore-certificate-errors");
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.setAcceptInsecureCerts(true);
        driver = new ChromeDriver(chromeOptions);

        // https://www.omgubuntu.co.uk/2022/04/how-to-install-firefox-deb-apt-ubuntu-22-04
//        WebDriverManager.firefoxdriver().setup();
//        driver = new FirefoxDriver();
    }

    @AfterAll
    static void afterAllTests() {
        driver.quit();
    }

    @BeforeEach
    void beforeEachTestMethod() {

    }

    @AfterEach
    void afterEachTestMethod() {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }

    private void setValue(String id, String value) {
        WebElement element = driver.findElement(By.id(id));
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.chord(Keys.BACK_SPACE));
        element.clear();
        element.sendKeys(value);
    }

    private void setDateValue(String id, String value) {
        WebElement element = driver.findElement(By.id(id));
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.chord(Keys.BACK_SPACE));
        element.clear();
        element.sendKeys(value);
        element.sendKeys(Keys.chord(Keys.TAB));
    }

    private void sleepThread(Long time) {
        try {
            Thread.sleep(time); // Pause for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Order(6)
    @ParameterizedTest
    @CsvSource(value = {
            "DAUSTIN@dmit2015.ca,Password2015,DAUSTIN@dmit2015.ca,6,Rent,Active,DLEE@dmit2015.ca,Password2015,4,DLEE@dmit2015.ca,Tacos,Food,$25.12,1,'May 18, 2023'",
    })
    void shouldLoginAndImport(String itUsername,
                     String itPassword,
                     String expectedLastRowCatUsername,
                     String expectedLastRowCatId,
                     String expectedLastRowCatDescription,
                     String expectedLastRowCatActive,
                     String salesUsername,
                     String salesPassword,
                     String expectedLastRowSalesId,
                     String expectedLastRowSalesUsername,
                     String expectedLastRowSalesDescription,
                     String expectedLastRowSalesCategory,
                     String expectedLastRowSalesAmount,
                     String expectedLastRowSalesTotal,
                     String expectedLastRowExpenseDate
    ) {
        // Login with IT
        driver.get("https://localhost:8443/shiroLogin.xhtml");

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Shiro Login");

        setValue("username", itUsername);
        setValue("password", itPassword);

        // Maximize the browser window so we can see the data being inputted
        driver.manage().window().maximize();

        // Find the login button and click on it
        driver.findElement(By.id("j_idt29")).click();

        sleepThread(2000L);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Shiro Login");

        List<WebElement> elements = driver.findElements(By.cssSelector("ul[role='menubar'] > li:last-of-type a[role='menuitem']"));

        assertThat(elements)
                .isNotEmpty();

        //Import data via csv file
        driver.get("https://localhost:8443/categories/crud.xhtml");

        sleepThread(2000L);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Category - CRUD");

        sleepThread(200L);

        // Click category import button
        driver.findElement(By.id("form:j_idt24")).click();

        sleepThread(1000L);

        driver.get("https://localhost:8443/categories/crud.xhtml");

        sleepThread(1000L);

        // Check the number of rows in the entity listing table
        int lastRow = (driver.findElements(By.xpath("//table[@role='grid']/tbody/tr")).size());
        final String lastRowFirstColumnColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[1]", lastRow);
        final String lastRowSecondColumnColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[2]", lastRow);
        final String lastThirdColumnColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[3]", lastRow);
        final String lastRowFourthColumnColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[4]", lastRow);

        // Get the text for each column in the last row
        final String lastRowColumn1 = driver.findElement(By.xpath(lastRowFirstColumnColumnXpathExpression)).getText();
        final String lastRowColumn2 = driver.findElement(By.xpath(lastRowSecondColumnColumnXpathExpression)).getText();
        final String lastRowColumn3 = driver.findElement(By.xpath(lastThirdColumnColumnXpathExpression)).getText();
        final String lastRowColumn4 = driver.findElement(By.xpath(lastRowFourthColumnColumnXpathExpression)).getText();

        assertThat(lastRowColumn1)
                .isEqualToIgnoringCase(expectedLastRowCatUsername);
        assertThat(lastRowColumn2)
                .isEqualToIgnoringCase(expectedLastRowCatId);
        assertThat(lastRowColumn3)
                .isEqualToIgnoringCase(expectedLastRowCatDescription);
        assertThat(lastRowColumn4)
                .isEqualToIgnoringCase(expectedLastRowCatActive);

        // Logout IT role
        driver.findElement(By.cssSelector("ul[role='menubar'] > li:last-of-type a[role='menuitem']")).click();

//        elements = driver.findElements(By.cssSelector("ul[role='menubar'] > li:last-of-type a[role='menuitem']"));
//
//        assertThat(elements)
//                .isEmpty();

        sleepThread(1000L);

        driver.get("https://localhost:8443/index.xhtml");

        sleepThread(1000L);

        // Login with Sales
        driver.get("https://localhost:8443/shiroLogin.xhtml");

        sleepThread(1000L);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Shiro Login");

        setValue("username", salesUsername);
        setValue("password", salesPassword);

        // Maximize the browser window so we can see the data being inputted
        driver.manage().window().maximize();

        // Find the login button and click on it
        driver.findElement(By.id("j_idt29")).click();

        sleepThread(2000L);

//        elements = driver.findElements(By.cssSelector("ul[role='menubar'] > li:last-of-type a[role='menuitem']"));
//
//        assertThat(elements)
//                .isNotEmpty();

        //Import data via csv file
        driver.get("https://localhost:8443/expenses/index.xhtml");

        sleepThread(2000L);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Expense - List");

        sleepThread(200L);

        // Click expense import button
        driver.findElement(By.id("j_idt26")).click();

        sleepThread(1000L);

        // Check the number of rows in the entity listing table
        lastRow = (driver.findElements(By.xpath("//table[@role='grid']/tbody/tr")).size());
        final String lastRowFirstColumnColumnExpenseXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[1]", lastRow);
        final String lastRowSecondColumnColumnExpenseXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[2]", lastRow);
        final String lastThirdColumnExpenseColumnExpenseXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[3]", lastRow);
        final String lastRowFourthColumnExpenseColumnExpenseXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[4]", lastRow);
        final String lastRowFifthColumnExpenseColumnExpenseXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[5]", lastRow);
        final String lastRowSixthColumnExpenseColumnExpenseXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[6]", lastRow);
        final String lastRowSeventhColumnExpenseColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[7]", lastRow);

        // Get the text for each column in the last row
        final String lastRowExpenseColumn1 = driver.findElement(By.xpath(lastRowFirstColumnColumnExpenseXpathExpression)).getText();
        final String lastRowExpenseColumn2 = driver.findElement(By.xpath(lastRowSecondColumnColumnExpenseXpathExpression)).getText();
        final String lastRowExpenseColumn3 = driver.findElement(By.xpath(lastThirdColumnExpenseColumnExpenseXpathExpression)).getText();
        final String lastRowExpenseColumn4 = driver.findElement(By.xpath(lastRowFourthColumnExpenseColumnExpenseXpathExpression)).getText();
        final String lastRowExpenseColumn5 = driver.findElement(By.xpath(lastRowFifthColumnExpenseColumnExpenseXpathExpression)).getText();
        final String lastRowExpenseColumn6 = driver.findElement(By.xpath(lastRowSixthColumnExpenseColumnExpenseXpathExpression)).getText();
        final String lastRowExpenseColumn7 = driver.findElement(By.xpath(lastRowSeventhColumnExpenseColumnXpathExpression)).getText();

        assertThat(lastRowExpenseColumn1)
                .isEqualToIgnoringCase(expectedLastRowSalesId);
        assertThat(lastRowExpenseColumn2)
                .isEqualToIgnoringCase(expectedLastRowSalesUsername);
        assertThat(lastRowExpenseColumn3)
                .isEqualToIgnoringCase(expectedLastRowSalesDescription);
        assertThat(lastRowExpenseColumn4)
                .isEqualToIgnoringCase(expectedLastRowSalesCategory);
        assertThat(lastRowExpenseColumn5)
                .isEqualToIgnoringCase(expectedLastRowSalesAmount);
        assertThat(lastRowExpenseColumn6)
                .isEqualToIgnoringCase(expectedLastRowSalesTotal);
        assertThat(lastRowExpenseColumn7)
                .isEqualToIgnoringCase(expectedLastRowExpenseDate);

        // Verify that clicking on the edit link navigates to the Edit page
        driver.findElements(By.xpath("//a[contains(@id,'editLink')]")).get(0).click();
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Expense - Edit");
        // Navigate back to the listing page
        driver.navigate().back();

        // Verify that clicking on the details link navigates to the Details page
        driver.findElements(By.xpath("//a[contains(@id,'detailsLink')]")).get(0).click();
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Expense - Details");
        // Navigate back to the listing page
        driver.navigate().back();

        // Verify that clicking on the details link navigates to the Delete page
        driver.findElements(By.xpath("//a[contains(@id,'deleteLink')]")).get(0).click();
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Expense - Delete");
        // Navigate back to the listing page
        driver.navigate().back();

        // Logout Sales role
        driver.findElement(By.cssSelector("ul[role='menubar'] > li:last-of-type a[role='menuitem']")).click();

//        elements = driver.findElements(By.cssSelector("ul[role='menubar'] > li:last-of-type a[role='menuitem']"));
//
//        assertThat(elements)
//                .isEmpty();

    }

//    @Order(1)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "Expense number 1,Food,24.00,1,2023-03-28",
//            "Expense number 2,Dog Food,10.00,1,2023-03-26",
//            "Expense number 3,Gaming,80.00,1,2023-04-24",
//            "Expense number 4,Other,25.00,1,2023-05-18",
//    })
//    void shouldCreate(String description, String category, String amount, String total, String expenseDate) {
//        driver.get("http://localhost:8080/expenses/create.xhtml");
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - Create");
//
//        // Set the value for each form field
//        setValue("description", description);
//        setValue("category", category);
//        setValue("amount", amount);
//        setValue("total", total);
//        setDateValue("expenseDate_input", expenseDate);
//
//        // Maximize the browser window so we can see the data being inputted
//        driver.manage().window().maximize();
//        // Find the create button and click on it
//        driver.findElement(By.id("createButton")).click();
//
//        // Wait for 3 seconds and verify navigate has been redirected to the listing page
//        var wait = new WebDriverWait(driver, Duration.ofSeconds(3));
//        var facesMessages = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-messages-info-summary")));
//        // Verify the title of the page
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - List");
//        // Verify the feedback message is displayed in the page
//        String feedbackMessage = facesMessages.getText();
//        assertThat(feedbackMessage)
//                .containsIgnoringCase("Create was successful.");
//        // The primary key of the entity is at the end of the feedback message after the period
//        final int indexOfPrimaryKeyValue = feedbackMessage.indexOf(".") + 2;
//        // Extract the primary key for re-use if we need to edit or delete the entity
//        sharedEditId = Long.parseLong(feedbackMessage.substring(indexOfPrimaryKeyValue));
//    }
//
//    @Order(2)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "Expense number 2|Dog Food|$10.00|1|Mar. 26, 2023"
//    }, delimiter = '|')
//    void shouldList(String expectedLastRowDescription,
//                    String expectedLastRowCategory,
//                    String expectedLastRowAmount,
//                    String expectedLastQuantity,
//                    String expectedLastRowExpenseDate
//    ) {
//        // Open a browser and navigate to the page to list entities
//        driver.get("http://localhost:8080/expenses/index.xhtml");
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - List");
//
//        // Check the number of rows in the entity listing table
//        int lastRow = (driver.findElements(By.xpath("//table[@role='grid']/tbody/tr")).size());
//
//        // Define the XPATH for locating the each column of the last row
////        final String lastRowFirstColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[1]", lastRow);
//        final String lastRowSecondColumnColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[2]", lastRow);
//        final String lastThirdColumnColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[3]", lastRow);
//        final String lastRowFourthColumnColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[4]", lastRow);
//        final String lastRowFifthColumnColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[5]", lastRow);
//        final String lastRowSixthColumnColumnXpathExpression = String.format("//table[@role='grid']/tbody/tr[%d]/td[6]", lastRow);
//        // Get the text for each column in the last row
////        final String lastRowColumn1 = driver.findElement(By.xpath(lastRowFirstColumnXpathExpression)).getText();
//        final String lastRowColumn2 = driver.findElement(By.xpath(lastRowSecondColumnColumnXpathExpression)).getText();
//        final String lastRowColumn3 = driver.findElement(By.xpath(lastThirdColumnColumnXpathExpression)).getText();
//        final String lastRowColumn4 = driver.findElement(By.xpath(lastRowFourthColumnColumnXpathExpression)).getText();
//        final String lastRowColumn5 = driver.findElement(By.xpath(lastRowFifthColumnColumnXpathExpression)).getText();
//        final String lastRowColumn6 = driver.findElement(By.xpath(lastRowSixthColumnColumnXpathExpression)).getText();
//
//        // Verify each column of the last row
////        assertThat(lastRowColumn1)
////                .isEqualToIgnoringCase(expectedLastRowDescription);
//        assertThat(lastRowColumn2)
//                .isEqualToIgnoringCase(expectedLastRowDescription);
//        assertThat(lastRowColumn3)
//                .isEqualToIgnoringCase(expectedLastRowCategory);
//        assertThat(lastRowColumn4)
//                .isEqualToIgnoringCase(expectedLastRowAmount);
//        assertThat(lastRowColumn5)
//                .isEqualToIgnoringCase(expectedLastQuantity);
//        assertThat(lastRowColumn6)
//                .isEqualToIgnoringCase(expectedLastRowExpenseDate);
//
//        // Verify that clicking on the edit link navigates to the Edit page
//        driver.findElements(By.xpath("//a[contains(@id,'editLink')]")).get(0).click();
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - Edit");
//        // Navigate back to the listing page
//        driver.navigate().back();
//
//        // Verify that clicking on the details link navigates to the Details page
//        driver.findElements(By.xpath("//a[contains(@id,'detailsLink')]")).get(0).click();
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - Details");
//        // Navigate back to the listing page
//        driver.navigate().back();
//
//        // Verify that clicking on the details link navigates to the Delete page
//        driver.findElements(By.xpath("//a[contains(@id,'deleteLink')]")).get(0).click();
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - Delete");
//        // Navigate back to the listing page
//        driver.navigate().back();
//    }
//
//    @Order(3)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "description|Expense number 2|category|Dog Food|amount|$10.00|total|1|expenseDate|Mar. 26, 2023",
//    }, delimiter = '|')
//    void shouldDetails(String descriptionId, String descriptionText,
//                       String categoryId, String categoryText,
//                       String amountId, String amountText,
//                       String totalId, String totalText,
//                       String expenseDateId, String expenseDateText
//   ) {
//        // Open a browser and navigate to the page to list entities
//        driver.get("http://localhost:8080/expenses/index.xhtml");
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - List");
//
//        int tableRowCount = (driver.findElements(By.xpath("//table[@role='grid']/tbody/tr")).size());
//        int lastRowIndex = tableRowCount - 1;
//        driver.findElements(By.xpath("//a[contains(@id,'detailsLink')]")).get(lastRowIndex).click();
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - Details");
//
//        var actualField1Value = driver.findElement(By.id(descriptionId)).getText();
//        var actualField2Value = driver.findElement(By.id(categoryId)).getText();
//        var actualField3Value = driver.findElement(By.id(amountId)).getText();
//        var actualField4Value = driver.findElement(By.id(totalId)).getText();
//        var actualField5Value = driver.findElement(By.id(expenseDateId)).getText();
//        assertThat(actualField1Value)
//                .isEqualToIgnoringCase(descriptionText);
//        assertThat(actualField2Value)
//                .isEqualToIgnoringCase(categoryText);
//        assertThat(actualField3Value)
//                .isEqualToIgnoringCase(amountText);
//        assertThat(actualField4Value)
//                .isEqualToIgnoringCase(totalText);
//        assertThat(actualField5Value)
//                .isEqualToIgnoringCase(expenseDateText);
//
//    }
//
//    @Order(4)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "description|Expense number 2|category|Dog Food|amount|10.15|total|2|expenseDate_input|2023-03-26",
//    }, delimiter = '|')
//    void shouldEdit(String descriptionId, String descriptionText,
//                    String categoryId, String categoryText,
//                    String amountId, String amountText,
//                    String totalId, String totalText,
//                    String expenseDateId, String expenseDateText
//    ) {
//        // Open a browser and navigate to the page to list entities
//        driver.get("http://localhost:8080/expenses/index.xhtml");
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - List");
//
//        int tableRowCount = (driver.findElements(By.xpath("//table[@role='grid']/tbody/tr")).size());
//        int lastRowIndex = tableRowCount - 1;
//
//        driver.findElements(By.xpath("//a[contains(@id,'editLink')]")).get(lastRowIndex).click();
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - Edit");
//
//        // Set the value for each form field
//        setValue(descriptionId, descriptionText);
//        setValue(categoryId, categoryText);
//        setValue(amountId, amountText);
//        setValue(totalId, totalText);
//        setDateValue(expenseDateId, expenseDateText);
//
//        driver.manage().window().maximize();
//        driver.findElement(By.id("updateButton")).click();
//
//        var wait = new WebDriverWait(driver, Duration.ofSeconds(11));
//        var facesMessages = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-messages-info-summary")));
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - List");
//        String feedbackMessage = facesMessages.getText();
//
//        assertThat(feedbackMessage)
//                .containsIgnoringCase("Update was successful.");
//    }
//
//
//    @Order(5)
//    @Test
//    void shouldDelete() {
//        // Open a browser and navigate to the page to list entities
//        driver.get("http://localhost:8080/expenses/index.xhtml");
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - List");
//
//        int tableRowCount = (driver.findElements(By.xpath("//table[@role='grid']/tbody/tr")).size());
//        int lastRowIndex = tableRowCount - 1;
//
//        driver.findElements(By.xpath("//a[contains(@id,'deleteLink')]")).get(lastRowIndex).click();
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - Delete");
//
//        driver.findElement(By.id("deleteButton")).click();
//
//        var wait = new WebDriverWait(driver, Duration.ofSeconds(1));
//
//        var yesConfirmationButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-confirmdialog-yes")));
//        yesConfirmationButton.click();
//
//        wait = new WebDriverWait(driver, Duration.ofSeconds(3));
//        var facesMessages = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-messages-info-summary")));
//        assertThat(driver.getTitle())
//                .isEqualToIgnoringCase("Expense - List");
//        String feedbackMessage = facesMessages.getText();
//
//        assertThat(feedbackMessage)
//                .containsIgnoringCase("Delete was successful.");
//    }

}