package robocup.view.sections;

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.model.FieldObject;
import robocup.model.Obstruction;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.TeamColor;
import robocup.view.FieldPanel;
import robocup.view.FieldPanel;
import robocup.view.GUIModel;
import robocup.view.SectionBox;

/**
 * {@link FieldControlSection} is a {@link SectionBox} for controlling a
 * graphical interface for the field. It has {@link JCheckBox}s for showing the
 * {@link FieldPanel} in a {@link JFrame} and for toggling what to show in the
 * {@link FieldPanel}. Inter alia, there are {@link JCheckBox}s to show a raster
 * and to display a free shot.
 */
@SuppressWarnings("serial")
public class FieldControlSection extends SectionBox {

	private JFrame frame;
	private FieldPanel fieldpanel;
	private HashMap<String, JPanel> tabs = new HashMap<String, JPanel>();
	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JSlider orientationSlider;
	private JPanel items = new JPanel();
	private World world;
	private GUIModel model;

	public FieldControlSection() {
		super("Field Control Section");
		world = World.getInstance();

		GraphicsDevice fieldMonitor = getSecondaryMonitor();

		frame = new JFrame(fieldMonitor.getConfigurations()[0]);

		model = world.getGuiModel();
		fieldpanel = new FieldPanel();

		setLayout(new MigLayout("", "[grow]"));
		createItemTabs();
		createSettingsTab();
		createTabs();
	}

	/**
	 * @return Monitor as GraphicsDevice for the fieldframe to be placed
	 */
	private GraphicsDevice getSecondaryMonitor() {
		GraphicsDevice[] monitors = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice secondaryMonitor = null;
		try {
			String mainMonitor = ((JFrame) SwingUtilities
					.getWindowAncestor(this)).getGraphicsConfiguration()
					.getDevice().getIDstring();
			for (GraphicsDevice monitor : monitors) {
				if (mainMonitor.equals(monitor.getIDstring()))
					continue;
				secondaryMonitor = monitor;
				break;
			}
			LOGGER.info("Set '" + mainMonitor + "' as primary display.");
			LOGGER.info("Set '" + secondaryMonitor.getIDstring()
					+ "' as secondary display.");
		} catch (Exception e) {
			// error found? return first monitor
			secondaryMonitor = monitors[0];
			LOGGER.info("Only one monitor found");
		}
		return secondaryMonitor;
	}

	private void createSettingsTab() {
		JPanel settingsTab = new JPanel();
		settingsTab.setLayout(new MigLayout("wrap 4", "[grow]",
				"[grow][grow][grow]"));


		ActionListener buttonListener = new ButtonListener();
		JButton showField = new JButton("Show field");
		showField.addActionListener(buttonListener);
		settingsTab.add(showField, "growx, span 2");
		JButton showField2 = new JButton("Turn field");
		showField2.addActionListener(buttonListener);
		settingsTab.add(showField2, "growx, span 2, wrap");

		JCheckBox showRaster = new JCheckBox("Show raster");
		showRaster.addActionListener(buttonListener);
		settingsTab.add(showRaster, "growx");
		showRaster.setSelected(true);

		JCheckBox showZones = new JCheckBox("Show zones");
		showZones.addActionListener(buttonListener);
		settingsTab.add(showZones, "growx");

		JCheckBox showRobots = new JCheckBox("Show robots");
		showRobots.addActionListener(buttonListener);
		settingsTab.add(showRobots, "growx");
		showRobots.setSelected(true);

		JCheckBox showBall = new JCheckBox("Show ball");
		showBall.addActionListener(buttonListener);
		settingsTab.add(showBall, "growx");
		showBall.setSelected(true);

		JCheckBox mirrorField = new JCheckBox("Mirror North/South");
		mirrorField.addActionListener(buttonListener);
		settingsTab.add(mirrorField, "growx");

		JCheckBox drawFreeShot = new JCheckBox("Draw free shot");
		drawFreeShot.addActionListener(buttonListener);
		settingsTab.add(drawFreeShot, "growx");

		JCheckBox drawCoords = new JCheckBox("Draw coordinates");
		drawCoords.addActionListener(buttonListener);
		settingsTab.add(drawCoords, "growx");

		JCheckBox drawPaths = new JCheckBox("Draw paths");
		drawPaths.addActionListener(buttonListener);
		settingsTab.add(drawPaths, "growx");

		JCheckBox drawNeighbours = new JCheckBox("Draw All paths");
		drawNeighbours.addActionListener(buttonListener);
		settingsTab.add(drawNeighbours, "growx");

		JCheckBox drawVertices = new JCheckBox("Draw vertices");
		drawVertices.addActionListener(buttonListener);
		settingsTab.add(drawVertices, "growx");

		JCheckBox drawVectors = new JCheckBox("Draw vectors");
		drawVectors.addActionListener(buttonListener);
		settingsTab.add(drawVectors, "growx");

		JCheckBox drawObstructions = new JCheckBox("Draw obstructions");
		drawObstructions.addActionListener(buttonListener);
		settingsTab.add(drawObstructions, "growx");

		tabs.put("Settings", settingsTab);
	}

	private class PanelClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			model.setMouseObject(((ItemPanel) arg0.getSource()).getItem());
			for (Component component : tabs.get("items").getComponents())
				if (component instanceof ItemPanel)
					((ItemPanel)component).update();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	private void createItemTabs() {
		items.setLayout(new MigLayout("wrap 11", "[][][][][][]"));
		TeamColor teamcolor = TeamColor.YELLOW;
		JPanel settingsPanel = new JPanel();
		settingsPanel.add(new JLabel("Rotation"));
		orientationSlider = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
		orientationSlider.addChangeListener(new SliderListener());
		settingsPanel.add(orientationSlider);
		items.add(settingsPanel, "wrap, span 4");
		for (int teamnr = 0; teamnr < 2; teamnr++) {
			Team team = (world.getReferee().getAllyTeamColor()
					.equals(teamcolor)) ? world.getReferee()
					.getAlly() : world.getReferee().getEnemy();

			for (int id = 0; id < Main.POSSIBLE_IDS; id++) {
				ItemPanel robotPanel = new ItemPanel(team.getRobotByID(id));
				robotPanel.addMouseListener(new PanelClickListener());
				items.add(robotPanel);
			}
			teamcolor = TeamColor.BLUE;
		}
		ItemPanel panel = new ItemPanel(world.getBall());
		panel.addMouseListener(new PanelClickListener());
		items.add(panel);

		for (Obstruction obstruction : world.getObstructions()) {
			ItemPanel obstructionPanel = new ItemPanel(obstruction);
			obstructionPanel.addMouseListener(new PanelClickListener());
			items.add(obstructionPanel);
		}
		JPanel addObstructionPanel = new JPanel();
		addObstructionPanel.add(new JButton(new AbstractAction("+") {
			public void actionPerformed(ActionEvent e) {
				world.getObstructions()
						.add(new Obstruction(100, 200));
				items.removeAll();
				createItemTabs();
				repaint();
			}
		}), "");
		items.add(addObstructionPanel);

		tabs.put("items", items);
	}

	private class SliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (model.getMouseObject() instanceof Robot) {
				int newOrientation = ((JSlider) e.getSource()).getValue();
				((Robot) model.getMouseObject()).setOrientation(newOrientation);
			}
			if (model.getMouseObject() instanceof Obstruction) {
				int newOrientation = ((JSlider) e.getSource()).getValue();
				((Obstruction) model.getMouseObject())
						.setOrientation(newOrientation);
			}
		}
	}

	private void createTabs() {
		JTabbedPane tabbedPane = new JTabbedPane();

		for (Map.Entry<String, JPanel> entry : tabs.entrySet())
			tabbedPane.add(entry.getKey(), entry.getValue());
		add(tabbedPane, "growx");
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String buttonText = ((e.getSource() instanceof JButton) ? ((JButton) e
					.getSource()) : ((JCheckBox) e.getSource())).getText();

			switch (buttonText) {
			case "Show field":
				frame.setLocation(getSecondaryMonitor()
						.getDefaultConfiguration().getBounds().x,
						getSecondaryMonitor().getDefaultConfiguration()
								.getBounds().y);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				frame.setTitle("Field");
				frame.setSize(400, 300);
				frame.setContentPane(fieldpanel);
				frame.setVisible(true);
				((JButton) e.getSource()).setText("Hide field");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;

			case "Hide field":
				frame.dispatchEvent(new WindowEvent(frame,
						WindowEvent.WINDOW_CLOSING));
				((JButton) e.getSource()).setText("Show field");
				break;

				
			case "Show raster":
				model.setShowRaster(((JCheckBox)e.getSource()).isSelected());
				break;

//			case "Show zones":
//				model.setShowZones(((JCheckBox)e.getSource()).isSelected());
//				break;

			case "Show robots":
				model.setShowRobots(((JCheckBox)e.getSource()).isSelected());
				break;

			case "Show ball":

				model.setShowBall(((JCheckBox)e.getSource()).isSelected());
				break;
				
			case "Turn field":
				model.nextQuadrantRotation();
				break;
/*
			case "Draw vectors":
				fieldPanel.toggleShowVectors();
				break;

			case "Mirror North/South":
				fieldPanel.toggleMirror();
				break;

			case "Draw free shot":
				fieldPanel.toggleShowFreeShot();
				break;

			case "Draw coordinates":
				fieldPanel.toggleCoords();
				break;

			case "Draw paths":
				fieldPanel.toggleShowPathPlanner();
				break;

			case "Draw All paths":
				fieldPanel.toggleDrawNeighbours();
				break;

			case "Draw vertices":
				fieldPanel.toggleDrawVertices();
				break;

			case "Draw obstructions":
				fieldPanel.toggleObstructions();
				break;
				*/
			}

		}
	}

	@Override
	public void update() {
		fieldpanel.update();
		if (model.getMouseObject() instanceof Robot && orientationSlider != null)
			orientationSlider.setValue((int) ((Robot) model.getMouseObject())
					.getOrientation());
		if (model.getMouseObject() instanceof Obstruction
				&& orientationSlider != null)
			orientationSlider
					.setValue((int) ((Obstruction) model.getMouseObject())
							.getOrientation());
		items.repaint();
	}
}