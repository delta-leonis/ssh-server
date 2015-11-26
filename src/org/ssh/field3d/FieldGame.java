package org.ssh.field3d;

import java.util.ArrayList;
import java.util.List;

import org.ssh.field3d.core.game.Game;
// import org.ssh.field3d.gameobjects.CarGO;
import org.ssh.field3d.gameobjects.FieldGO;
import org.ssh.field3d.gameobjects.RobotGO;
import org.ssh.field3d.gameobjects.overlay.CameraControlOverlayGO;
import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.models.Goal;
import org.ssh.models.Robot;
import org.ssh.models.Team;
import org.ssh.models.enums.Direction;
import org.ssh.models.enums.TeamColor;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Parent;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;
import protobuf.Geometry.FieldCicularArc;
import protobuf.Geometry.FieldLineSegment;
import protobuf.Geometry.GeometryData;
import protobuf.Geometry.GeometryFieldSize;
import protobuf.Geometry.Vector2f;

/**
 *
 * FieldGame class This class is the main part of the 3d field, from here everything in the 3d world
 * is created & managed.
 *
 * @author Mark Lefering - 330430
 *         
 */
public class FieldGame extends Game {
                                             
    /** The ambient light. */
    private final AmbientLight           ambientLight;

    /** The west point lights. */
    private final PointLight             pointLightWestSouth, pointLightWestNorth;

    /** The east point lights. */
    private final PointLight             pointLightEastSouth, pointLightEastNorth;
    
    /** The field game object. */
    private final FieldGO                fieldGO;
                                         
    /** The camera control overlay game object. */
    private final CameraControlOverlayGO cameraControlOverlayGO;
                                         
    /** The robots. */
    private List<Robot>                  robotsVisionModel;
                                         
    /** The field vision model. */
    private Field                        fieldVisionModel;
                                         
    /** The easter car game object */
    // private final CarGO easterCarGO;
    
    /**
     * Constructor.
     *
     * @param root
     *            The root of the SubScene.
     * @param width
     *            The width of the SubScene.
     * @param height
     *            The height of the SubScene.
     * @param antiAliasing
     *            Anti-aliasing mode, SceneAntialiasing.DISABLED.
     */
    public FieldGame(final Parent root, final double width, final double height, final SceneAntialiasing antiAliasing) {
        
        // Initialize super class
        super(root, width, height, true, antiAliasing);
        
        // Create the models needed
        this.createModels();
        
        // Creating ambient light
        this.ambientLight = new AmbientLight(Color.DARKGRAY);
        
        // Creating point lights
        this.pointLightWestSouth = new PointLight(Color.WHITE);
        this.pointLightWestNorth = new PointLight(Color.WHITE);
        this.pointLightEastSouth = new PointLight(Color.WHITE);
        this.pointLightEastNorth = new PointLight(Color.WHITE);
        
        // Create some robots
        this.createRobots();
        
        // Creating field GameObject
        this.fieldGO = new FieldGO(this, fieldVisionModel);
        // Creating camera control overlay GameObject
        this.cameraControlOverlayGO = new CameraControlOverlayGO(this);
        // Creating easter egg car GameObject
        // this.easterCarGO = new CarGO(this);
        
        // Setup lights
        this.pointLightWestSouth.setTranslateX(-(this.fieldVisionModel.getFieldLength() / 4.0));
        this.pointLightWestSouth.setTranslateY(2000.0);
        this.pointLightWestSouth.setTranslateZ(-(this.fieldVisionModel.getFieldWidth() / 4.0));
        
        this.pointLightWestNorth.setTranslateX(-(this.fieldVisionModel.getFieldLength() / 4.0));
        this.pointLightWestNorth.setTranslateY(2000.0);
        this.pointLightWestNorth.setTranslateZ(this.fieldVisionModel.getFieldWidth() / 4.0);
        
        this.pointLightEastSouth.setTranslateX(this.fieldVisionModel.getFieldLength() / 4.0);
        this.pointLightEastSouth.setTranslateY(2000.0);
        this.pointLightEastSouth.setTranslateZ(-(this.fieldVisionModel.getFieldWidth() / 4.0));
        
        this.pointLightEastNorth.setTranslateX(this.fieldVisionModel.getFieldLength() / 4.0);
        this.pointLightEastNorth.setTranslateY(2000.0);
        this.pointLightEastNorth.setTranslateZ(this.fieldVisionModel.getFieldWidth() / 4.0);
        
        // Set minimal mouse wheel value
        this.getMouseInputHandler().setMinMouseWheelValue(-1000);
        // Set maximal mouse wheel value
        this.getMouseInputHandler().setMaxMouseWheelValue(1000);
        
        // Set black fill color
        this.setFill(Color.BLACK);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        
        // Setting bounds for the location of the camera
        this.getThirdPersonCamera().setMaxLocX(this.fieldVisionModel.getFieldLength() / 2.0);
        this.getThirdPersonCamera().setMinLocX(-(this.fieldVisionModel.getFieldLength() / 2.0));
        this.getThirdPersonCamera().setMaxLocZ(this.fieldVisionModel.getFieldWidth() / 2.0);
        this.getThirdPersonCamera().setMinLocZ(-(this.fieldVisionModel.getFieldWidth() / 2.0));
        
        // Add lights to the world
        this.getWorldGroup().getChildren().add(this.ambientLight);
        this.getWorldGroup().getChildren().add(this.pointLightWestSouth);
        this.getWorldGroup().getChildren().add(this.pointLightWestNorth);
        this.getWorldGroup().getChildren().add(this.pointLightEastSouth);
        this.getWorldGroup().getChildren().add(this.pointLightEastNorth);
        
        // Adding game objects
        this.addGameObject(this.fieldGO);
        this.addGameObject(this.cameraControlOverlayGO);
        // this.addGameObject(this.easterCarGO);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long timeDivNano) {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
    }
    
    /**
     * 
     * AddRobot method This method adds a RobotGO to the game.
     * 
     * @param robot
     *            The RobotGO to add to the game.
     */
    public void addRobot(final RobotGO robot) {
        
        // Add robot to game
        this.addGameObject(robot);
    }
    
    /**
     * 
     * RemoveRobot method This method removes a RobotGO from the game.
     * 
     * @param robot
     *            The RobotGO to remove from the game.
     */
    public void removeRobot(final RobotGO robot) {
        
        // Remove robot from game
        this.removeGameObject(robot);
    }
    
    /**
     * Gets the {@link List} of robots used in the game.
     *
     * @return The {@link List} of robots.
     */
    public List<Robot> getRobots() {
        return this.robotsVisionModel;
    }
    
    /**
     * createRobots method. This method creates the robots on the field.
     */
    @SuppressWarnings ("unchecked")
    private void createRobots() {
        
        // Getting list of robots from the vision model
        this.robotsVisionModel = (ArrayList<Robot>) Models.getAll("robot");
        
        // Loop through robot models
        for (Robot robot : this.robotsVisionModel) {
            
            // Creating new robot
            RobotGO tmpRobot = new RobotGO(this, robot);
            
            // Add to game objects
            addGameObject(tmpRobot);
        }
    }
    
    private GeometryFieldSize createGeometryFieldSize() {
        
        // Middle line
        Vector2f midLineStart = Vector2f.newBuilder().setX(0.0f).setY(3000.0f).build();
        Vector2f midLineEnd = Vector2f.newBuilder().setX(0.0f).setY(-3000.0f).build();
        FieldLineSegment midLine = FieldLineSegment.newBuilder().setP1(midLineStart).setP2(midLineEnd)
                .setThickness(10.0f).build();
                
        // North line
        Vector2f northLineStart = Vector2f.newBuilder().setX(-4500.0f).setY(3000.0f).build();
        Vector2f northLineEnd = Vector2f.newBuilder().setX(4500.0f).setY(3000.0f).build();
        FieldLineSegment northLine = FieldLineSegment.newBuilder().setP1(northLineStart).setP2(northLineEnd)
                .setThickness(10.0f).build();
                
        // South line
        Vector2f southLineStart = Vector2f.newBuilder().setX(-4500.0f).setY(-3000.0f).build();
        Vector2f southLineEnd = Vector2f.newBuilder().setX(4500.0f).setY(-3000.0f).build();
        FieldLineSegment southLine = FieldLineSegment.newBuilder().setP1(southLineStart).setP2(southLineEnd)
                .setThickness(10.0f).build();
                
        // East line
        Vector2f eastLineStart = Vector2f.newBuilder().setX(4500.0f).setY(-3000.0f).build();
        Vector2f eastLineEnd = Vector2f.newBuilder().setX(4500.0f).setY(3000.0f).build();
        FieldLineSegment eastLine = FieldLineSegment.newBuilder().setP1(eastLineStart).setP2(eastLineEnd)
                .setThickness(10.0f).build();
                
        // East defense line
        Vector2f eastDefenseLineStart = Vector2f.newBuilder().setX(3500.0f).setY(250.0f).build();
        Vector2f eastDefenseLineEnd = Vector2f.newBuilder().setX(3500.0f).setY(-250.0f).build();
        FieldLineSegment eastDefenseLine = FieldLineSegment.newBuilder().setP1(eastDefenseLineStart)
                .setP2(eastDefenseLineEnd).setThickness(10.0f).build();
                
        // West line
        Vector2f westLineStart = Vector2f.newBuilder().setX(-4500.0f).setY(-3000.0f).build();
        Vector2f westLineEnd = Vector2f.newBuilder().setX(-4500.0f).setY(3000.0f).build();
        FieldLineSegment westLine = FieldLineSegment.newBuilder().setP1(westLineStart).setP2(westLineEnd)
                .setThickness(10.0f).build();
                
        // West defense line
        Vector2f westDefenseLineStart = Vector2f.newBuilder().setX(-3500.0f).setY(250.0f).build();
        Vector2f westDefenseLineEnd = Vector2f.newBuilder().setX(-3500.0f).setY(-250.0f).build();
        FieldLineSegment westDefenseLine = FieldLineSegment.newBuilder().setP1(westDefenseLineStart)
                .setP2(westDefenseLineEnd).setThickness(10.0f).build();
                
        // Mid circle
        FieldCicularArc midCircle = FieldCicularArc.newBuilder().setA1(0.0f).setA2(360.0f).setThickness(10.0f)
                .setRadius(500.0f).build();
                
        // East defense arc left
        Vector2f eastDefenseArcLeftCenter = Vector2f.newBuilder().setX(-4500.0f).setY(250.0f).build();
        FieldCicularArc eastDefenseArcLeft = FieldCicularArc.newBuilder().setA1(0.0f).setA2(90.0f).setThickness(10.0f)
                .setRadius(1000.0f).setCenter(eastDefenseArcLeftCenter).build();
                
        // East defense arc right
        Vector2f eastDefenseArcRightCenter = Vector2f.newBuilder().setX(-4500.0f).setY(-250.0f).build();
        FieldCicularArc eastDefenseArcRight = FieldCicularArc.newBuilder().setA1(270.0f).setA2(360.0f)
                .setThickness(10.0f).setRadius(1000.0f).setCenter(eastDefenseArcRightCenter).build();
                
        // West defense arc left
        Vector2f westDefenseArcLeftCenter = Vector2f.newBuilder().setX(4500.0f).setY(-250.0f).build();
        FieldCicularArc westDefenseArcLeft = FieldCicularArc.newBuilder().setA1(180.0f).setA2(270.0f)
                .setThickness(10.0f).setRadius(1000.0f).setCenter(westDefenseArcLeftCenter).build();
                
        // West defense arc right
        Vector2f westDefenseArcRightCenter = Vector2f.newBuilder().setX(4500.0f).setY(250.0f).build();
        FieldCicularArc westDefenseArcRight = FieldCicularArc.newBuilder().setA1(90.0f).setA2(180.0f)
                .setThickness(10.0f).setRadius(1000.0f).setCenter(westDefenseArcRightCenter).build();
                
        GeometryFieldSize fieldSize = GeometryData.newBuilder().getFieldBuilder().setFieldWidth(6000)
                .setFieldLength(9000).addFieldLines(midLine).addFieldLines(northLine).addFieldLines(southLine)
                .addFieldLines(eastLine).addFieldLines(eastDefenseLine).addFieldLines(westLine)
                .addFieldLines(westDefenseLine).addFieldArcs(midCircle).addFieldArcs(eastDefenseArcLeft)
                .addFieldArcs(eastDefenseArcRight).addFieldArcs(westDefenseArcLeft).addFieldArcs(westDefenseArcRight)
                .build();
                
        return fieldSize;
    }
    
    private void createModels() {
        
        // Create robots
        Robot robot = (Robot) Models.create(Robot.class, 0, TeamColor.BLUE);
        Robot robot2 = (Robot) Models.create(Robot.class, 1, TeamColor.BLUE);
        Robot robot3 = (Robot) Models.create(Robot.class, 2, TeamColor.BLUE);
        Robot robot4 = (Robot) Models.create(Robot.class, 3, TeamColor.BLUE);
        
        // Allies (yellow) on the west side of the field
        Team teamAllies = (Team) Models.create(Team.class, Direction.WEST, TeamColor.YELLOW);
        // Opponents (blue) on the east side of the field
        Team teamOpponents = (Team) Models.create(Team.class, Direction.EAST, TeamColor.BLUE);
        
        // Creating field model
        this.fieldVisionModel = (Field) Models.create(Field.class);
        
        List<Goal> goals = new ArrayList<Goal>();
        Goal goalEast = (Goal) Models.create(Goal.class, Direction.EAST);
        Goal goalWest = (Goal) Models.create(Goal.class, Direction.WEST);
        
        goalEast.update("goalWidth", new Integer(1000));
        goalEast.update("goalHeight", new Integer(160));
        goalEast.update("goalDepth", new Integer(180));
        goalEast.update("position", new Point2D(4500.0f + 90.0f, 0.0f));
        
        goalWest.update("goalWidth", new Integer(1000));
        goalWest.update("goalHeight", new Integer(160));
        goalWest.update("goalDepth", new Integer(180));
        goalWest.update("position", new Point2D(-4500.0f - 90.0f, 0.0f));
        
        goals.add(goalWest);
        goals.add(goalEast);
        
        // Update field, create field geometry field size
        this.fieldVisionModel.update("field", createGeometryFieldSize());
        
        this.fieldVisionModel.update("goals", goals);
        
        this.fieldVisionModel.save();
        this.fieldVisionModel.saveAsDefault();
        
        robot.update("isSelected", true);
        robot2.update("isSelected", false);
        robot3.update("isSelected", false);
        robot4.update("isSelected", false);
        
        robot.update("position", new Point3D(0.0, 0.0, 0.0));
    }
}
