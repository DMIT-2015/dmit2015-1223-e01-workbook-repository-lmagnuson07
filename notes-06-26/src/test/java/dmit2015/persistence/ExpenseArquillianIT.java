//package dmit2015.persistence;
//
//import dmit2015.config.ApplicationConfig;
//import dmit2015.entity.Expense;
//import jakarta.annotation.Resource;
//import jakarta.inject.Inject;
//import jakarta.transaction.NotSupportedException;
//import jakarta.transaction.SystemException;
//import jakarta.transaction.UserTransaction;
//import org.apache.maven.model.Model;
//import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
//import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit5.ArquillianExtension;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.jboss.shrinkwrap.resolver.api.maven.Maven;
//import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import java.io.*;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@ExtendWith(ArquillianExtension.class)
//public class ExpenseArquillianIT { // The class must be declared as public
//
//    static String mavenArtifactIdId;
//
//    @Deployment
//    public static WebArchive createDeployment() throws IOException, XmlPullParserException {
//        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");
//        MavenXpp3Reader reader = new MavenXpp3Reader();
//        Model model = reader.read(new FileReader("pom.xml"));
//        mavenArtifactIdId = model.getArtifactId();
//        final String archiveName = model.getArtifactId() + ".war";
//        return ShrinkWrap.create(WebArchive.class, archiveName)
//                .addAsLibraries(pomFile.resolve("org.codehaus.plexus:plexus-utils:3.4.2").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest:2.2").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.assertj:assertj-core:3.24.2").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.h2database:h2:2.1.214").withTransitivity().asFile())
////                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:11.2.3.jre17").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc11:23.2.0.0").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.hibernate.orm:hibernate-spatial:6.2.3.Final").withTransitivity().asFile())
//                // .addAsLibraries(pomFile.resolve("org.eclipse:yasson:3.0.3").withTransitivity().asFile())
//                .addClass(ApplicationConfig.class)
//                .addClasses(Expense.class, ExpenseRepository.class)
//                // .addClasses(ApplicationStartupListener.class)
//                 .addPackage("dmit2015.entity")
//                .addAsResource("META-INF/persistence.xml")
//                // .addAsResource(new File("src/test/resources/META-INF/persistence-entity.xml"),"META-INF/persistence.xml")
//                .addAsResource("META-INF/beans.xml");
//    }
//
//    @Inject
//    private ExpenseRepository _expenseRepository;
//
//    @Resource
//    private UserTransaction _beanManagedTransaction;
//
//    @BeforeAll
//    static void beforeAllTestMethod() {
//        // code to execute before test methods are executed
//    }
//
//    @BeforeEach
//    void beforeEachTestMethod() {
//        // Code to execute before each method such as creating the test data
//    }
//
//    @AfterEach
//    void afterEachTestMethod() {
//        // code to execute after each test method such as deleteing the test data
//    }
//
//
//    @Order(1)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "3,Expense number 3,Gaming,80,1,2023-04-24,Expense number 1,Food,24,1,2023-03-28"
//    })
//    void findAll_Size_BoundaryValues(int expectedSize,
//                                     String expectedFirstRecordDescription,
//                                     String expectedFirstRecordCategory,
//                                     BigDecimal expectedFirstRecordAmount,
//                                     Integer expectedFirstRecordTotal,
//                                     LocalDate expectedFirstRecordExpenseDate,
//                                     String expectedLastDescription,
//                                     String expectedLastRecordCategory,
//                                     BigDecimal expectedLastAmount,
//                                     Integer expectedLastRecordTotal,
//                                     LocalDate expectedLastRecordExpenseDate
//    ) {
//        assertThat(_expenseRepository).isNotNull();
//        // Arrange and Act
//        List<Expense> expenseList = _expenseRepository.findAll();
//        // Assert
//        assertThat(expenseList.size())
//                .isEqualTo(expectedSize);
//
//        // Get the first entity and compare with expected results
//        var firstExpense = expenseList.get(0);
//        assertThat(firstExpense.getDescription()).isEqualTo(expectedFirstRecordDescription);
//        assertThat(firstExpense.getCategory()).isEqualTo(expectedFirstRecordCategory);
//        assertThat(firstExpense.getAmount()).isEqualTo(expectedFirstRecordAmount);
//        assertThat(firstExpense.getTotal()).isEqualTo(expectedFirstRecordTotal);
//        assertThat(firstExpense.getExpenseDate()).isEqualTo(expectedFirstRecordExpenseDate);
//
//        // Get the last entity and compare with expected results
//        var lastExpense = expenseList.get(expenseList.size() - 1);
//        assertThat(lastExpense.getDescription()).isEqualTo(expectedLastDescription);
//        assertThat(lastExpense.getCategory()).isEqualTo(expectedLastRecordCategory);
//        assertThat(lastExpense.getAmount()).isEqualTo(expectedLastAmount);
//        assertThat(lastExpense.getTotal()).isEqualTo(expectedLastRecordTotal);
//        assertThat(lastExpense.getExpenseDate()).isEqualTo(expectedLastRecordExpenseDate);
//
//    }
//
//
//    @Order(2)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "3,Expense number 3,Gaming,80,1,2023-04-24",
//            "4,Expense number 4,Other,25,1,2023-05-18"
//    })
//    void findById_ExistingId_IsPresent(Long expenseId,
//                                       String expectedDescription,
//                                       String expectedCategory,
//                                       BigDecimal expectedAmount,
//                                       Integer expectedTotal,
//                                       LocalDate expectedExpenseDate
//    ) {
//        // Arrange and Act
//        Optional<Expense> optionalExpense = _expenseRepository.findById(expenseId);
//        assertThat(optionalExpense.isPresent())
//                .isTrue();
//        Expense existingExpense = optionalExpense.orElseThrow();
//
//        // Assert
//        assertThat(existingExpense)
//                .isNotNull();
//        assertThat(existingExpense.getDescription())
//                .isEqualTo(expectedDescription);
//        assertThat(existingExpense.getCategory())
//                .isEqualTo(expectedCategory);
//        assertThat(existingExpense.getAmount())
//                .isEqualTo(expectedAmount);
//        assertThat(existingExpense.getTotal())
//                .isEqualTo(expectedTotal);
//        assertThat(existingExpense.getExpenseDate())
//                .isEqualTo(expectedExpenseDate);
//    }
//
//
//    @Order(3)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "Expense number 5,Gas,50,1,2023-05-16",
//            "Expense number 6,Car Payment,140.21,1,2023-04-02",
//    })
//    void add_ValidData_Added(String description,
//                             String category,
//                             BigDecimal amount,
//                             Integer total,
//                             LocalDate expenseDate
//    ) throws SystemException, NotSupportedException
//    {
//        // Arrange
//        Expense newExpense = new Expense();
//        newExpense.setDescription(description);
//        newExpense.setCategory(category);
//        newExpense.setAmount(amount);
//        newExpense.setTotal(total);
//        newExpense.setExpenseDate(expenseDate);
//
//        _beanManagedTransaction.begin();
//
//        try {
//            // Act
//            _expenseRepository.add(newExpense);
//
//            // Assert
//             Optional<Expense> optionalExpense = _expenseRepository.findById(newExpense.getExpenseId());
//             assertThat(optionalExpense.isPresent())
//                 .isTrue();
//
//        } catch (Exception ex) {
//            fail("Failed to add entity with exception", ex.getMessage());
//        } finally {
//            _beanManagedTransaction.rollback();
//        }
//
//    }
//
//
//    @Order(4)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "3,Edited Expense number 3,Gaming,82,2,2023-04-24",
//            "1,Edited Expense number 1,Other,26,3,2023-05-18",
//    })
//    void update_ExistingId_UpdatedData(Long expenseId,
//                                       String description,
//                                       String category,
//                                       BigDecimal amount,
//                                       Integer total,
//                                       LocalDate expenseAmount
//    ) throws SystemException, NotSupportedException
//    {
//        // Arrange
//        Optional<Expense> optionalExpense = _expenseRepository.findById(expenseId);
//        assertThat(optionalExpense.isPresent()).isTrue();
//
//        Expense existingExpense = optionalExpense.orElseThrow();
//        assertThat(existingExpense).isNotNull();
//
//        // Act
//         existingExpense.setDescription(description);
//         existingExpense.setCategory(category);
//         existingExpense.setAmount(amount);
//         existingExpense.setTotal(total);
//         existingExpense.setExpenseDate(expenseAmount);
//
//        _beanManagedTransaction.begin();
//
//        try {
//            Expense updatedExpense = _expenseRepository.update(expenseId, existingExpense);
//
//            // Assert
//            assertThat(existingExpense)
//                    .usingDefaultComparator()
//                    .isEqualTo(updatedExpense);
//        } catch (Exception ex) {
//            fail("Failed to update entity with exception", ex.getMessage());
//        } finally {
//            _beanManagedTransaction.rollback();
//        }
//
//    }
//
//
//    @Order(5)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "1",
//            "4",
//    })
//    void deleteById_ExistingId_DeletedData(Long expenseId) throws SystemException, NotSupportedException {
//        _beanManagedTransaction.begin();
//
//        try {
//            // Arrange and Act
//            _expenseRepository.deleteById(expenseId);
//
//            // Assert
//            assertThat(_expenseRepository.findById(expenseId))
//                    .isEmpty();
//
//        } catch (Exception ex) {
//            fail("Failed to delete entity with exception message %s", ex.getMessage());
//        } finally {
//            _beanManagedTransaction.rollback();
//        }
//
//    }
//
//
//    @Order(6)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "2025",
//            "102"
//    })
//    void findById_NonExistingId_IsEmpty(Long expenseId) {
//        // Arrange and Act
//        Optional<Expense> optionalExpense = _expenseRepository.findById(expenseId);
//
//        // Assert
//        assertThat(optionalExpense.isEmpty())
//                .isTrue();
//
//    }
//
//
//    @Order(7)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "null,You must enter the amount of the Expense.,null,You must choose a category for the Expense.,null,You must enter a description for the Expense.,null,You must enter a date for the Expense.,null,You must enter a total quantity for the Expense.",
//            "            ,You must enter the amount of the Expense.,            ,You must choose a category for the Expense.,            ,You must enter a description for the Expense.,            ,You must enter a date for the Expense.,            ,You must enter a total quantity for the Expense.",
//    }, nullValues = {"null"})
//    void create_beanValidation_shouldFail(String description,
//                                          String descriptionException,
//                                          String category,
//                                          String categoryException,
//                                          BigDecimal amount,
//                                          String amountException,
//                                          Integer total,
//                                          String totalException,
//                                          LocalDate expenseDate,
//                                          String expenseException
//    ) throws SystemException, NotSupportedException
//    {
//        // Arrange
//        Expense newExpense = new Expense();
////         newExpense.setDescription(description);
////         newExpense.setCategory(category);
////         newExpense.setAmount(amount);
////         newExpense.setTotal(total);
//         newExpense.setExpenseDate(expenseDate);
//
//        _beanManagedTransaction.begin();
//        try {
//            // Act
//            _expenseRepository.add(newExpense);
//            fail("An bean validation constraint should have been thrown");
//        } catch (Exception ex) {
//            // Assert
////            assertThat(ex)
////                    .hasMessageContaining(descriptionException);
////            assertThat(ex)
////                    .hasMessageContaining(categoryException);
////            assertThat(ex)
////                    .hasMessageContaining(amountException);
////            assertThat(ex)
////                    .hasMessageContaining(totalException);
//            assertThat(ex)
//                    .hasMessageContaining(expenseException);
//        } finally {
//            _beanManagedTransaction.rollback();
//        }
//
//    }
//
//}