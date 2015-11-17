package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.TransformComponent;

public class MovementSystem extends IteratingSystem {

    private ComponentMapper<MovementComponent> movementM = ComponentMapper.getFor(MovementComponent.class);
    private ComponentMapper<TransformComponent> transformM = ComponentMapper.getFor(TransformComponent.class);

    private Vector2 tmpV = new Vector2();

    public MovementSystem() {
        super(Family.all(MovementComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementComponent movement = movementM.get(entity);
        TransformComponent transform = transformM.get(entity);

        if (movement.velocity.x > 0.01f) {
            movement.velocity.x -= movement.damping * deltaTime;
        } else if (movement.velocity.x < -0.01f) {
            movement.velocity.x += movement.damping * deltaTime;
        }
        else {
            movement.velocity.x = 0;
        }

        if (movement.velocity.y > 0.01f) {
            movement.velocity.y -= movement.damping * deltaTime;
        } else if (movement.velocity.y < -0.01f) {
            movement.velocity.y += movement.damping * deltaTime;
        }
        else {
            movement.velocity.y = 0;
        }
        
        tmpV.set(movement.velocity);
        transform.pos.add(tmpV.scl(deltaTime));

    }
}
