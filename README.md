# Student Manager Console App (Java, JDBC + SQL)

This is a simple **Java console application** that manages student data using a **MySQL database** and a local **text file**. It allows users to:

- Add new students with validation
- Filter students by grade
- Store information persistently
- Practice basic CRUD and file I/O operations

## 💡 Features

- Input validation for student name, age, email, and grade
- SQL-backed persistence using JDBC and PreparedStatement
- Local backup via `students.txt` file
- Automatically generates a unique student ID from the database
- Graceful error handling and user prompts

## 🛠️ Tech Stack

- Java (JDK 17+ recommended)
- JDBC (Java Database Connectivity)
- MySQL
- FileWriter / BufferedReader for text file operations

## 📁 Project Structure
<pre> 📁 <b>src/</b> 
├── 📄 <b>Person.java</b> // Person class with attributes like age and name
├── 📄 <b>Student.java</b> // Student class with attributes like grade and email
├── 📄 <b>Main.java</b> // Main logic to manage students: filtering, multithreading, etc. 
├── 📄 <b>Streams.java</b> // Class with Stream and Threading functions 
├── 📄 <b>DatabaseConnection.java</b> // Handles DB connections
</pre>

## ⚙️ Setup

1. Clone the repository
2. Set up your MySQL database and create a `users` table:

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    age INT,
    email VARCHAR(100),
    grade INT
);
```

3. Update your database credentials in DatabaseConnection.java

4. Run the project from your main() method or terminal

## 🧑 Author

Created by Martin Reimer
