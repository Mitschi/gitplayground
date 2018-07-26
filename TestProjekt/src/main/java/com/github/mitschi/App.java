package com.github.mitschi;


import at.aau.diff.common.Change;
import at.aau.diff.maven.MavenBuildChange;
import at.aau.diff.maven.MavenBuildFileDiffer;
import at.aau.fixStrategies.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.demo.richtext.LinkedImage;
import org.fxmisc.richtext.demo.richtext.LinkedImageOps;
import org.fxmisc.richtext.demo.richtext.ParStyle;
import org.fxmisc.richtext.demo.richtext.TextStyle;
import org.fxmisc.richtext.model.*;
import org.reactfx.util.Either;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class App extends Application {

    protected Stage stage;
    protected static String logPath;
    protected static String pomFile;
    protected static int maxSteps;
    protected Scene scene;
    protected static String savePath;
    protected static String revision;
    protected static ArrayList<Process> processList;
    protected int processCounter;
    protected static TabOnCloseListener tabListener;
    protected static ProgressListener progressListener;
    protected static String sourcePath;
    protected static String targetPath;
    protected String backgroundColorBuildDiff = "#0000ff";
    protected String textColorBuildDiff = "#000000";
    protected String[] target;
    protected String[] source;

    protected GenericStyledArea<ParStyle, Either<String, LinkedImage>, TextStyle> areaSource;
    private final TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();
    private final LinkedImageOps<TextStyle> linkedImageOps = new LinkedImageOps<>();
    protected GenericStyledArea<ParStyle, Either<String, LinkedImage>, TextStyle> areaTarget;


    @FXML
    protected TextField sourceField;
    @FXML
    protected TextField targetField;
    @FXML
    protected TextField textFieldPath;
    @FXML
    protected TextField textFieldLog;
    @FXML
    protected ChoiceBox choiceBox;
    @FXML
    protected Label lblPath;
    @FXML
    protected Label lblStrategy;
    @FXML
    protected Label lblLog;
    @FXML
    protected TextField textFieldRevision;
    @FXML
    protected Tab detailsTab;
    @FXML
    protected TabPane tapPane;
    @FXML
    protected TableView<TableRow> tableView;
    @FXML
    protected javafx.scene.control.TableColumn step;
    @FXML
    protected javafx.scene.control.TableColumn strategies;
    @FXML
    protected javafx.scene.control.TableColumn buildResult;
    @FXML
    protected CheckBox delete;
    @FXML
    protected CheckBox add;
    @FXML
    protected CheckBox insert;
    @FXML
    protected CheckBox version;

    @FXML
    protected ListView<Process> listView;

    @FXML
    protected Tab tabBuildDiff;

    @FXML
    protected Pane scrollPaneSource;

    @FXML
    protected Pane scrollPaneTarget;


    public void initialize() {
        //initialize areaSource
        areaSource = new GenericStyledArea<>(
                ParStyle.EMPTY,                                                 // default paragraph style
                (paragraph, style) -> paragraph.setStyle(style.toCss()),        // paragraph style setter

                TextStyle.EMPTY.updateFontSize(10).updateFontFamily("Consolas").updateTextColor(Color.BLACK),  // default segment style
                styledTextOps._or(linkedImageOps, (s1, s2) -> Optional.empty()),                            // segment operations
                seg -> createNode(seg, (text, style) -> text.setStyle(style.toCss())));                     // Node creator and segment style setter
        areaSource.setWrapText(true);
        areaSource.setEditable(false);
        areaSource.setStyleCodecs(
                ParStyle.CODEC,
                Codec.styledSegmentCodec(Codec.eitherCodec(Codec.STRING_CODEC, LinkedImage.codec()), TextStyle.CODEC));
        areaSource.setPrefSize(scrollPaneSource.getPrefWidth(), scrollPaneSource.getPrefHeight());

        //add line number to areaSource
        IntFunction<Node> numberFactoryS = LineNumberFactory.get(areaSource);
        IntFunction<Node> graphicFactoryS = line -> {
            HBox hbox = new HBox(numberFactoryS.apply(line));
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };
        areaSource.setParagraphGraphicFactory(graphicFactoryS);
        VirtualizedScrollPane<GenericStyledArea> vsPaneS = new VirtualizedScrollPane(areaSource); //add areaSource to VirtualizedScrollPaneTo "vsPane" to get a scrollbar
        scrollPaneSource.getChildren().add(vsPaneS);

        //initialize areaTarget
        areaTarget = new GenericStyledArea<>(
                ParStyle.EMPTY,                                                 // default paragraph style
                (paragraph, style) -> paragraph.setStyle(style.toCss()),        // paragraph style setter

                TextStyle.EMPTY.updateFontSize(10).updateFontFamily("Consolas").updateTextColor(Color.BLACK),  // default segment style
                styledTextOps._or(linkedImageOps, (s1, s2) -> Optional.empty()),                            // segment operations
                seg -> createNode(seg, (text, style) -> text.setStyle(style.toCss())));                     // Node creator and segment style setter
        areaTarget.setWrapText(true);
        areaTarget.setEditable(false);
        areaTarget.setStyleCodecs(
                ParStyle.CODEC,
                Codec.styledSegmentCodec(Codec.eitherCodec(Codec.STRING_CODEC, LinkedImage.codec()), TextStyle.CODEC));
        areaTarget.setPrefSize(scrollPaneTarget.getPrefWidth(), scrollPaneTarget.getPrefHeight());

        //add line number to areaTarget
        IntFunction<Node> numberFactoryT = LineNumberFactory.get(areaTarget);
        IntFunction<Node> graphicFactoryT = line -> {
            HBox hbox = new HBox(numberFactoryT.apply(line));
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };
        areaTarget.setParagraphGraphicFactory(graphicFactoryT);
        VirtualizedScrollPane<GenericStyledArea> vsPaneT = new VirtualizedScrollPane(areaTarget);//add areaTarget to VirtualizedScrollPaneTo "vsPane" to get a scrollbar
        scrollPaneTarget.getChildren().add(vsPaneT);

        // Set detailsTab to non-visible in the beginning
        tapPane.getTabs().remove(detailsTab);
        tapPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        // initialize tabListener
        tabListener = new TabOnCloseListener() {
            @Override
            public void tabOnClose() {
                // remove Tab from ListView
                Tab t = tapPane.getSelectionModel().getSelectedItem();
                for (int i = 0; i < listView.getItems().size(); i++) {
                    if (listView.getItems().get(i).getProcessTab().equals(t)) {
                        listView.getItems().remove(i);
                    }
                }
            }
        };
        // initialize Progresslistener
        progressListener = new ProgressListener() {
            @Override
            public void changeProgress(Process process) {
                // change Progress
                process.getProgressBar().setProgress(process.getProgress());
                process.getLabel().setText(process.getProgress() * 100 + "");
            }

            @Override
            public void progressFinished(Process process) {
                // remove Process from ListView and update Progress
                listView.getItems().remove(process);
                process.getProgressBar().setProgress(1);
                process.getLabel().setText("100%");
                processList.remove(process);
                processCounter--;
            }
        };

        // initialize processList
        processList = new ArrayList<Process>();
        //processList.add(new Process(null, detailsTab));
        processCounter = 0;

        // Setting up ChoiceBox
        choiceBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12", "13", "14", "15", "16", "17", "18", "19", "20"));
        choiceBox.setValue("1");
        String pick = choiceBox.getValue().toString();
        maxSteps = Integer.parseInt(pick);
        // Setting up TableView
        tableView.setEditable(true);
        step.setCellValueFactory(new PropertyValueFactory<TableRow, Integer>("step"));
        strategies.setCellValueFactory(new PropertyValueFactory<TableRow, String>("strategie"));
        buildResult.setCellValueFactory(new PropertyValueFactory<TableRow, String>("buildResult"));

        //ObservableList<TableRow> data = FXCollections.observableArrayList(new TableRow(1, "strat1", "success", stage, pomFile), new TableRow(2, "strat2", "failed", stage, pomFile));

        //tableView.setItems(data);

        // Load existing Properties
        try {
            Properties startProperties = new Properties();
            savePath = System.getProperty("user.home") + "\\.buildMedic"; // initializing savePath
            startProperties.load(new FileReader(savePath + "\\config.properties"));
            logPath = startProperties.getProperty("logPath");
            pomFile = startProperties.getProperty("pomFile");
            maxSteps = Integer.parseInt(startProperties.getProperty("max_steps"));
            revision = startProperties.getProperty("revision");
            targetPath = startProperties.getProperty("targetPath");
            sourcePath = startProperties.getProperty("sourcePath");
            choiceBox.setValue(maxSteps + "");
            textFieldLog.setText(logPath);
            textFieldPath.setText(pomFile);
            textFieldRevision.setText(revision);
            targetField.setText(targetPath);
            sourceField.setText(sourcePath);

        } catch (Exception e) {

        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {


        logPath = ""; // initializing logPath
        maxSteps = 1; // initializing maxSteps
        pomFile = ""; // initializing pomFile

        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml")); // loading fxml-File
        scene = new Scene(root, 800, 600); // initializing scene
        primaryStage.setTitle("BuildMedic"); // Setting Title of Application
        primaryStage.setResizable(false); // Set Stage to not resizeable
        primaryStage.setScene(scene); // Adding Scene to Stage
        primaryStage.show(); // Showing Stage
        stage = primaryStage; // make a global reference for primaryStage
    }

    public static void main(String[] args) {
        launch(args); // Launch the Application
        // Saves Properties before the Application terminates
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                try {
                    saveProperties();
                } catch (NullPointerException ignored) {

                }

            }
        }));
    }

    @FXML
    protected void startProgram(ActionEvent event) {
        // show detailsTab when the Startbutton is pressed


        boolean isPom = testForPom();
        boolean isSelected = true;
        boolean isTxt = testForTxt();
        List<Class> allowedStrats = new ArrayList<Class>();
        // add allowed Strategies
        if (add.isSelected())
            allowedStrats.add(AddRepositoryEntryAction.class);

        if (delete.isSelected())
            allowedStrats.add(DeleteDependencyAction.class);

        if (insert.isSelected())
            allowedStrats.add(InsertDependencyAction.class);

        if (version.isSelected())
            allowedStrats.add(VersionUpdateAction.class);

        // Marking missing parameters
        if (pomFile.equals("") || !isPom)
            lblPath.setTextFill(Color.web("#FF0000"));
        else
            lblPath.setTextFill(Color.web("#000000"));

        if (!isTxt)
            lblLog.setTextFill(Color.web("#FF0000"));
        else
            lblLog.setTextFill(Color.web("#000000"));


        if (!delete.isSelected() && !add.isSelected() && !insert.isSelected() && !version.isSelected()) {
            lblStrategy.setTextFill(Color.web("#FF0000"));
            isSelected = false;
        } else
            lblStrategy.setTextFill(Color.web("#000000"));

        if (pomFile.isEmpty() || !isSelected) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Missing Fields!");
            alert.show();
        } else {
            // initialize new Process
            Process process = new Process(pomFile, stage, logPath);
            boolean isAlreadyRunning = false;
            // check if Process had been started already
            for (Process p : processList) {
                if (p.equals(process))
                    isAlreadyRunning = true;
            }
            // add Process to processList and start it
            if (!isAlreadyRunning) {
                processList.add(process);
                processCounter++;
                process.getProcessTab().setClosable(false);
                tapPane.getTabs().add(processList.get(processCounter - 1).getProcessTab());
                listView.getItems().add(process);

                File repoFile = new File(pomFile); // Load File
                getRevision(); // Load Revision-String

                //Load maxSteps from ChoiceBox
                String pick = choiceBox.getValue().toString();
                maxSteps = Integer.parseInt(pick);

                try {
                    process.start(repoFile.getParentFile(), revision, maxSteps, allowedStrats);

                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to start Repairtool!" + e.getClass() + " " + e.getMessage());
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Process already running!");
                alert.show();
            }
        }

    }

    @FXML
    protected void cancelProgram(ActionEvent event) {
        // get Selected Process
        ObservableList<Process> selectedItems = listView.getSelectionModel().getSelectedItems();

        if (listView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No process chosen");
            alert.show();

        } else {

            Alert alert =
                    new Alert(Alert.AlertType.WARNING,
                            "Do you want to cancel the process?",
                            ButtonType.YES,
                            ButtonType.NO);

            Optional<ButtonType> decision = alert.showAndWait();
            if (decision.get() == ButtonType.YES) {
                for (int j = 0; j < selectedItems.size(); j++) {
                    Process selP = selectedItems.get(j);
                    for (int i = 0; i < processList.size(); i++) {
                        Process p = processList.get(i);
                        if (p.equals(selP)) {
                            p.getRepair().abortRepair(); // cancel the process
                            // remove Process from processList
                            processList.remove(p);
                            listView.getItems().remove(p);
                            processCounter--;
                            p.getProcessTab().setClosable(true);
                        }
                    }
                }
            }
        }

    }


    @FXML
    protected void choosePath(ActionEvent event) {
        // Initialize FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose pom.xml");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Extensible Markup Language", "*.xml"));
        // Open FileChooser and wait for Input
        File file = fileChooser.showOpenDialog(lblPath.getScene().getWindow());

        try {
            // Check if input is a pom.xml file otherwise open an alert
            if (!file.getName().equals("pom.xml")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File has to be a pom.xml file!");
                alert.show();
            } else {
                textFieldPath.setText(file.getPath());
                pomFile = file.getPath();
            }
        } catch (RuntimeException e) {

        }

    }

    @FXML
    protected void chooseLog(ActionEvent event) {
        // Initialize FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Logfile");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Textfile", "*.txt"));
        try {
            // Open FileChooser
            File file = fileChooser.showOpenDialog(lblPath.getScene().getWindow());
            // Load Path to TextField
            logPath = file.getPath();
            textFieldLog.setText(logPath);
        } catch (Exception e) {

        }

    }

    private static void saveProperties() {
        // Initialize Properties
        Properties properties = new Properties();
        properties.setProperty("logPath", logPath);
        properties.setProperty("pomFile", pomFile);
        properties.setProperty("max_steps", maxSteps + "");
        properties.setProperty("revision", revision);
        properties.setProperty("targetPath", targetPath);
        properties.setProperty("sourcePath", sourcePath);

        try {
            // write properties to C:\Users\%USERPROFILE%\.buildMedic\config.properties if non-existing, Path will be created
            if (!Files.exists(Paths.get(savePath))) {
                new File(savePath).mkdirs();
                properties.store(new FileWriter(savePath + "\\config.properties"), "Properties");

            } else {
                properties.store(new FileWriter(savePath + "\\config.properties"), "Properties");


            }
        } catch (IOException e) {
        }

    }

    protected void getRevision() {
        // Load revision-String
        revision = textFieldRevision.getText();
    }


    protected boolean testForPom() {
        // checks if file is a pom.xml
        String s = textFieldPath.getText();
        try {
            File file = new File(s);

            if (!file.getName().equals("pom.xml")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Path has to be a pom.xml file!");
                alert.show();
                return false;
            }

            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "This is not a file!");
            alert.show();

            return false;
        }
    }

    protected boolean testForTxt() {
        // checks if logFile is a .txt file
        String s = textFieldLog.getText();
        try {
            File file = new File(s);

            if (!file.getName().contains(".txt") && !file.getName().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "LogFile has to be a .txt file!");
                alert.show();
                return false;
            }

            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "This is not a file!");
            alert.show();

            return false;
        }
    }

    @FXML
    protected void contextMenuListView(ActionEvent event) {
        // change Tab via contextMenu show Details
        if (listView.getItems().size() != 0) {
            MultipleSelectionModel<Process> sel = listView.getSelectionModel();
            Process p = sel.getSelectedItem();
            SingleSelectionModel<Tab> selectionModel = tapPane.getSelectionModel();
            if (!sel.isEmpty())
                selectionModel.select(p.getProcessTab());
        }
    }

    @FXML
    protected void chooseSource(ActionEvent event) {
        // Initialize FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose pom.xml");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Extensible Markup Language", "*.xml"));
        // Open FileChooser and wait for Input
        try {
            File file = fileChooser.showOpenDialog(lblPath.getScene().getWindow());

            if (file.exists()) {
                sourceField.setText(file.getPath());
                sourcePath = file.getPath();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File could not be found");
                alert.show();
            }
        } catch (Exception e) {

        }

    }

    @FXML
    protected void chooseTarget(ActionEvent event) {
        // Initialize FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose pom.xml");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Extensible Markup Language", "*.xml"));
        // Open FileChooser and wait for Input

        try {
            File file = fileChooser.showOpenDialog(lblPath.getScene().getWindow());

            if (file.exists()) {
                targetField.setText(file.getPath());
                targetPath = file.getPath();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File could not be found");
                alert.show();
            }
        } catch (Exception e) {

        }

    }

    @FXML
    protected void startDiffer(ActionEvent event) {
        //get Text
        targetPath = targetField.getText();
        sourcePath = sourceField.getText();

        //check if the paths are empty
        if (targetPath.isEmpty() || sourcePath.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Missing argument!");
            alert.show();
        } else {
            //check if the paths are the same
            if (targetPath.equals(sourcePath)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Source and Target cannot be the same!");
                alert.show();
            } else {
                //check if the paths are pom.xml datas
                if (!targetPath.endsWith("pom.xml") || !sourcePath.endsWith("pom.xml")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Source and Target have to be pom.xml files!");
                    alert.show();
                } else {
                    //check if the paths exists
                    if (!new File(sourcePath).exists() || !new File(targetPath).exists()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "File could not be found!");
                        alert.show();
                    } else {
                        //clear the areas
                        areaTarget.clear();
                        areaSource.clear();

                        MavenBuildFileDiffer differ = new MavenBuildFileDiffer();
                        try {
                            //Read paths
                            BufferedReader brS = new BufferedReader(new FileReader(sourcePath));
                            BufferedReader brT = new BufferedReader(new FileReader(targetPath));

                            String s = "";
                            //setText in areaSource formatted
                            while ((s = brS.readLine()) != null) {
                                areaSource.appendText(s + "\n");
                            }

                            //setText in areaTarget formatted
                            while ((s = brT.readLine()) != null) {
                                areaTarget.appendText(s + "\n");
                            }

                            //split the text after \n to write each line in a different array index
                            target = areaTarget.getText().split("\n");
                            source = areaSource.getText().split("\n");

                        } catch (Exception e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to read files!");
                            alert.show();
                        }
                        try {
                            //get the differences between the paths
                            List<Change> changes = differ.extractChanges(new File(sourcePath), new File(targetPath));

                            //for each change do
                            for (int i = 0; i < changes.size(); i++) {
                                //get what changed
                                String changeTyp = ((MavenBuildChange) changes.get(i)).getChangeType();

                                //set the backgroundColor and textColor
                                switch (changeTyp) {

                                    case "INSERT":
                                        backgroundColorBuildDiff = "#00b300"; //green
                                        textColorBuildDiff = "#ffffff";//white
                                        break;

                                    case "UPDATE":
                                        backgroundColorBuildDiff = "#e6e600";//yellow
                                        textColorBuildDiff = "#000000";//black
                                        break;

                                    case "DELETE":
                                        backgroundColorBuildDiff = "#db0000";//red
                                        textColorBuildDiff = "#ffffff";//white
                                        break;

                                    default:
                                        break;
                                }


                                int startSrc = 0;
                                int endSrc = 0;
                                //get the line where sth changed
                                int startLineNumberSource = ((MavenBuildChange) changes.get(i)).getSrcPositionInfo().getStartLineNumber();
                                int endLineNumberSource = ((MavenBuildChange) changes.get(i)).getSrcPositionInfo().getEndLineNumber();

                                //get the startValue where you start to color the change
                                for (int j = 0; j < startLineNumberSource - 1; j++) {
                                    startSrc = startSrc + source[j].length() + 1;
                                }
                                //get the endValue where you stop to color the change
                                for (int j = 0; j < endLineNumberSource; j++) {
                                    endSrc = endSrc + source[j].length() + 1;
                                }

                                //set backgroundColor, textColor and range of coloring for source
                                IndexRange selectionSource = IndexRange.normalize(startSrc, endSrc);
                                updateStyleInSelectionSource(TextStyle.backgroundColor(Color.web(backgroundColorBuildDiff)), selectionSource);
                                updateStyleInSelectionSource(TextStyle.textColor(Color.web(textColorBuildDiff)), selectionSource);


                                int startTrg = 0;
                                int endTrg = 0;
                                //get the line where sth changed
                                int startLineNumberTarget = ((MavenBuildChange) changes.get(i)).getDstPositionInfo().getStartLineNumber();
                                int endLineNumberTarget = ((MavenBuildChange) changes.get(i)).getDstPositionInfo().getEndLineNumber();

                                //get the startValue where you start to color the change
                                for (int j = 0; j < startLineNumberTarget - 1; j++) {
                                    startTrg = startTrg + target[j].length() + 1;
                                }
                                //get the endValue where you stop to color the change
                                for (int j = 0; j < endLineNumberTarget; j++) {
                                    endTrg = endTrg + target[j].length() + 1;
                                }

                                //set backgroundColor, textColor and range of coloring for target
                                IndexRange selectionTarget = IndexRange.normalize(startTrg, endTrg);
                                updateStyleInSelectionTarget(TextStyle.backgroundColor(Color.web(backgroundColorBuildDiff)), selectionTarget);
                                updateStyleInSelectionTarget(TextStyle.textColor(Color.web(textColorBuildDiff)), selectionTarget);

                            }


                        } catch (Exception e) {

                        }
                    }

                }
            }
        }
    }

    private Node createNode(StyledSegment<Either<String, LinkedImage>, TextStyle> seg,
                            BiConsumer<? super TextExt, TextStyle> applyStyle) {
        return seg.getSegment().unify(
                text -> StyledTextArea.createStyledTextNode(text, seg.getStyle(), applyStyle),
                LinkedImage::createNode
        );
    }

    private void updateStyleInSelectionSource(TextStyle mixin, IndexRange selection) {
        //color text in IndexRange "selection"
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = areaSource.getStyleSpans(selection);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            areaSource.setStyleSpans(selection.getStart(), newStyles);
        }
    }


    private void updateStyleInSelectionTarget(TextStyle mixin, IndexRange selection) {
        //color text in IndexRange "selection"
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = areaTarget.getStyleSpans(selection);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            areaTarget.setStyleSpans(selection.getStart(), newStyles);
        }
    }

}