package com.github.mitschi;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.demo.richtext.LinkedImage;
import org.fxmisc.richtext.demo.richtext.LinkedImageOps;
import org.fxmisc.richtext.demo.richtext.ParStyle;
import org.fxmisc.richtext.demo.richtext.TextStyle;
import org.fxmisc.richtext.model.*;

import org.reactfx.util.Either;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

        dialog.setMinHeight(600);
        dialog.setMaxHeight(600);
        dialog.setMinWidth(800);
        dialog.setMaxWidth(800);

        dialog.setResizable(false);
        dialog.setMaximized(false);

        textPane = new ScrollPane();
        pane = new Pane();
        lblPath = new Label("Path:");
        lblStep = new Label("Step:");
        txtPath = new TextField();
        txtStep = new TextField();
        separator= new Separator();

        area = new GenericStyledArea<>(
                ParStyle.EMPTY,                                                 // default paragraph style
                (paragraph, style) -> paragraph.setStyle(style.toCss()),        // paragraph style setter

                TextStyle.EMPTY.updateFontSize(10).updateFontFamily("Consolas").updateTextColor(Color.BLACK),  // default segment style
                styledTextOps._or(linkedImageOps, (s1, s2) -> Optional.empty()),                            // segment operations
                seg -> createNode(seg, (text, style) -> text.setStyle(style.toCss())));                     // Node creator and segment style setter
        area.setWrapText(true);
        area.setStyleCodecs(
                ParStyle.CODEC,
                Codec.styledSegmentCodec(Codec.eitherCodec(Codec.STRING_CODEC, LinkedImage.codec()), TextStyle.CODEC));
        area.setPrefSize(760, 490);


        String text = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jana\\gitplayground\\TestProjekt\\blub.log"));
            String s = "";
            while ((s = br.readLine()) != null) {
                text += s + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        area.appendText(text);

        String [] patternArray = {"\\[INFO\\]","\\[ERROR\\]","\\[WARNING\\]","BUILD FAILURE","BUILD SUCCES",};
        String [] patternColor = {"#006edb","#ff0000","#ff9090","#db0000","#00FF00"};

        for(int i = 0; i < patternArray.length; i++){
            Pattern pattern = Pattern.compile(patternArray[i]);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                IndexRange selection = IndexRange.normalize(matcher.start(), matcher.end());
                updateStyleInSelection(TextStyle.textColor(Color.web(patternColor[i])), selection);
            }
        }

        this.textPane.setContent(area);

        scene = new Scene(pane);
        dialog.setScene(scene);

        pane.setMinSize(900, 800);

        lblPath.setPrefSize(30, 20);
        lblPath.setTranslateX(20);
        lblPath.setTranslateY(10);
        txtPath.setPrefSize(300, 20);
        txtPath.setTranslateX(70);
        txtPath.setTranslateY(10);
        txtPath.setDisable(true);

        lblStep.setPrefSize(30, 20);
        lblStep.setTranslateX(400);
        lblStep.setTranslateY(10);
        txtStep.setPrefSize(300, 20);
        txtStep.setTranslateX(450);
        txtStep.setTranslateY(10);
        txtStep.setDisable(true);

        separator.setPrefWidth(30);
        separator.setRotate(90);
        separator.setTranslateX(375);
        separator.setTranslateY(10);

        textPane.setTranslateX(20);
        textPane.setTranslateY(90);


        pane.getChildren().addAll(lblPath, txtPath, lblStep, txtStep, textPane);
    }

    private void updateStyleInSelection(TextStyle mixin, IndexRange selection) {
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            area.setStyleSpans(selection.getStart(), newStyles);

        }
    }





    public void showDialog(String filePath, String step){
        txtPath.setText(filePath);
        txtStep.setText(step);

        dialog.show();
    }
}

