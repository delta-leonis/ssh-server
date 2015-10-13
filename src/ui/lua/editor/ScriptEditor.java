package ui.lua.editor;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextArea;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import ui.lua.console.ColoredCodeArea;

/**
 * Test Module for loading and editing scripts into a {@link ColoredCodeArea}
 * The file gets saved automatically on close.
 */
public class ScriptEditor extends VBox{
	private ColoredCodeArea codeArea;
	private IReloadable reloadable;
	private String path;
	private String styleSheet = "/css/java-keywords.css";
	
	/**
	 * Creates a {@link ScriptEditor} and fills the codeArea with the code the {@link IReloadable} is pointing to.
	 * @param reloadable The {@link IReloadable} the editor needs to present.
	 */
	public ScriptEditor(IReloadable reloadable){
		this.reloadable = reloadable;
		initializeMenu();
		initializeTextArea();
		setTextFromFile(reloadable.getPath());
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
	
	private void initializeMenu(){
		MenuBar menubar = new MenuBar();
		
		Menu fileMenu = new Menu("File");
		MenuItem saveItem = new MenuItem("Save\t\t\t");
		fileMenu.getItems().add(saveItem);
		saveItem.setOnAction(e -> saveFile(path));
		
		menubar.getMenus().addAll(fileMenu);
		getChildren().add(menubar);
	}
	
	/**
	 * Prerequisites: {@link #initializeTextArea(String)} has to be called at least once before this function is called.
	 * @param path The path to the file that needs to be copied into the {@link ColoredCodeArea}
	 */
	public void setTextFromFile(String path){
		this.path = path;
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
	 */
	public void saveFile(String path){
		try {
			FileWriter writer = new FileWriter(path);
			writer.write(codeArea.getText());
			writer.close();
			if(reloadable != null)
				reloadable.reload();
		} catch (IOException e) {
			// TODO Logger
			e.printStackTrace();
		}
	}
	
	/**
	 * @returns the path of what this {@link ScriptEditor} is currently editing.
	 */
	public String getPath(){
		return path;
	}
}
