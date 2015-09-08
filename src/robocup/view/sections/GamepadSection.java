package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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

	private JTextField gamepadConnectedField;
	private JButton gamepadUseButton;
	private JCheckBox cameraUseCheckBox;

	public GamepadSection() {
		super("Gamepad Section");
		this.setLayout(new MigLayout("wrap", "[grow]", "[grow]"));
		gamepadModel = World.getInstance().getGamepadModel();
		ButtonListener buttonListener = new ButtonListener();
		CheckBoxListener checkBoxListener = new CheckBoxListener();

		gamepadConnectedField = new JTextField();
		gamepadConnectedField.setEnabled(false);
		add(new JLabel("Current controller"), "split");
		add(gamepadConnectedField, "growx, wrap");

		gamepadUseButton = new JButton("Start using gamepad");
		gamepadUseButton.addActionListener(buttonListener);
		gamepadUseButton.setName("gamepadUse");
		add(gamepadUseButton, "growx, split");
		
		cameraUseCheckBox = new JCheckBox("Use camera's");
		cameraUseCheckBox.addActionListener(checkBoxListener);
		cameraUseCheckBox.setName("cameraUse");
		cameraUseCheckBox.setSelected(useCamera);
		add(cameraUseCheckBox, "growx");
	}

	private class CheckBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			useCamera = ((JCheckBox)arg0.getSource()).isSelected();
			if (useGamepad) {
				gamepadModel.getGamepadThread().stop(true);
				gamepadModel.setGamepadThread(new GamepadThread(gamepadModel, useCamera));
				gamepadModel.getGamepadThread().start();
			}
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			useGamepad = !useGamepad;
			if (useGamepad) {
				gamepadUseButton.setText("Stop using gamepad");
				gamepadModel.setGamepadThread(new GamepadThread(gamepadModel, useCamera));
				gamepadModel.getGamepadThread().start();
			} else {
				gamepadUseButton.setText("Start using gamepad");
				gamepadModel.getGamepadThread().stop(true);
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
