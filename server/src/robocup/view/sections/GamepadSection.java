package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import robocup.gamepad.GamepadModel;
import robocup.gamepad.GamepadThread;
import robocup.model.World;
import robocup.view.SectionBox;

@SuppressWarnings("serial")
public class GamepadSection extends SectionBox {

	GamepadModel gamepadModel;
	private boolean useGamepad = false;
	private boolean useCamera = true;

	JTextField gamepadConnectedField;
	JButton gamepadUseButton;
	JButton cameraUseButton;

	public GamepadSection() {
		super("Gamepad Section");
		this.setLayout(new MigLayout("wrap", "[grow]", "[grow]"));
		gamepadModel = World.getInstance().getGamepadModel();
		ButtonListener listener = new ButtonListener();

		gamepadConnectedField = new JTextField();
		gamepadConnectedField.setEnabled(false);
		add(new JLabel("Current controller"), "split");
		add(gamepadConnectedField, "growx, wrap");

		gamepadUseButton = new JButton("Start using gamepad");
		gamepadUseButton.addActionListener(listener);
		gamepadUseButton.setName("gamepadUse");
		add(gamepadUseButton, "growx, split");
		
		cameraUseButton = new JButton("Stop using camera's");
		cameraUseButton.addActionListener(listener);
		cameraUseButton.setName("cameraUse");
		add(cameraUseButton, "growx");
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String buttonText = ((JButton) e.getSource()).getName();
			switch (buttonText) {
			case "gamepadUse":
				useGamepad = !useGamepad;
				if (useGamepad) {
					gamepadUseButton.setText("Stop using gamepad");
					gamepadModel.setGamepadThread(new GamepadThread(gamepadModel, useCamera));
					gamepadModel.getGamepadThread().start();
				} else {
					gamepadUseButton.setText("Start using gamepad");
					gamepadModel.getGamepadThread().stop(true);
				}
				break;
			case "cameraUse":
				useCamera = !useCamera;
				if (useCamera) {
					cameraUseButton.setText("Stop using camera");
					gamepadModel.getGamepadThread().stop(true);
					gamepadModel.setGamepadThread(new GamepadThread(gamepadModel, useCamera));
					gamepadModel.getGamepadThread().start();
				} else {
					cameraUseButton.setText("Start using camera");
					gamepadModel.getGamepadThread().stop(true);
					gamepadModel.setGamepadThread(new GamepadThread(gamepadModel, useCamera));
					gamepadModel.getGamepadThread().start();
				}
				break;
			}
		}
	}

	@Override
	public void update() {
		if (gamepadModel.getGamepad() != null)
			gamepadConnectedField.setText(gamepadModel.getGamepad().getName());
		else
			gamepadConnectedField.setText("No gamepad found");
	}

}
