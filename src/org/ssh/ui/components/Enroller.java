package org.ssh.ui.components;

import javafx.scene.layout.Region;
import org.ssh.ui.UIComponent;
import org.ssh.util.Logger;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Enroller for vertical or horizontal use. It uses an internal
 * {@link BorderPane} where the {@link ReadOnlyDoubleProperty doubleproperties}
 * bound to. A {@link Pane} is set as TopSection or bottom (vertical) or as left or
 * right (horizontal) of the {@link BorderPane} depending on the
 * {@link ExtendDirection}. The content is contained by this pane.
 * 
 * @author Joost Overeem
 */
public class Enroller<N extends Region> extends BorderPane {

	private static final Logger LOG = Logger.getLogger();

	/**
	 * {@link BorderPane} that is to contain the real content.
	 */
	private BorderPane slidingWrapper;
	/**
	 * State representing if the enroller is collapsed or extended.
	 */
	private State state;
	/**
	 * Direction to where the slider grows.
	 */
	private ExtendDirection extendDirection;
	/**
	 * The content displayed in the slider.
	 */
	private N content;
	/**
	 * The properties where the sliderBase is bound to. These properties are
	 * used to fit the enroller in.
	 */
	private ReadOnlyDoubleProperty collapsedSizeProperty, extendedSizeProperty;

    /**
     * Lambda that will be run after an animation is finished
     */
    private Runnable onfinishMethod;
	/**
	 * Extendsize is the maximum size of the slider in the extending direction,
	 * so to where the slider should grow.
	 */
	private double extendsize;
	/**
	 * Collapsesize is the size of the slider in the extending direction when
	 * collapsed.
	 */
	private double collapsesize;

	/**
	 * Animation to extend the slidingWrapper.
	 */
	private final Animation collapse = new Transition() {
		{
			// Sliding takes 250 milliseconds
			setCycleDuration(Duration.millis(250));
		}

		// Function that fixes the sliding, frac indicates how big the displayed
		// fraction of the slidingWrapper should be
		@Override
		protected void interpolate(double frac) {
			// Calculate the size of the slidingWrapper
			final double size = collapsesize + ((extendsize - collapsesize) * (1.0 - frac));

			// Check if we have a vertical or a horizontal slider
			if (extendDirection == ExtendDirection.DOWN || extendDirection == ExtendDirection.UP) {
				// Make sure it is the size we want by setting the min, max and
				// pref height
				slidingWrapper.setMinHeight(size);
				slidingWrapper.setMaxHeight(size);
				slidingWrapper.setPrefHeight(size);
			} else {
				// Make sure it is the size we want by setting the min, max and
				// pref width
				slidingWrapper.setMinWidth(size);
				slidingWrapper.setMaxWidth(size);
				slidingWrapper.setPrefWidth(size);
			}
		}
	};

	/**
	 * Animation to collapse the slidingWrapper
	 */
	private final Animation extend = new Transition() {
		{
			// Sliding takes 250 milliseconds
			setCycleDuration(Duration.millis(250));
		}

		// Function that fixes the sliding, frac indicates how big the displayed
		// fraction of the slidingWrapper should be
		@Override
		protected void interpolate(double frac) {
			// Calculate the size of the slidingWrapper
			final double size = collapsesize + ((extendsize - collapsesize) * frac);

			// Check if we have a vertical or a horizontal slider
			if (extendDirection == ExtendDirection.DOWN || extendDirection == ExtendDirection.UP) {
				// Make sure it is the size we want by setting the min, max and
				// pref height
				slidingWrapper.setMinHeight(size);
				slidingWrapper.setMaxHeight(size);
				slidingWrapper.setPrefHeight(size);
			} else {
				// Make sure it is the size we want by setting the min, max and
				// pref width
				slidingWrapper.setMinWidth(size);
				slidingWrapper.setMaxWidth(size);
				slidingWrapper.setPrefWidth(size);
			}
		}
	};

	/**
	 * State represents if the enroller is collapsed or extended
	 * 
	 * @author Joost Overeem
	 *
	 */
	public enum State {
		COLLAPSED, EXTENDED
	}

	/**
	 * Represents the direction to slide
	 * 
	 * @author Joost Overeem
	 *
	 */
	public enum ExtendDirection {
		UP, DOWN, LEFT, RIGHT
	}

	/**
	 * Constructor for an enroller which is partially visible when collapsed.
	 * 
	 * @param content
	 *            {@link UIComponent} which should be displayed in the 3enroller
	 * @param extendDirection
	 *            {@link ExtendDirection} to indicate if the enroller expands
	 *            down, up, left or right
	 * @param fixedSizeProperty
	 *            {@link ReadOnlyDoubleProperty heightProperty} or
	 *            {@link ReadOnlyDoubleProperty widthProperty} of the
	 *            NON-extending directionto bind the enroller to (e.g. from a
	 *            pane it is put in)
	 * @param collapsedSizeProperty
	 *            {@link ReadOnlyDoubleProperty heightProperty} or
	 *            {@link ReadOnlyDoubleProperty widthProperty} for the size when
	 *            collapsed (if constandly 0.0, you should use the other
	 *            constructor)
	 * @param extendedSizeProperty
	 *            {@link ReadOnlyDoubleProperty heightProperty} or
	 *            {@link ReadOnlyDoubleProperty widthProperty} for the extended
	 *            size
	 * @param buttonIncluded
	 *            boolean for if there should be a {@link Button} included in
	 *            the enroller. If so, the button will fill the size of the
	 *            collapsed size
	 */
	public Enroller(N content, ExtendDirection extendDirection, ReadOnlyDoubleProperty fixedSizeProperty,
			ReadOnlyDoubleProperty collapsedSizeProperty, ReadOnlyDoubleProperty extendedSizeProperty,
			boolean buttonIncluded) {
		super();
		// Only start the initialisation if shit aint null, if there is any null
		// we anounce it with a severe logging. This is because all parameters
		// are required.
		if (content != null && extendDirection != null && fixedSizeProperty != null && collapsedSizeProperty != null
				&& extendedSizeProperty != null) {
			// Set the parameters as object variables for later use
			this.content = content;
			this.extendDirection = extendDirection;
			this.collapsedSizeProperty = collapsedSizeProperty;
			this.extendedSizeProperty = extendedSizeProperty;
			// Make new pane to place the content in. This is the pane used for
			// the animated sliding
			slidingWrapper = new BorderPane();

			// We create a Button (outside the if buttonIncluded to make sure it
			// is initialized and so prevent nullpointers)
			Button enrollButton = new Button();

			// If there should be a button included..
			if (buttonIncluded) {
				// Set a fancy icon as graphic
				ImageView icon = new ImageView(new Image("org/ssh/view/icon/enroll16.png"));
				enrollButton.setGraphic(icon);
				// See to what side the pointing icon should point when
				// collapsed
				// the original icon points down..
				switch (extendDirection) {
				case DOWN:
					break;
				case LEFT:
					icon.setRotate(90);
					break;
				case RIGHT:
					icon.setRotate(270);
					break;
				case UP:
					icon.setRotate(180);
					break;
				default:
					break;
				}

				// Check if the slider extend vertical or horizontal
				if (extendDirection == ExtendDirection.DOWN || extendDirection == ExtendDirection.UP) {
					// If vertical, we bind the collapsed size to the height so
					// that the button is exactly the size of a collapsed slider
					enrollButton.minHeightProperty().bind(collapsedSizeProperty);
					enrollButton.maxHeightProperty().bind(collapsedSizeProperty);
					enrollButton.minWidthProperty().bind(fixedSizeProperty);
					enrollButton.maxWidthProperty().bind(fixedSizeProperty);
				} else {
					// If horizontal, we bind the collapsed size to the width so
					// that the burron is exactly the size of a collapsed slider
					enrollButton.minHeightProperty().bind(fixedSizeProperty);
					enrollButton.maxHeightProperty().bind(fixedSizeProperty);
					enrollButton.minWidthProperty().bind(collapsedSizeProperty);
					enrollButton.maxWidthProperty().bind(collapsedSizeProperty);
				}
				// Now we bind the onAction to the button
				enrollButton.setOnAction(arg0 -> {
                    // Handle the enrollment
                    Enroller.this.handleEnrollment();
                    enrollButton.getGraphic().setRotate((enrollButton.getGraphic().getRotate() + 180) % 360);
                });
			}

			// The animations ensures that the slidingWrapper grows and shrinks,
			// so if it placed in the TopSection of the base borderpane it looks like
			// sliding down and so for the other directions
			switch (extendDirection) {
			case DOWN:
				this.setTop(slidingWrapper);
				slidingWrapper.setTop(content);
				if (buttonIncluded)
					slidingWrapper.setBottom(enrollButton);
				break;
			case LEFT:
				this.setRight(slidingWrapper);
				slidingWrapper.setRight(content);
				if (buttonIncluded)
					slidingWrapper.setLeft(enrollButton);
				break;
			case RIGHT:
				this.setLeft(slidingWrapper);
				slidingWrapper.setLeft(content);
				if (buttonIncluded)
					slidingWrapper.setRight(enrollButton);
				break;
			case UP:
				this.setBottom(slidingWrapper);
				slidingWrapper.setBottom(content);
				if (buttonIncluded)
					slidingWrapper.setTop(enrollButton);
				break;
			default:
				break;
			}

			// Set the starting state
			state = State.COLLAPSED;

			// Bind the content height and width to the slidingWrapper. Not
			// visible yet, but will show off when the slidingWrapper grows
			this.content.minHeightProperty().bind(slidingWrapper.heightProperty());
			this.content.maxHeightProperty().bind(slidingWrapper.heightProperty());
			this.content.minWidthProperty().bind(slidingWrapper.widthProperty());
			this.content.maxWidthProperty().bind(slidingWrapper.widthProperty());
			// Check if the extending direction is vertical or horizontal and
			// then bind the right properties
			if (extendDirection == ExtendDirection.DOWN || extendDirection == ExtendDirection.UP) {
				// Bind the height and width of this Enroller to the given
				// properties
				this.minWidthProperty().bind(fixedSizeProperty);
				this.maxWidthProperty().bind(fixedSizeProperty);
				this.minHeightProperty().bind(extendedSizeProperty);
				this.maxHeightProperty().bind(extendedSizeProperty);

				// Now bind the slidingWrapper to the size it should get,
				// startposition is collapsed, so height is bound to
				// collapsedHeightPropperty
				slidingWrapper.minWidthProperty().bind(fixedSizeProperty);
				slidingWrapper.maxWidthProperty().bind(fixedSizeProperty);
				slidingWrapper.minHeightProperty().bind(collapsedSizeProperty);
				slidingWrapper.maxHeightProperty().bind(collapsedSizeProperty);
			} else {
				// Bind the height and width of this Enroller to the given
				// properties
				this.minWidthProperty().bind(extendedSizeProperty);
				this.maxWidthProperty().bind(extendedSizeProperty);
				this.minHeightProperty().bind(fixedSizeProperty);
				this.maxHeightProperty().bind(fixedSizeProperty);

				// Now bind the slidingWrapper to the size it should get,
				// startposition is collapsed, so width is bound to
				// collapsedSizePropperty
				slidingWrapper.minWidthProperty().bind(collapsedSizeProperty);
				slidingWrapper.maxWidthProperty().bind(collapsedSizeProperty);
				slidingWrapper.minHeightProperty().bind(fixedSizeProperty);
				slidingWrapper.maxHeightProperty().bind(fixedSizeProperty);
			}

			// Initialize the onFinishedProperties of the Animations
			initAnimations();

			// Set the pickOnBounds false for mouse transparency
			this.setPickOnBounds(false);
		} else {
			// Log that shit went wrong because there is a null parameter
			LOG.warning("Enroller not created because of nullpointer parameters in constructor");
		}
	}

	/**
	 * Constructor for an enroller that collapses to a height of 0.0
	 * 
	 * 
	 * @param content
	 *            {@link UIComponent} which should be displayed in the enroller
	 * @param extendDirection
	 *            {@link ExtendDirection} to indicate if the enroller expands
	 *            down, up, left or right
	 * @param fixedSizeProperty
	 *            {@link ReadOnlyDoubleProperty heightProperty} or
	 *            {@link ReadOnlyDoubleProperty widthProperty} of the
	 *            NON-extending directionto bind the enroller to (e.g. from a
	 *            pane it is put in)
	 * @param extendedSizeProperty
	 *            {@link ReadOnlyDoubleProperty heightProperty} or
	 *            {@link ReadOnlyDoubleProperty widthProperty} for the extended
	 *            size
	 */
	public Enroller(N content, ExtendDirection extendDirection, ReadOnlyDoubleProperty fixedSizeProperty,
			ReadOnlyDoubleProperty extendedSizeProperty) {
		super();
		// Only start the initialisation if shit aint null, if there is any null
		// we anounce it with a severe logging. This is because all parameters
		// are required.
		if (content != null && extendDirection != null && fixedSizeProperty != null && extendedSizeProperty != null) {
			// Set the parameters as object variables for later use
			this.content = content;
			this.extendDirection = extendDirection;
			this.extendedSizeProperty = extendedSizeProperty;
			// Set the collapsesize to 0.0 because this constructor is for an
			// enroller that completely collapses
			collapsesize = 0.0;
			// Make new pane to place the content in. This is the pane used for
			// the animated sliding
			slidingWrapper = new BorderPane();

			// The animations ensures that the slidingWrapper grows and shrinks,
			// so if it placed in the TopSection of the base borderpane it looks like
			// sliding down and so for the other directions
			switch (extendDirection) {
			case DOWN:
				this.setTop(slidingWrapper);
				slidingWrapper.setBottom(content);
				break;
			case LEFT:
				this.setRight(slidingWrapper);
				slidingWrapper.setLeft(content);
				break;
			case RIGHT:
				this.setLeft(slidingWrapper);
				slidingWrapper.setRight(content);
				break;
			case UP:
				this.setBottom(slidingWrapper);
				slidingWrapper.setTop(content);
				break;
			default:
				break;
			}

			// Set the starting state
			state = State.COLLAPSED;
			// Set the content invisible, otherwise images would crop op and
			// stil be partially visible
			content.setVisible(false);

			// Bind the content height and width to the slidingWrapper. Not
			// visible yet, but will show off when the slidingWrapper grows
			this.content.minHeightProperty().bind(slidingWrapper.heightProperty());
			this.content.maxHeightProperty().bind(slidingWrapper.heightProperty());
			this.content.minWidthProperty().bind(slidingWrapper.widthProperty());
			this.content.maxWidthProperty().bind(slidingWrapper.widthProperty());
			// Check if the extending direction is vertical or horizontal and
			// then bind the right properties
			if (extendDirection == ExtendDirection.DOWN || extendDirection == ExtendDirection.UP) {
				// Bind the height and width of this Enroller to the given
				// properties
				this.minWidthProperty().bind(fixedSizeProperty);
				this.maxWidthProperty().bind(fixedSizeProperty);
				this.minHeightProperty().bind(extendedSizeProperty);
				this.maxHeightProperty().bind(extendedSizeProperty);

				// Now bind the slidingWrapper to the width it should get
				slidingWrapper.minWidthProperty().bind(fixedSizeProperty);
				slidingWrapper.maxWidthProperty().bind(fixedSizeProperty);
			} else {
				// Bind the height and width of this Enroller to the given
				// properties
				this.minWidthProperty().bind(extendedSizeProperty);
				this.maxWidthProperty().bind(extendedSizeProperty);
				this.minHeightProperty().bind(fixedSizeProperty);
				this.maxHeightProperty().bind(fixedSizeProperty);

				// Now bind the slidingWrapper to the width it should get
				slidingWrapper.minHeightProperty().bind(fixedSizeProperty);
				slidingWrapper.maxHeightProperty().bind(fixedSizeProperty);
			}

			// Initialize the onFinishedProperties of the Animations
			initAnimations();

			// Set the pickOnBounds false for mousetransparency
			this.setPickOnBounds(false);
		} else {
			// Log that shit went wrong because there is a null parameter
			LOG.warning("Enroller not created because of nullpointer parameters in constructor");
		}
	}

    /**
     *
     * @param onFinish lambda to execute when animation is finished
     */
    public void handleEnrollment(Runnable onFinish){
        onfinishMethod = onFinish;
        this.handleEnrollment();
    }



	/**
	 * Function to set the onFinishedProperties of the {@link Animation}s.
	 */
	private void initAnimations() {
		collapse.onFinishedProperty().set(actionEvent -> {
						// If the pane should not collapse completely we need to set the
						// extending size property again
						if (collapsedSizeProperty != null) {
							// Of course first check if we have a vertical or horizontal
							// sliding enroller
							if (extendDirection == ExtendDirection.DOWN || extendDirection == ExtendDirection.UP) {
								// It is vertical so we bind the height
								slidingWrapper.minHeightProperty().bind(collapsedSizeProperty);
								slidingWrapper.maxHeightProperty().bind(collapsedSizeProperty);
							} else {
								// It is horizontal so we bind the width
								slidingWrapper.minWidthProperty().bind(collapsedSizeProperty);
								slidingWrapper.maxWidthProperty().bind(collapsedSizeProperty);
							}
						} else {
							// Set the content invisible, otherwise images would crop op
							// and still be partially visible. This counts of course not for
							// a content that doesn't dissapear completely
							content.setVisible(false);
						}
            // Set the state to collapsed because collapsing is finished
            state = State.COLLAPSED;

            if(onfinishMethod != null)
                onfinishMethod.run();
        });
		extend.onFinishedProperty().set(actionEvent -> {
            // Now the animation is finished we can bind the size property
            // again to the property of the extended size
            // So check if we slide horizontal or vertical..
            if (extendDirection == ExtendDirection.DOWN || extendDirection == ExtendDirection.UP) {
                // It is vertical so we unbind the height
                slidingWrapper.minHeightProperty().bind(extendedSizeProperty);
                slidingWrapper.maxHeightProperty().bind(extendedSizeProperty);
            } else {
                // It is horizontal so we unbind the width
                slidingWrapper.minWidthProperty().bind(extendedSizeProperty);
                slidingWrapper.maxWidthProperty().bind(extendedSizeProperty);
            }

			// Set the content visible, so that you see the content appearing
			// instead of when finished the animation suddenly see it
			content.setVisible(true);

            if(onfinishMethod != null)
                onfinishMethod.run();

            // Set the state to extended because extending is finished
            state = State.EXTENDED;
        });
	}

	/**
	 * Function for handeling the enrollment. Checks if
	 * there should be collapsed or extended and then plays the
	 * {@link Animation}. <br />
	 * <br />
	 * Note: The functions unbinds the heightproperties but in the
	 * {@link Animation animations'} own onFinishedProperties the
	 */
	public void handleEnrollment() {
		// Unbind the moving sizeproperties so that it becomes possible to
		// set the size in the animation. Therefore we check whether the height or
		// the width should be unbound
		if (extendDirection == ExtendDirection.DOWN || extendDirection == ExtendDirection.UP) {
			// It is vertical so we unbind the height
			slidingWrapper.minHeightProperty().unbind();
			slidingWrapper.maxHeightProperty().unbind();
		} else {
			// It is horizontal so we unbind the width
			slidingWrapper.minWidthProperty().unbind();
			slidingWrapper.maxWidthProperty().unbind();
		}
		// Set the extend and collapse sizes
		extendsize = extendedSizeProperty.doubleValue();
		// But collapse size only when there is an height property for because
		// otherwise it stays 0.0
		if (collapsedSizeProperty != null)
			collapsesize = collapsedSizeProperty.doubleValue();

		// Check if the slider is collapsed or extended
		if (state == State.COLLAPSED) {
			// play extendanimation for sliding up
			extend.play();
		} else {
			// play collapseanimation for sliding down
			collapse.play();
		}
	}
}
