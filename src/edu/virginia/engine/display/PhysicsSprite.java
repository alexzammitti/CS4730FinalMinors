package edu.virginia.engine.display;

import edu.virginia.engine.controller.GamePad;
import edu.virginia.engine.event.Event;

import java.util.ArrayList;

/**
 * Created by jaz on 3/2/17.
 */
public class PhysicsSprite extends AnimatedSprite {

    private int mass;
    private int xAcceleration;
    private int yAcceleration;
    private int xVelocity;
    private int yVelocity;
    private int xForce;
    private int yForce;
    private boolean airborne = true;
    private boolean onPlatform = false;

    public int getMass() {return mass;}
    public void setMass(int mass) {this.mass = mass;}
    public int getxAcceleration() {return xAcceleration;}
    public void setxAcceleration(int xAcceleration) {this.xAcceleration = xAcceleration;}
    public int getyAcceleration() {return yAcceleration;}
    public void setyAcceleration(int yAcceleration) {this.yAcceleration = yAcceleration;}
    public int getxVelocity() {return xVelocity;}
    public void setxVelocity(int xVelocity) {this.xVelocity = xVelocity;}
    public int getyVelocity() {return yVelocity;}
    public void setyVelocity(int yVelocity) {this.yVelocity = yVelocity;}
    public int getxForce() {return xForce;}
    public void setxForce(int xForce) {this.xForce = xForce;}
    public int getyForce() {return yForce;}
    public void setyForce(int yForce) {this.yForce = yForce;}
    public boolean isAirborne() {
        return airborne;
    }
    public void setAirborne(boolean airborne) {
        this.airborne = airborne;
    }
    public boolean isOnPlatform() {
        return onPlatform;
    }
    public void setOnPlatform(boolean onPlatform) {
        this.onPlatform = onPlatform;
    }

    public PhysicsSprite(String id, String imageFileName) {
        super(id, imageFileName);
        mass=1;
        xAcceleration=0;
        yAcceleration=0;
        xVelocity=0;
        yVelocity=0;
        xForce=0;
        yForce=0;
    }

    public void update(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
        super.update(pressedKeys,gamePads);
    }

}
