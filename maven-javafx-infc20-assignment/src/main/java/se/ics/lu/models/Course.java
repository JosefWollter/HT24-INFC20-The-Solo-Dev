package se.ics.lu.models;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseCode;
    private String name;
    private double credits;
    private List<Study> studies = new ArrayList<>();

    public Course(String courseCode, String name, double credits) {
        this.courseCode = courseCode;
        this.name = name;
        this.credits = credits;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public List<Study> getStudies() {
        return studies;
    }
}
