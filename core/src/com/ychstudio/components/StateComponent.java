package com.ychstudio.components;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component {
    private float stateTime;
    private int state;
    
    public StateComponent() {
        this(0);
    }
    
    public StateComponent(int state) {
        this.state = state;
        stateTime = 0;
    }
    
    public void increaseStateTime(float delta) {
        stateTime += delta;
    }
    
    public float getStateTime() {
        return stateTime;
    }
    
    public void resetStateTime() {
        stateTime = 0;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int newState) {
        if (state == newState) {
            return;
        }
        state = newState;
        stateTime = 0;
    }
}
