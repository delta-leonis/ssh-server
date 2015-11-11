package org.ssh.ui.components.timerslider;

import java.io.IOException;

import org.ssh.ui.components.BorderSlideBar;

import examples.TimerSliderExample;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Example pane that partially implements the bottom bar of the end-implementation.
 *
 * @Deprecated Please remove as soon as possible. Don't forget to update {@link TimerSliderExample}
 *             first
 * @author Thomas Hakkers
 *        
 */
public class TimerPane extends VBox {
    
    private final Button         slideBarButton;
    private Parent               slider;
    private final BorderSlideBar borderSlideBar;
    private final HBox           root;
                                 
    public TimerPane() {
        this.root = new HBox();
        this.root.setStyle("-fx-background-color: \"white\";");
        this.root.setSpacing(10);
        this.slideBarButton = new Button("^");
        this.slideBarButton.setStyle("-fx-font: 16px \"Serif\";");
        this.slideBarButton.prefHeightProperty().bind(this.root.heightProperty());
        
        try {
            this.slider = FXMLLoader.load(this.getClass().getResource("/fxml/TimeSlider.fxml"));
            this.slider.getStylesheets().add(this.getClass().getResource("/css/slider.css").toString());
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        
        final Label blueLabel = new Label("MIAUW.");
        blueLabel.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");
        this.borderSlideBar = new BorderSlideBar(100, this.slideBarButton, Pos.BOTTOM_CENTER, blueLabel);
        
        this.root.getChildren().addAll(this.slideBarButton, this.slider);
        this.getChildren().addAll(this.borderSlideBar, this.root);
    }
}
