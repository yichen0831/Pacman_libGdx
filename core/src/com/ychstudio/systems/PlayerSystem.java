package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.PlayerComponent;
import com.ychstudio.components.StateComponent;

public class PlayerSystem extends IteratingSystem {

    private ComponentMapper<PlayerComponent> playerM = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<MovementComponent> movementM = ComponentMapper.getFor(MovementComponent.class);
    private ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    private Vector2 tmpV = new Vector2();

    public PlayerSystem() {
        super(Family.all(PlayerComponent.class, MovementComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementComponent movement = movementM.get(entity);
        Body body = movement.body;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.applyLinearImpulse(tmpV.set(movement.speed, 0).scl(body.getMass()), body.getWorldCenter(), true);

        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.applyLinearImpulse(tmpV.set(-movement.speed, 0).scl(body.getMass()), body.getWorldCenter(), true);

        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.applyLinearImpulse(tmpV.set(0, movement.speed).scl(body.getMass()), body.getWorldCenter(), true);

        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            body.applyLinearImpulse(tmpV.set(0, -movement.speed).scl(body.getMass()), body.getWorldCenter(), true);

        }

    }

}
