package org.ssh.ui.components.widget;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

/**
 * Created by joost on 1/7/16.
 */
public class TestWidget extends AbstractWidget {

    /**
     * rootPain
     */
    @FXML
    private FlowPane root;

    /**
     * Constructor for testwidget
     *
     * @param name
     *          The name
     */
    public TestWidget(String name) {
        super(name, "widget/testwidget.fxml", WidgetCategory.ANALYSIS);
        Random r = new Random();

        root.getChildren().add(new Label(name));

        root.getChildren().addAll(
                new Rectangle(100, 150,
                        Color.rgb((int) (r.nextDouble() * 255), (int) (r.nextDouble() * 255),
                                (int) (r.nextDouble() * 255))),
                new Rectangle(100, 150, Color.rgb((int) (r.nextDouble() * 255), (int) (r.nextDouble() * 255),
                        (int) (r.nextDouble() * 255))));
        root.setStyle("-fx-background-color: #6699ff;");
        root.setPrefSize(410, 510);
        root.setMinSize(300, 300);
    }
}
