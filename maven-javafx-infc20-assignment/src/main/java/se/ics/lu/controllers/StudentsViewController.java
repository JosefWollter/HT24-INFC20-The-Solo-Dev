package se.ics.lu.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
    private TextField textFieldStudentEmail;

    @FXML
    private Button btnStudentAdd;

    @FXML
    private Label labelErrorMessage;

    private StudentDao studentDao;

    public StudentsViewController() {
        // Constructor logic if any
    }

    @FXML
    private void initialize() {
        try {
            studentDao = new StudentDao();
            // Initialize table columns
            columnStudentPersonalNumber.setCellValueFactory(new PropertyValueFactory<>("personalNumber"));
            columnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
            columnStudentEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            displayErrorMessage("Error initializing database connection: " + e.getMessage());
        }
    }

    @FXML
    private void btnStudentAdd_OnClick(MouseEvent event) {
        // Event handler logic
        System.out.println("Button clicked");
    }

    public void displayErrorMessage(String message) {
        if (labelErrorMessage != null) {
            labelErrorMessage.setText(message);
        } else {
            System.out.println("labelErrorMessage is null");
        }
    }

    // other methods and fields
}