package org.ssh.ui.lua.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.ssh.ui.UIComponent;
import org.ssh.ui.lua.console.ColoredCodeArea;
import org.ssh.util.Logger;
import org.ssh.util.LuaUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * Test Module for loading and editing scripts into a {@link ColoredCodeArea} The file gets saved
 * automatically on close.
 *
 * @author Thomas Hakkers
 *         
 */
public class ScriptEditor extends UIComponent {
    private ScriptArea          codeArea;
    private String              path;
    private FileChooser         fileChooser;
    @FXML
    private VBox                root;
                                
    /**
     * Constructor for the {@link ScriptEditor}
     * @param name Name of the {@link ScriptEditor}
     */
    public ScriptEditor(final String name) {
        super(name, "bottomsection/scripteditor.fxml");
        
        this.initializeMenu();
        this.initializeTextArea();

        // Create a FileChooser for future use
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("File Chooser");
        this.fileChooser.getExtensionFilters().add(new ExtensionFilter("All Files", "*.*"));
        this.fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
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
        saveItem.setOnAction(actionEvent -> this.saveFile());
        
        final MenuItem saveAsItem = new MenuItem("Save as...\t\t\t");
        saveAsItem.setOnAction(actionEvent -> this.saveAsFile());
        
        final MenuItem openItem = new MenuItem("Open\t\t\t");
        openItem.setOnAction(actionEvent -> this.openFile());

        final MenuItem runItem = new MenuItem("Run\t\t\t");
        saveItem.setOnAction(actionEvent -> this.codeArea.runScript(this.codeArea.getText()));

        fileMenu.getItems().addAll(runItem, saveItem, saveAsItem, openItem);
        menubar.getMenus().addAll(fileMenu);
        this.root.getChildren().add(menubar);
    }
    
    /**
     * Initializes the {@link ColoredCodeArea}
     */
    private void initializeTextArea() {
        // Set up colored code area
        this.codeArea = new ScriptArea();
        this.codeArea.setupColoredCodeArea(LuaUtils.getLuaClasses(),
                LuaUtils.getLuaFunctions());
        this.codeArea.prefWidthProperty().bind(root.widthProperty());
        this.codeArea.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(this.codeArea);
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
        File file = fileChooser.showSaveDialog(root.getScene().getWindow());
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
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        // Set the text of the textarea if a file has been found.
        if (file != null)
            setTextFromFile(file.getAbsolutePath());
    }
    
    /**
     * Puts the text from a certain file into the {@link ScriptEditor text area}
     * 
     * @param path
     *            The path to the file that needs to be copied into the {@link ColoredCodeArea}
     */
    public void setTextFromFile(final String path) {
        this.path = path;
        this.codeArea.clear();
        try {
            Files.lines(FileSystems.getDefault().getPath(path)).forEach(line ->
                    this.codeArea.insertText(this.codeArea.getLength(), line + '\n'));
        }
        catch (final IOException exception) {
            LOG.exception(exception);
        }
    }
}
