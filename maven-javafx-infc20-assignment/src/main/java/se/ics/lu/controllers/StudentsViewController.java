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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import se.ics.lu.data.DaoException;
import se.ics.lu.data.StudentDao;
import se.ics.lu.models.Student;

import java.io.IOException;
import java.util.List;

public class StudentsViewController {
    @FXML
    private TableView<Student> tableViewStudent;

    @FXML
    private TableColumn<Student, String> columnStudentPersonalNumber;

    @FXML
    private TableColumn<Student, String> columnStudentName;

    @FXML
    private TableColumn<Student, String> columnStudentEmail;

    @FXML
    private TextField textFieldStudentPersonalNumber;

    @FXML
    private TextField textFieldStudentName;

    @FXML
    private TextField textFieldStudentEmail;

    @FXML
    private Button btnStudentAdd;

    @FXML
    private Button btnStudentDelete;

    @FXML
    private Button btnStudentUpdate;

    @FXML
    private Label labelErrorMessage;

    private StudentDao studentDao;

    public StudentsViewController() {
        try {
            studentDao = new StudentDao();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            displayErrorMessage("Error initializing database connection: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        columnStudentPersonalNumber.setCellValueFactory(new PropertyValueFactory<>("studentPersonalNumber"));
        columnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnStudentEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableViewStudent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadStudentDetails(newValue));

        loadStudents();

    }

    @FXML
    private void btnStudentAdd_OnClick(MouseEvent event) {
        try{
            String studentPersonalNumber = textFieldStudentPersonalNumber.getText();
            String studentName = textFieldStudentName.getText();
            String studentEmail = textFieldStudentEmail.getText();

            if(studentPersonalNumber.isEmpty() || studentName.isEmpty() || studentEmail.isEmpty()){
                displayErrorMessage("Please fill in all fields when adding a student");
                return;
            }

            Student student = new Student(studentPersonalNumber, studentName, studentEmail);

            studentDao.save(student);

            loadStudents();

            textFieldStudentPersonalNumber.clear();
            textFieldStudentName.clear();
            textFieldStudentEmail.clear();
        } catch (DaoException e){
            displayErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void btnStudentDelete_OnClick(MouseEvent event){
        try{
            Student student = tableViewStudent.getSelectionModel().getSelectedItem();
            if (student == null){
                displayErrorMessage("Please select a student to delete");
                return;
            }
            studentDao.deleteByNo(student.getStudentPersonalNumber());
            loadStudents();
        } catch (DaoException e){
            displayErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void btnStudentUpdate_OnClick(MouseEvent event) {
        try{
            Student student = tableViewStudent.getSelectionModel().getSelectedItem();
            String newName = textFieldStudentName.getText();
            String newEmail = textFieldStudentEmail.getText();
            if(student == null){
                displayErrorMessage("Please select a student to update");
                return;
            }
            if(newName.isEmpty() || newEmail.isEmpty()){
                displayErrorMessage("Please fill in all fields when updating");
                return;
            }

            student.setName(newName);
            student.setEmail(newEmail);

            studentDao.update(student);

            tableViewStudent.refresh();

            loadStudents();
            clearTextFields();
            textFieldStudentPersonalNumber.setEditable(true);
        } catch (DaoException e){
            displayErrorMessage(e.getMessage());
        }
    }

    private void loadStudents(){
        clearErrorMessage();
        try{
            List<Student> students = studentDao.getAllStudents();
            ObservableList<Student> studentObservableList = FXCollections.observableArrayList(students);
            tableViewStudent.setItems(studentObservableList);
            textFieldStudentPersonalNumber.setEditable(true);
        } catch (DaoException e){
            displayErrorMessage("Error loading students: " + e.getMessage());
        }
    }

    private void loadStudentDetails(Student student){
        if(student != null){
            textFieldStudentPersonalNumber.setText(student.getStudentPersonalNumber());
            textFieldStudentName.setText(student.getName());
            textFieldStudentEmail.setText(student.getEmail());

            textFieldStudentPersonalNumber.setEditable(false);
        }
    }

    public void displayErrorMessage(String message) {
        if (labelErrorMessage != null) {
            labelErrorMessage.setText(message);
            labelErrorMessage.setStyle("-fx-text-fill: red");
        } else {
            System.out.println("labelErrorMessage is null");
        }
    }

    public void clearErrorMessage(){
        labelErrorMessage.setText("");
    }

    public void clearTextFields(){
        textFieldStudentPersonalNumber.clear();
        textFieldStudentName.clear();
        textFieldStudentEmail.clear();
    }

    public void menuCourses_OnClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/ics/lu/fxml/CoursesView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tableViewStudent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Courses");
            stage.show();
        } catch (IOException e) {
            displayErrorMessage("Error loading courses: " + e.getMessage());
        }
    }
}