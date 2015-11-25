package com.ychstudio.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.ychstudio.ai.PlayerAI;
import com.ychstudio.ai.fsm.PlayerAgent;
import com.ychstudio.ai.fsm.PlayerState;

public class PlayerComponent implements Component {

    public static final int IDLE = 0;
    public static final int IDLE_UP = 0;
    public static final int IDLE_DOWN = 1;
    public static final int IDLE_LEFT = 2;
    public static final int IDLE_RIGHT = 3;

    public static final int MOVE_UP = 4;
    public static final int MOVE_DOWN = 5;
    public static final int MOVE_LEFT = 6;
    public static final int MOVE_RIGHT = 7;
    public static final int DIE = 8;

    public PlayerAI ai;
    public PlayerAgent playerAgent;

    private final Body body;

    public int currentState;

    public int hp;

    public float invincibleTimer;

    public PlayerComponent(Body body) {
        this.body = body;
        ai = new PlayerAI(body);
        playerAgent = new PlayerAgent(this);
        playerAgent.stateMachine.setInitialState(PlayerState.IDLE_RIGHT);
        currentState = IDLE_RIGHT;
        hp = 1;
        invincibleTimer = 0;
    }

    public Body getBody() {
        return body;
    }
}
