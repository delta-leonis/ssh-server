package org.ssh.ui.lua.editor;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextArea;

import org.ssh.ui.lua.console.ColoredCodeArea;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

/**
 * Test Module for loading and editing scripts into a {@link ColoredCodeArea} The file gets saved
 * automatically on close.
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *         
 */
public class ScriptEditor extends VBox {
    
    private ColoredCodeArea   codeArea;
    private final IReloadable reloadable;
    private String            path;
    private final String      styleSheet = "/css/java-keywords.css";
                                         
    /**
     * Creates a {@link ScriptEditor} and fills the codeArea with the code the {@link IReloadable}
     * is pointing to.
     * 
     * @param reloadable
     *            The {@link IReloadable} the editor needs to present.
     */
    public ScriptEditor(final IReloadable reloadable) {
        this.reloadable = reloadable;
        this.initializeMenu();
        this.initializeTextArea();
        this.setTextFromFile(reloadable.getPath());
    }
    
    /**
     * @returns the path of what this {@link ScriptEditor} is currently editing.
     */
    public String getPath() {
        return this.path;
    }
    
    private void initializeMenu() {
        final MenuBar menubar = new MenuBar();
        
        final Menu fileMenu = new Menu("File");
        final MenuItem saveItem = new MenuItem("Save\t\t\t");
        fileMenu.getItems().add(saveItem);
        saveItem.setOnAction(e -> this.saveFile(this.path));
        
        menubar.getMenus().addAll(fileMenu);
        this.getChildren().add(menubar);
    }
    
    /**
     * Initializes the {@link ColoredCodeArea}
     */
    private void initializeTextArea() {
        this.codeArea = new ColoredCodeArea();
        this.codeArea.setupColoredCodeArea(this.styleSheet, null, null);
        this.codeArea.prefWidthProperty().bind(this.widthProperty());
        this.codeArea.prefHeightProperty().bind(this.heightProperty());
        this.getChildren().add(this.codeArea);
    }
    
    /**
     * Saves the file, and then reloads the given object.
     * 
     * @param path
     *            Path to the file to be displayed and edited by the {@link JTextArea} (Relative and
     *            absolute both work)
     */
    public void saveFile(final String path) {
        try {
            final FileWriter writer = new FileWriter(path);
            writer.write(this.codeArea.getText());
            writer.close();
            if (this.reloadable != null) this.reloadable.reload();
        }
        catch (final IOException e) {
            // TODO Logger
            e.printStackTrace();
        }
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
                this.codeArea.insertText(this.codeArea.getLength(), "" + ((char) c));
            }
            in.close();
        }
        catch (final IOException e) {
            // TODO: logger
            e.printStackTrace();
        }
    }
}
