package se.ics.lu.controllers;

import javafx.beans.property.SimpleStringProperty;
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
import se.ics.lu.data.StudentDao;
import se.ics.lu.data.StudyDao;
import se.ics.lu.data.CourseDao;
import se.ics.lu.models.Course;
import se.ics.lu.models.Student;
import se.ics.lu.models.Study;

import java.io.IOException;
import java.util.List;

public class StudiesViewController {

    @FXML
    private TableView<Study> tableViewStudies;

    @FXML
    private TableColumn<Study, String> columnStudiesStudentPersonalNumber;

    @FXML
    private TableColumn<Study, String> columnStudiesStudentName;

    @FXML
    private TableColumn<Study, String> columnStudiesGrade;

    @FXML
    private TextField textFieldStudiesStudentPersonalNumber;

    @FXML
    private TextField textFieldStudiesGrade;

    @FXML
    private Button btnStudiesAdd;

    @FXML
    private Button btnStudiesDelete;

    @FXML
    private Button btnStudiesUpdate;

    @FXML
    private Button btnStudiesCourses;

    @FXML
    private Label labelErrorMessage;

    @FXML
    private Label labelStudiesCourseCode;

    private StudyDao studyDao;

    private Course course;

    public StudiesViewController() {
        try {
            this.studyDao = new StudyDao();
        } catch (IOException e) {
            displayErrorMessage("Error initializing database connection: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {

        columnStudiesStudentPersonalNumber.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStudent().getStudentPersonalNumber()));
        columnStudiesStudentName.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStudent().getName()));
        columnStudiesGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        tableViewStudies.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadStudyDetails(newValue));
    }

    @FXML
    private void btnStudiesAdd_OnClick(MouseEvent event) {
        clearErrorMessage();

        try {
            String studentPersonalNumber = textFieldStudiesStudentPersonalNumber.getText();
            String grade = textFieldStudiesGrade.getText();

            StudentDao studentDao = new StudentDao();
            Student student = studentDao.getStudentByNumber(studentPersonalNumber);
            if(student == null) {
                displayErrorMessage("Student not found");
                return;
            }
            Study study = new Study(student, this.course, grade);
            studyDao.save(study);

            loadStudies();
            clearTextFields();
        } catch (DaoException e) {
            displayErrorMessage("Error adding study: " + e.getMessage());
        } catch (IOException e) {
            displayErrorMessage("Error adding study: " + e.getMessage());
        }
    }

    @FXML
    private void btnStudiesDelete_OnClick(MouseEvent event) {
        clearErrorMessage();

        try {
            Study study = tableViewStudies.getSelectionModel().getSelectedItem();
            if(study == null) {
                displayErrorMessage("No student selected");
                return;
            }
            studyDao.delete(study.getStudent().getStudentPersonalNumber(), study.getCourse().getCourseCode());
            loadStudies();
        } catch (DaoException e) {
            displayErrorMessage("Error removing student: " + e.getMessage());
        }
    }

    @FXML
    private void btnStudiesUpdate_OnClick(MouseEvent even) {
        clearErrorMessage();

        try {
            Study study = tableViewStudies.getSelectionModel().getSelectedItem();

            if(study == null) {
                displayErrorMessage("No student selected");
                return;
            }

            study.setGrade(textFieldStudiesGrade.getText());

            studyDao.update(study);

            tableViewStudies.refresh();


            loadStudies();
            clearTextFields();
            textFieldStudiesStudentPersonalNumber.setEditable(true);
        } catch (DaoException e) {
            displayErrorMessage("Error updating study: " + e.getMessage());
        }
    }

    @FXML
    private void btnStudiesCourses_OnClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/ics/lu/fxml/CoursesView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tableViewStudies.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Courses");
            stage.show();
        } catch (IOException e) {
            displayErrorMessage("Error opening courses view: " + e.getMessage());
        }
    }

    private void loadStudies() {
        clearErrorMessage();
        try {
            List<Study> studies = studyDao.getStudiesByCourse(course.getCourseCode());
            ObservableList<Study> observableStudies = FXCollections.observableArrayList(studies);
            tableViewStudies.setItems(observableStudies);
            textFieldStudiesStudentPersonalNumber.setEditable(true);
            labelStudiesCourseCode.setText("Students studying " + course.getCourseCode() + ":");
        } catch (DaoException e) {
            e.printStackTrace();
            displayErrorMessage("Error loading studies: " + e.getMessage());   
        }
    }

    private void loadStudyDetails(Study study) {
        if (study != null) {
            textFieldStudiesStudentPersonalNumber.setText(study.getStudent().getStudentPersonalNumber());
            textFieldStudiesGrade.setText(study.getGrade());

            textFieldStudiesStudentPersonalNumber.setEditable(false);
        }
    }

    private void clearTextFields(){
        textFieldStudiesStudentPersonalNumber.clear();
        textFieldStudiesGrade.clear();
    }

    private void displayErrorMessage(String message) {
        labelErrorMessage.setText(message);
        labelErrorMessage.setStyle("-fx-text-fill: red");
    }

    private void clearErrorMessage() {
        labelErrorMessage.setText("");
    }

    public void setCourse(Course course) {
        this.course = course;
        loadStudies();
    }

}
