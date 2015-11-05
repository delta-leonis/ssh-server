package examples;

import java.io.File;

import org.ssh.ui.lua.editor.Animal;
import org.ssh.ui.lua.editor.IReloadable;
import org.ssh.ui.lua.editor.ScriptEditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Example that opens several {@link ScriptEditor Editors}.
 * Pressing the save button doesn't only save the file, but also reloads the class its connected to
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *
 */
public class EditorExample extends Application{
	private int width = 600;
	private int height = 400;
	private static final String path = "resource/examples/scripteditor/";
	
	/**
	 * Initializes every lua script in the folder "resource/examples/scripteditor/" and runs the "create" function.
	 */
	public static void main(String args[]){
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		if(listOfFiles != null){
			for (int i = 0; i < listOfFiles.length; i++) {
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
	
	/**
	 * Shows the given {@link IReloadable} class in a {@link ScriptEditor}
	 */
	public void showInEditorWindow(IReloadable reloadable){
		ScriptEditor root = new ScriptEditor(reloadable);
		Stage stage = new Stage();
        stage.setTitle(reloadable.getPath() + " Editor");
        stage.setScene(new Scene(root, width, height));
        stage.show();
	}
}
