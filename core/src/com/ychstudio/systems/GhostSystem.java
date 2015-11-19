package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ychstudio.components.GhostComponent;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.StateComponent;
import com.ychstudio.gamesys.GameManager;

public class GhostSystem extends IteratingSystem {

    private final ComponentMapper<GhostComponent> ghostM = ComponentMapper.getFor(GhostComponent.class);
    private final ComponentMapper<MovementComponent> movementM = ComponentMapper.getFor(MovementComponent.class);
    private final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);
    
    public GhostSystem() {
        super(Family.all(GhostComponent.class, MovementComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GhostComponent ghost = ghostM.get(entity);
        
        if (ghost.getPosition().dst2(GameManager.instance.playerLocation.getPosition()) < 25f) {
            ghost.setBehavior(GhostComponent.ARRIVE_BEHAVIOR);
        }
        else {
            ghost.setBehavior(GhostComponent.WANDER_BEHAVIOR);
        }
        
        ghost.update(deltaTime);
    }
    
}
