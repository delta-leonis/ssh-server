package org.ssh.services.consumers;

import java.util.Optional;

import org.ssh.field3d.FieldGame;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.UI;
import org.ssh.models.Model;
import org.ssh.models.Robot;
import org.ssh.models.enums.TeamColor;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.services.service.Consumer;
import org.ssh.ui.UIController;
import org.ssh.ui.windows.MainWindow;

import javafx.geometry.Point2D;
import protobuf.Detection.DetectionFrame;
import protobuf.Detection.DetectionRobot;

/**
 * The detection
 * @author marklef2
 *
 */
public class Field3DDetectionConsumer extends Consumer<DetectionPacket> {
    
    private final FieldGame fieldGame;
                            
    /**
     * Constructor.
     * 
     * @param fieldGame
     *            The {@link FieldGame}.
     */
    public Field3DDetectionConsumer() {
        
        // Initialize super class
        super("field3ddetectionconsumer");
        
        MainWindow mainWindow = (MainWindow) ((UIController<?>) UI.get("main").get());

        // Setting the field game
        this.fieldGame = mainWindow.field;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean consume(DetectionPacket pipelinePacket) {
        
        boolean changed = false;
        
        // Read detection frame
        DetectionFrame detectionFrame = pipelinePacket.read();
             
        // Loop through blue robot list
        for (DetectionRobot tmpDetectionRobot : detectionFrame.getRobotsBlueList()) {
            
            Optional<Model> optionalModel = Models.get("robot B" + tmpDetectionRobot.getRobotId());
            
            if (optionalModel.isPresent()) {
                
                // Update model
                ((Robot)optionalModel.get()).update("position",
                        new Point2D((float) tmpDetectionRobot.getX(), (float) tmpDetectionRobot.getY()));

            }
            else {
                
                // Create model
                Robot tmpRobot = (Robot) Models.create(Robot.class, tmpDetectionRobot.getRobotId(), TeamColor.BLUE);
                
                // Update model
                tmpRobot.update("position",
                        new Point2D((float) tmpDetectionRobot.getX(), (float) tmpDetectionRobot.getY()));
            
                // Changed
                changed = true;
            }
        }
        
        // Loop through yellow robot list
        for (DetectionRobot tmpDetectionRobot : detectionFrame.getRobotsYellowList()) {
            
            Optional<Model> optionalModel = Models.get("robot Y" + tmpDetectionRobot.getRobotId());
            
            if (optionalModel.isPresent()) {
                
                // Update model
                ((Robot)(optionalModel.get())).update("position",
                        new Point2D((float) tmpDetectionRobot.getX(), (float) tmpDetectionRobot.getY()));
                        
            }
            else {
                
                // Create model
                Robot tmpRobot = (Robot)Models.create(Robot.class, tmpDetectionRobot.getRobotId(), TeamColor.YELLOW);
            
                // Update model
                tmpRobot.update("position",
                        new Point2D((float) tmpDetectionRobot.getX(), (float) tmpDetectionRobot.getY()));
                
                // Changed
                changed = true;
            }
        }
        
        // Check if new models have been created
        if (changed) {
            
            // Update detection data
            this.fieldGame.updateDetection();           
        }
   
        return true;
    }
    
}
