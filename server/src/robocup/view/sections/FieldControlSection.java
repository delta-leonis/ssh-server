package robocup.view.sections;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.model.World;
import robocup.view.FieldPanel;
import robocup.view.SectionBox;

/**
 * {@link FieldControlSection} is a {@link SectionBox} for controlling a graphical interface for the field.
 * It has {@link JButton}s for showing the {@link FieldPanel} in a {@link JFrame} and for toggling what to show
 * in the {@link FieldPanel}. Inter alia, there are {@link JButton}s to show a raster and to display a free shot.
 */
@SuppressWarnings("serial")
public class FieldControlSection extends SectionBox {

	private JFrame frame;
	private FieldPanel fieldPanel;
	private Logger LOGGER = Logger.getLogger(Main.class.getName());

	public FieldControlSection() {
		super("Field Conrol Section");

		GraphicsDevice fieldMonitor = getSecondaryMonitor();

		frame = new JFrame(fieldMonitor.getConfigurations()[0]);
		fieldPanel = new FieldPanel();

		this.setLayout(new MigLayout("wrap 4", "[grow]", "[grow][grow][grow]"));

		ActionListener buttonListener = new ButtonListener();
		JButton showField = new JButton("Show field");
		showField.addActionListener(buttonListener);
		add(showField, "growx");

		JButton showRaster = new JButton("Show raster");
		showRaster.addActionListener(buttonListener);
		add(showRaster, "growx");

		JButton showZones = new JButton("Show zones");
		showZones.addActionListener(buttonListener);
		add(showZones, "growx");

		JButton showRobots = new JButton("Show robots");
		showRobots.addActionListener(buttonListener);
		add(showRobots, "growx");

		JButton showBall = new JButton("Show ball");
		showBall.addActionListener(buttonListener);
		add(showBall, "growx");
		
		JButton mirrorField = new JButton("Mirror North/South");
		mirrorField.addActionListener(buttonListener);
		add(mirrorField, "growx");

		JButton drawFreeShot = new JButton("Draw free shot");
		drawFreeShot.addActionListener(buttonListener);
		add(drawFreeShot, "growx");

		JButton drawCoords = new JButton("Draw coordinates");
		drawCoords.addActionListener(buttonListener);
		add(drawCoords, "growx");

		JButton drawPaths = new JButton("Draw paths");
		drawPaths.addActionListener(buttonListener);
		add(drawPaths, "growx");

		JButton drawNeighbours = new JButton("Draw All paths");
		drawNeighbours.addActionListener(buttonListener);
		add(drawNeighbours, "growx");

		JButton drawVertices = new JButton("Draw vertices");
		drawVertices.addActionListener(buttonListener);
		add(drawVertices, "growx");

	}
	
	/**
	 * @return Monitor as GraphicsDevice for the fieldframe to be placed
	 */
	private GraphicsDevice getSecondaryMonitor() {
		GraphicsDevice[] monitors = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice secondaryMonitor = null;
		try{
		String mainMonitor = ((JFrame) SwingUtilities.getWindowAncestor(this)).getGraphicsConfiguration().getDevice().getIDstring();
		for(GraphicsDevice monitor : monitors){
			if(mainMonitor.equals(monitor.getIDstring()))
				continue;
			secondaryMonitor = monitor;
			break;
		}
		LOGGER.info("Set '" + mainMonitor + "' as primary display.");
		LOGGER.info("Set '" + secondaryMonitor.getIDstring() + "' as secondary display.");
		}catch(Exception e){
			//error found? return first monitor
			secondaryMonitor = monitors[0];
			LOGGER.info("Only one monitor found");
		}
		return secondaryMonitor;
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String buttonText = ((JButton) e.getSource()).getText();
			switch (buttonText) {
				case "Show field":
					frame.setLocation(getSecondaryMonitor().getDefaultConfiguration().getBounds().x, getSecondaryMonitor().getDefaultConfiguration().getBounds().y);
					frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
					frame.setTitle("Field");
					frame.setSize(fieldPanel.getFrameSizeX(), fieldPanel.getFrameSizeY());
					frame.setContentPane(fieldPanel);
					frame.setVisible(true);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;

				case "Show raster":
					fieldPanel.toggleShowRaster();
				break;

				case "Show zones":
					fieldPanel.toggleShowZones();
				break;

				case "Show robots":
					fieldPanel.toggleShowRobots();
				break;

				case "Show ball":
					fieldPanel.toggleShowBall();
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
			}
			
		}
	}
	@Override
	public void update() {
		fieldPanel.update();
	}

}
