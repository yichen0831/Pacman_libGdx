package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ychstudio.components.StateComponent;

public class StateSystem extends IteratingSystem {

    private final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);
    
    public StateSystem() {
        super(Family.all(StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = stateM.get(entity);
        state.increaseStateTime(deltaTime);
    }
    
}
