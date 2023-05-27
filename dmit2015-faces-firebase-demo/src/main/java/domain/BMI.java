package domain;

public class BMI {

    private int weight;
    private int height;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BMI() {

    }

    public BMI(int weight, int height) {
        this.weight = weight;
        this.height = height;
    }

    public double bmi() {
        return (703 * this.weight) / Math.pow(this.height, 2);
    }

    public String bmiCategory() {
        double bmi = this.bmi();
        String category = "";

        if (bmi >= 30) {
            category = "obese";
        } else if (bmi <= 29.9 && bmi >= 25) {
            category = "overweight";
        } else if (bmi <= 24.9 && bmi >= 18.5) {
            category = "normal";
        } else if (bmi < 18.5) {
            category = "underweight";
        }
        return category;
    }
}

