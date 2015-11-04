package ui.sections;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import ui.UIComponent;

public class Toolbox extends UIComponent {

	@FXML
	private GridPane toolboxcontainer;
	
	Pane widgetPane = new Pane(new Label("Widgets"));
	Pane consolePane = new Pane(new Label("Console"));
	Pane luaPane = new Pane(new Label("LUA"));
	Pane settingsPane = new Pane(new Label("Settings"));
	
	public Toolbox() {
		super("toolbox", "toolbox.fxml");
		toolboxcontainer.minHeightProperty().bind(this.heightProperty());
		toolboxcontainer.maxHeightProperty().bind(this.heightProperty());
		toolboxcontainer.minWidthProperty().bind(this.widthProperty());
		toolboxcontainer.maxWidthProperty().bind(this.widthProperty());
		toolboxcontainer.add(widgetPane, 1, 0);
	}

	@FXML
	private void enrollWidgets(ActionEvent e) {
		Optional<Node> optionalNode = toolboxcontainer.getChildren().parallelStream().filter(node -> GridPane.getRowIndex(node) == 0)
				.filter(node -> GridPane.getColumnIndex(node) == 1).findFirst();
		if (optionalNode.isPresent()) {
			toolboxcontainer.getChildren().remove((Node) optionalNode.get());
			toolboxcontainer.add(widgetPane, 1, 0);
		}
	}

	@FXML
	private void enrollConsole(ActionEvent e) {
		Optional<Node> optionalNode = toolboxcontainer.getChildren().parallelStream().filter(node -> GridPane.getRowIndex(node) == 0)
				.filter(node -> GridPane.getColumnIndex(node) == 1).findFirst();
		if (optionalNode.isPresent()) {
			toolboxcontainer.getChildren().remove((Node) optionalNode.get());
			toolboxcontainer.add(consolePane, 1, 0);
		}
	}

	@FXML
	private void enrollLua(ActionEvent e) {
		Optional<Node> optionalNode = toolboxcontainer.getChildren().parallelStream().filter(node -> GridPane.getRowIndex(node) == 0)
				.filter(node -> GridPane.getColumnIndex(node) == 1).findFirst();
		if (optionalNode.isPresent()) {
			toolboxcontainer.getChildren().remove((Node) optionalNode.get());
			toolboxcontainer.add(luaPane, 1, 0);
		}
	}

	@FXML
	private void enrollSettings(ActionEvent e) {
		Optional<Node> optionalNode = toolboxcontainer.getChildren().parallelStream().filter(node -> GridPane.getRowIndex(node) == 0)
				.filter(node -> GridPane.getColumnIndex(node) == 1).findFirst();
		if (optionalNode.isPresent()) {
			toolboxcontainer.getChildren().remove((Node) optionalNode.get());
			toolboxcontainer.add(settingsPane, 1, 0);
		}
	}
}
