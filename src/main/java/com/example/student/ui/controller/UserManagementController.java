package com.example.student.ui.controller;

import com.example.student.dto.user.UserResponse;
import com.example.student.dto.user.UserResponseList;
import com.example.student.service.UserService;
import com.example.student.ui.util.Toast;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserManagementController {

    @FXML private TableView<UserResponse> userTable;
    @FXML private TableColumn<UserResponse, Long> userIdCol;
    @FXML private TableColumn<UserResponse, String> userEmailCol;
    @FXML private TableColumn<UserResponse, String> userRolesCol;

    @Autowired private UserService userService;
    @Autowired private ApplicationContext context;

    @FXML
    public void initialize() {
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRolesCol.setCellValueFactory(new PropertyValueFactory<>("roles"));
        loadUsers();
    }

    @FXML
    public void loadUsers() {
        try {
            UserResponseList responseList = userService.findAll();
            ObservableList<UserResponse> users = FXCollections.observableArrayList(responseList.getResponses());
            userTable.setItems(users);
        } catch (Exception e) {
            Toast.show(userTable.getScene(), "Ошибка загрузки пользователей", true);
        }
    }

    @FXML
    public void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_dialog.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            UserDialogController controller = loader.getController();
            controller.setOnSaveCallback(this::loadUsers);

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(userTable.getScene().getWindow());
            stage.setTitle("Создать пользователя");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}