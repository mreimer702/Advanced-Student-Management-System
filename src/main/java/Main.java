import java.io.*;
import java.sql.*;
import java.util.*;

public class Main {
    //Global variables
    static Scanner scanner = new Scanner(System.in);
    private static final String TEXT_FILE = "students.txt";
    public static List <String> students = new ArrayList<>();

    public static void main(String[] args) {

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {

            String createStudentTableSQL = "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), age INT, email VARCHAR(200), grade INT)";
            stmt.executeUpdate(createStudentTableSQL);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        File file = new File("students.txt");

        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            }
            else{
                System.out.println("Student File Ready for Updates");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Menu for SMS
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("Student Management System Program: ");

            System.out.println("1. Add a Student");
            System.out.println("2. View Student by ID");
            System.out.println("3. View every Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");

            System.out.println("Input the number of your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    getStudentById();
                    break;
                case 3:
                    getStudents();
                    break;
                case 4:
                    updateStudent();
                    break;
                case 5:
                    deleteStudent();
                    break;
                case 6:
                    System.out.println("Exiting the Student Management System");
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid input. Please input a valid choice");
            }

        }
    }

    // Functions for the SMS
    public static void addStudent() {
        System.out.println("Enter student's name: ");
        String nameInput = scanner.nextLine().trim();
        while (nameInput.isEmpty()) {
            System.out.println("Name cannot be empty. Please enter a student name:");
            nameInput = scanner.nextLine().trim();
        }

        int ageInput = getValidatedIntInput("Enter student's age: ");
        int gradeInput = getValidatedIntInput("Enter student's grade: ");

        System.out.println("Enter student's email: ");
        String emailInput = scanner.nextLine().trim();
        while (!emailInput.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("Please enter a valid student email:");
            emailInput = scanner.nextLine().trim();
        }

        int generatedId = -1;
        try (PreparedStatement insertStudent = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO users (name, age, email, grade) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            insertStudent.setString(1, nameInput);
            insertStudent.setInt(2, ageInput);
            insertStudent.setString(3, emailInput);
            insertStudent.setInt(4, gradeInput);

            insertStudent.executeUpdate();

            ResultSet keys = insertStudent.getGeneratedKeys();
            if (keys.next()) {
                generatedId = keys.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
            return;
        }

        try (FileWriter fw = new FileWriter(TEXT_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(generatedId + "," + nameInput + "," + ageInput + "," + emailInput + "," + gradeInput);
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + e.getMessage());
        }

        Student newStudent = new Student(nameInput, ageInput, emailInput, gradeInput);
        System.out.println(newStudent.getName() + " added successfully.");
    }

    // Helper method for validated numeric input
    private static int getValidatedIntInput(String prompt) {
        int input = 0;
        while (input <= 0) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                scanner.nextLine();
                if (input <= 0) {
                    System.out.println("Value must be greater than 0.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
        return input;
    }

    public static void getStudents() {
        students.clear();

        System.out.println("===== Students from Database =====");

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            String query = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(query);

            boolean hasStudents = false;

            while (rs.next()) {
                hasStudents = true;
                String s = rs.getInt("id") + ". " +
                        rs.getString("name") + " | " +
                        rs.getInt("age") + " | " +
                        rs.getString("email") + " | " +
                        rs.getInt("grade");
                System.out.println(s);
                students.add(s);
            }

            if (!hasStudents) {
                System.out.println("There are no students currently listed in the database.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        System.out.println("\n===== Students from File =====");

        try (BufferedReader br = new BufferedReader(new FileReader(TEXT_FILE))) {
            String line;
            boolean hasLines = false;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String s = parts[0] +
                            parts[1] +
                            parts[2] +
                            parts[3] +
                            parts[4];
                    System.out.println(s);
                    students.add(s);
                    hasLines = true;
                } else {
                    System.out.println("Malformed line in file: " + line);
                }
            }

            if (!hasLines) {
                System.out.println("There are no students currently listed in the file.");
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }


    public static void getStudentById() {
        getStudents();
        System.out.println("Please enter the ID of the student you want to view: ");
        int idInput = scanner.nextInt();
        scanner.nextLine();

        String query = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, idInput);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Grade: " + rs.getInt("grade"));
            } else {
                System.out.println("No student with a matching ID found in the database. Checking file...");
                getStudentByIdFromFile(idInput);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getStudentByIdFromFile(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] student = line.split(",");
                if (student.length != 5) continue;

                int studentId = Integer.parseInt(student[0]);
                if (studentId == id) {
                    String name = student[1];
                    int age = Integer.parseInt(student[2]);
                    String email = student[3];
                    int grade = Integer.parseInt(student[4]);

                    System.out.println(studentId + ". " + name);
                    System.out.println("Age: " + age);
                    System.out.println("Email: " + email);
                    System.out.println("Grade: " + grade);
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("No student found with ID " + id + " in file.");
            }

        } catch (IOException e) {
            System.out.println("Error reading student file: " + e.getMessage());
        }
    }

    public static void updateStudent() {
        getStudents();
        System.out.println("Enter the ID of the student to update:");
        int idInput = scanner.nextInt();
        scanner.nextLine();

        String query = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, idInput);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Current student data:");
                System.out.println(rs.getInt("id") + ". " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Grade: " + rs.getInt("grade"));

                System.out.println("Enter new name (leave blank to keep current):");
                String name = scanner.nextLine();
                if (name.isEmpty()) name = rs.getString("name");

                System.out.println("Enter new age (or 0 to keep current):");
                int age = scanner.nextInt();
                scanner.nextLine();
                if (age <= 0) age = rs.getInt("age");

                System.out.println("Enter new email (leave blank to keep current):");
                String email = scanner.nextLine();
                if (email.isEmpty()) email = rs.getString("email");

                System.out.println("Enter new grade (or -1 to keep current):");
                int grade = scanner.nextInt();
                scanner.nextLine();
                if (grade < 0) grade = rs.getInt("grade");

                String updateQuery = "UPDATE users SET name = ?, age = ?, email = ?, grade = ? WHERE id = ?";
                try (PreparedStatement updateStmt = DatabaseConnection.getConnection().prepareStatement(updateQuery)) {
                    updateStmt.setString(1, name);
                    updateStmt.setInt(2, age);
                    updateStmt.setString(3, email);
                    updateStmt.setInt(4, grade);
                    updateStmt.setInt(5, idInput);

                    updateStmt.executeUpdate();
                    syncStudentToFile(idInput, name, age, email, grade);
                    System.out.println("Student updated successfully.");
                }

            } else {
                System.out.println("No student found in database. Trying file...");
                updateStudentInFile(idInput);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateStudentInFile(int id) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 5) {
                    lines.add(line);
                    continue;
                }

                int studentId = Integer.parseInt(parts[0]);
                if (studentId == id) {
                    String currentName = parts[1];
                    int currentAge = Integer.parseInt(parts[2]);
                    String currentEmail = parts[3];
                    int currentGrade = Integer.parseInt(parts[4]);

                    System.out.println("Current student data:");
                    System.out.println(studentId + ". " + currentName);
                    System.out.println("Age: " + currentAge);
                    System.out.println("Email: " + currentEmail);
                    System.out.println("Grade: " + currentGrade);

                    System.out.println("Enter new name (leave blank to keep current):");
                    String name = scanner.nextLine();
                    if (name.isEmpty()) name = currentName;

                    System.out.println("Enter new age (or 0 to keep current):");
                    int age = scanner.nextInt();
                    scanner.nextLine();
                    if (age <= 0) age = currentAge;

                    System.out.println("Enter new email (leave blank to keep current):");
                    String email = scanner.nextLine();
                    if (email.isEmpty()) email = currentEmail;

                    System.out.println("Enter new grade (or -1 to keep current):");
                    int grade = scanner.nextInt();
                    scanner.nextLine();
                    if (grade < 0) grade = currentGrade;

                    String updatedLine = studentId + "," + name + "," + age + "," + email + "," + grade;
                    lines.add(updatedLine);
                    found = true;
                } else {
                    lines.add(line);
                }
            }

            if (found) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEXT_FILE))) {
                    for (String l : lines) {
                        writer.write(l);
                        writer.newLine();
                    }
                    System.out.println("Student updated successfully in file.");
                }
            } else {
                System.out.println("No student found with ID " + id + " in file.");
            }

        } catch (IOException e) {
            System.out.println("Error updating student file: " + e.getMessage());
        }
    }

    public static void syncStudentToFile(int id, String name, int age, String email, int grade) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 5) {
                    lines.add(line);
                    continue;
                }

                int studentId = Integer.parseInt(parts[0]);
                if (studentId == id) {
                    lines.add(id + "," + name + "," + age + "," + email + "," + grade);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file during sync: " + e.getMessage());
        }

        if (!found) {
            lines.add(id + "," + name + "," + age + "," + email + "," + grade);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEXT_FILE))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
            System.out.println("Student info synced to file.");
        } catch (IOException e) {
            System.out.println("Error writing to file during sync: " + e.getMessage());
        }
    }

    public static void deleteStudent() {
        getStudents();
        System.out.println("Enter the student ID to delete:");
        int idToDelete = scanner.nextInt();
        scanner.nextLine();

        String query = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, idToDelete);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Student found:");
                System.out.println(rs.getInt("id") + ". " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Grade: " + rs.getInt("grade"));

                System.out.println("Are you sure you want to delete this student? (yes/no)");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("yes")) {
                    String deleteQuery = "DELETE FROM users WHERE id = ?";
                    try (PreparedStatement deleteStmt = DatabaseConnection.getConnection().prepareStatement(deleteQuery)) {
                        deleteStmt.setInt(1, idToDelete);
                        deleteStmt.executeUpdate();
                    }

                    System.out.println("Student deleted from database.");
                    deleteStudentFromFile(idToDelete);
                } else {
                    System.out.println("Deletion canceled.");
                }

            } else {
                System.out.println("No student found in the database with ID " + idToDelete);
                System.out.println("Would you like to delete this student from the file instead? (yes/no)");
                String fileDeleteConfirm = scanner.nextLine();
                if (fileDeleteConfirm.equalsIgnoreCase("yes")) {
                    deleteStudentFromFile(idToDelete);
                } else {
                    System.out.println("File deletion canceled.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteStudentFromFile(int idToDelete) {
        List<String> lines = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && Integer.parseInt(parts[0]) == idToDelete) {
                    deleted = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEXT_FILE))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }

        if (deleted) {
            System.out.println("Student deleted from file.");
        } else {
            System.out.println("Student was not found in file.");
        }
    }
}