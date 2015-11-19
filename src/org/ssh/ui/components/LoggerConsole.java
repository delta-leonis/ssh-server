package org.ssh.ui.components;

import org.ssh.ui.UIComponent;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class LoggerConsole extends UIComponent {

	@FXML
	private GridPane rootPane;

	public LoggerConsole() {
		super("loggerconsole", "loggerconsole.fxml");
		rootPane.minHeightProperty().bind(this.heightProperty());
		rootPane.maxHeightProperty().bind(this.heightProperty());
		rootPane.minWidthProperty().bind(this.widthProperty());
		rootPane.maxWidthProperty().bind(this.widthProperty());
	}
}
