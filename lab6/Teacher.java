class Teacher {
    private String name;
    private int age;
    private String sex;
    private Subject subject;

    public Teacher(String name, int age, String sex, Subject subject) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.subject = subject;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getSex() { return sex; }
    public Subject getSubject() { return subject; }

    // Метод для выставления оценки студенту
    public void giveGrade(Student student, int grade) {
        if (grade >= 1 && grade <= 5) {
            student.addGrade(this, grade);
        }
    }

    @Override
    public String toString() {
        return "Преподаватель: " + name + ", Возраст: " + age +
                ", Пол: " + sex + ", " + subject;
    }
}