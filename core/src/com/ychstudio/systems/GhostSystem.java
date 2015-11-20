package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ychstudio.components.GhostComponent;
import com.ychstudio.components.StateComponent;

public class GhostSystem extends IteratingSystem {

    private final ComponentMapper<GhostComponent> ghostM = ComponentMapper.getFor(GhostComponent.class);
    private final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    public GhostSystem() {
        super(Family.all(GhostComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GhostComponent ghost = ghostM.get(entity);
        StateComponent state = stateM.get(entity);

        ghost.ghostAgent.update(deltaTime);
        state.setState(ghost.currentState);
        
    }

}
