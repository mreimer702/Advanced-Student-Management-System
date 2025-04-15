import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Main {

    //Global variables
    static Scanner scanner = new Scanner(System.in);
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
        String nameInput = scanner.nextLine();
        while (nameInput.trim().isEmpty()) {
            System.out.println("Please enter a student name:");
            nameInput = scanner.nextLine();
        }

        int ageInput = 0;
        while (ageInput <= 0) {
            System.out.println("Enter student's age: ");
            if (scanner.hasNextInt()) {
                ageInput = scanner.nextInt();
                scanner.nextLine();
                if (ageInput <= 0) {
                    System.out.println("Age must be greater than 0.");
                }
            } else {
                System.out.println("Invalid input. Please enter a numeric age.");
                scanner.nextLine();
            }
        }

        System.out.println("Enter student's email: ");
        String emailInput = scanner.nextLine();
        while (emailInput.trim().isEmpty()) {
            System.out.println("Please enter a valid student email:");
            emailInput = scanner.nextLine();
        }

        int gradeInput = 0;
        while (gradeInput <= 0) {
            System.out.println("Enter student's grade: ");
            if (scanner.hasNextInt()) {
                gradeInput = scanner.nextInt();
                scanner.nextLine();
                if (gradeInput <= 0) {
                    System.out.println("Grade must be greater than 0.");
                }
            } else {
                System.out.println("Invalid input. Please enter a numeric grade.");
                scanner.nextLine();
            }
        }

        try (PreparedStatement insertStudent = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO users (name, age, email, grade) VALUES (?, ?, ?, ?)")) {

            insertStudent.setString(1, nameInput);
            insertStudent.setInt(2, ageInput);
            insertStudent.setString(3, emailInput);
            insertStudent.setInt(4, gradeInput);

            insertStudent.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
            return;
        }

        Student newStudent = new Student(nameInput, ageInput, emailInput, gradeInput);
        System.out.println(newStudent.getName() + " added successfully.");
    }


    public static void getStudents() {
        System.out.println("List of Students By ID");

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            String query = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(query);

            boolean hasStudents = false;

            while (rs.next()) {
                hasStudents = true;
                System.out.println(rs.getInt("id") + ". " + rs.getString("name"));
            }

            if (!hasStudents) {
                System.out.println("There are no students currently listed");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                System.out.println("No student found with ID " + idInput);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateStudent() {
        getStudents();
        System.out.println("Enter the ID of the student to update:");
        int id = scanner.nextInt();
        scanner.nextLine();

        String query = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Current student data:");
                System.out.println(rs.getInt("id") + ". " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Grade: " + rs.getInt("grade"));

                // Prompt for updates
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

                // Perform update in database
                String updateQuery = "UPDATE users SET name = ?, age = ?, email = ?, grade = ? WHERE id = ?";
                try (PreparedStatement updateStmt = DatabaseConnection.getConnection().prepareStatement(updateQuery)) {
                    updateStmt.setString(1, name);
                    updateStmt.setInt(2, age);
                    updateStmt.setString(3, email);
                    updateStmt.setInt(4, grade);
                    updateStmt.setInt(5, id);

                    updateStmt.executeUpdate();
                    System.out.println("Student updated successfully.");
                }

            } else {
                System.out.println("No student found with ID " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
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

                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Deletion canceled.");
            }

        } else {
            System.out.println("No student found with ID " + idToDelete);
        }

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

}