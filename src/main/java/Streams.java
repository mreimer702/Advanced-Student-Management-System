import java.util.*;
import java.util.stream.Collectors;

public class Streams {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main.getStudents();
        filterByGrade();
        countByGrade();
        sortAlphabeticallyByName();
        ThreadsExample();
    }

    public static void filterByGrade() {
        System.out.println("Enter a grade to view students from that grade:");
        int gradeInput = scanner.nextInt();
        scanner.nextLine();

        List<String> result = Main.students.stream()
                .filter(s -> {
                    String[] parts = s.split(", ");
                    for (String part : parts) {
                        if (part.startsWith("Grade:")) {
                            try {
                                int grade = Integer.parseInt(part.replace("Grade:", "").trim());
                                return grade == gradeInput;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            System.out.println("No students found in grade " + gradeInput + ".");
        } else {
            System.out.println("Students in grade " + gradeInput + ":");
            result.forEach(System.out::println);
        }
    }

    public static void countByGrade() {
        System.out.println("Enter a grade to see how many students are in that grade:");
        int gradeInput = scanner.nextInt();
        scanner.nextLine();

        long result = Main.students.stream()
                .filter(s -> {
                    String[] parts = s.split(", ");
                    for (String part : parts) {
                        if (part.startsWith("Grade:")) {
                            try {
                                int grade = Integer.parseInt(part.replace("Grade:", "").trim());
                                return grade == gradeInput;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }
                    }
                    return false;
                })
                .count();

        if (result <= 0) {
            System.out.println("No students found in grade " + gradeInput + ".");
        } else {
            System.out.println("There are " + result + " in grade " + gradeInput + ".");
        }
    }

    public static void sortAlphabeticallyByName() {
        List<String> result = Main.students.stream()
                .sorted(Comparator.comparing(s -> {
                    String[] parts = s.split(", ");
                    for (String part : parts) {
                        if (part.contains("Name:")) {
                            return part.replace("Name:", "").trim().toLowerCase();
                        }
                    }
                    return "";
                }))
                .collect(Collectors.toList());

        System.out.println("Students sorted alphabetically by name:");
        result.forEach(System.out::println);
    }

    public static void ThreadsExample() {
        int size = Main.students.size();
        int mid = size / 2;

        Runnable task1 = () -> {
            for (int i = 0; i < mid; i++) {
                System.out.println("[Thread 1] " + Main.students.get(i));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.err.println("Thread 1 interrupted.");
                }
            }
        };

        Runnable task2 = () -> {
            for (int i = mid; i < size; i++) {
                System.out.println("[Thread 2] " + Main.students.get(i));
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
