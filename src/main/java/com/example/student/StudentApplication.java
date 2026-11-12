package com.example.student;

import com.example.student.ui.JavaFxApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentApplication {

	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}

}