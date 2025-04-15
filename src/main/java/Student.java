public class Student extends Person{
    private int grade;

    public Student(String name, int age, String email, int grade) {
        super(name, age, email);
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

}
