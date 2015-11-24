package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ychstudio.components.GhostComponent;
import com.ychstudio.components.StateComponent;
import com.ychstudio.gamesys.GameManager;

public class GhostSystem extends IteratingSystem {

    private final ComponentMapper<GhostComponent> ghostM = ComponentMapper.getFor(GhostComponent.class);
    private final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    public GhostSystem() {
        super(Family.all(GhostComponent.class, StateComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        GameManager.instance.bigPillEaten = false;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GhostComponent ghost = ghostM.get(entity);
        StateComponent state = stateM.get(entity);

        ghost.ghostAgent.update(deltaTime);
        state.setState(ghost.currentState);

        if (GameManager.instance.bigPillEaten) {
            ghost.weak_time = 0;
        }

        if (ghost.weaken) {
            ghost.weak_time += deltaTime;
            if (ghost.weak_time >= GhostComponent.WEAK_TIME) {
                ghost.weaken = false;
                ghost.weak_time = 0;
            }
        }

        if (GameManager.instance.bigPillEaten) {
            ghost.weaken = true;
            state.resetStateTime();
        }

    }

}
