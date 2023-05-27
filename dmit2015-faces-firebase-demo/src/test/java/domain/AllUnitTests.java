package domain;

import org.junit.platform.suite.api.*;

@Suite
@SuiteDisplayName("JUnit 5 Platform Suite")
@SelectPackages("domain")
@IncludeClassNamePatterns(".*Test")
public class AllUnitTests {

}
