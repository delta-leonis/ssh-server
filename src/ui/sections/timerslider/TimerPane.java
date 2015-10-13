package ui.sections.timerslider;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.sections.BorderSlideBar;

/**
 * Example pane that partially implements the bottom bar of the end-implementation.
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *
 */
public class TimerPane extends VBox{
	private Button slideBarButton;
	private Parent slider;
	private BorderSlideBar borderSlideBar;
	private HBox root;
	
	public TimerPane(){
		root = new HBox();
		root.setStyle("-fx-background-color: \"white\";");
		root.setSpacing(10);
		slideBarButton = new Button("^");
		slideBarButton.setStyle("-fx-font: 16px \"Serif\";");
		slideBarButton.prefHeightProperty().bind(root.heightProperty());
		
		try {
			slider = FXMLLoader.load(getClass().getResource("/fxml/TimeSlider.fxml"));
			slider.getStylesheets().add(getClass().getResource("/css/slider.css").toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Label blueLabel = new Label("MIAUW.");
        blueLabel.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");
		borderSlideBar = new BorderSlideBar(100, slideBarButton, Pos.BOTTOM_CENTER, blueLabel);
		
		root.getChildren().addAll(slideBarButton, slider);
		getChildren().addAll(borderSlideBar, root);
	}
}
