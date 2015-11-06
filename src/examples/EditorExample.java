package examples;

import java.io.File;

import org.ssh.ui.lua.editor.Animal;
import org.ssh.ui.lua.editor.IReloadable;
import org.ssh.ui.lua.editor.ScriptEditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Example that opens several {@link ScriptEditor Editors}. Pressing the save button doesn't only
 * save the file, but also reloads the class its connected to
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *        
 */
public class EditorExample extends Application {
    
    private static final String path = "resource/examples/scripteditor/";
    
    /**
     * Initializes every lua script in the folder "resource/examples/scripteditor/" and runs the
     * "create" function.
     */
    public static void main(final String args[]) {
        Application.launch();
    }
    
    private final int width  = 600;
                             
    private final int height = 400;
                             
    /**
     * Shows the given {@link IReloadable} class in a {@link ScriptEditor}
     */
    public void showInEditorWindow(final IReloadable reloadable) {
        final ScriptEditor root = new ScriptEditor(reloadable);
        final Stage stage = new Stage();
        stage.setTitle(reloadable.getPath() + " Editor");
        stage.setScene(new Scene(root, this.width, this.height));
        stage.show();
    }
    
    @Override
    public void start(final Stage primaryStage) throws Exception {
        final File folder = new File(EditorExample.path);
        final File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (final File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    this.showInEditorWindow(new Animal(listOfFile.getPath()));
                }
            }
        }
        else {
            System.err.println("Couldn't find files");
            System.exit(-1);
        }
    }
}
