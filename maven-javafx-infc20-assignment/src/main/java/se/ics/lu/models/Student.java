package se.ics.lu.models;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String studentPersonalNumber;
    private String name;
    private String email;
    private List<Study> studies = new ArrayList<>();

    public Student(String studentPersonalNumber, String name, String email) {
        this.studentPersonalNumber = studentPersonalNumber;
        this.name = name;
        this.email = email;
    }

    public String getStudentPersonalNumber() {
        return studentPersonalNumber;
    }

    public void setStudentPersonalNumber(String studentPersonalNumber) {
        this.studentPersonalNumber = studentPersonalNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Study> getStudies() {
        return studies;
    }

    public void setStudies(List<Study> studies) {
        this.studies = studies;
    }

    public void addStudy(Study study) {
        studies.add(study);
    }

    public void removeStudy(Study study) {
        studies.remove(study);
    }

}
