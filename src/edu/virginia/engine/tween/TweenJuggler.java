package edu.virginia.engine.tween;

import java.util.ArrayList;

/**
 * Created by jaz on 3/17/17.
 */
public class TweenJuggler {
    private ArrayList<Tween> tweens = new ArrayList<>(0);
    private static TweenJuggler ourInstance = new TweenJuggler();
    public static TweenJuggler getInstance() {return ourInstance;}
    public TweenJuggler() {

    }
    public void add(Tween tween) {
        if(!tweens.contains(tween)){
            tweens.add(tween);
        }
    }

    public boolean tweensComplete() {
        int incomplete = 0;
        for(Tween tween : tweens) {
            if(!tween.isComplete()) incomplete++;
        }
        return incomplete == 0;
    }

    public boolean remove(Tween t) {
        return this.tweens.remove(t);
    }

    public void nextFrame() {
        for (Tween tween:tweens) {
            if(tween != null && tween.object != null) {
                tween.update();
            }
        }
    }
}
