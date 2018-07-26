package com.github.mitschi;

import at.aau.building.BuildDuration;
import at.aau.building.BuildLog;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.demo.richtext.LinkedImage;
import org.fxmisc.richtext.demo.richtext.LinkedImageOps;
import org.fxmisc.richtext.demo.richtext.ParStyle;
import org.fxmisc.richtext.demo.richtext.TextStyle;
import org.fxmisc.richtext.model.*;

import org.reactfx.util.Either;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogWindow {

    private Stage dialog;
    private Label lblPath;
    private Label lblStep;
    private TextField txtPath;
    private TextField txtStep;
    private ScrollPane textPane;
    private Pane pane;
    private Scene scene;
    private Separator separator1;
    private Separator separator2;
    private Label lblBuildDuration;
    private Label getlblBuildDurationMin;
    private Label getlblBuildDurationSec;
    private Label lblBuildResult;
    private Label getlblBuildResult;
    private Label lblFailingModuleName;
    private Label getLblFailingModuleName;
    private Label lblMissingDependencies;
    private ListView getMissingDependencies;
    private Label lblMissingTypes;
    private ListView getMissingTypes;
    private Label lblFailingPlugins;
    private ListView getFailingPlugins;
    private int textEnd;
    private String result;
    private int minutes, seconds;

    protected GenericStyledArea<ParStyle, Either<String, LinkedImage>, TextStyle> area;
    private final TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();
    private final LinkedImageOps<TextStyle> linkedImageOps = new LinkedImageOps<>();


    public LogWindow(Stage primaryStage) {

        //initialize
        dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Log");
        textEnd = 0;
        dialog.setMinHeight(800);
        dialog.setMinWidth(920);
        dialog.setResizable(false);
        dialog.setMaximized(false);

        textPane = new ScrollPane();
        pane = new Pane();
        lblPath = new Label("Path:");
        lblStep = new Label("Step:");
        txtPath = new TextField();
        txtStep = new TextField();
        separator1 = new Separator();
        scene = new Scene(pane);

        lblBuildDuration = new Label("BuildDuration:");
        lblBuildResult = new Label("BuildResult:");
        getlblBuildDurationMin = new Label();
        getlblBuildDurationSec = new Label();
        getlblBuildResult = new Label();
        lblFailingModuleName = new Label("Failing Module:");
        getLblFailingModuleName = new Label();
        separator2 = new Separator();

        lblMissingDependencies = new Label("Missing Dependencies:");
        lblMissingTypes = new Label("Missing Types:");
        lblFailingPlugins = new Label("Failing Plugins:");
        getFailingPlugins = new ListView();
        getMissingDependencies = new ListView();
        getMissingTypes = new ListView();

        dialog.setScene(scene);

        pane.setMinSize(920, 800);

        lblPath.setPrefSize(30, 20);
        lblPath.setTranslateX(20);
        lblPath.setTranslateY(10);
        txtPath.setPrefSize(300, 20);
        txtPath.setTranslateX(70);
        txtPath.setTranslateY(10);
        txtPath.setEditable(false);

        lblStep.setPrefSize(30, 20);
        lblStep.setTranslateX(550);
        lblStep.setTranslateY(10);
        txtStep.setPrefSize(300, 20);
        txtStep.setTranslateX(600);
        txtStep.setTranslateY(10);
        txtStep.setEditable(false);

        separator1.setPrefWidth(880);
        separator1.setTranslateX(20);
        separator1.setTranslateY(45);

        lblBuildDuration.setPrefSize(80, 20);
        lblBuildDuration.setTranslateX(20);
        lblBuildDuration.setTranslateY(50);

        getlblBuildDurationMin.setPrefSize(35, 20);
        getlblBuildDurationMin.setTranslateX(110);
        getlblBuildDurationMin.setTranslateY(50);

        getlblBuildDurationSec.setPrefSize(35, 20);
        getlblBuildDurationSec.setTranslateX(150);
        getlblBuildDurationSec.setTranslateY(50);

        lblBuildResult.setPrefSize(70, 20);
        lblBuildResult.setTranslateX(320);
        lblBuildResult.setTranslateY(50);

        getlblBuildResult.setPrefSize(200, 20);
        getlblBuildResult.setTranslateX(400);
        getlblBuildResult.setTranslateY(50);

        lblFailingModuleName.setPrefSize(100, 20);
        lblFailingModuleName.setTranslateX(620);
        lblFailingModuleName.setTranslateY(50);

        getLblFailingModuleName.setPrefSize(120, 20);
        getLblFailingModuleName.setTranslateX(710);
        getLblFailingModuleName.setTranslateY(50);

        separator2.setPrefWidth(880);
        separator2.setTranslateX(20);
        separator2.setTranslateY(75);


        lblMissingDependencies.setPrefSize(150, 20);
        lblMissingDependencies.setTranslateX(20);
        lblMissingDependencies.setTranslateY(80);

        getMissingDependencies.setPrefSize(280, 50);
        getMissingDependencies.setTranslateX(20);
        getMissingDependencies.setTranslateY(100);

        lblMissingTypes.setPrefSize(100, 20);
        lblMissingTypes.setTranslateX(320);
        lblMissingTypes.setTranslateY(80);

        getMissingTypes.setPrefSize(280, 50);
        getMissingTypes.setTranslateX(320);
        getMissingTypes.setTranslateY(100);

        lblFailingPlugins.setPrefSize(100, 20);
        lblFailingPlugins.setTranslateX(620);
        lblFailingPlugins.setTranslateY(80);

        getFailingPlugins.setPrefSize(280, 50);
        getFailingPlugins.setTranslateX(620);
        getFailingPlugins.setTranslateY(100);

        //Initialize GenericStyledArea area;
        area = new GenericStyledArea<>(
                ParStyle.EMPTY,                                                 // default paragraph style
                (paragraph, style) -> paragraph.setStyle(style.toCss()),        // paragraph style setter

                TextStyle.EMPTY.updateFontSize(10).updateFontFamily("Consolas").updateTextColor(Color.BLACK),  // default segment style
                styledTextOps._or(linkedImageOps, (s1, s2) -> Optional.empty()),                            // segment operations
                seg -> createNode(seg, (text, style) -> text.setStyle(style.toCss())));                     // Node creator and segment style setter
        area.setWrapText(true);
        area.setEditable(false);
        area.setStyleCodecs(
                ParStyle.CODEC,
                Codec.styledSegmentCodec(Codec.eitherCodec(Codec.STRING_CODEC, LinkedImage.codec()), TextStyle.CODEC));
        area.setPrefSize(880, 610); //set size

        VirtualizedScrollPane<GenericStyledArea> vsPane = new VirtualizedScrollPane(area); //Add area to VirtualizedScrollPaneTo "vsPane" to get a scrollbar

        this.textPane.setContent(vsPane);
        textPane.setTranslateX(20);
        textPane.setTranslateY(160);

        //add all controls and container to the main pane
        pane.getChildren().addAll(lblPath, txtPath, lblStep, txtStep, textPane, separator1, separator2, lblBuildDuration, getlblBuildDurationMin, getlblBuildDurationSec, lblBuildResult, getlblBuildResult, lblFailingModuleName, lblFailingPlugins, lblMissingDependencies, lblMissingTypes, getFailingPlugins, getLblFailingModuleName, getMissingDependencies, getMissingTypes);
    }

    private Node createNode(StyledSegment<Either<String, LinkedImage>, TextStyle> seg,
                            BiConsumer<? super TextExt, TextStyle> applyStyle) {
        return seg.getSegment().unify(
                text -> StyledTextArea.createStyledTextNode(text, seg.getStyle(), applyStyle),
                LinkedImage::createNode
        );
    }

    private void updateStyleInSelection(TextStyle mixin, IndexRange selection) {
        //color text in IndexRange "selection"
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            area.setStyleSpans(selection.getStart(), newStyles);

        }
    }

    public void updateLogWindow(){
        //patternArray = words you want to color
        String[] patternArray = {"\\[INFO\\]", "\\[ERROR\\]", "\\[WARNING\\]", "BUILD FAILURE", "ERROR", "BUILD SUCCESS", "SUCCESS", "\\@.*\\---", "FAILURE"};
        //for each word in patternArray is a color in patternColor
        String[] patternColor = {"#006edb", "#db0000", "#e6b800", "#db0000", "#db0000", "#009900", "#009900", "#ff9090", "#db0000"};

        //find the words from patternArray in area and set textColor
        for (int i = 0; i < patternArray.length; i++) {
            Pattern pattern = Pattern.compile(patternArray[i]);
            Matcher matcher = pattern.matcher(area.getText());

            while (matcher.find(textEnd)) {
                IndexRange selection = IndexRange.normalize(matcher.start(), matcher.end());
                updateStyleInSelection(TextStyle.textColor(Color.web(patternColor[i])), selection);
                if(matcher.end() > textEnd){
                    textEnd = matcher.end();
                }
            }

        }
    }

    public void updateResult(String result, BuildDuration buildDuration, BuildLog buildLog){
        // update Result and Build Duration in LogWindow
        getlblBuildResult.setText(result);
        seconds = buildDuration.getSeconds();
        minutes = buildDuration.getMinutes();
        getlblBuildDurationMin.setText(minutes + " min ");
        getlblBuildDurationSec.setText(seconds + " sec");
        this.result = result;
        if (result.equals("SUCCESS")) {
            getlblBuildResult.setTextFill(Color.web("#009900"));
        } else {
            getlblBuildResult.setTextFill(Color.web("#db0000"));
        }

        getLblFailingModuleName.setText(buildLog.getFailingModuleName());

        getMissingDependencies.getItems().addAll(buildLog.getMissingDependencies());
        getMissingTypes.getItems().addAll(buildLog.getMissingTypes());

    }

    public void showDialog(String filePath, String step, BuildLog buildLog) {
        //set text
        txtPath.setText(filePath);
        txtStep.setText(step);
        getlblBuildDurationMin.setText(minutes + " min ");
        getlblBuildDurationSec.setText(seconds + " sec");

        //set color of getlblBuildResult
        if (result.equals("SUCCESS")) {
            getlblBuildResult.setTextFill(Color.web("#009900"));
        } else {
            getlblBuildResult.setTextFill(Color.web("#db0000"));
        }

        //set text
        getlblBuildResult.setText(result);
//        getLblFailingModuleName.setText(buildLog.getFailingModuleName());
//
//        getMissingDependencies.getItems().addAll(buildLog.getMissingDependencies());
//        getMissingTypes.getItems().addAll(buildLog.getMissingTypes());

        //There is nothing in buildLog.getFailingPlugins()
        // getFailingPlugins.getItems().addAll(buildLog.getFailingPlugins());


        dialog.show();
    }

}

