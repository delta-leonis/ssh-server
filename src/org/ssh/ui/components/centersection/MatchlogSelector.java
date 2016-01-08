package org.ssh.ui.components.centersection;

import org.ssh.ui.UIComponent;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class MatchlogSelector extends UIComponent {
    
	@FXML
	private GridPane rootPane;

    public MatchlogSelector() {
        super("matchlogselector", "centersection/matchlogselector.fxml");
    }
}
