package com.ychstudio.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.ychstudio.ai.fsm.GhostAgent;
import com.ychstudio.ai.fsm.GhostState;

public class GhostComponent implements Component {

    // state
    public static final int MOVE_UP = 0;
    public static final int MOVE_DOWN = 1;
    public static final int MOVE_LEFT = 2;
    public static final int MOVE_RIGHT = 3;
    public static final int ESCAPE = 4;
    public static final int DIE = 5;

    public static final float WEAK_TIME = 10f;
    public float weak_time;

    public GhostAgent ghostAgent;

    private final Body body;

    public int currentState;

    public boolean weaken;
    public int hp;

    public GhostComponent(Body body) {
        this.body = body;
        ghostAgent = new GhostAgent(this);
        ghostAgent.stateMachine.setInitialState(GhostState.MOVE_UP);
        currentState = MOVE_UP;
        weaken = false;
        hp = 1;
    }

    public Body getBody() {
        return body;
    }

    public void respawn() {
        hp = 1;
        weaken = false;
    }
}
