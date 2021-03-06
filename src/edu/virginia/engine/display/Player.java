package edu.virginia.engine.display;

import edu.virginia.engine.controller.GamePad;
import edu.virginia.engine.event.Event;
import edu.virginia.engine.util.GameClock;

import java.util.ArrayList;

/**
 * Created by jaz on 4/11/17.
 */
public class Player extends PhysicsSprite {

    protected boolean alive = true;
    protected boolean courseCompleted = false;
    public DisplayObject platformPlayerIsOn;
    public Sprite item;
    public SlidingPlatform slidingPlatform = null;
    public Sprite cursor;
    public int playerNumber;
    protected int score = 1;
    protected boolean first = false;
    protected Sprite scoreBar = null;
    protected Sprite head = null;
    public GameClock hoverClock = new GameClock();

    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public boolean isCourseCompleted() {
        return courseCompleted;
    }
    public void setCourseCompleted(boolean courseCompleted) {
        this.courseCompleted = courseCompleted;
    }
    public boolean isFirst() {
        return first;
    }
    public void setFirst(boolean first) {
        this.first = first;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void incrementScore(int numberOfPoints) { this.score +=numberOfPoints;}
    public Sprite getScoreBar() {
        return scoreBar;
    }
    public void setScoreBar(Sprite scoreBar) {
        this.scoreBar = scoreBar;
    }
    public Sprite getHead() {
        return head;
    }
    public void setHead(Sprite head) {
        this.head = head;
    }

    public void sizeScoreBar(int winScore) {
        scoreBar.setyScale(1);
        scoreBar.setxScale((double)this.score/(double)winScore);
    }

    public Player(String id, String imageFileName, String cursorFileName, int number) {
        super(id,imageFileName);
        this.fileName = imageFileName;
        cursor = new Sprite(id + "cursor",cursorFileName);
        this.playerNumber = number;
        this.scoreBar = new Sprite("player" + playerNumber + "scorebar", "scorebar" + playerNumber + ".png");
        this.head = new Sprite("player" + playerNumber + "head", "player" + playerNumber + "-head.png");
        hoverClock.resetGameClock();
    }

    public void update(ArrayList<Integer> pressedKeys, ArrayList<GamePad> gamePads) {
        super.update(pressedKeys,gamePads);
        if(this.isAlive()) { //originally checked if player was alive
            this.setxPosition(this.getxPosition()+this.getxVelocity());
            this.setyPosition(this.getyPosition()+this.getyVelocity());
            if(this.isAirborne()) {
                this.setyVelocity(this.getyVelocity()+this.getyAcceleration());
                platformPlayerIsOn = null;
            }
        }
    }

    @Override
    public boolean collidesWith(DisplayObject object) {
        if(object != null) {
            if (object.getHitbox().intersects(this.hitbox)) {
                if(object.dangerous) {
                    this.dispatchEvent(new Event(this,object,Event.UNSAFE_COLLISION));
                } else {
                    this.dispatchEvent(new Event(this,object,Event.SAFE_COLLISION));
                }
                return true;
            }
        }
        return false;
    }

    public void fallOffPlatforms(DisplayObject platform) {
        if(platform != null) {
            if (this.isOnPlatform()) {
                if (this.getRight() < platform.getLeft() || this.getLeft() > platform.getRight()) {
                    if (this.getBottom() > platform.getTop() - 2 && this.getBottom() < platform.getTop() + 2) {
                        this.setAirborne(true);
                        this.setOnPlatform(false);
                    }
                }
            }
        }
    }

    public void constrainToLevel(int gameWidth,int gameHeight) {
        if(this.getTop() > gameHeight+100) {
            // kill players for falling off the map
            this.dispatchEvent(new Event(Event.UNSAFE_COLLISION,this));
        } else if(this.getTop() < 0) {
            //TODO there is not currently a way for us to set the global position of a sprite if it is a child
            this.setyPosition(0);
            this.setAirborne(true);
        }
        if(this.getRight() > gameWidth) {
            this.setxPosition(gameWidth-this.getScaledWidth());
            this.setxVelocity(0);
        } else if(this.getLeft() < 0) {
            this.setxPosition(0);
            this.setxVelocity(0);
        }
    }


}
