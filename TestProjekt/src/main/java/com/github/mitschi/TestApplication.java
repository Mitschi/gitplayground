package com.github.mitschi;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.loadui.testfx.GuiTest;
import org.testfx.service.query.NodeQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.loadui.testfx.Assertions.*;

import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import org.testfx.service.query.NodeQuery;

import static org.junit.Assert.*;

import java.util.regex.Matcher;

public class TestApplication extends ApplicationTest {

    private Stage stage;
    int randomNumber;

    @Override
    public void start(Stage stage) throws Exception {
        Parent sceneRoot = FXMLLoader.load(App.class.getResource("Sample.fxml"));
        stage.setScene(new Scene(sceneRoot));
        stage.show();
        stage.toFront();
        this.stage = stage;
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new MouseButton[]{});
    }

    @Test
    public void testAll() {
        clickOn("#textFieldRevision");
        write("ab2134ef45d");

        clickOn("Insert Dependency");
        clickOn("Start");

        clickOn("#detailsTab");
        clickOn("BuildMedic");
        doubleClickOn("#textFieldPath");
        write("Test");
        clickOn("Start");

    }

    @Test
    public void testPathOtherDatatyp() {
        doubleClickOn("#textFieldPath");
        write("Other typ");
        clickOn("Start");

        Label label = (Label) stage.getScene().lookup("#lblPath");
        assertThat(label.getTextFill().toString(), CoreMatchers.is("0xff0000ff"));
    }

    @Test
    public void testPathNoPath() {
        clickOn("#textFieldPath");

        TextField text = (TextField) stage.getScene().lookup("#textFieldPath");
        eraseText(text.getLength());

        clickOn("Start");

        Label label = (Label) stage.getScene().lookup("#lblPath");
        assertThat(label.getTextFill().toString(), CoreMatchers.is("0xff0000ff"));
    }

    @Test
    public void testStepChangeNumber() {
        setRandomNumber();

        clickOn("#choiceBox");
        ChoiceBox choiceBox = (ChoiceBox) stage.getScene().lookup("#choiceBox");
        choiceBox.setValue(randomNumber);

        clickOn("#delete");
        clickOn("Start");

        assertEquals(randomNumber, App.maxSteps);

    }

    public int setRandomNumber() {
        int controlVar = randomNumber;
        randomNumber = (int) (Math.random() * 20 + 1);

        while (controlVar == randomNumber) {

            if (controlVar != randomNumber) {
                return randomNumber;
            }
        }

        return randomNumber;
    }

    @Test
    public void testStrategyNothingChosen() {
        clickOn("Start");

        Label label = (Label) stage.getScene().lookup("#lblStrategy");
        assertThat(label.getTextFill().toString(), CoreMatchers.is("0xff0000ff"));
    }

    //@Test
    //public void testStrategyChooseDelete(){
    //   clickOn("#delete");
    //}

    @Test
    public void testLogFileOtherDatatyp() {
        doubleClickOn("#textFieldLog");
        write("Other typ");
        clickOn("Start");

        Label label = (Label) stage.getScene().lookup("#lblLog");
        assertThat(label.getTextFill().toString(), CoreMatchers.is("0xff0000ff"));


    }
}
