import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// Графический интерфейс для управления университетскими данными
public class UniversityGUI extends JFrame {
    private JTabbedPane tabbedPane;

    // Данные
    private List<Teacher> teachers = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Parent> parents = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();

    // Таблицы
    private JTable teacherTable, studentTable, parentTable, subjectTable;
    private TeacherTableModel teacherTableModel;
    private StudentTableModel studentTableModel;
    private ParentTableModel parentTableModel;
    private SubjectTableModel subjectTableModel;

    // Панели для ввода данных
    private JPanel teacherInputPanel, studentInputPanel, parentInputPanel, subjectInputPanel;

    public UniversityGUI() {
        super("Управление университетскими данными");
        initializeData();
        createGUI();
        setupEventHandlers();
    }

    private void initializeData() {
        // Инициализация начальных данных (можно загрузить из файла)
        Teacher teacher1 = new Teacher("Иван Петров", 45, "мужской", null);
        Teacher teacher2 = new Teacher("Мария Сидорова", 38, "женский", null);

        Subject subject1 = new Subject("Математика", teacher1);
        Subject subject2 = new Subject("Физика", teacher2);

        teacher1 = new Teacher("Иван Петров", 45, "мужской", subject1);
        teacher2 = new Teacher("Мария Сидорова", 38, "женский", subject2);

        teachers.add(teacher1);
        teachers.add(teacher2);
        subjects.add(subject1);
        subjects.add(subject2);

        Student student1 = new Student("Андрей Клочков", 20, "мужской");
        Student student2 = new Student("Екатерина Смирнова", 19, "женский");

        students.add(student1);
        students.add(student2);

        Parent parent1 = new Parent("Ольга Клочкова", 45, "женский");
        parent1.addChild(student1);

        Parent parent2 = new Parent("Сергей Смирнов", 50, "мужской");
        parent2.addChild(student2);

        parents.add(parent1);
        parents.add(parent2);

        // Выставление случайных оценок
        Random random = new Random();
        for (Teacher teacher : teachers) {
            for (Student student : students) {
                for (int i = 0; i < 5; i++) {
                    int grade = random.nextInt(3) + 3;
                    teacher.giveGrade(student, grade);
                }
            }
        }

        for (Parent parent : parents) {
            parent.checkChildrenPerformance();
        }
    }

    private void createGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Создание вкладок
        tabbedPane = new JTabbedPane();

        // Вкладка преподавателей
        teacherTableModel = new TeacherTableModel(teachers);
        teacherTable = new JTable(teacherTableModel);
        teacherInputPanel = createTeacherInputPanel();
        JPanel teacherPanel = createTableWithInputPanel(teacherTable, teacherInputPanel, "Преподаватели");
        tabbedPane.addTab("Преподаватели", teacherPanel);

        // Вкладка студентов
        studentTableModel = new StudentTableModel(students);
        studentTable = new JTable(studentTableModel);
        studentInputPanel = createStudentInputPanel();
        JPanel studentPanel = createTableWithInputPanel(studentTable, studentInputPanel, "Студенты");
        tabbedPane.addTab("Студенты", studentPanel);

        // Вкладка родителей
        parentTableModel = new ParentTableModel(parents);
        parentTable = new JTable(parentTableModel);
        parentInputPanel = createParentInputPanel();
        JPanel parentPanel = createTableWithInputPanel(parentTable, parentInputPanel, "Родители");
        tabbedPane.addTab("Родители", parentPanel);

        // Вкладка предметов
        subjectTableModel = new SubjectTableModel(subjects);
        subjectTable = new JTable(subjectTableModel);
        subjectInputPanel = createSubjectInputPanel();
        JPanel subjectPanel = createTableWithInputPanel(subjectTable, subjectInputPanel, "Предметы");
        tabbedPane.addTab("Предметы", subjectPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Панель управления
        JPanel controlPanel = new JPanel();
        JButton refreshButton = new JButton("Обновить данные");
        JButton calculatePerformanceButton = new JButton("Пересчитать успеваемость");

        refreshButton.addActionListener(e -> refreshAllTables());
        calculatePerformanceButton.addActionListener(e -> calculatePerformance());

        controlPanel.add(refreshButton);
        controlPanel.add(calculatePerformanceButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createTableWithInputPanel(JTable table, JPanel inputPanel, String title) {
        JPanel panel = new JPanel(new BorderLayout());

        // Заголовок
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Таблица с прокруткой
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Панель ввода
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTeacherInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JPanel inputPanel = new JPanel(new GridLayout(1, 6));

        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> sexComboBox = new JComboBox<>(new String[]{"мужской", "женский"});
        JComboBox<Subject> subjectComboBox = new JComboBox<>();
        updateSubjectComboBox(subjectComboBox);

        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить");

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String sex = (String) sexComboBox.getSelectedItem();
                Subject subject = (Subject) subjectComboBox.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Введите имя преподавателя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (age < 18 || age > 100) {
                    JOptionPane.showMessageDialog(this, "Возраст должен быть от 18 до 100 лет", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Teacher teacher = new Teacher(name, age, sex, subject);
                teachers.add(teacher);
                teacherTableModel.fireTableDataChanged();

                // Очистка полей
                nameField.setText("");
                ageField.setText("");

                JOptionPane.showMessageDialog(this, "Преподаватель добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некорректный возраст", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow != -1) {
                teachers.remove(selectedRow);
                teacherTableModel.fireTableDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Выберите преподавателя для удаления", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(new JLabel("Имя:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Возраст:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Пол:"));
        inputPanel.add(sexComboBox);
        inputPanel.add(new JLabel("Предмет:"));
        inputPanel.add(subjectComboBox);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);

        panel.add(inputPanel);

        // Панель для выставления оценок
        JPanel gradePanel = new JPanel(new GridLayout(1, 5));
        JComboBox<Student> studentComboBox = new JComboBox<>();
        updateStudentComboBox(studentComboBox);
        JComboBox<Teacher> teacherComboBox = new JComboBox<>();
        updateTeacherComboBox(teacherComboBox);
        JTextField gradeField = new JTextField();
        JButton giveGradeButton = new JButton("Выставить оценку");

        giveGradeButton.addActionListener(e -> {
            try {
                Student student = (Student) studentComboBox.getSelectedItem();
                Teacher teacher = (Teacher) teacherComboBox.getSelectedItem();
                int grade = Integer.parseInt(gradeField.getText().trim());

                if (grade < 1 || grade > 5) {
                    JOptionPane.showMessageDialog(this, "Оценка должна быть от 1 до 5", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                teacher.giveGrade(student, grade);
                studentTableModel.fireTableDataChanged();
                gradeField.setText("");

                JOptionPane.showMessageDialog(this, "Оценка выставлена", "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некорректная оценка", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        gradePanel.add(new JLabel("Студент:"));
        gradePanel.add(studentComboBox);
        gradePanel.add(new JLabel("Преподаватель:"));
        gradePanel.add(teacherComboBox);
        gradePanel.add(new JLabel("Оценка:"));
        gradePanel.add(gradeField);
        gradePanel.add(giveGradeButton);

        panel.add(gradePanel);

        return panel;
    }

    private JPanel createStudentInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 7));

        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> sexComboBox = new JComboBox<>(new String[]{"мужской", "женский"});

        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить");

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String sex = (String) sexComboBox.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Введите имя студента", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (age < 16 || age > 35) {
                    JOptionPane.showMessageDialog(this, "Возраст должен быть от 16 до 35 лет", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Student student = new Student(name, age, sex);
                students.add(student);
                studentTableModel.fireTableDataChanged();

                // Очистка полей
                nameField.setText("");
                ageField.setText("");

                JOptionPane.showMessageDialog(this, "Студент добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некорректный возраст", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                students.remove(selectedRow);
                studentTableModel.fireTableDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Выберите студента для удаления", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Имя:"));
        panel.add(nameField);
        panel.add(new JLabel("Возраст:"));
        panel.add(ageField);
        panel.add(new JLabel("Пол:"));
        panel.add(sexComboBox);
        panel.add(addButton);
        panel.add(deleteButton);

        return panel;
    }

    private JPanel createParentInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JPanel inputPanel = new JPanel(new GridLayout(1, 8));

        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> sexComboBox = new JComboBox<>(new String[]{"мужской", "женский"});
        JComboBox<Student> childComboBox = new JComboBox<>();
        updateStudentComboBox(childComboBox);

        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить");
        JButton addChildButton = new JButton("Добавить ребенка");

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String sex = (String) sexComboBox.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Введите имя родителя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (age < 25 || age > 100) {
                    JOptionPane.showMessageDialog(this, "Возраст должен быть от 25 до 100 лет", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Parent parent = new Parent(name, age, sex);
                parents.add(parent);
                parentTableModel.fireTableDataChanged();

                // Очистка полей
                nameField.setText("");
                ageField.setText("");

                JOptionPane.showMessageDialog(this, "Родитель добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некорректный возраст", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        addChildButton.addActionListener(e -> {
            int selectedRow = parentTable.getSelectedRow();
            Student selectedStudent = (Student) childComboBox.getSelectedItem();

            if (selectedRow != -1 && selectedStudent != null) {
                Parent parent = parents.get(selectedRow);
                parent.addChild(selectedStudent);
                parentTableModel.fireTableDataChanged();
                JOptionPane.showMessageDialog(this, "Ребенок добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Выберите родителя и студента", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = parentTable.getSelectedRow();
            if (selectedRow != -1) {
                parents.remove(selectedRow);
                parentTableModel.fireTableDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Выберите родителя для удаления", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(new JLabel("Имя:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Возраст:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Пол:"));
        inputPanel.add(sexComboBox);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);

        panel.add(inputPanel);

        JPanel childPanel = new JPanel(new GridLayout(1, 3));
        childPanel.add(new JLabel("Добавить ребенка:"));
        childPanel.add(childComboBox);
        childPanel.add(addChildButton);

        panel.add(childPanel);

        return panel;
    }

    private JPanel createSubjectInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5));

        JTextField nameField = new JTextField();
        JComboBox<Teacher> teacherComboBox = new JComboBox<>();
        updateTeacherComboBox(teacherComboBox);

        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить");

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            Teacher teacher = (Teacher) teacherComboBox.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите название предмета", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Subject subject = new Subject(name, teacher);
            subjects.add(subject);
            subjectTableModel.fireTableDataChanged();

            // Очистка полей
            nameField.setText("");

            JOptionPane.showMessageDialog(this, "Предмет добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = subjectTable.getSelectedRow();
            if (selectedRow != -1) {
                subjects.remove(selectedRow);
                subjectTableModel.fireTableDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Выберите предмет для удаления", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Название:"));
        panel.add(nameField);
        panel.add(new JLabel("Преподаватель:"));
        panel.add(teacherComboBox);
        panel.add(addButton);
        panel.add(deleteButton);

        return panel;
    }

    private void setupEventHandlers() {
        // Обработчик изменения вкладки
        tabbedPane.addChangeListener(e -> refreshAllTables());
    }

    private void refreshAllTables() {
        teacherTableModel.fireTableDataChanged();
        studentTableModel.fireTableDataChanged();
        parentTableModel.fireTableDataChanged();
        subjectTableModel.fireTableDataChanged();
    }

    private void calculatePerformance() {
        for (Parent parent : parents) {
            parent.checkChildrenPerformance();
        }
        refreshAllTables();
        JOptionPane.showMessageDialog(this, "Успеваемость пересчитана", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTeacherComboBox(JComboBox<Teacher> comboBox) {
        comboBox.removeAllItems();
        for (Teacher teacher : teachers) {
            comboBox.addItem(teacher);
        }
    }

    private void updateStudentComboBox(JComboBox<Student> comboBox) {
        comboBox.removeAllItems();
        for (Student student : students) {
            comboBox.addItem(student);
        }
    }

    private void updateSubjectComboBox(JComboBox<Subject> comboBox) {
        comboBox.removeAllItems();
        for (Subject subject : subjects) {
            comboBox.addItem(subject);
        }
    }

    // Модели таблиц
    class TeacherTableModel extends AbstractTableModel {
        private final List<Teacher> teachers;
        private final String[] columnNames = {"Имя", "Возраст", "Пол", "Предмет"};

        public TeacherTableModel(List<Teacher> teachers) {
            this.teachers = teachers;
        }

        @Override
        public int getRowCount() {
            return teachers.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Teacher teacher = teachers.get(rowIndex);
            switch (columnIndex) {
                case 0: return teacher.getName();
                case 1: return teacher.getAge();
                case 2: return teacher.getSex();
                case 3: return teacher.getSubject() != null ? teacher.getSubject().getName() : "Нет";
                default: return null;
            }
        }
    }

    class StudentTableModel extends AbstractTableModel {
        private final List<Student> students;
        private final String[] columnNames = {"Имя", "Возраст", "Пол", "Средний балл", "Премиальные"};

        public StudentTableModel(List<Student> students) {
            this.students = students;
        }

        @Override
        public int getRowCount() {
            return students.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Student student = students.get(rowIndex);
            switch (columnIndex) {
                case 0: return student.getName();
                case 1: return student.getAge();
                case 2: return student.getSex();
                case 3: return String.format("%.2f", student.getAverageGrade());
                case 4: return student.getPremium() + " руб.";
                default: return null;
            }
        }
    }

    class ParentTableModel extends AbstractTableModel {
        private final List<Parent> parents;
        private final String[] columnNames = {"Имя", "Возраст", "Пол", "Настроение", "Дети", "Выплачено"};

        public ParentTableModel(List<Parent> parents) {
            this.parents = parents;
        }

        @Override
        public int getRowCount() {
            return parents.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Parent parent = parents.get(rowIndex);
            switch (columnIndex) {
                case 0: return parent.getName();
                case 1: return parent.getAge();
                case 2: return parent.getSex();
                case 3: return parent.toString().contains("радостный") ? "радостный" :
                        parent.toString().contains("удовлетворенный") ? "удовлетворенный" :
                                parent.toString().contains("хмурый") ? "хмурый" : "расстроенный";
                case 4:
                    StringBuilder children = new StringBuilder();
                    for (Student child : Arrays.asList(students.get(0), students.get(1))) { // Упрощенная версия
                        if (parent.toString().contains(child.getName())) {
                            children.append(child.getName()).append(", ");
                        }
                    }
                    if (children.length() > 0) children.setLength(children.length() - 2);
                    return children.toString();
                case 5: return parent.toString().contains("10000") ? "10000 руб." :
                        parent.toString().contains("5000") ? "5000 руб." : "0 руб.";
                default: return null;
            }
        }
    }

    class SubjectTableModel extends AbstractTableModel {
        private final List<Subject> subjects;
        private final String[] columnNames = {"Название", "Преподаватель"};

        public SubjectTableModel(List<Subject> subjects) {
            this.subjects = subjects;
        }

        @Override
        public int getRowCount() {
            return subjects.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Subject subject = subjects.get(rowIndex);
            switch (columnIndex) {
                case 0: return subject.getName();
                case 1: return subject.getTeacher() != null ? subject.getTeacher().getName() : "Нет";
                default: return null;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            UniversityGUI gui = new UniversityGUI();
            gui.setVisible(true);
        });
    }
}
