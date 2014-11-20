package robocup.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;

import robocup.Main;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author Mark
 */
public class GUI extends javax.swing.JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1337455436438979938L;
	private World world;
	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private long startTime;
	private String fieldHalf = "Left";

	/**
	 * Creates new form GUI
	 */
	public GUI(World world) {
		LOGGER.info("GUI started");

		this.world = world;
		world.addObserver(this);
		initComponents();
		// TODO init gui part of robot status data
		this.setLocationRelativeTo(null);
	}

	public GUI() {
		world = World.getInstance();
		initComponents();
		this.setLocationRelativeTo(null);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		startTime = System.currentTimeMillis();

		robotDataPanel = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		timePlayedField = new javax.swing.JTextPane();
		jLabel1 = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		gameStateField = new javax.swing.JTextPane();
		jLabel2 = new javax.swing.JLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		refereeStateField = new javax.swing.JTextPane();
		jLabel3 = new javax.swing.JLabel();
		jScrollPane4 = new javax.swing.JScrollPane();
		goalsField = new javax.swing.JTextPane();
		jLabel4 = new javax.swing.JLabel();
		jScrollPane5 = new javax.swing.JScrollPane();
		fieldHalfField = new javax.swing.JTextPane();
		jLabel7 = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		jLabel5 = new javax.swing.JLabel();
		fieldHalfSpinner = new javax.swing.JSpinner();
		jLabel6 = new javax.swing.JLabel();
		setButton = new javax.swing.JButton();
		keeperIdSpinner = new javax.swing.JSpinner();
		jPanel4 = new javax.swing.JPanel();
		startButton = new javax.swing.JButton();
		terminateButton = new javax.swing.JButton();
		kickButton = new javax.swing.JButton();
		robotPanels = new RobotPanel[12];

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		robotDataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Robot Data"));

		javax.swing.GroupLayout robotDataPanelLayout = new javax.swing.GroupLayout(robotDataPanel);
		robotDataPanel.setLayout(robotDataPanelLayout);

		GroupLayout.SequentialGroup hGroup = robotDataPanelLayout.createSequentialGroup();
		ParallelGroup phGroup = robotDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);

		GroupLayout.SequentialGroup vGroup = robotDataPanelLayout.createSequentialGroup();
		hGroup.addGroup(phGroup);
		phGroup.addGap(0, 0, Short.MAX_VALUE);

		for (int i = 0; i < robotPanels.length; i++) {
			robotPanels[i] = new RobotPanel(i);
			phGroup.addComponent(robotPanels[i], javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
			vGroup.addComponent(robotPanels[i], javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
			vGroup.addGap(2, 2, 2);
		}
		phGroup.addGap(0, 0, Short.MAX_VALUE);
		vGroup.addGap(0, 0, Short.MAX_VALUE);

		robotDataPanelLayout.setHorizontalGroup(robotDataPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(hGroup));
		robotDataPanelLayout.setVerticalGroup(robotDataPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(vGroup));

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Status"));

		timePlayedField.setEditable(false);
		jScrollPane1.setViewportView(timePlayedField);

		jLabel1.setText("Time played");

		gameStateField.setEditable(false);
		jScrollPane2.setViewportView(gameStateField);

		jLabel2.setText("Game status");

		refereeStateField.setEditable(false);
		jScrollPane3.setViewportView(refereeStateField);

		jLabel3.setText("Referee status");

		goalsField.setEditable(false);
		jScrollPane4.setViewportView(goalsField);

		jLabel4.setText("Goals");

		fieldHalfField.setEditable(false);
		jScrollPane5.setViewportView(fieldHalfField);

		jLabel7.setText("Field Half");

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel2Layout
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel1).addComponent(jLabel2).addComponent(jLabel3)
												.addComponent(jLabel4).addComponent(jLabel7))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jScrollPane2).addComponent(jScrollPane1)
												.addComponent(jScrollPane3).addComponent(jScrollPane4)
												.addComponent(jScrollPane5)).addContainerGap()));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addGap(0, 11, Short.MAX_VALUE)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel7)
														.addComponent(jScrollPane5,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jScrollPane1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel1))
										.addGap(10, 10, 10)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jScrollPane2,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel2))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jScrollPane3,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel3))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jScrollPane4,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel4))));

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings"));

		jLabel5.setText("Keeper Id");

		fieldHalfSpinner.setModel(new javax.swing.SpinnerListModel(new String[] { "Left", "Right" }));

		jLabel6.setText("Field Half");

		setButton.setText("Set");
		setButton.setName("setButton"); // NOI18N
		setButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setButtonActionPerformed(evt);
			}
		});

		keeperIdSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 12, 1));

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel3Layout.createSequentialGroup()
																		.addGap(0, 0, Short.MAX_VALUE)
																		.addComponent(setButton))
														.addGroup(
																jPanel3Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel3Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(jLabel5)
																						.addComponent(jLabel6))
																		.addGap(36, 36, 36)
																		.addGroup(
																				jPanel3Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(fieldHalfSpinner)
																						.addComponent(keeperIdSpinner))))
										.addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel3Layout
								.createSequentialGroup()
								.addContainerGap(17, Short.MAX_VALUE)
								.addGroup(
										jPanel3Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel5)
												.addComponent(keeperIdSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(13, 13, 13)
								.addGroup(
										jPanel3Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(fieldHalfSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel6))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(setButton)));

		jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Controls"));

		startButton.setText("Start");
		startButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startButtonActionPerformed(evt);
			}
		});

		terminateButton.setText("Terminate");
		terminateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				terminateButtonActionPerformed(evt);
			}
		});

		kickButton.setText("Kick");
		kickButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				kickButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel4Layout
								.createSequentialGroup()
								.addGroup(
										jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														jPanel4Layout.createSequentialGroup().addGap(8, 8, 8)
																.addComponent(startButton).addGap(18, 18, 18)
																.addComponent(terminateButton))
												.addGroup(
														jPanel4Layout.createSequentialGroup().addContainerGap()
																.addComponent(kickButton)))
								.addGap(0, 0, Short.MAX_VALUE)));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel4Layout
								.createSequentialGroup()
								.addContainerGap(15, Short.MAX_VALUE)
								.addComponent(kickButton)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(startButton).addComponent(terminateButton))
								.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap(35, Short.MAX_VALUE)
						.addComponent(robotDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												layout.createSequentialGroup()
														.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(robotDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap()));

		// TODO: generate full list of robots in gui with their respective Id,
		// state, position and assigned behavior

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void setButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_setButtonActionPerformed
		fieldHalf = fieldHalfSpinner.getValue().toString();
		fieldHalfField.setText(fieldHalf);
		// TODO add your handling code here:
	}// GEN-LAST:event_setButtonActionPerformed

	private void terminateButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_terminateButtonActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_terminateButtonActionPerformed

	private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_startButtonActionPerformed
		boolean startAction = false;
		if (startButton.getText().equals("Start")) {
			startAction = true;
		}
		
		if(startAction){
			startButton.setText("Stop");
		 } else {
			 startButton.setText("Start");
		 }
		
		world.getReferee().setStart(startAction);
	}// GEN-LAST:event_startButtonActionPerformed

	private void kickButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_kickButtonActionPerformed
		System.out.println("Kick");
		ComInterface.getInstance(RobotCom.class).send(1, robocup.Main.TEST_ROBOT_ID, 0, 0, 0, 0, 0, 40, false);
	}// GEN-LAST:event_kickButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTextPane fieldHalfField;
	private javax.swing.JSpinner fieldHalfSpinner;
	private javax.swing.JTextPane gameStateField;
	private javax.swing.JTextPane goalsField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JScrollPane jScrollPane5;
	private javax.swing.JSpinner keeperIdSpinner;
	private javax.swing.JButton kickButton;
	private javax.swing.JTextPane refereeStateField;
	private javax.swing.JPanel robotDataPanel;
	private javax.swing.JButton setButton;
	private javax.swing.JButton startButton;
	private javax.swing.JButton terminateButton;
	private javax.swing.JTextPane timePlayedField;
	private RobotPanel[] robotPanels;

	// End of variables declaration//GEN-END:variables

	private void setRobotNumber(int number) {
		// robotNumber.setText(Integer.toString(number));
	}

	private void setRobotSpeed(int number) {
		// robotSpeed.setText(Integer.toString(number));

	}

	private void setTeamColor(String color) {
		// teamColor.setText(color);
	}

	private void setTeamName(String name) {
		// teamName.setText(name);
	}

	private void setTeamSide(String side) {
		// teamSide.setText(side);
	}

	private void setIsOnline(boolean online) {
		// isOnline.setEnabled(online);
		// isOnline.setSelected(online);
	}

	private void setBatteryVoltage(float voltage) {
		// batteryVoltage.setText(Float.toString(voltage));
	}

	private void setGoals(int number) {
		goalsField.setText(Integer.toString(number));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0.equals(world)) {

			//
			long timePassed = ((System.currentTimeMillis() - startTime));

			boolean[] robotUpdated = new boolean[12];

			Arrays.fill(robotUpdated, false);
			for (Robot robot : world.getAlly().getRobots()) {
				//System.out.println(robot.getRobotID() + "    pos:" + robot.getPosition());
				robotUpdated[robot.getRobotID()] = true;
			}

			if (timePassed % 25 == 0) {
				for (int i = 0; i < robotPanels.length; i++) {
					// if robotpanelid also in list of robot id's then set their
					// status else set to default

					if (robotUpdated[i]) {
						// update goed
						Robot robot = world.getAlly().getRobotByID(i);
						robotPanels[i].setPoint(robot.getPosition());
						robotPanels[i].setStatus("online");
						robotPanels[i].setRole("unavailable");
						
					} else {
						// update is niet langer verbonden
						robotPanels[i].setToDefault();
					}

				}

			}
			timePlayedField.setText("" + ((System.currentTimeMillis() - startTime) / 1000));
			fieldHalfField.setText("null");
			goalsField.setText("" + world.getAlly().getScore());
			
			
		//	System.out.println(world.getReferee().);;
			// refereeStateField.setText(""+
			// world.getReferee().getStage().name());

			// setRobotNumber(int number)
			// TODO set the game status data
			// TODO set the status for all robots
			//System.out.println(" hoi," + world.getReferee().getl .getYellow().getGoalie() + " command: " + message.getYellow().getRedCards());
			
			

		}
	}
}
