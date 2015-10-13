package ui.lua.editor;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextArea;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import ui.lua.console.ColoredCodeArea;

/**
 * Test Module for loading and editing scripts into a {@link JTextArea}
 * The file gets saved automatically on close.
 */
public class ScriptEditor extends Pane{
	private ColoredCodeArea codeArea;
	private IReloadable reloadable;
	private String path;
	private Scene scene;
	private String styleSheet = "/css/java-keywords.css";
	
	public ScriptEditor(IReloadable reloadable){
		this.reloadable = reloadable;
		initializeTextArea();
		setTextFromFile(path = reloadable.getPath());
	}
	
	/**
	 * Initializes the {@link ColoredCodeArea}
	 */
	private void initializeTextArea(){
		codeArea = new ColoredCodeArea(styleSheet, null, null);
        codeArea.prefWidthProperty().bind(widthProperty());
        codeArea.prefHeightProperty().bind(heightProperty());
        getChildren().add(codeArea);
	}
	
	/**
	 * Prerequisites: {@link #initializeTextArea(String)} has to be called at least once before this function is called.
	 * @param path The path to the file that needs to be copied into the {@link ColoredCodeArea}
	 */
	public void setTextFromFile(String path){
		FileInputStream in;
        try {
			in = new FileInputStream(path);
			int c;
			while((c = in.read()) != -1){
				codeArea.insertText(codeArea.getLength(), "" + ((char)c));
			}
			in.close();
		} catch (IOException e) {
			// TODO: logger
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the file, and then reloads the given object.
	 * @param path Path to the file to be displayed and edited by the {@link JTextArea} (Relative and absolute both work)
	 * @param reload True if the {@link IReloadable} object needs to be reloaded after saving
	 */
	public void saveFile(String path, boolean reload){
		try {
			FileWriter writer = new FileWriter(path);
			writer.write(codeArea.getText());
			writer.close();
			reloadable.reload();
		} catch (IOException e) {
			// TODO Logger
			e.printStackTrace();
		}
	}
	
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		primaryStage.setTitle("Lua Editor");
//		path = "scripts/initObjects/chicken.lua";
//
//        // Create base
//        FlowPane root = new FlowPane();
//        scene = new Scene(root, width, height, Color.WHITE);
//        
//        // Create TextArea
//        initiliazeTextArea(path, root);
//        
//
//        // Add TextArea
//        root.getChildren().add(codeArea);        
//        primaryStage.setScene(scene);
//        primaryStage.show();
//	}
}
