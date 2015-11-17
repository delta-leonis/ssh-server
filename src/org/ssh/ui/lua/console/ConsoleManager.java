package org.ssh.ui.lua.console;

import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.ssh.ui.UIComponent;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;

/**
 * Class that manages all {@link Console Consoles} by placing them in different {@link Tab tabs}
 * 
 * @author Thomas Hakkers
 */
public class ConsoleManager extends UIComponent{
	/** The {@link TabPane} that holds all tabs */
	@FXML
	private TabPane tabPane;
	
	private int currentTab = 0;
	
	/**
	 * Constructor that manages a bunch of {@link Console consoles}.
	 * @param name The name of the {@link UIComponent}
	 */
	public ConsoleManager(String name){
		super(name, "consolemanager.fxml");
		// Open a new tab
		this.openNewTab();
		// Open a new tab when pressing CTRL + T
		EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.T, KeyCombination.CONTROL_DOWN))
                        .act(event -> this.openNewTab()).create());
		// Open a new tab when pressing CTRL + T
		EventHandlerHelper.install(this.onKeyPressedProperty(),
		        EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.W, KeyCombination.CONTROL_DOWN))
		                .act(event -> this.closeTab(tabPane.getSelectionModel().getSelectedItem())).create());
	}
	
	/**
	 * Opens a new tab and gives it a fitting name
	 */
	private void openNewTab(){
		String tabName = "console" + currentTab++;
		// Create a new tab, and make sure it has the correct size
        Tab tab = new Tab(tabName);
        tabPane.minHeightProperty().bind(this.heightProperty());
        tabPane.maxHeightProperty().bind(this.heightProperty());
        tabPane.minWidthProperty().bind(this.widthProperty());
        tabPane.maxWidthProperty().bind(this.widthProperty());
        
        Console console = new Console(tabName);
        // Assign a Console to the tab
        tab.setContent(console);
        tab.setOnClosed(event -> closeTab(tab));
        // Add the tab
        tabPane.getTabs().add(tab);
        // Change focus to the newly created tab
		tabPane.getSelectionModel().select(tab);
		console.requestFocus();
	}
	
	/**
	 * Close the given tab and stop any {@link Thread threads} still running in {@link Console}
	 * @param selectedTab The {@link Tab} to close
	 */
	private void closeTab(Tab selectedTab){
		((Console)selectedTab.getContent()).cancel();
		// Remove the selected tab
		tabPane.getTabs().remove(selectedTab);
		// Change focus to the newly created tab
		tabPane.requestFocus();
	}
}
