package se.ics.lu.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import se.ics.lu.data.DaoException;
import se.ics.lu.data.CourseDao;
import se.ics.lu.models.Course;
import se.ics.lu.models.Student;

import java.io.IOException;
import java.util.List;

public class CoursesViewController {

    @FXML
    private TableView<Course> tableViewCourse;

    @FXML
    private TableColumn<Course, String> columnCourseCode;

    @FXML
    private TableColumn<Course, String> columnCourseName;

    @FXML
    private TableColumn<Course, Double> columnCourseCredits;

    @FXML
    private TextField textFieldCourseCode;

    @FXML
    private TextField textFieldCourseName;

    @FXML
    private TextField textFieldCourseCredits;

    @FXML
    private Button btnCourseAdd;

    @FXML
    private Button btnCourseDelete;

    @FXML
    private Button btnCourseUpdate;

    @FXML
    private Button btnCourseStudies;

    @FXML
    private Label labelErrorMessage;

    private CourseDao courseDao;

    public CoursesViewController() {

    }

    @FXML
    private void initialize() {
        try {
            courseDao = new CourseDao();
            
            columnCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
            columnCourseName.setCellValueFactory(new PropertyValueFactory<>("name"));
            columnCourseCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));

            tableViewCourse.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadCourseDetails(newValue));

            loadCourses();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            displayErrorMessage("Error initializing database connection: " + e.getMessage());
        }
    }

    private void loadCourses() {
        clearErrorMessage();
        try {
            List<Course> courseList = courseDao.getAllCourses();
            ObservableList<Course> courseObservableList = FXCollections.observableArrayList(courseList);
            tableViewCourse.setItems(courseObservableList);
            textFieldCourseCode.setEditable(true);
        } catch(DaoException e) {
            displayErrorMessage("Error loading courses: " + e.getMessage());
        }
    }

    @FXML
    private void btnCourseAdd_OnClick(MouseEvent event) {
        try{
            String courseCode = textFieldCourseCode.getText();
            String courseName = textFieldCourseName.getText();
            Double courseCredits = Double.parseDouble(textFieldCourseCredits.getText());

            Course course = new Course(courseCode, courseName, courseCredits);

            courseDao.save(course);

            loadCourses();

            textFieldCourseCode.clear();
            textFieldCourseName.clear();
            textFieldCourseCredits.clear();
        } catch (DaoException e){
            displayErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void btnCourseDelete_OnClick(MouseEvent event){
        try{
            Course course = tableViewCourse.getSelectionModel().getSelectedItem();
            courseDao.deleteByCourseCode(course.getCourseCode());
            loadCourses();
        } catch (DaoException e){
            displayErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void btnCourseUpdate_OnClick(MouseEvent event) {
        try{
            Course course = tableViewCourse.getSelectionModel().getSelectedItem();
            String newName = textFieldCourseName.getText();
            Double newCredits = Double.parseDouble(textFieldCourseCredits.getText());

            if(course == null){
                displayErrorMessage("Please select a course to update");
                return;
            }

            if(newName.isEmpty() || newCredits == 0){
                displayErrorMessage("Please fill in all fields when updating");
                return;
            }

            course.setName(newName);
            course.setCredits(newCredits);

            courseDao.update(course);

            tableViewCourse.refresh();

            loadCourses();
            clearTextFields();
            textFieldCourseCode.setEditable(true);
        } catch (DaoException e){
            displayErrorMessage(e.getMessage());
        }
    }

    private void loadCourseDetails(Course course){
        if(course != null){
            textFieldCourseCode.setText(course.getCourseCode());
            textFieldCourseName.setText(course.getName());
            textFieldCourseCredits.setText(Double.toString(course.getCredits()));

            textFieldCourseCode.setEditable(false);
        }
    }

    private void displayErrorMessage(String message) {
        labelErrorMessage.setText(message);
        labelErrorMessage.setStyle("-fx-text-fill: red");
    }

    private void clearErrorMessage() {
        labelErrorMessage.setText("");
    }

    private void clearTextFields() {
        textFieldCourseCode.clear();
        textFieldCourseName.clear();
        textFieldCourseCredits.clear();
    }

    public void btnCourseStudies_OnClick(MouseEvent event) {
        try {
            if(tableViewCourse.getSelectionModel().getSelectedItem() == null) {
                displayErrorMessage("Please select a course to view studies");
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/ics/lu/fxml/StudiesView.fxml"));
            Parent root = loader.load();

            StudiesViewController studiesViewController = loader.getController();
            studiesViewController.setCourse(tableViewCourse.getSelectionModel().getSelectedItem());

            Stage stage = (Stage) tableViewCourse.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Studies");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            displayErrorMessage("Error loading Studies: " + e.getMessage());
        }
    }

    public void menuStudents_OnClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/ics/lu/fxml/StudentsView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tableViewCourse.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Students");
            stage.show();
        } catch (IOException e) {
            displayErrorMessage("Error loading Students: " + e.getMessage());
        }
    }

}
