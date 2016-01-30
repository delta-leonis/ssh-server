package org.ssh.ui.components.centersection;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ssh.field3d.FieldGame;
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

    private FieldGame fieldGame;


    private Label FPSLabel;

    public CenterSection() {
        super("center", "centersection/center.fxml");
        fieldGame = new FieldGame(new Group(), 500, 500, SceneAntialiasing.BALANCED);
        add(new GameScene(500, 500), "#fieldBase", true);

        MatchlogSelector matchlogSelector = new MatchlogSelector();
        // MatchlogSelector wrapped in an Enroller for fancy up and down sliding
        Enroller matchlogEnroller = new Enroller(matchlogSelector.getComponent(), Enroller.ExtendDirection.RIGHT, matchlogWrapper.heightProperty(),
                matchlogButtonSizer.widthProperty(), matchlogWrapper.widthProperty(), true);

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
        this.matchlogWrapper.getChildren().add(matchlogEnroller);
    }

    public FieldGame getFieldGame() {
        return fieldGame;
    }
}
