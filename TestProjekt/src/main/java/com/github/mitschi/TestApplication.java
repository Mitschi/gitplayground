package com.github.mitschi;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
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

    @Override
    public void start(Stage stage) throws Exception {
        Parent sceneRoot = FXMLLoader.load(App.class.getResource("Sample.fxml"));
        stage.setScene(new Scene(sceneRoot));
        stage.show();
        stage.toFront();
        this.stage=stage;
    }

    @Before
    public void setUp() throws Exception{

    }

    @After
    public void tearDown() throws Exception{
        FxToolkit.hideStage();
        release(new MouseButton[]{});
    }

    @Test
    public void testStartButton(){
        clickOn("#textFieldRevision");
        write("ab2134ef45d");
//        assertThat(lookup("lblStrategy").queryAs(Label));
        clickOn("Insert Dependency");
        clickOn("Start");
//        Label label = (Label) stage.getScene().lookup("#lblStrategy");
        clickOn("#detailsTab");
        clickOn("BuildMedic");
        doubleClickOn("#textFieldPath");
        write("Test");
        clickOn("Start");

    }
}
