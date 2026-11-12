package com.example.student.ui.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.util.Duration;

public class Toast {
    public static void show(Scene scene, String message, boolean isError) {
        Popup popup = new Popup();
        Label label = new Label(message);
        label.getStyleClass().add("toast");
        if (isError) label.getStyleClass().add("toast-error");

        StackPane pane = new StackPane(label);
        pane.setStyle("-fx-padding: 20;");
        popup.getContent().add(pane);

        double x = scene.getWindow().getX() + scene.getWidth() / 2 - 100;
        double y = scene.getWindow().getY() + scene.getHeight() - 100;
        popup.show(scene.getWindow(), x, y);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2.5), new KeyValue(popup.opacityProperty(), 0))
        );
        timeline.setOnFinished(e -> popup.hide());
        timeline.play();
    }
}