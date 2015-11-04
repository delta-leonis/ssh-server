package view.components;

import java.util.Optional;

import application.UI;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import ui.UIComponent;

//TODO Fix it that enrollbox automatically resizes in parent bounds (Works now, but not in every case)
public class Enrollbox extends BorderPane {
	private double expandedSize;
	private Region node;
	private Button controlButton;
	private boolean stationaryButton;
	private Direction slideDirection;

	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	public Enrollbox(Direction slideDirection, UIComponent content) {
		node = content;
		node.setStyle("-fx-background-color: rgba(0, 100, 100, 1.0); -fx-background-radius: 10 10 10 10;");
		this.slideDirection = slideDirection;
		expandedSize = 250;
		setExpandedSize(expandedSize);
		node.setVisible(false);
		if (slideDirection == null) {
			// Set default location
			slideDirection = Direction.DOWN;
		}
		this.slideDirection = slideDirection;
		setCenter(node);
		setMaxHeight(0);
		switch (slideDirection) {
		case DOWN:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 0 10 10;");
			break;
		case LEFT:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 0 0 10;");
			break;
		case RIGHT:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 10 10 0;");
			break;
		case UP:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 10 0 0;");
			break;
		default:
			break;
		}
	}

	public void setExpandSize(double expandSize) {
		if (slideDirection == Direction.UP || slideDirection == Direction.DOWN) {
			this.setHeight(expandSize);
		} else {
			this.setWidth(expandSize);
		}
		expandedSize = expandSize;
	}

	/**
	 * Creates a sidebar panel in a BorderPane, containing an horizontal
	 * alignment of the given nodes.
	 * 
	 * @param expandedSize
	 *            The size of the panel.
	 * @param controlButton
	 *            The button responsible to open/close slide bar.
	 * @param location
	 *            The location of the panel (TOP_LEFT, BOTTOM_LEFT,
	 *            BASELINE_RIGHT, BASELINE_LEFT).
	 * @param nodes
	 *            Nodes inside the panel.
	 */
	public Enrollbox(double expandedSize, Button button, Direction location, Region node) {
		setExpandedSize(expandedSize);
		this.node = node;
		node.setVisible(false);
		// Set location
		if (location == null) {
			slideDirection = Direction.DOWN; // Set default location
		}
		slideDirection = location;

		setMaxHeight(0);
		controlButton = button;
		setCenter(node);

		controlButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				handleRolling(event);
			}
		});
		switch (slideDirection) {
		case DOWN:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 0 10 10;");
			break;
		case LEFT:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 0 0 10;");
			break;
		case RIGHT:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 10 10 0;");
			break;
		case UP:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 10 0 0;");
			break;
		default:
			break;
		}
	}

	/**
	 * Creates a sidebar panel in a BorderPane, which creates a default button
	 * for you.
	 * 
	 * @param expandedSize
	 *            The size of the panel.
	 * @param location
	 *            The location of the panel (TOP_LEFT, BOTTOM_LEFT,
	 *            BASELINE_RIGHT, BASELINE_LEFT).
	 * @param nodes
	 *            Nodes inside the panel.
	 * @param stationaryButton
	 *            True if the button should stay in place, false otherwise
	 */
	public Enrollbox(double expandedSize, Direction location, Region node, boolean stationaryButton) {
		this.stationaryButton = stationaryButton;
		setExpandedSize(expandedSize);
		this.node = node;
		this.setMinWidth(0.0);
		node.setVisible(false);
		// Set location
		if (location == null) {
			slideDirection = Direction.DOWN; // Set default location
		}
		slideDirection = location;
		setCenter(node);
		controlButton = new Button(".");
		controlButton.setMinWidth(35);
		controlButton.setMinHeight(35);
		setup();

		controlButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				handleRolling(event);
			}
		});
		switch (slideDirection) {
		case DOWN:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 0 10 10;");
			break;
		case LEFT:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 0 0 10;");
			break;
		case RIGHT:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 10 10 0;");
			break;
		case UP:
			setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 10 0 0;");
			break;
		default:
			break;
		}
	}

	public void handleRolling(ActionEvent actionEvent) {
		// Create an animation to hide the panel.
		final Animation hidePanel = new Transition() {
			{
				setCycleDuration(Duration.millis(250));
			}

			@Override
			protected void interpolate(double frac) {
				final double size = getExpandedSize() * (1.0 - frac);
				translateByPos(size);
			}
		};

		hidePanel.onFinishedProperty().set(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				node.setVisible(false);
			}
		});

		// Create an animation to show the panel.
		final Animation showPanel = new Transition() {
			{
				setCycleDuration(Duration.millis(250));
			}

			@Override
			protected void interpolate(double frac) {
				final double size = getExpandedSize() * frac;
				translateByPos(size);
			}
		};

		showPanel.onFinishedProperty().set(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				node.setVisible(true);
			}
		});

		if (showPanel.statusProperty().get() == Animation.Status.STOPPED
				&& hidePanel.statusProperty().get() == Animation.Status.STOPPED) {

			if (node.isVisible()) {
				hidePanel.play();

			} else {
				node.setVisible(true);
				showPanel.play();
			}
		}
	}

	public void setup() {
		switch (slideDirection) {
		case DOWN:
			controlButton.prefWidthProperty().bind(widthProperty());
			setMaxHeight(35);
			setCenter(node);
			if (stationaryButton)
				setTop(controlButton);
			else
				setBottom(controlButton);
			break;
		case UP:
			controlButton.prefWidthProperty().bind(widthProperty());
			setMaxHeight(35);
			setCenter(node);
			if (stationaryButton)
				setBottom(controlButton);
			else
				setTop(controlButton);
			break;
		case RIGHT:
			controlButton.prefHeightProperty().bind(heightProperty());
			setMaxWidth(35);
			setCenter(node);
			if (stationaryButton)
				setLeft(controlButton);
			else
				setRight(controlButton);
			break;
		case LEFT:
			controlButton.prefHeightProperty().bind(heightProperty());
			setMaxWidth(35);
			setCenter(node);
			if (stationaryButton)
				setRight(controlButton);
			else
				setLeft(controlButton);
			break;
		default:
			break;
		}
	}

	/**
	 * Translate the VBox according to location Pos.
	 *
	 * @param size
	 */
	private void translateByPos(double size) {
		switch (slideDirection) {
		case DOWN:
			setMinHeight(size);
			break;
		case UP:
			setMinHeight(size);
			break;
		case RIGHT:
		case LEFT:
			setMinWidth(size);
			break;
		default:
			break;
		}
	}

	/**
	 * @return the expandedSize
	 */
	public double getExpandedSize() {
		return expandedSize;
	}

	/**
	 * @param expandedSize
	 *            the expandedSize to set
	 */
	public void setExpandedSize(double expandedSize) {
		this.expandedSize = expandedSize;
	}
}
