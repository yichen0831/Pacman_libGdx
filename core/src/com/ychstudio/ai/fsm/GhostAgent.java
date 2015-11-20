package com.ychstudio.ai.fsm;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.ychstudio.components.GhostComponent;
import com.ychstudio.gamesys.GameManager;

public class GhostAgent implements Telegraph {

    public StateMachine<GhostAgent> stateMachine;

    public GhostComponent ghostComponent;

    public float speed = 2.4f;

    private Location<Vector2> target;
    
    public float timer;

    public GhostAgent(GhostComponent ghostComponent) {
        this.ghostComponent = ghostComponent;
        stateMachine = new DefaultStateMachine<>(this);
        
        target = GameManager.instance.playerLocation;
        
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
