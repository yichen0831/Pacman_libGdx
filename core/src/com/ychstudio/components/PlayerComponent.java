package com.ychstudio.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ychstudio.utils.Box2dLocation;
import com.ychstudio.utils.Box2dSteeringUtils;

public class PlayerComponent implements Component, Location<Vector2> {

    public static final int IDLE = 0;
    public static final int IDLE_UP = 0;
    public static final int IDLE_DOWN = 1;
    public static final int IDLE_LEFT = 2;
    public static final int IDLE_RIGHT = 3;

    public static final int MOVE_UP = 4;
    public static final int MOVE_DOWN = 5;
    public static final int MOVE_LEFT = 6;
    public static final int MOVE_RIGHT = 7;
    public static final int DIE = 8;
    
    private final Body body;

    public PlayerComponent(Body body) {
        this.body = body;
    }
    
    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) {
        body.setTransform(getPosition(), orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return Box2dSteeringUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return Box2dSteeringUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return new Box2dLocation();
    }

}
