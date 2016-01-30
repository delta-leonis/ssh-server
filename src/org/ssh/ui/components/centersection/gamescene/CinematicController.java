package org.ssh.ui.components.centersection.gamescene;

import javafx.geometry.Point2D;
import javafx.scene.Camera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.awt.*;

/**
 * @author Jeroen
 * @date 26-1-2016
 */
public class CinematicController extends CameraController {
    private final static double MAX_SPEED = 5.0, MIN_SPEED = 1.0;

    public CinematicController(Camera camera) {
        super(camera);
    }

    @Override
    protected void update() {
    }

    @Override
    protected void updateTransition(double now) {
    }

    @Override
    protected void handleKeyEvent(KeyEvent event, boolean handle) {
        if(event.getCode() == KeyCode.SHIFT)
            speed = (event.getEventType() == KeyEvent.KEY_PRESSED) ? CinematicController.MAX_SPEED : CinematicController.MIN_SPEED;
    }

    @Override
    protected void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        t.setX(getPosition().getX());
        t.setY(getPosition().getY());
        t.setZ(getPosition().getZ());

        debug();

        affine.setToIdentity();

        ry.setAngle(
                Math.min(Math.max(((ry.getAngle() + dragDelta.getX() * (1.0 * 0.25)) % 360 + 540) % 360 - 180, -360), 360)
        ); // horizontal

        rx.setAngle(
                Math.min(Math.max(((rx.getAngle() - dragDelta.getY() * (1.0 * 0.25)) % 360 + 540) % 360 - 180, -90), 90)
        ); // vertical


        affine.prepend(t.createConcatenation(ry.createConcatenation(rx)));

    }

    @Override
    protected void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
    }

    @Override
    protected void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {

    }

    @Override
    protected void handlePrimaryMouseClick(MouseEvent e) {

    }

    @Override
    protected void handleSecondaryMouseClick(MouseEvent e) {

    }

    @Override
    protected void handleMiddleMouseClick(MouseEvent e) {

    }

    @Override
    protected void handlePrimaryMousePress(MouseEvent e) {

    }

    @Override
    protected void handleSecondaryMousePress(MouseEvent e) {

    }

    @Override
    protected void handleMiddleMousePress(MouseEvent e) {

    }

    @Override
    protected void handleMouseMoved(MouseEvent event, Point2D moveDelta, double modifier) {

    }

    @Override
    protected void handleScrollEvent(ScrollEvent event) {
        setScale(event.getDeltaX() > 0 ? s.getX() + 0.1 : Math.min(s.getX() - 0.1, 0.1));
    }

    @Override
    protected double getSpeedModifier(KeyEvent event) {
        return speed;
    }
}
