package org.ssh.ui.components.widget;

import java.util.Random;

import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ExampleWidget extends FlowPane {

    public ExampleWidget() {
        Random r = new Random();
        this.getChildren().addAll(
                new Rectangle(100, 150,
                        Color.rgb((int) (r.nextDouble() * 255), (int) (r.nextDouble() * 255),
                                (int) (r.nextDouble() * 255))),
                new Rectangle(100, 150, Color.rgb((int) (r.nextDouble() * 255), (int) (r.nextDouble() * 255),
                        (int) (r.nextDouble() * 255))));
        this.setStyle("-fx-background-color: #6699ff;");
        this.setPrefSize(410, 510);
        this.setMinSize(300, 300);
    }
}
