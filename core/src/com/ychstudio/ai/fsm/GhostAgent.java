package com.ychstudio.ai.fsm;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.ychstudio.components.GhostComponent;

public class GhostAgent implements Telegraph {

    public StateMachine<GhostAgent> stateMachine;
    
    public GhostComponent ghostComponent;
    
    public GhostAgent(GhostComponent ghostComponent) {
        this.ghostComponent = ghostComponent;
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
