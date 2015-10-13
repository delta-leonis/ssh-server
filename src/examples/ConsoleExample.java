package examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ui.lua.console.Console;

/**
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *
 */
public class ConsoleExample extends Application{
	private int width = 600;
	private int height = 400;
	private String title = "Lua Console";
	
	public static void main(String args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(title);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        
        Console console = new Console();
        
        // Create base
        Pane root = new Pane();
        Scene scene = new Scene(root, width, height, Color.WHITE);
        console.prefWidthProperty().bind(root.widthProperty());
        console.prefHeightProperty().bind(root.heightProperty());
        
        // Add TextArea
        root.getChildren().add(console);  
        primaryStage.setScene(scene);
        primaryStage.show();
	}
}
