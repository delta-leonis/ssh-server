package org.ssh.managers.manager;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.ssh.managers.AbstractManageable;
import org.ssh.managers.ManagerInterface;
import org.ssh.managers.controllers.UIComponentController;
import org.ssh.ui.UIComponent;
import org.ssh.ui.UIController;
import org.ssh.ui.components.widget.AbstractWidget;
import org.ssh.ui.windows.MainWindow;
import org.ssh.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The Class UI.
 *
 * This class is one of the main components of the framework. UI.java gets instantiated as
 * lazy-loaded Singleton {@link https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom}
 * . This class holds references to all the windows and makes instantiating windows easier.
 *
 * @author Rimon Oz
 * @author Jeroen de Jong
 */
public final class UI implements ManagerInterface<UIComponent<? extends Pane>> {
    
    /**
     * The services store has a controller that runs the store.
     */
    private static List<UIController<?>> uiControllers;

    private static UIComponentController controller;

    /**
     * The single instance of the class (lazy-loaded singleton).
     */
    private static final Object          instance = new Object();

    /** The Constant LOG. */
    // a logger for good measure
    private static final Logger          LOG      = Logger.getLogger();
                                                  
    /**
     * Private constructor to hide the implicit public one.
     */
    private UI() {
    }
    
    /**
     * Adds a window to the UI store.
     *
     * @param <T>
     *            The type of Pane used as a root Node for the window.
     * @param window
     *            The window to be added to the UI store.
     * @return true, if successful
     */
    public static <T extends UIController<?>> boolean addWindow(final T window) {
        UI.LOG.info("Adding new window named %s to the UI store", window.getName());
        return UI.uiControllers.add(window);
    }
    
    /**
     * Returns an Optional containing the window with the specified name.
     *
     * @param
     *            <P>
     *            the generic type
     * @param name
     *            The name of the requested window.
     * @return An Optional containing the window.
     */
    @SuppressWarnings ("unchecked")
    public static <U extends UIController<? extends Pane>> Optional<U> get(final String name) {
        UI.LOG.fine("Getting a window named %s from the UI store", name);
        // get a stream of all the windows
        return UI.uiControllers.stream()
                // filter out the window with the correct name
                .filter(controller -> controller.getName().equals(name)).map(controller -> (U) controller)
                // and return the first one in the list
                .findFirst();
    }
    
    /**
     * Gets the Singleton instance of the UI store.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return UI.instance;
    }

    public static <N extends Node> Optional<N> getByName(String name, Parent parent) {
        return (Optional<N>) UI.getAllNodes(
                UI.getHighestParent(parent)).stream()
                .filter(child -> child.getId() != null && child.getId().equals(name))
                .findAny();
    }

    public static void add(final UIComponent<?> component) {
        controller.add(component);
    }

    public <N extends UIComponent<?>> List<N> find(String pattern) {
        return controller.find(pattern);
    }

//    public <N extends Manageable> Optional<N> get(String name) {
//        return controller.get(name);
//    }

    public <N extends AbstractManageable> List<N> getAll() {
        return controller.getAll();
    }

    public static Parent getHighestParent(Parent child){
        if(child.getParent() == null)
            return child;
        return UI.getHighestParent(child.getParent());
    }

    public static List<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                UI.addAllDescendents((Parent)node, nodes);
        }
    }

    public static void addWidget(AbstractWidget widget) {
        controller.addWidget(widget);
    }

    public static List<AbstractWidget> getOrderedWidgetsForShortcuts() {
        return controller.getOrderedShortcuts();
    }

    public static List<AbstractWidget> getOrderedWidgetsForWindow() {
        return controller.getOrderedWidgetsForWindow();
    }

    public static void swapWindowOrder(int row1, int row2) {
        controller.swapWindowOrder(row1, row2);
    }

    public static void swapShortcutOrder(int col1, int col2) {
        controller.swapShortcutOrder(col1, col2);
    }

    public static int getNumberOfWidgets() {
        return controller.getNumberOfWidgets();
    }

    /**
     * Starts the UI store and spawns the main window.
     *
     * @param primaryStage
     *            The primary stage to be passed to the main window.
     * @return true, if successful
     */
    public static boolean start(final Stage primaryStage) {
        // set attributes
        UI.uiControllers = new ArrayList<UIController<?>>();
        UI.controller = new UIComponentController();

        UI.LOG.info("Instantiating new window with id mainWindow");
        // instantiate a new MainWindow and add it to the UI store
        return UI.uiControllers.add(new MainWindow("Leo regulus", primaryStage));
    }

    public static void flipImage(ImageView image) {
        image.setRotate((image.getRotate() + 180) % 360);
    }

    public static final void bindSize(Node child, Node parent) {
        if(!(child instanceof Region || child instanceof SubScene)) {
            UI.LOG.info("Could not bind child, since it isn't an instance of Region or SubScene");
            return;
        }

        if(!(parent instanceof Region || parent instanceof SubScene)) {
            UI.LOG.info("Could not bind to parent, since it isn't an instance of Region or SubScene");
            return;
        }

        //karig²
        if(child instanceof SubScene)
            if (parent instanceof SubScene)
                bindSize((SubScene) child, (SubScene) parent);
            else
                bindSize((SubScene) child, (Region) parent);
        else
            if(parent instanceof Region)
                bindSize((Region) child, (Region) parent);
            else
                bindSize((Region) child, (SubScene) parent);
    }

    private static final void bindSize(SubScene child, Region parent) {
        child.heightProperty().bind(parent.heightProperty());
        child.widthProperty().bind(parent.widthProperty());
    }

    private static final void bindSize(SubScene child, SubScene parent) {
        child.heightProperty().bind(parent.heightProperty());
        child.widthProperty().bind(parent.widthProperty());
    }

    public static final void bindSize(Region child, SubScene parent) {
        child.maxHeightProperty().bind(parent.heightProperty());
        child.minHeightProperty().bind(parent.heightProperty());
        child.maxWidthProperty().bind(parent.widthProperty());
        child.minWidthProperty().bind(parent.widthProperty());
    }

    public static final void bindSize(Region child, Region parent) {
        child.maxHeightProperty().bind(parent.heightProperty());
        child.minHeightProperty().bind(parent.heightProperty());
        child.maxWidthProperty().bind(parent.widthProperty());
        child.minWidthProperty().bind(parent.widthProperty());
    }
}
