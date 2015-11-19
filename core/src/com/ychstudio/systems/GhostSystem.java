package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
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
        StateComponent state = stateM.get(entity);
        MovementComponent movement = movementM.get(entity);
        Body body = movement.body;

        if (ghost.getPosition().dst2(GameManager.instance.playerLocation.getPosition()) < 25f) {
            ghost.setBehavior(GhostComponent.ARRIVE_BEHAVIOR);
        } else {
            ghost.setBehavior(GhostComponent.WANDER_BEHAVIOR);
        }

        ghost.update(deltaTime);

        // for updating animation
        if (body.getLinearVelocity().x > 0.1f) {
            state.setState(GhostComponent.MOVE_RIGHT);

        } else if (body.getLinearVelocity().x < -0.1f) {
            state.setState(GhostComponent.MOVE_LEFT);

        } else if (body.getLinearVelocity().y > 0.1f) {
            state.setState(GhostComponent.MOVE_UP);

        } else if (body.getLinearVelocity().y < -0.1f) {
            state.setState(GhostComponent.MOVE_DOWN);

        }
    }

}
