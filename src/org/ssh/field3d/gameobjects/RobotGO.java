/**************************************************************************************************
 *
 * RobotGO This class is for our Robot GameObjects.
 *
 **************************************************************************************************
 *
 * TODO: change size according to zoom of camera TODO: javadoc TODO: comment TODO: cleanup
 *
 **************************************************************************************************
 * @see GameObject
 *      
 * @author marklef2
 * @date 15-10-2015
 */
package org.ssh.field3d.gameobjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.ssh.field3d.FieldGame;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.shapes.Arc3D;
import org.ssh.field3d.gameobjects.contextmenus.RobotInfoContextMenu;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

public class RobotGO extends GameObject {
    
    // TODO: MOVE TO CONFIG
    public static final double         ROBOT_HEIGHT                     = 200.0;
    public static final double         ROBOT_RADIUS                     = 250.0;
    public static final double         ROBOT_SELECTION_CIRCLE_THICKNESS = 50.0;
                                                                        
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private MeshView                   _model;
    private final MeshView             _selectionArcMesh;
    private Vector3f                   _loc;
    private final Vector3f             _vel;
    private final PhongMaterial        _material;
    private final PhongMaterial        _selectionCircleMaterial;
    private final RobotInfoContextMenu _contextMenu;
    private final Arc3D                _selectionArc;
    private Color                      _selectionCircleColor;
                                       
    private boolean                    _isSelected;
                                       
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public RobotGO(final Game game) {
        
        super(game);
        
        final ObjModelImporter modelImporter = new ObjModelImporter();
        
        this._loc = new Vector3f(0.0f, 10.0f, 0.0f);
        this._vel = new Vector3f();
        this._selectionArc = new Arc3D(0.0, 360.0, RobotGO.ROBOT_RADIUS, RobotGO.ROBOT_SELECTION_CIRCLE_THICKNESS, 100);
        
        this._material = new PhongMaterial(Color.WHITE);
        this._selectionCircleMaterial = new PhongMaterial();
        this._contextMenu = new RobotInfoContextMenu(game);
        
        this._selectionArcMesh = this._selectionArc.MeshView();
        this._selectionArcMesh.setCullFace(CullFace.NONE);
        this._selectionArcMesh.setRotationAxis(Rotate.X_AXIS);
        this._selectionArcMesh.setRotate(90.0);
        
        // Setting selection circle color & material
        this._selectionCircleColor = Color.BLUE;
        this._selectionCircleMaterial.setDiffuseColor(this._selectionCircleColor);
        this._selectionCircleMaterial.setSpecularColor(this._selectionCircleColor);
        this._selectionCircleMaterial.setSpecularPower(20.0);
        
        this._selectionArcMesh.setMaterial(this._selectionCircleMaterial);
        ;
        
        // Read org.ssh.models
        modelImporter.read("./assets/models/robot_model.obj");
        
        // Check if we've loaded successfully
        if (modelImporter.getImport().length > 0) {
            
            // Getting org.ssh.models from the org.ssh.models importer
            this._model = modelImporter.getImport()[0];
            
            try {
                this._material.setDiffuseMap(new Image(new FileInputStream("./assets/textures/robotTextureTest2.png")));
                this._material.setSpecularColor(Color.WHITE);
            }
            catch (final FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Setting org.ssh.models material
            this._model.setMaterial(this._material);
            
        }
        else {
            
            // Show error
            System.out.println("Missing ./assets/models/robots/yellow/robot_yellow_8.obj");
        }
        
        // Setting not selected
        this._isSelected = false;
        this.SetSelected(this._isSelected);
    }
    
    @Override
    public void Destroy() {
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public Vector3f GetLocation() {
        return this._loc;
    }
    
    public boolean GetSelected() {
        return this._isSelected;
    }
    
    public Color GetSelectionCircleColor() {
        return this._selectionCircleColor;
    }
    
    public Vector3f GetVelocity() {
        return this._vel;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Overridden methods from GameObject
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void Initialize() {
        
        // Create a org.ssh.models group
        final Group modelGroup = new Group();
        
        // Add our org.ssh.models to the org.ssh.models group
        modelGroup.getChildren().add(this._model);
        modelGroup.getChildren().add(this._selectionArcMesh);
        
        // Add org.ssh.models group to the world group
        this.GetGame().GetWorldGroup().getChildren().addAll(modelGroup);
        
        this.GetGame().AddGameObject(this._contextMenu);
        
        this._model.setOnMouseClicked(mouseEvent -> RobotGO.this.SetSelected(!RobotGO.this._isSelected));
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Setters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void SetLocation(final Vector3f location) {
        this._loc = location;
    }
    
    public void SetSelected(final boolean isSelected) {
        
        this._isSelected = isSelected;
        this._selectionArcMesh.setVisible(this._isSelected);
    }
    
    public void SetSelectionCircleColor(final Color color) {
        
        // Set selection circle color
        this._selectionCircleColor = color;
        
        // Update material
        this._selectionCircleMaterial.setDiffuseColor(this._selectionCircleColor);
        this._selectionCircleMaterial.setSpecularColor(this._selectionCircleColor);
    }
    
    @Override
    public void Update(final long timeDivNano) {
        
        // Check if we are on the border
        if (this._loc.z > (FieldGame.FIELD_DEPTH / 2)) {
            
            // Limit location
            this._loc.z = (float) (FieldGame.FIELD_DEPTH / 2.0f);
            // Invert velocity
            this._vel.z *= -1;
            
        }
        else if (this._loc.z < -(FieldGame.FIELD_DEPTH / 2)) {
            
            // Limit location
            this._loc.z = (float) -(FieldGame.FIELD_DEPTH / 2.0f);
            // Invert velocity
            this._vel.z *= -1;
        }
        
        // Update location
        this._loc = this._loc.add(this._vel.scale(timeDivNano / 1000000000.0f));
        
        this._contextMenu.Translate(this._loc.x, this._loc.y + this._contextMenu.GetHeight(), this._loc.z);
        
        this._contextMenu.SetLabelLocationText("Location: " + this._loc);
        this._contextMenu.SetLabelSpeedText("Speed: " + this._vel);
        
        // Translate to location
        this._model.setTranslateX(this._loc.x);
        this._model.setTranslateY(this._loc.y);
        this._model.setTranslateZ(this._loc.z);
        
        // Translate selection circle to location
        this._selectionArcMesh.setTranslateX(this._loc.x);
        this._selectionArcMesh.setTranslateY(this._loc.y - (RobotGO.ROBOT_HEIGHT / 2.2));
        this._selectionArcMesh.setTranslateZ(this._loc.z);
    }
}
