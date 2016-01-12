package org.ssh.ui.components.topsection;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.ssh.models.Robot;
import org.ssh.ui.UIComponent;

import java.util.stream.Collectors;

/**
 * this class contains all the functionallity behind the robotstatusbox. The layout is defined in
 * ssh-server/src/org/ssh/view/components/robotstatus.fxml. The style is defined in
 * ssh-server/src/org/ssh/view/css/application.css.
 *
 * @author Ryan Meulenkamp
 */
public class RobotStatus extends UIComponent {

    /**
     * the data model behind a robot.
     */
    private Robot model;
    /**
     * the root pane containing all other panes and other UI stuff this should change color
     * according to the status
     */
    @FXML
    private GridPane robotstatusRoot;
    /**
     * this pane will be used to color the robot-status-box: With no errors it will be green If a
     * warning is present, it will be yellow If an error is present, it will be red
     */
    @FXML
    private Pane robotIconBackground;
    /**
     * this pane will be used to give the border a color: blue when the status-box is selected grey
     * when the status-box is unselected
     * <p>
     * it is chosen to be an anchorpane because it contains an empty label which should be streched
     * over the full pane. This label is a control, so it can contain a tooltip.
     */
    @FXML
    private AnchorPane robotIconBorder;
    /**
     * these panes will be used to display icons representing connection and camera sight status.
     */
    @FXML
    private Pane connectionIcon, sightIcon;
    /**
     * An text-shape for displaying the id of this robot. It is chosen to be a text-shape instead of
     * a label to make resizing more easy.
     */
    @FXML
    private Text robotId;
    /**
     * The tooltip of the robot. It is used to diplay information of the robot, like malfunctions
     * and position, when the mouse is over this robot-status-box.
     */
    @FXML
    private Tooltip robotTooltip;

    public RobotStatus(Robot model) {
        super("Robot status", "topsection/robotstatus.fxml");

        this.model = model;

        // Two ways of using properties are shown ( using invalidation-listeners and binding a
        // single property:

        // here, a invalidation-listener is used. When one of properties in a binding (or the only
        // property present) is changed, it is also invalidated. Now the binding or property will
        // only get validated when its value is withdrawn. This is also the only moment when it is
        // recalculated (lazy loading). It is also possible to add an listener to the property or
        // binding, so an invalidation-event is raised when it is modified.
        model.isSelectedProperty().addListener(
                new BooleanStylePropertyHandler(robotIconBorder, "statusbox-selected", "statusbox-unselected"));
        model.isConnectedProperty()
                .addListener(new BooleanStylePropertyHandler(connectionIcon, "connection-icon", "noconnection-icon"));
        model.isOnSightProperty().addListener(new BooleanStylePropertyHandler(sightIcon, "sight-icon", "nosight-icon"));
        model.malfunctionsProperty().addListener(new StatusPropertyHandler());

        // Because text has no size property, this is the only way to resize text
        connectionIcon.widthProperty().addListener(new ResizePropertyHandler());

        // Get the text shape to display the id as text. This is an example of simple property binding
        robotId.textProperty().set(model.getRobotId().toString());

        // here, a string is constructed of the robot positions, as well as all errors and all
        // warnings in the list of malfunctions.
        TooltipPropertyHandler tooltipPropertyHandler = new TooltipPropertyHandler();

        model.xPositionProperty().addListener(tooltipPropertyHandler);
        model.yPositionProperty().addListener(tooltipPropertyHandler);
        model.malfunctionsProperty().addListener(tooltipPropertyHandler);
    }

    /**
     * This function is referenced to in the .fxml file. When the mouse is clicked in this pane, the
     * following function will be called. The robot will be selected.
     */
    @FXML
    private void selectRobot() {
        model.update("isSelected", !model.isSelected());
    }

    /**
     * This function returns the style of a styleable object. it also cleares it.
     *
     * @param styleable the object of which the style should be returned.
     * @return the style as a observable list of strings.
     */
    private static ObservableList<String> getStyle(Styleable styleable) {
        ObservableList<String> styleClass = styleable.getStyleClass();

        // Styleclass is cleared. When this is done, it returns to its base style (#stylename, in
        // which stylename = id in fxml file).
        styleClass.clear();
        return styleClass;
    }

    /**
     * This handler returns one of two strings depending on a boolean
     *
     * @author Ryan Meulenkamp
     */
    public class BooleanStylePropertyHandler implements InvalidationListener {

        /**
         * The object of which the style should be modified.
         */
        private Styleable styleable;

        /**
         * the two strings between which a choice has to be made.
         */
        private String trueString, falseString;

        /**
         * The constructor of this handler. It has two strings and a object of a type implementing
         * the Styleable interface.
         *
         * @param styleable   the object of which the styling should be modified
         * @param trueString  the style in case of true
         * @param falseString the style in case of false
         */
        public BooleanStylePropertyHandler(Styleable styleable, String trueString, String falseString) {
            this.styleable = styleable;
            this.trueString = trueString;
            this.falseString = falseString;

            invalidated(new SimpleBooleanProperty(false));
        }

        /**
         * @param observable
         */
        @Override
        public void invalidated(Observable observable) {
            RobotStatus.getStyle(styleable).add(((BooleanProperty) observable).get() ? trueString : falseString);
        }
    }

    /**
     * this handler is more complex than booleanstylepropertyhandler. therefore it has its own
     * handler.
     *
     * @author Ryan Meulenkamp
     */
    public class StatusPropertyHandler implements InvalidationListener {

        /**
         * the construcotr is implemented to get the style to be drawn once at the beginning.
         */
        public StatusPropertyHandler() {
            invalidated(null);
        }

        /**
         * When the property is invalidated, this function is called.
         *
         * @param observable the property raising this event.
         */
        @Override
        public void invalidated(Observable observable) {
            // if an error is found in the list of malfunctions, the statusbox becomes red. if the
            // robot has warnings, it becomes yellow. else it will be green.
            ObservableList<String> styleClass = RobotStatus.getStyle(robotIconBackground);

            if (model.hasErrors())
                styleClass.add("statusbox-error");
            else if (model.hasWarnings())
                styleClass.add("statusbox-warning");
            else
                styleClass.add("statusbox-operational");
        }
    }

    /**
     * When the window is resized, the font of the id-number should also get an other size. This
     * seems to be the only way to do this.
     *
     * @author Ryan Meulenkamp
     */
    public class ResizePropertyHandler implements InvalidationListener {

        /**
         * When the size changes, a new font is created with a new size.
         */
        @Override
        public void invalidated(Observable observable) {
            Font font = robotId.getFont();

            // the font name also contains options like weight
            robotId.setFont(new Font(font.getName(), connectionIcon.getWidth()));
        }
    }

    /**
     * When one of the properties to be shown in the tooltip changes, the text of the tooltip is
     * updated.
     *
     * @author Ryan Meulenkamp
     */
    public class TooltipPropertyHandler implements InvalidationListener {
        /**
         * the constructor is used to initialize the tooltip the first time
         */
        public TooltipPropertyHandler() {
            super();
            invalidated(null);
        }

        @Override
        public void invalidated(Observable observable) {
            // turn a list of errors or warnings into its textual representation.
            String error = model.getErrors().stream().map(malfunction -> "   " + malfunction)
                    .collect(Collectors.joining("\n"));

            String warning = model.getWarnings().stream().map(malfunction -> "   " + malfunction)
                    .collect(Collectors.joining("\n"));

            //add the current position to this string
            String tooltip = String.format("X position: %.1f%nY position: %.1f",
                    model.getXPosition(), model.getYPosition())
                    + (!error.isEmpty() ? "\nErrors:\n" + error : "")
                    + (!warning.isEmpty() ? "\nWarnings:\n" + warning : "");

            // update the tooltip in the UI. The tooltip is build up beforehand so the UI-thread has a small load
            Platform.runLater(() -> robotTooltip.setText(tooltip));
        }
    }

    /**
     * the robotstatusRoot is needed to bind its size properties to its parent node.
     *
     * @return the robotstatusroot node.
     */
    public GridPane getRobotstatusRoot() {
        return robotstatusRoot;
    }
}