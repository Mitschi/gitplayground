package com.github.mitschi;

import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class LogWindow {

    private Stage dialog;
    private TextFlow textArea;
    private Label lblPath;
    private Label lblStep;
    private TextField txtPath;
    private TextField txtStep;
    private Pane pane;
    private Scene scene;

    public LogWindow(Stage primaryStage){
        dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Log");

        dialog.setMinHeight(600);
        dialog.setMaxHeight(600);
        dialog.setMinWidth(800);
        dialog.setMaxWidth(800);

        dialog.setResizable(false);
        dialog.setMaximized(false);

    }

    public void showDialog(String filePath, String step){

        lblPath = new Label("Path:");
        lblStep= new Label("Step:");
        textArea= new TextFlow();
        Text text1 = new Text("Hello There!");
        text1.setFill(Color.BLUE);
        textArea.setStyle("-fx-border-color: lightgray");
        textArea.getChildren().add(text1);
        txtPath = new TextField(filePath);
        txtStep= new TextField(step);

        lblPath.setPrefSize(30,20);
        lblPath.setTranslateX(20);
        lblPath.setTranslateY(10);
        txtPath.setPrefSize(710,20);
        txtPath.setTranslateX(70);
        txtPath.setTranslateY(10);
        txtPath.setDisable(true);

        lblStep.setPrefSize(30,20);
        lblStep.setTranslateX(20);
        lblStep.setTranslateY(50);
        txtStep.setPrefSize(710,20);
        txtStep.setTranslateX(70);
        txtStep.setTranslateY(50);
        txtStep.setDisable(true);

        textArea.setPrefSize(760,490);
        textArea.setTranslateX(20);
        textArea.setTranslateY(90);
        textArea.setDisable(true);

        pane = new Pane();
        pane.setMinSize(800,600);
        pane.getChildren().addAll(lblPath, txtPath ,lblStep, txtStep,textArea);

        scene = new Scene(pane);

        dialog.setScene(scene);
        dialog.show();

    }
}
