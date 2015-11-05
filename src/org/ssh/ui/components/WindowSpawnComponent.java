package org.ssh.ui.components;

import org.ssh.managers.UI;
import org.ssh.ui.UIComponent;
import org.ssh.ui.windows.WidgetWindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * The WindowSpawnComponent Class.
 *
 * @author Rimon Oz
 */
public class WindowSpawnComponent extends UIComponent {

    /** The spawn window button. It's defined in /org.ssh.view/components/windowspawn.fxml*/
    @FXML
    protected Button spawnWindowButton;

    /**
     * Instantiates a new window spawn component.
     *
     * @param name The name of the component.
     */
    public WindowSpawnComponent(final String name) {
        super(name, "windowspawn.fxml");
    }

    /**
     * Window spawn button action.
     * 
     * Because the Button has an onAction="#windowSpawnButtonAction" set in the FXML-file
     * this method will get triggered on every click of that specific button.
     *
     * @param buttonEvent The button event.
     */
    public void windowSpawnButtonAction(final ActionEvent buttonEvent) {
        System.out.println("got here");
        UI.addWindow(new WidgetWindow("gekkehenkie"));
    }

}
