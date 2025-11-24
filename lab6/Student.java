import java.util.*;

class Student {
    private String name;
    private int age;
    private String sex;
    private Map<Teacher, List<Integer>> grades;
    private double averageGrade;
    private int premium;

    public Student(String name, int age, String sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.grades = new HashMap<>();
        this.averageGrade = 0.0;
        this.premium = 0;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getSex() { return sex; }
    public double getAverageGrade() { return averageGrade; }
    public int getPremium() { return premium; }

    public void addGrade(Teacher teacher, int grade) {
        grades.putIfAbsent(teacher, new ArrayList<>());
        grades.get(teacher).add(grade);
        calculateAverageGrade();
    }

    private void calculateAverageGrade() {
        double sum = 0;
        int count = 0;
        for (List<Integer> teacherGrades : grades.values()) {
            for (int grade : teacherGrades) {
                sum += grade;
                count++;
            }
        }
        averageGrade = count > 0 ? sum / count : 0.0;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    @Override
    public String toString() {
        return "Студент: " + name + ", Возраст: " + age +
                ", Пол: " + sex + ", Средний балл: " +
                String.format("%.2f", averageGrade) +
                ", Премиальные: " + premium + " руб.";
    }
}