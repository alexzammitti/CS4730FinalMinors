package edu.virginia.engine.tween;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.event.Event;
import edu.virginia.engine.event.EventDispatcher;

/**
 * Created by jaz on 3/17/17.
 */
public class Tween extends EventDispatcher {

    public DisplayObject object;
    private TweenTransition transition;
    private double elapsed;
    private double range;
    private TweenParam param;
    private boolean completed = false;
    private boolean isRunning = false;

    public Tween(DisplayObject object) {
        this.object = object;
    }
    public Tween(DisplayObject object, TweenTransition transition) {
        this.object = object;
        this.transition = transition;
    }
    public void animate(TweenableParam fieldToAnimate, double startVal, double endVal, double time) {
        if(!isRunning) {
            param = new TweenParam(fieldToAnimate, startVal, endVal, time);
            this.range = endVal - startVal;
            elapsed = 0;
            isRunning = true;
        }
    }

    public void update() {
        if(param != null) {
            // c * t/d + b
            double curVal = range * transition.applyTransition(elapsed / param.time) + param.startVal;
            elapsed++;
            if (elapsed / param.time >= 1) {
                Event event = new Event(Event.TWEEN_COMPLETE_EVENT);
                event.setSource(this);
                this.dispatchEvent(event);
                this.completed = true;
                if(object.getAlpha() < 0.05) {
                    object.setVisible(false);
                }
                return;
            }
            switch (param.getParam()) {
                case ALPHA:
                    object.setAlpha((float) curVal);
                    break;
                case X:
                    object.setxPosition((int) curVal);
                    break;
                case Y:
                    object.setyPosition((int) curVal);
                    break;
                case ROTATION:
                    object.setRotation(curVal);
                    break;
                case SCALE_X:
                    object.setxScale(curVal);
                    break;
                case SCALE_Y:
                    object.setyScale(curVal);
                    break;
            }
        }
    }
    public boolean isComplete() {
        return completed;
    }
    public void setValue(TweenableParam param, double value) {
    }
}
