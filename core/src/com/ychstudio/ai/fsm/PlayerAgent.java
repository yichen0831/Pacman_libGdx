package com.ychstudio.ai.fsm;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.ychstudio.components.PlayerComponent;

public class PlayerAgent implements Telegraph {
    
    public PlayerComponent playerComponent;
    public StateMachine<PlayerAgent> stateMachine;

    public PlayerAgent(PlayerComponent playerComponent) {
        this.playerComponent = playerComponent;
        stateMachine = new DefaultStateMachine<>(this);
    }
    
    public void update(float deltaTime) {
        stateMachine.update();
    }
    
    @Override
    public boolean handleMessage(Telegram msg) {
        return stateMachine.handleMessage(msg);
    }
    
}
