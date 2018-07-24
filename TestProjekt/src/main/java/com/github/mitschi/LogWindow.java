package com.github.mitschi;

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
    private Separator separator;
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

    protected GenericStyledArea<ParStyle, Either<String, LinkedImage>, TextStyle> area;
    private final TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();
    private final LinkedImageOps<TextStyle> linkedImageOps = new LinkedImageOps<>();


    private Node createNode(StyledSegment<Either<String, LinkedImage>, TextStyle> seg,
                            BiConsumer<? super TextExt, TextStyle> applyStyle) {
        return seg.getSegment().unify(
                text -> StyledTextArea.createStyledTextNode(text, seg.getStyle(), applyStyle),
                LinkedImage::createNode
        );
    }

    public LogWindow(Stage primaryStage) {
        dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Log");

        dialog.setMinHeight(800);
        dialog.setMinWidth(900);

        dialog.setResizable(false);
        dialog.setMaximized(false);

        textPane = new ScrollPane();
        pane = new Pane();
        lblPath = new Label("Path:");
        lblStep = new Label("Step:");
        txtPath = new TextField();
        txtStep = new TextField();
        separator = new Separator();
        scene = new Scene(pane);

        lblBuildDuration = new Label("BuildDuration:");
        lblBuildResult = new Label("BuildResult:");
        getlblBuildDurationMin = new Label();
        getlblBuildDurationSec = new Label();
        getlblBuildResult = new Label();

        lblFailingModuleName = new Label("Failing Module Name:");
        lblMissingDependencies = new Label("Missing Dependencies:");
        lblMissingTypes = new Label("Missing Types:");
        lblFailingPlugins = new Label("Failing Plugins:");

        getLblFailingModuleName = new Label();
        getFailingPlugins = new ListView();
        getMissingDependencies = new ListView();
        getMissingTypes = new ListView();

        dialog.setScene(scene);

        pane.setMinSize(900, 800);

        lblPath.setPrefSize(30, 20);
        lblPath.setTranslateX(20);
        lblPath.setTranslateY(10);
        txtPath.setPrefSize(300, 20);
        txtPath.setTranslateX(70);
        txtPath.setTranslateY(10);
        txtPath.setEditable(false);

        lblStep.setPrefSize(30, 20);
        lblStep.setTranslateX(530);
        lblStep.setTranslateY(10);
        txtStep.setPrefSize(300, 20);
        txtStep.setTranslateX(580);
        txtStep.setTranslateY(10);
        txtStep.setEditable(false);

        separator.setPrefWidth(860);
        separator.setTranslateX(20);
        separator.setTranslateY(40);

        lblBuildDuration.setPrefSize(80, 20);
        lblBuildDuration.setTranslateX(20);
        lblBuildDuration.setTranslateY(50);

        getlblBuildDurationMin.setPrefSize(30, 20);
        getlblBuildDurationMin.setTranslateX(110);
        getlblBuildDurationMin.setTranslateY(50);

        getlblBuildDurationSec.setPrefSize(35, 20);
        getlblBuildDurationSec.setTranslateX(140);
        getlblBuildDurationSec.setTranslateY(50);

        lblBuildResult.setPrefSize(70, 20);
        lblBuildResult.setTranslateX(20);
        lblBuildResult.setTranslateY(80);

        getlblBuildResult.setPrefSize(200, 20);
        getlblBuildResult.setTranslateX(110);
        getlblBuildResult.setTranslateY(80);



        lblFailingModuleName.setPrefSize(100, 20);
        lblFailingModuleName.setTranslateX(20);
        lblFailingModuleName.setTranslateY(110);

        getLblFailingModuleName.setPrefSize(100, 20);
        getLblFailingModuleName.setTranslateX(120);
        getLblFailingModuleName.setTranslateX(110);

        lblMissingDependencies.setPrefSize(100,20);
        lblMissingDependencies.setTranslateX(190);
        lblMissingDependencies.setTranslateY(50);

        getMissingDependencies.setPrefSize(200,50);
        getMissingDependencies.setTranslateX(190);
        getMissingDependencies.setTranslateY(80);

        lblMissingTypes.setPrefSize(100,20);
       // lblMissingTypes.setTranslateX();
        lblMissingTypes.setTranslateY(50);



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
        area.setPrefSize(860, 630);

        VirtualizedScrollPane<GenericStyledArea> vsPane = new VirtualizedScrollPane(area);

        this.textPane.setContent(vsPane);
        textPane.setTranslateX(20);
        textPane.setTranslateY(150);

        pane.getChildren().addAll(lblPath, txtPath, lblStep, txtStep, textPane, separator, lblBuildDuration, getlblBuildDurationMin, getlblBuildDurationSec, lblBuildResult, getlblBuildResult, lblFailingModuleName, lblFailingPlugins, lblMissingDependencies, lblMissingTypes, getFailingPlugins, getLblFailingModuleName, getMissingDependencies, getMissingTypes);
    }

    private void updateStyleInSelection(TextStyle mixin, IndexRange selection) {
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            area.setStyleSpans(selection.getStart(), newStyles);

        }
    }


    public void showDialog(String filePath, String step, BuildLog buildLog) {
        txtPath.setText(filePath);
        txtStep.setText(step);

        getlblBuildDurationMin.setText(buildLog.getBuildDuration().getMinutes() + " min");
        getlblBuildDurationSec.setText(buildLog.getBuildDuration().getSeconds() + " sec");


        if (buildLog.getBuildResult().toString().equals("SUCCESS")) {
            getlblBuildResult.setTextFill(Color.web("#009900"));
        } else {
            getlblBuildResult.setTextFill(Color.web("#db0000"));
        }

        getlblBuildResult.setText(buildLog.getBuildResult() + "");

        String[] patternArray = {"\\[INFO\\]", "\\[ERROR\\]", "\\[WARNING\\]", "BUILD FAILURE", "ERROR", "BUILD SUCCESS", "SUCCESS", "\\@.*\\---", "FAILURE"};
        String[] patternColor = {"#006edb", "#db0000", "#e6b800", "#db0000", "#db0000", "#009900", "#009900", "#ff9090", "#db0000"};

        for (int i = 0; i < patternArray.length; i++) {
            Pattern pattern = Pattern.compile(patternArray[i]);
            Matcher matcher = pattern.matcher(area.getText());

            while (matcher.find()) {
                IndexRange selection = IndexRange.normalize(matcher.start(), matcher.end());
                updateStyleInSelection(TextStyle.textColor(Color.web(patternColor[i])), selection);
            }
        }
        dialog.show();
    }

}

