import java.util.ArrayList;
import java.util.List;

class Parent {
    private String name;
    private int age;
    private String sex;
    private List<Student> children;
    private String mood;
    private int totalPremiumPaid;

    public Parent(String name, int age, String sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.children = new ArrayList<>();
        this.mood = "нейтральный";
        this.totalPremiumPaid = 0;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getSex() { return sex; }

    public void addChild(Student student) {
        children.add(student);
    }

    // Метод для определения настроения и выплаты премиальных
    public void checkChildrenPerformance() {
        totalPremiumPaid = 0;
        for (Student child : children) {
            double avg = child.getAverageGrade();
            if (avg >= 4.6 && avg <= 5.0) {
                mood = "радостный";
                child.setPremium(10000);
                totalPremiumPaid += 10000;
            } else if (avg >= 4.0 && avg < 4.6) {
                mood = "удовлетворенный";
                child.setPremium(5000);
                totalPremiumPaid += 5000;
            } else if (avg >= 3.0 && avg < 4.0) {
                mood = "хмурый";
                child.setPremium(0);
            } else {
                mood = "расстроенный";
                child.setPremium(0);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder childrenNames = new StringBuilder();
        for (Student child : children) {
            childrenNames.append(child.getName()).append(", ");
        }
        if (childrenNames.length() > 0) {
            childrenNames.setLength(childrenNames.length() - 2);
        }

        return "Родитель: " + name + ", Возраст: " + age +
                ", Пол: " + sex + ", Настроение: " + mood +
                ", Дети: " + childrenNames.toString() +
                ", Выплачено премиальных: " + totalPremiumPaid + " руб.";
    }
}