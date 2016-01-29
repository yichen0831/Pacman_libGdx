package com.ychstudio.ai.fsm;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.ychstudio.ai.astar.Node;
import com.ychstudio.components.GhostComponent;

public class GhostAgent implements Telegraph {

    public StateMachine<GhostAgent, GhostState> stateMachine;

    public GhostComponent ghostComponent;

    public float speed = 2.4f;

    public float timer;

    public Node nextNode; // for pursue or escape

    public GhostAgent(GhostComponent ghostComponent) {
        this.ghostComponent = ghostComponent;
        stateMachine = new DefaultStateMachine<>(this);

        timer = 0;
    }

    public Vector2 getPosition() {
        return ghostComponent.getBody().getPosition();
    }

    public void update(float deltaTime) {
        timer += deltaTime;

        stateMachine.update();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return stateMachine.handleMessage(msg);
    }

}
