package examples;


import java.util.ArrayList;

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
import ui.sections.BorderSlideBar;

public class BorderSlideBarExample extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane borderPane = new BorderPane();
        StackPane stackPane = new StackPane();
        
        Label testLabel = new Label("Kippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\n");
        testLabel.setStyle("-fx-background-color: orange;");
        
        Scene scene = new Scene(stackPane);
        stage.setTitle("Game Logs");
        stage.setWidth(600);
        stage.setHeight(431);
        
        Button button = new Button("Activate!");        
        borderPane.setTop(button);
        
        Label blueLabel = new Label("MIAUW.");
        blueLabel.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");
        BorderSlideBar bottomFlapBar = new BorderSlideBar(100, button, Pos.BOTTOM_CENTER, blueLabel);
        borderPane.setBottom(bottomFlapBar);
        
        BorderSlideBar rightFlapBar = new BorderSlideBar(100, Pos.CENTER_RIGHT, new GameLogSection(30), false);
        borderPane.setRight(rightFlapBar);
        
        BorderSlideBar leftFlapBar = new BorderSlideBar(100, Pos.CENTER_LEFT, new GameLogSection(30), true);
        borderPane.setLeft(leftFlapBar);
        
        stackPane.getChildren().addAll(testLabel,borderPane);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    
    /** *********************************** */
    /* ********* Private Classes ********** */
    /** *********************************** */

    private class GameLogSection extends VBox{
    	private ArrayList<GameLog> gamelogs;
    	private Button showMatchButton;
    	
    	/**
    	 * Constructor for the GameLogSection
    	 * @param expandsize How far the section is allowed to move to the right
    	 * @param charLimit The amount of characters per item in the list (required for spacing. Recommended to use a font with the same character size for every character.)
    	 */
    	public GameLogSection(int charLimit){
        	setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");

    		gamelogs = new ArrayList<GameLog>();
    		init();
    		
    		showMatchButton = new Button("Show Match");
    		showMatchButton.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");
    		showMatchButton.setMinWidth(0);
    		showMatchButton.setPrefHeight(50);
    		showMatchButton.setMinHeight(0);
    		showMatchButton.prefWidthProperty().bind(minWidthProperty());
    		
    		ListView<String> listView = new ListView<String>();
    		listView.setPadding(new Insets(0,0,0,0));
    		listView.prefHeightProperty().bind(heightProperty());
    		listView.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");


    		
    		listView.setStyle("-fx-font: 9pt \"Consolas\";");

    		ObservableList<String> items =FXCollections.observableArrayList ();
    		for(GameLog game : gamelogs){
    			String s = game.getFilename();
    			for(int i = 0; i < charLimit - game.getFilename().length() - game.getLength().length(); ++i){
    				s+= " ";
    			}
    			s+=game.getLength();
    			items.add(s);
    		}
    		listView.setItems(items);
    		

    		getChildren().addAll(showMatchButton, listView);
    		
    	}
    	
    	public void refresh(){
    		// TODO Make a refresh function that adds new items to the list when created
    	}
    	
    	public void init(){
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		gamelogs.add(new GameLog("SSH - ERF", 34782));
    		gamelogs.add(new GameLog("RoboFei - MRL", 498293));
    		gamelogs.add(new GameLog("Kip - Kip", 3));
    		
    	}
    }
    	
  	/**
   	 * Dummy class for the game logs
   	 */
   	private class GameLog {
   		private String filename;
   		private int length;
    		
   		/**
   		 * Constructor of the GameLog
   		 * @param filename Name of the file
   		 * @param length Length in seconds
   		 */
   		public GameLog(String filename, int length){
   			this.filename = filename;
   			this.length = length;
   		}
   		
   		public String getFilename(){
   			return filename;
   		}
    		
   		public String getLength(){
   			int hours = length / 3600;
   			int minutes = (length - hours * 3600) / 60;
   			int seconds = length % 60;
   			return hours + ":" + minutes + ":" + seconds;
   		}
   	}
}