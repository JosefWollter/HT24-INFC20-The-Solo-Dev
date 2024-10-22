package se.ics.lu.models;

public class Study {
    private String studentPersonalNumber;
    private String courseCode;
    private String grade;

    public Study(String studentPersonalNumber, String courseCode){
        this.studentPersonalNumber = studentPersonalNumber;
        this.courseCode = courseCode;
    }

    public Study(String studentPersonalNumber, String courseCode, String grade) {
        this.studentPersonalNumber = studentPersonalNumber;
        this.courseCode = courseCode;
        this.grade = grade;
    }

    public String getStudentPersonalNumber() {
        return studentPersonalNumber;
    }

    public void setStudentPersonalNumber(String studentPersonalNumber) {
        this.studentPersonalNumber = studentPersonalNumber;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getGrade() {
        return grade;
    }

}
