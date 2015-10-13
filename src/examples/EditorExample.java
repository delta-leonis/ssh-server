package examples;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.lua.editor.Animal;
import ui.lua.editor.IReloadable;
import ui.lua.editor.ScriptEditor;

public class EditorExample extends Application{
	private int width = 600;
	private int height = 400;
	
	/**
	 * Initializes every lua script in the folder "scripts/" and runs the "create" function.
	 */
	public static void main(String args[]){
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		File folder = new File("resource/examples/scripteditor/");
		File[] listOfFiles = folder.listFiles();
		if(listOfFiles != null){
			System.out.println("Length: " + listOfFiles.length);
			for (int i = 0; i < listOfFiles.length; i++) {
				System.out.println("Path: " + listOfFiles[i].getPath());
				if (listOfFiles[i].isFile()) {
					showInEditorWindow(new Animal(listOfFiles[i].getPath()));
				} 
			}
		}
		else{
			System.err.println("Couldn't find files");
			System.exit(-1);
		}
	}
	
	public void showInEditorWindow(IReloadable reloadable){
		ScriptEditor root = new ScriptEditor(reloadable);
		Stage stage = new Stage();
        stage.setTitle(reloadable.getPath() + " Editor");
        stage.setScene(new Scene(root, width, height));
        stage.show();
	}
}
