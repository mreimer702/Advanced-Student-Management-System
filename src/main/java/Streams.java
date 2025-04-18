import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Streams {
    static Scanner scanner = new Scanner(System.in);

    public static void filterByGrade() {
        System.out.println("Enter a grade to view students from that grade:");
        int gradeInput = scanner.nextInt();
        scanner.nextLine();

        List<Student> results = Main.students.stream()
                .filter(student -> student.getGrade() == gradeInput)
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No students found in grade " + gradeInput + ".");
        } else {
            System.out.println("Students in grade " + gradeInput + ":");
            results.forEach(student ->
                    System.out.println("Name: " + student.getName() + " , Age: " + student.getAge() + " , Email: " + student.getEmail())
            );

        }
    }

    public static void countByGrade() {
        System.out.println("Enter a grade to see how many students are in that grade:");
        int gradeInput = scanner.nextInt();
        scanner.nextLine();

        long result = Main.students.stream()
                .filter(student -> student.getGrade() == gradeInput)
                .count();

        if (result <= 0) {
            System.out.println("No students found in grade " + gradeInput + ".");
        } else {
            System.out.println("There are " + result + " students in grade " + gradeInput + ".");
        }
    }

    public static void sortAlphabeticallyByName() {
        Stream<Student> result = Main.students.stream()
                        .sorted(Comparator.comparing(Person::getName));

        System.out.println("Students sorted alphabetically by name:");
        result.forEach(student ->
                System.out.println(student.getName())
        );
    }

    public static void threadsExample() {
        int size = Main.students.size();
        int mid = size / 2;

        Runnable task1 = () -> {
            for (int i = 0; i < mid; i++) {
                Student student = Main.students.get(i);
                System.out.println("[Thread 1] " + student.getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.err.println("Thread 1 interrupted.");
                }
            }
        };

        Runnable task2 = () -> {
            for (int i = mid; i < size; i++) {
                Student student = Main.students.get(i);
                System.out.println("[Thread 2] " + student.getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.err.println("Thread 2 interrupted.");
                }
            }
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.err.println("Main thread interrupted.");
        }

        System.out.println("Both threads have finished processing students.");
    }
}

