package com.ychstudio.ai.fsm;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.ychstudio.components.PlayerComponent;

public class PlayerAgent implements Telegraph {

    public final PlayerComponent playerComponent;
    public final StateMachine<PlayerAgent, PlayerState> stateMachine;

    public float timer;

    public PlayerAgent(PlayerComponent playerComponent) {
        this.playerComponent = playerComponent;
        stateMachine = new DefaultStateMachine<>(this);
        timer = 0;
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
