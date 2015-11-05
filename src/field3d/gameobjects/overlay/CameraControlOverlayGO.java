package field3d.gameobjects.overlay;


import field3d.core.game.Game;
import field3d.core.gameobjects.GameObject;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;


/**
 * 
 * CameraPresetOverlay class
 * 
 * 	This class is responsible for the 2d camera preset controls
 * 
 * @author marklef2
 * @date 5-11-2015
 */
public class CameraControlOverlayGO extends GameObject {
	
	// TODO: move to config
	private static final int BUTTON_OFFSET = 35;
	
	private Group _controlsGroup;
	
	private Button _buttonTopView;
	private Button _buttonSideView;
	private Button _button45DegView;
	
	private Button _buttonRotateLeft;
	private Button _buttonRotateRight;
	private Button _buttonRotateUp;
	private Button _buttonRotateDown;
	
	private Button _buttonZoomIn;
	private Button _buttonZoomOut;
	

	public CameraControlOverlayGO(Game game) {
		
		// Initialize super class
		super(game);
		
		// Create group for the controls
		_controlsGroup = new Group();
		
		// Creating buttons
		_buttonTopView = new Button("Top View");
		_buttonSideView = new Button("Side View");
		_button45DegView = new Button("45 Degree View");
		
		_buttonRotateLeft = new Button("Rotate Left");
		_buttonRotateRight = new Button("Rotate Right");
		_buttonRotateUp = new Button("Rotate Up");
		_buttonRotateDown = new Button("Rotate Down");
		
		_buttonZoomIn = new Button("+");
		_buttonZoomOut = new Button("-");
		
		// Setting translation
		_buttonSideView.setTranslateY(BUTTON_OFFSET);
		_button45DegView.setTranslateY((2 * BUTTON_OFFSET));
		
		_buttonRotateLeft.setTranslateY(BUTTON_OFFSET * 4);
		_buttonRotateRight.setTranslateY(BUTTON_OFFSET * 5); 
		_buttonRotateUp.setTranslateY(BUTTON_OFFSET * 6); 
		_buttonRotateDown.setTranslateY(BUTTON_OFFSET * 7); 
		
		_buttonZoomIn.setTranslateY(BUTTON_OFFSET * 9);
		_buttonZoomOut.setTranslateY(BUTTON_OFFSET * 10);
		
		
		// Adding buttons to controls group
		_controlsGroup.getChildren().add(_button45DegView);
		_controlsGroup.getChildren().add(_buttonSideView);
		_controlsGroup.getChildren().add(_buttonTopView);
		
		_controlsGroup.getChildren().add(_buttonRotateLeft);
		_controlsGroup.getChildren().add(_buttonRotateRight);
		_controlsGroup.getChildren().add(_buttonRotateUp);
		_controlsGroup.getChildren().add(_buttonRotateDown);
		
		_controlsGroup.getChildren().add(_buttonZoomIn);
		_controlsGroup.getChildren().add(_buttonZoomOut);
	}

	@Override
	public void Initialize() {
		
		// Adding buttons to 2d group
		GetGame().Get2DGroup().getChildren().add(_controlsGroup);
		
		// Hook events
		_button45DegView.setOnMouseClicked(new OnButton45DegViewClicked());
		_buttonSideView.setOnMouseClicked(new OnButtonSideViewClicked());
		_buttonTopView.setOnMouseClicked(new OnButtonTopViewClicked());
		
		_buttonRotateLeft.setOnMouseClicked(new OnButtonRotateLeftClicked());
		_buttonRotateRight.setOnMouseClicked(new OnButtonRotateRightClicked());
		_buttonRotateUp.setOnMouseClicked(new OnButtonRotateUpClicked());
		_buttonRotateDown.setOnMouseClicked(new OnButtonRotateDownClicked());
		
		_buttonZoomIn.setOnMouseClicked(new OnButtonZoomInClicked());
		_buttonZoomOut.setOnMouseClicked(new OnButtonZoomOutClicked());
	}

	@Override
	public void Update(long timeDivNano) { }

	@Override
	public void Destroy() {
		
		// TODO remove event handlers
		
		// If 2d group contains the controls group
		if (GetGame().Get2DGroup().getChildren().contains(_controlsGroup)) {
						
			// Remove from 2d group
			GetGame().Get2DGroup().getChildren().remove(_controlsGroup);
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Preset button click events
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * Button 45 degree view click event
	 * 
	 * @author marklef2
	 *
	 */
	class OnButton45DegViewClicked implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			// TODO Set camera to 45 degree angle, set location to center, set zoom
			GetGame().GetThirdPersonCamera().SetRotateY(0);
			GetGame().GetThirdPersonCamera().SetRotateX(45);
			
		}
		
	}
	
	/**
	 * 
	 * Button side view click event
	 * 
	 * @author marklef2
	 *
	 */
	class OnButtonSideViewClicked implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			
			// TODO Set camera to side view, set location to center, set zoom
			GetGame().GetThirdPersonCamera().SetRotateY(0);
			GetGame().GetThirdPersonCamera().SetRotateX(0);
			
		}
		
	}
	
	
	/**
	 * 
	 * Button top view click event.
	 * 
	 * @author marklef2
	 *
	 */
	class OnButtonTopViewClicked implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			// TODO Set camera to top view, set location to center, set zoom
			GetGame().GetThirdPersonCamera().SetRotateY(0);
			GetGame().GetThirdPersonCamera().SetRotateX(90);
			
		}
		
	}
	

	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Rotation button click events
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * Button rotate left click event.
	 * 
	 * @author marklef2
	 *
	 */
	class OnButtonRotateLeftClicked implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			// TODO Smooth out rotation
			GetGame().GetThirdPersonCamera().SetRotateY(GetGame().GetThirdPersonCamera().GetRotateY() - 10);
		}		
	}
	
	
	/**
	 * 
	 * Button rotate right click event.
	 * 
	 * @author marklef2
	 *
	 */
	class OnButtonRotateRightClicked implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			// TODO Smooth out rotation
			GetGame().GetThirdPersonCamera().SetRotateY(GetGame().GetThirdPersonCamera().GetRotateY() + 10);
		}
		
	}
	
	/**
	 * 
	 * Button rotate up click event.
	 * 
	 * @author marklef2
	 *
	 */
	class OnButtonRotateUpClicked implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			// TODO Smooth out rotation
			GetGame().GetThirdPersonCamera().SetRotateX(GetGame().GetThirdPersonCamera().GetRotateX() + 10);
		}
		
	}
	
	
	/**
	 * 
	 * Button rotate down click event.
	 * 
	 * @author marklef2
	 *
	 */
	class OnButtonRotateDownClicked implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			// TODO Smooth out rotation
			GetGame().GetThirdPersonCamera().SetRotateX(GetGame().GetThirdPersonCamera().GetRotateX() - 10);
		}
		
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Zoom button click events
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * Button zoom in click event.
	 * 
	 * @author marklef2
	 *
	 */
	class OnButtonZoomInClicked implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			// TODO zoom in
			// GetGame().GetThirdPersonCamera().SetRotateY(0);
			// GetGame().GetThirdPersonCamera().SetRotateX(90);
			
		}
		
	}
	
	
	/**
	 * 
	 * Button zoom out click event.
	 * 
	 * @author marklef2
	 *
	 */
	class OnButtonZoomOutClicked implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			// TODO Zoom out			
		}
	}
}
