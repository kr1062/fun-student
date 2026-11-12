package com.example.student.ui.controller;

import com.example.student.dto.StudentFullResponse;
import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentUpdateRequest;
import com.example.student.service.StudentService;
import com.example.student.ui.util.Toast;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentDialogController {

    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private Label errorLabel;

    @Autowired private StudentService studentService;

    private StudentFullResponse currentStudent;
    private Runnable onSaveCallback;

    public void setStudent(StudentFullResponse student, Runnable onSaveCallback) {
        this.currentStudent = student;
        this.onSaveCallback = onSaveCallback;

        if (student != null) {
            nameField.setText(student.getName());
            surnameField.setText(student.getSurname());
            phoneField.setText(student.getPhoneNumber());
            emailField.setText(student.getEmail());
            nameField.setDisable(true);
            surnameField.setDisable(true);
        }
    }

    @FXML
    public void handleSave() {
        try {
            if (currentStudent == null) {
                StudentRequest request = new StudentRequest();
                request.setName(nameField.getText());
                request.setSurname(surnameField.getText());
                request.setPhoneNumber(phoneField.getText());
                request.setEmail(emailField.getText());
                studentService.save(request);
            } else {
                StudentUpdateRequest request = new StudentUpdateRequest();
                request.setPhoneNumber(phoneField.getText());
                request.setEmail(emailField.getText());
                studentService.update(currentStudent.getId(), request);
            }

            onSaveCallback.run();
            closeStage();

            Toast.show(nameField.getScene(), "Данные успешно сохранены!", false);
        } catch (Exception e) {
            errorLabel.setText("Ошибка: " + e.getMessage());
            errorLabel.setVisible(true);
            Toast.show(nameField.getScene(), "Ошибка сохранения", true);
        }
    }

    @FXML
    public void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}