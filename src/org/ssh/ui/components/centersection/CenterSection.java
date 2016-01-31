package org.ssh.ui.components.centersection;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.Enroller;

/**
 * @author Jeroen de Jong
 * @date 12/22/2015
 */
public class CenterSection extends UIComponent<StackPane> {

    @FXML
    private Pane matchlogWrapper;

    @FXML
    private Pane matchlogButtonSizer;


    private Label FPSLabel;

    public CenterSection() {
        super("center", "centersection/center.fxml");
        GameScene gameScene = new GameScene(500, 500);
        add(gameScene, "#fieldBase", true);

        FPSLabel = new Label();
        FPSLabel.setTextFill(Color.YELLOW);
        FPSLabel.setFont(new Font("Arial", 32));

        StackPane.setAlignment(FPSLabel, Pos.TOP_RIGHT);
        getComponent().getChildren().add(FPSLabel);

        AnimationTimer frameRateMeter = new AnimationTimer() {
            private final long[] frameTimes = new long[100];
            private int frameTimeIndex = 0 ;

            @Override
            public void handle(long now) {
                long oldFrameTime = frameTimes[frameTimeIndex];
                frameTimes[frameTimeIndex] = now;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                if (frameTimeIndex == 0) {
                    long elapsedNanos = now - oldFrameTime;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
                    FPSLabel.setText(String.format("FPS: %.0f", frameRate));
                }
            }
        };
        frameRateMeter.start();

        Slider heightSlider = new Slider(100, 1500, 500);
        heightSlider.setMinorTickCount(10);
        heightSlider.setMajorTickUnit(50);
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);

        heightSlider.setOrientation(Orientation.VERTICAL);

        gameScene.chartTranslateY().bind(heightSlider.valueProperty());

        StackPane.setAlignment(heightSlider, Pos.CENTER_RIGHT);
        getComponent().getChildren().add(heightSlider);

        MatchlogSelector matchlogSelector = new MatchlogSelector();
        // MatchlogSelector wrapped in an Enroller for fancy up and down sliding
        this.matchlogWrapper.getChildren().add(new Enroller(matchlogSelector.getComponent(), Enroller.ExtendDirection.RIGHT, matchlogWrapper.heightProperty(),
                matchlogButtonSizer.widthProperty(), matchlogWrapper.widthProperty(), true));
    }
}
