package org.ssh.ui.lua.console;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;

/**
 * Class that manages all {@link Console Consoles} by placing them in different {@link Tab tabs}
 *
 * @author Thomas Hakkers
 */
public class ConsoleManager extends UIComponent<Pane> {

    @FXML
    private Pane rootPane;

    /**
     * The {@link TabPane} that holds all tabs
     */
    @FXML
    private TabPane tabPane;
    /* number of latest created tab (current tabIndex) */
    private int currentTab = 0;

    /**
     * Constructor that manages a bunch of {@link Console consoles}.
     *
     * @param name The name of the {@link UIComponent}
     */
    public ConsoleManager(String name) {
        super(name, "bottomsection/consolemanager.fxml");

        UI.bindSize(tabPane, rootPane);

        // Open a new tab
        this.openNewTab();
        // Open a new tab when pressing CTRL + T
        EventHandlerHelper.install(tabPane.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.T, KeyCombination.CONTROL_DOWN))
                        .act(event -> this.openNewTab()).create());
        // Close a tab when pressing CTRL + W
        EventHandlerHelper.install(tabPane.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.W, KeyCombination.CONTROL_DOWN))
                        .act(event -> this.closeTab((Console) tabPane.getSelectionModel().getSelectedItem())).create());
        // Keycombination to cancel a command
        EventHandlerHelper.install(tabPane.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.C, KeyCombination.CONTROL_DOWN))
                        .act(event -> ConsoleManager.cancelTab((Console) tabPane.getSelectionModel().getSelectedItem()))
                        .create());
    }

    /**
     * Opens a new tab and gives it a fitting name
     */
    private void openNewTab() {
        String tabName = "console" + currentTab++;
        // Create a new Console, which works as a Tab
        Console console = new Console(tabName);
        console.setOnClosed(event -> closeTab(console));
        // Add the tab
        tabPane.getTabs().add(console);
        // Change focus to the newly created tab
        tabPane.getSelectionModel().select(console);
        console.requestFocus();

        // Make closing unavailable if only one tab is open
        tabPane.setTabClosingPolicy(tabPane.getTabs().size() > 1 ? TabPane.TabClosingPolicy.ALL_TABS : TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    /**
     * Close the given tab and stop any {@link Thread threads} still running in {@link Console}
     *
     * @param selectedTab The {@link Tab} to close
     */
    private void closeTab(Console selectedTab) {
        selectedTab.cancelTask();
        // Remove the selected tab
        this.tabPane.getTabs().remove(selectedTab);
        // Make closing unavailable if only one tab is open
        tabPane.setTabClosingPolicy(tabPane.getTabs().size() > 1 ? TabPane.TabClosingPolicy.ALL_TABS : TabPane.TabClosingPolicy.UNAVAILABLE);
        this.switchFocusToCurrentTab();
    }

    /**
     * Cancels the command running in the given {@link Tab}
     *
     * @param selectedTab The {@link Tab} to cancel
     * @return true on success
     */
    private static boolean cancelTab(Console selectedTab) {
        return selectedTab.cancelTask();
    }

    /**
     * Requests focus on the {@link ConsoleArea} contained by the {@link Tab}
     */
    private void switchFocusToCurrentTab() {
        Console curTab = (Console) tabPane.getSelectionModel().getSelectedItem();
        curTab.requestFocus();
    }
}
