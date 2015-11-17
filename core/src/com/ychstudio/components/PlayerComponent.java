package com.ychstudio.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {

    public final int IDLE = 0;
    public final int MOVE_UP = 1;
    public final int MOVE_DOWN = 2;
    public final int MOVE_LEFT = 3;
    public final int MOVE_RIGHT = 4;
    public final int DIE = 5;
    
    public int currentState;

    public PlayerComponent() {
        currentState = 0;
    }
}
