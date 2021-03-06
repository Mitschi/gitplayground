package com.github.mitschi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.hamcrest.CoreMatchers;
import org.junit.*;

import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;
import static org.testfx.api.FxToolkit.registerPrimaryStage;

public class ApplicationGuiTest extends ApplicationTest {
    //Warning: These tests only work when in the path a pom.xml data stands

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
//        Application app = (Application) stage.getScene().getWindow();
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new MouseButton[]{});
    }


    //Test different things together
    //WARNING: Not CORRECT or more specifically not WORKING because of changes in "App"!
//    @Test
//    public void testAll() {
//
//        //Write sth in the textField "Revision"
//        clickOn("#textFieldRevision");
//        write("ab2134ef45d");
//
//
//        clickOn("Insert Dependency"); //Choose "Insert Dependency" from the checkBoxes
//        clickOn("Start"); //Start the program
//
//
//        clickOn("#detailsTab"); //Go to the new tab "DetailsTab"
//        clickOn("BuildMedic"); //Go to the tab "BuildMedic"
//
//        doubleClickOn("#textFieldPath");//Mark the last word
//        write("Test");//Delete the marked word and write instead "Test"
//
//        clickOn("Start");//Start the program
//
//        //There should be an alert with a warning that in the path is no pom.xml data
//    }

    //Test what happens when in the path a false typ stands
//    @Test
//    public void testPathOtherDatatyp() {
//
//        doubleClickOn("#textFieldPath"); //Mark the last word
//        write("Other typ"); //Delete the marked word and write instead "Test"
//
//        clickOn("Start"); //Start the program
//
//        Label label = (Label) stage.getScene().lookup("#lblPath"); //Get component lblPath
//        assertThat(label.getTextFill().toString(), CoreMatchers.is("0xff0000ff")); //AssertThat the color of the label "lblPath" changes to red
//
//        //There should be an alert with a warning that in the path is no pom.xml data
//    }

    //Test what happens when in the path is empty
    @Test
    public void testPathNoPath() {


       // clickOn("#textFieldPath"); //Click in the textField "textFieldPath"
        TextField text = (TextField) stage.getScene().lookup("#textFieldPath"); //Get component textFieldPath to know how long the text is (get int)
        eraseText(text.getLength()); //Delete the hole text

        clickOn("#startButton");//Start the program

        Label label = (Label) stage.getScene().lookup("#lblPath"); //Get component lblPath
        assertThat(label.getTextFill().toString(), CoreMatchers.is("0xff0000ff")); //AssertThat the color of the label "lblPath" changes to red

        //There should be an alert with a warning that the path is missing
    }

    //Test if step can change
    @Test
    public void testStepChangeNumber() {

        setRandomNumber(); //Set variable "randomNumber" to another number

        ChoiceBox choiceBox = (ChoiceBox) stage.getScene().lookup("#choiceBox"); //Get component "choiceBox"

        //Set a new value for the steps
        clickOn(choiceBox);
        for (int i = 1; i < randomNumber; i++) {
            type(KeyCode.DOWN);
        }
        type(KeyCode.ENTER);

        clickOn("#delete"); //Choose "delete" as a strategy because there would be an alert

        clickOn("#startButton"); //Start the program


    }

    //Set a new random number for the steps
    public int setRandomNumber() {

        int controlVar = randomNumber; //Save old randomNumber

        //Generate a new randomNumber
        while (controlVar == randomNumber) {
            randomNumber = (int) (Math.random() * 20 + 1);

            //If randomNumber is not the old number (controlVar) then return randomNumber
            if (controlVar != randomNumber) {
                return randomNumber;

            }
        }

        return 0;//Return statement because the program does not know that the while loop only ends with the return of randomNumber
    }

    //Test what happens when no strategy is chosen
    @Test
    public void testStrategyNothingChosen() {

        clickOn("#startButton"); //Start the program


        Label label = (Label) stage.getScene().lookup("#lblStrategy"); //Get component "lblStrategy"
        assertThat(label.getTextFill().toString(), CoreMatchers.is("0xff0000ff")); //AssertThat the color of the label "lblStrategy" changes to red

        //There should be an alert with a warning that fields are missing
    }

    //@Test
    //public void testStrategyChooseDelete(){
    //   clickOn("#delete");
    //}

    //Test what happens when in logFile a false typ stands
    @Test
    public void testLogFileOtherDatatyp() {

        doubleClickOn("#textFieldLog"); //Mark the last word
        write("Other typ"); //Delete the mark word and write "Other typ"

        clickOn("#startButton"); //Start the program

        Label label = (Label) stage.getScene().lookup("#lblLog"); //Get component "lblLog"
        assertThat(label.getTextFill().toString(), CoreMatchers.is("0xff0000ff")); //AssertThat the color of the label "lblLog" changes to red


        //There should be an alert with a warning that in the logFile is no .txt data
    }

//    @Test
//    public void testRevision(){
//
//        clickOn("#insert"); //Choose "insert" as a strategy because there would be an alert
//        //Write in revision "Hello World
//        clickOn("#textFieldRevision");
//        write("Hello World");
//
//        clickOn("#startButton"); //Start the program
//
//        assertEquals("Hello World", App.revision); //Check if the class "App" gets the right text
//    }

    @Test
    public void testListViewAddAndDeletePath(){

        clickOn("#delete"); //Choose "delete" as a strategy because there would be an alert
        clickOn("#startButton"); //Start the program

        clickOn("#listView"); //Select path in listView
        //Cancel the program
        clickOn("Cancel");
        clickOn("Yes");
    }

    @Test
    public void testBuildDiffSourceEmpty(){
        clickOn("BuildDiff");
        clickOn("#sourceField");
        TextField source = (TextField) stage.getScene().lookup("#sourceField");
        eraseText(source.getText().length());
        clickOn("Start");
    }

    @Test
    public void testBuildDiffSameInput(){
        clickOn("BuildDiff");
        TextField source = (TextField) stage.getScene().lookup("#sourceField");
        TextField target = (TextField) stage.getScene().lookup("#targetField");
        doubleClickOn(target);
        type(KeyCode.DELETE);
        clickOn(target);
        write(source.getText());
        clickOn("Start");
    }

    @Test
    public void testBuildDiffNotPom(){
        clickOn("BuildDiff");
        clickOn("#sourceField");
        eraseText(2);
        clickOn("Start");
    }


//    @BeforeClass
//    public static void setupSpec() throws Exception{
//
//            System.setProperty("testfx.robot", "glass");
//            System.setProperty("testfx.headless", "true");
//            System.setProperty("prism.order", "sw");
//            System.setProperty("prism.text", "t2k");
//        System.setProperty("java.awt.headless", "true");
//
//        FxToolkit.registerPrimaryStage();
//    }
}
