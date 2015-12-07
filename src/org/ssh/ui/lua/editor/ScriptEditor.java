package org.ssh.ui.lua.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextArea;

import org.ssh.ui.UIComponent;
import org.ssh.ui.lua.console.ColoredCodeArea;
import org.ssh.util.Logger;
import org.ssh.util.LuaUtils;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Test Module for loading and editing scripts into a {@link ColoredCodeArea} The file gets saved
 * automatically on close.
 *
 * @author Thomas Hakkers
 *         
 */
public class ScriptEditor extends UIComponent {
    
    // A logger for errorhandling
    private static final Logger LOG        = Logger.getLogger();
                                           
    private ColoredCodeArea     codeArea;
    private String              path;
    private FileChooser         fileChooser;
    private VBox                root;
                                
    /**
     * Constructor for the {@link ScriptEditor}
     * @param name Name of the {@link ScriptEditor}
     */
    public ScriptEditor(final String name) {
        super(name, "scripteditor.fxml");
        this.root = new VBox();
        root.minHeightProperty().bind(this.heightProperty());
        root.maxHeightProperty().bind(this.heightProperty());
        root.minWidthProperty().bind(this.widthProperty());
        root.maxWidthProperty().bind(this.widthProperty());
        
        this.initializeMenu();
        this.initializeTextArea();
        
        this.getChildren().add(root);
        // Create a FileChooser for future use
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("File Chooser");
        this.fileChooser.getExtensionFilters().add(new ExtensionFilter("All Files", "*.*"));
    }
    
    /**
     * @returns the path of what this {@link ScriptEditor} is currently editing.
     */
    public String getPath() {
        return this.path;
    }
    
    /**
     * Initializes the {@link MenuBar} of the {@link ScriptEditor}, adding the right buttons
     */
    private void initializeMenu() {
        final MenuBar menubar = new MenuBar();
        
        final Menu fileMenu = new Menu("File");
        
        final MenuItem saveItem = new MenuItem("Save\t\t\t");
        fileMenu.getItems().add(saveItem);
        saveItem.setOnAction(actionEvent -> this.saveFile());
        
        final MenuItem saveAsItem = new MenuItem("Save as...\t\t\t");
        fileMenu.getItems().add(saveAsItem);
        saveAsItem.setOnAction(actionEvent -> this.saveAsFile());
        
        final MenuItem openItem = new MenuItem("Open\t\t\t");
        fileMenu.getItems().add(openItem);
        openItem.setOnAction(actionEvent -> this.openFile());
        
        menubar.getMenus().addAll(fileMenu);
        this.root.getChildren().add(menubar);
    }
    
    /**
     * Initializes the {@link ColoredCodeArea}
     */
    private void initializeTextArea() {
        // Set up colored code area
        this.codeArea = new ColoredCodeArea();
        this.codeArea.setupColoredCodeArea(LuaUtils.getLuaClasses(),
                LuaUtils.getLuaFunctions());
        this.codeArea.prefWidthProperty().bind(root.widthProperty());
        this.codeArea.prefHeightProperty().bind(root.heightProperty());
        this.root.getChildren().add(this.codeArea);
    }
    
    /**
     * Saves the file, and then reloads the given object.
     *
     * @see {@link #saveAsFile()}
     */
    public void saveFile() {
        try {
            // Automatically saveAs if the path was null
            if (this.path == null) {
                saveAsFile();
            }
            // If the path wasn't null, save the file
            else {
                final FileWriter writer = new FileWriter(this.path);
                writer.write(this.codeArea.getText());
                writer.close();
            }
        }
        catch (final IOException exception) {
            LOG.exception(exception);
        }
    }
    
    /**
     * Save the {@link File} at the location that can be chosen from the {@link DirectoryChooser}
     * 
     * @see {@link #saveAsFile()}
     */
    private void saveAsFile() {
        // Select path
        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        // Only save file if path != null to avoid recursion
        if (file != null) {
            this.path = file.getAbsolutePath();
            saveFile();
        }
    }
    
    /**
     * Opens the {@link File} found using the {@link FileChooser}
     */
    public void openFile() {
        // Select path
        File file = fileChooser.showOpenDialog(this.getScene().getWindow());
        // Set the text of the textarea if a file has been found.
        if (file != null) setTextFromFile(file.getAbsolutePath());
    }
    
    /**
     * Prerequisites: {@link #initializeTextArea(String)} has to be called at least once before this
     * function is called.
     * 
     * @param path
     *            The path to the file that needs to be copied into the {@link ColoredCodeArea}
     */
    public void setTextFromFile(final String path) {
        this.path = path;
        FileInputStream in;
        try {
            in = new FileInputStream(path);
            int c;
            while ((c = in.read()) != -1) {
                this.codeArea.insertText(this.codeArea.getLength(), "" + Character.toString((char) c));
            }
            in.close();
        }
        catch (final IOException exception) {
            LOG.exception(exception);
        }
    }
}
