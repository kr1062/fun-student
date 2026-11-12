package com.example.student.ui.controller;

import com.example.student.dto.StudentFullResponse;
import com.example.student.dto.StudentResponseForList;
import com.example.student.dto.StudentResponseList;
import com.example.student.service.StudentService;
import com.example.student.ui.util.Toast;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MainController {

    @FXML private TableView<StudentFullResponse> studentTable;
    @FXML private TableColumn<StudentFullResponse, Long> idColumn;
    @FXML private TableColumn<StudentFullResponse, String> nameColumn;
    @FXML private TableColumn<StudentFullResponse, String> surnameColumn;
    @FXML private TableColumn<StudentFullResponse, String> phoneColumn;
    @FXML private TableColumn<StudentFullResponse, String> emailColumn;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button usersBtn;
    @FXML private TextField searchField;
    @FXML private Label totalStudentsLabel;
    @FXML private Label roleLabel;

    @Autowired private StudentService studentService;
    @Autowired private ApplicationContext context;

    private ObservableList<StudentFullResponse> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        setupSearch();
        checkUserRole();
        loadStudents();
    }

    private void setupSearch() {
        FilteredList<StudentFullResponse> filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (student.getName() != null && student.getName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (student.getSurname() != null && student.getSurname().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        SortedList<StudentFullResponse> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);
    }

    private void checkUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            List<String> roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String currentRole = roles.stream().findFirst().orElse("UNKNOWN").replace("ROLE_", "");
            roleLabel.setText(currentRole);

            if (roles.contains("ROLE_TEACHER") && !roles.contains("ROLE_ADMIN")) {
                addBtn.setVisible(false);
                editBtn.setVisible(false);
                deleteBtn.setVisible(false);
            }

            usersBtn.setVisible(roles.contains("ROLE_SUPER_ADMIN"));
        }
    }

    @FXML
    public void loadStudents() {
        try {
            StudentResponseList responseList = studentService.findAll();
            masterData.clear();
            for (StudentResponseForList s : responseList.getResponse()) {
                masterData.add(studentService.findById(s.getId()));
            }
            totalStudentsLabel.setText(String.valueOf(masterData.size()));
        } catch (Exception e) {
            Toast.show(studentTable.getScene(), "Ошибка загрузки: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleManageUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/users.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(studentTable.getScene().getWindow());
            stage.setTitle("Управление пользователями");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAdd() {
        openStudentDialog(null);
    }

    @FXML
    public void handleEdit() {
        StudentFullResponse selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Toast.show(studentTable.getScene(), "Выберите студента для редактирования", true);
            return;
        }
        openStudentDialog(selected);
    }

    @FXML
    public void handleDelete() {
        StudentFullResponse selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Toast.show(studentTable.getScene(), "Выберите студента для удаления", true);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText(null);
        confirm.setContentText("Удалить студента " + selected.getName() + " " + selected.getSurname() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                studentService.deleteById(selected.getId());
                loadStudents();
                Toast.show(studentTable.getScene(), "Студент успешно удален", false);
            } catch (Exception e) {
                Toast.show(studentTable.getScene(), "Ошибка удаления", true);
            }
        }
    }

    private void openStudentDialog(StudentFullResponse student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student_dialog.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            StudentDialogController controller = loader.getController();
            controller.setStudent(student, this::loadStudents);

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(studentTable.getScene().getWindow());
            stage.setTitle(student == null ? "Добавить студента" : "Редактировать студента");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        SecurityContextHolder.clearContext();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) studentTable.getScene().getWindow();
            stage.setScene(new Scene(root, 450, 500));
            stage.setTitle("Student Management System");
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}