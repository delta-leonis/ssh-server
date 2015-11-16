package examples;

import java.util.ArrayList;

import org.ssh.ui.components.BorderSlideBar;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Example that shows the three different ways you can use the {@link BorderSlideBar}
 * <ol>
 * <li>A {@link BorderSlideBar} with a <i>preassigned button</i> </br>
 * The following constructor is used for this: BorderSlideBar(expandedSize, button, Position, Node);
 * </li>
 * <li>A {@link BorderSlideBar} with a button that <i>stays stationary</i> The following constructor
 * is used for this: BorderSlideBar(expandedSize, Position, Node, true);</li>
 * <li>A {@link BorderSlideBar} with a button that <i>moves with the {@link BorderSlideBar} </i> The
 * following constructor is used for this: BorderSlideBar(expandedSize, Position, Node, false);</li>
 * <li>A {@link BorderSlideBar} with a <i>preassigned button</i> </br>
 * The following constructor is used for this: BorderSlideBar(expandedSize, button, Position, Node,
 * Runnable); Use this constructor if you want the Button's ActionListener to perform an extra
 * function before the SlideBar operates</li>
 * </ol>
 *
 * Notes have been put at the appropriate places to see the code snippets above in action
 *
 * @author Thomas Hakkers
 *         
 */
public class BorderSlideBarExample extends Application {
    
    /**
     * Dummy class for the game logs
     */
    private class GameLog {
        
        private final String filename;
        private final int    length;
                             
        /**
         * Constructor of the GameLog
         * 
         * @param filename
         *            Name of the file
         * @param length
         *            Length in seconds
         */
        public GameLog(final String filename, final int length) {
            this.filename = filename;
            this.length = length;
        }
        
        public String getFilename() {
            return this.filename;
        }
        
        public String getLength() {
            final int hours = this.length / 3600;
            final int minutes = (this.length - (hours * 3600)) / 60;
            final int seconds = this.length % 60;
            return hours + ":" + minutes + ":" + seconds;
        }
    }
    
    /**
     * Sample section representing a list with {@link GameLog Gamelogs}
     *
     * @author Thomas Hakkers
     *         
     */
    private class GameLogSection extends VBox {
        
        private final ArrayList<GameLog> gamelogs;
        private final Button             showMatchButton;
                                         
        /**
         * Constructor for the GameLogSection
         * 
         * @param expandsize
         *            How far the section is allowed to move to the right
         * @param charLimit
         *            The amount of characters per item in the list (required for spacing.
         *            Recommended to use a font with the same character size for every character.)
         */
        public GameLogSection(final int charLimit) {
            this.gamelogs = new ArrayList<GameLog>();
            this.init();
            
            // Setup button
            this.showMatchButton = new Button("Show Match");
            this.showMatchButton.setMinWidth(0);
            this.showMatchButton.setPrefHeight(50);
            this.showMatchButton.setMinHeight(0);
            this.showMatchButton.prefWidthProperty().bind(this.minWidthProperty());
            
            // Setup ListView
            final ListView<String> listView = new ListView<String>();
            listView.setPadding(new Insets(0, 0, 0, 0));
            listView.prefHeightProperty().bind(this.heightProperty());
            listView.setStyle("-fx-font: 9pt \"Consolas\";");
            
            // Setup ObservableList for ListView
            final ObservableList<String> items = FXCollections.observableArrayList();
            for (final GameLog game : this.gamelogs) {
                String s = game.getFilename();
                for (int i = 0; i < (charLimit - game.getFilename().length() - game.getLength().length()); ++i) {
                    s += " ";
                }
                s += game.getLength();
                items.add(s);
            }
            listView.setItems(items);
            
            this.getChildren().addAll(this.showMatchButton, listView);
        }
        
        /**
         * Creates random {@link GameLog Gamelogs}
         */
        public void init() {
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            this.gamelogs.add(new GameLog("SSH - ERF", 34782));
            this.gamelogs.add(new GameLog("RoboFei - MRL", 498293));
            this.gamelogs.add(new GameLog("Kip - Kip", 3));
            
        }
    }
    
    /** *********************************** */
    /* ********* Private Classes ********** */
    /** *********************************** */
    
    public static void main(final String[] args) {
        Application.launch(args);
    }
    
    @Override
    public void start(final Stage stage) {
        final BorderPane borderPane = new BorderPane();
        final StackPane stackPane = new StackPane();
        
        final Label testLabel = new Label(
                "Kippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\n");
        testLabel.setStyle("-fx-background-color: orange;");
        
        final Scene scene = new Scene(stackPane);
        stage.setTitle("Game Logs");
        stage.setWidth(600);
        stage.setHeight(430);
        
        final Button button = new Button("Activate!");
        borderPane.setTop(button);
        
        final Label blueLabel = new Label("Hallo. Ik ben een kip.");
        blueLabel.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");
        
        final Button buttonForTop = new Button("Top");
        
        // See note 1 in {@link BorderSlideBarExample}
        final BorderSlideBar bottomFlapBar = new BorderSlideBar(100, button, Pos.BOTTOM_CENTER, blueLabel);
        borderPane.setBottom(bottomFlapBar);
        
        // See note 2 in {@link BorderSlideBarExample}
        final BorderSlideBar leftFlapBar = new BorderSlideBar(100,
                Pos.CENTER_LEFT,
                /* new GameLogSection(30) */ buttonForTop,
                true);
        borderPane.setLeft(leftFlapBar);
        
        // See note 3 in {@link BorderSlideBarExample}
        final BorderSlideBar rightFlapBar = new BorderSlideBar(100, Pos.CENTER_RIGHT, new GameLogSection(30), false);
        borderPane.setRight(rightFlapBar);
        
        // See note 4 in {@link BorderSlideBarExample}
        final BorderSlideBar topFlapBar = new BorderSlideBar(100,
                buttonForTop,
                Pos.TOP_CENTER,
                button,
                () -> System.out.println("Extra Function"));
        borderPane.setTop(topFlapBar);
        
        stackPane.getChildren().addAll(testLabel, borderPane);
        stage.setScene(scene);
        stage.show();
    }
}