package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.PlayerComponent;
import com.ychstudio.components.StateComponent;

public class PlayerSystem extends IteratingSystem {
    
    private ComponentMapper<PlayerComponent> playerM = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<MovementComponent> movementM = ComponentMapper.getFor(MovementComponent.class);
    private ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    public PlayerSystem() {
        super(Family.all(PlayerComponent.class, MovementComponent.class, StateComponent.class).get());
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementComponent movement = movementM.get(entity);
        
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movement.velocity.set(movement.speed, 0);
        }
        
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movement.velocity.set(-movement.speed, 0);
            
        }
        
        else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.velocity.set(0, movement.speed);
            
        }
        
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            movement.velocity.set(0, -movement.speed);
            
        }
        
    }
    
}
