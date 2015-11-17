package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.TransformComponent;

public class MovementSystem extends IteratingSystem {

    private ComponentMapper<MovementComponent> movementM = ComponentMapper.getFor(MovementComponent.class);
    private ComponentMapper<TransformComponent> transformM = ComponentMapper.getFor(TransformComponent.class);

    public MovementSystem() {
        super(Family.all(MovementComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementComponent movement = movementM.get(entity);
        TransformComponent transform = transformM.get(entity);

        if (movement.body.getPosition().x <= 0) {
            movement.body.setTransform(19.0f, movement.body.getPosition().y, 0);
        }
        
        else if (movement.body.getPosition().x >= 19f) {
            movement.body.setTransform(0, movement.body.getPosition().y, 0);
        }

        transform.pos.set(movement.body.getPosition());
    }
}
