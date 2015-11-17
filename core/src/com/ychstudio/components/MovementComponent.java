package com.ychstudio.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements Component {
    public final Vector2 velocity = new Vector2();
    public float speed = 2f;
    public float damping = 4f;
}
