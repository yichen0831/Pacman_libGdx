package com.ychstudio.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class MovementComponent implements Component {
    public float speed = 1f;
    public Body body;

    public MovementComponent(Body body) {
        this.body = body;
    }
}
