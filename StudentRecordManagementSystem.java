
import java.io.*;
import java.util.*;

class Student implements Serializable {
    private String id;
    private String name;
    private String department;
    private double cgpa;

    public Student(String id, String name, String department, double cgpa) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.cgpa = cgpa;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getCgpa() { return cgpa; }

    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Dept: %s | CGPA: %.2f", id, name, department, cgpa);
    }

    public String serialize() {
        return String.join("|", Arrays.asList(id, name, department, String.valueOf(cgpa)));
    }

    public static Student deserialize(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 4) return null;
        return new Student(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
    }
}

class StudentRecordManager {
    private List<Student> students = new ArrayList<>();
    private final String DATA_FILE = "students.txt";

    public StudentRecordManager() {
        loadFromFile();
    }

    public void addStudent(Student s) {
        students.add(s);
        saveToFile();
    }

    public List<Student> getAll() {
        return students;
    }

    public Student findById(String id) {
        return students.stream()
                .filter(s -> s.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public void updateStudent(String id, String name, String dept, double cgpa) {
        Student s = findById(id);
        if (s != null) {
            s.setName(name);
            s.setDepartment(dept);
            s.setCgpa(cgpa);
            saveToFile();
        }
    }

    public boolean deleteStudent(String id) {
        boolean removed = students.removeIf(s -> s.getId().equalsIgnoreCase(id));
        if (removed) saveToFile();
        return removed;
    }

    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Student s = Student.deserialize(line);
                if (s != null) students.add(s);
            }
        } catch (IOException e) {
            System.out.println("Error reading data file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Student s : students) {
                bw.write(s.serialize());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing data file: " + e.getMessage());
        }
    }
}

public class StudentRecordManagementSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static StudentRecordManager manager = new StudentRecordManager();

    public static void main(String[] args) {
        int choice;
        do {
            printMenu();
            while (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid number.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            switch (choice) {
                case 1: add(); break;
                case 2: viewAll(); break;
                case 3: search(); break;
                case 4: update(); break;
                case 5: delete(); break;
                case 6: System.out.println("Exiting... Goodbye!"); break;
                default: System.out.println("Invalid option. Try again.");
            }
        } while (choice != 6);
    }

    private static void printMenu() {
        System.out.println("\n===== Student Record Management System =====");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student by ID");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    private static void add() {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Department: ");
        String dept = scanner.nextLine();
        System.out.print("Enter CGPA: ");
        double cgpa = scanner.nextDouble();
        scanner.nextLine();

        manager.addStudent(new Student(id, name, dept, cgpa));
        System.out.println("Student added.");
    }

    private static void viewAll() {
        List<Student> list = manager.getAll();
        if (list.isEmpty()) {
            System.out.println("No records found.");
        } else {
            list.forEach(System.out::println);
        }
    }

    private static void search() {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();
        Student s = manager.findById(id);
        if (s == null) {
            System.out.println("Student not found.");
        } else {
            System.out.println(s);
        }
    }

    private static void update() {
        System.out.print("Enter Student ID to update: ");
        String id = scanner.nextLine();
        Student s = manager.findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Enter new Name [" + s.getName() + "]: ");
        String name = scanner.nextLine();
        if (name.isBlank()) name = s.getName();

        System.out.print("Enter new Department [" + s.getDepartment() + "]: ");
        String dept = scanner.nextLine();
        if (dept.isBlank()) dept = s.getDepartment();

        System.out.print("Enter new CGPA [" + s.getCgpa() + "]: ");
        String cgpaInput = scanner.nextLine();
        double cgpa = cgpaInput.isBlank() ? s.getCgpa() : Double.parseDouble(cgpaInput);

        manager.updateStudent(id, name, dept, cgpa);
        System.out.println("Student updated.");
    }

    private static void delete() {
        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine();
        boolean success = manager.deleteStudent(id);
        if (success) {
            System.out.println("Student deleted.");
        } else {
            System.out.println("Student not found.");
        }
    }
}
