package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ychstudio.components.GhostComponent;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.StateComponent;

public class GhostSystem extends IteratingSystem {

    private ComponentMapper<GhostComponent> ghostM = ComponentMapper.getFor(GhostComponent.class);
    private ComponentMapper<MovementComponent> movementM = ComponentMapper.getFor(MovementComponent.class);
    private ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);
    
    public GhostSystem() {
        super(Family.all(GhostComponent.class, MovementComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
    
}
