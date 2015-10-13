package examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ui.lua.console.AvailableInLua;
import ui.lua.console.Console;

/**
 * Example that runs a {@link Console}
 * The {@link Console} has access to every class that uses the {@link AvailableInLua} annotation
 * It features the following:
 * <ul>
 * 	<li> Autocomplete </li>
 *  <li> Function and Object highlights </li>
 *  <li> Command history (Use up and down keys) </li>
 * </ul>
 * 
 * Remember: It's a lua console, so if you wanna call an object's function, it's called like object:function() (not object.function())
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
