package domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class BMITest {

    @ParameterizedTest(name = "weight = {0}, height = {1}: expected bmi = {2} ")
    @CsvSource({
        "100, 66, 16.1",
        "140, 66, 22.6",
        "175, 66, 28.2",
        "200, 66, 32.3",
        "150, 68, 22.8"
    })
    void bmi(int weight, int height, double expectedBmi) {
        BMI bmi = new BMI();
        bmi.setWeight(weight);
        bmi.setHeight(height);

        assertEquals(expectedBmi, bmi.bmi(), 0.05);
    }

    @ParameterizedTest(name = "{0}, {1}: {2}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
        WEIGHT, HEIGHT, EXPECTED_WEIGHT
        100, 66, underweight
        140, 66, normal
        175, 66, overweight
        200, 66, obese
        150, 68, normal
    """)
    void bmiCategory(int weight, int height, String expectedBmi) {
        BMI bmi = new BMI(weight, height);

        assertEquals(expectedBmi, bmi.bmiCategory());
    }
}