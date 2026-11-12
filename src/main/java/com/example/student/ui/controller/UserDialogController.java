package com.example.student.ui.controller;

import com.example.student.dto.user.UserRequest;
import com.example.student.model.Role;
import com.example.student.service.UserService;
import com.example.student.ui.util.Toast;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserDialogController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Label errorLabel;

    @Autowired private UserService userService;

    private Runnable onSaveCallback;

    public void setOnSaveCallback(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    @FXML
    public void handleSave() {
        try {
            if (emailField.getText().isEmpty() || passwordField.getText().isEmpty() || roleComboBox.getValue() == null) {
                errorLabel.setText("Заполните все поля!");
                errorLabel.setVisible(true);
                return;
            }

            UserRequest request = new UserRequest();
            request.setEmail(emailField.getText());
            request.setPassword(passwordField.getText());
            request.setRoles(Set.of(roleComboBox.getValue()));

            userService.save(request);

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            closeStage();
            Toast.show(emailField.getScene(), "Пользователь создан!", false);
        } catch (Exception e) {
            errorLabel.setText("Ошибка: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}