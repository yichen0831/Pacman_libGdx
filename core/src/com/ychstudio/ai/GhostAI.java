package com.ychstudio.ai;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ychstudio.gamesys.GameManager;
import com.ychstudio.utils.Box2dLocation;
import com.ychstudio.utils.Box2dSteeringUtils;

public class GhostAI implements Steerable<Vector2> {

    // behavior
    public static final int WANDER_BEHAVIOR = 0;
    public static final int ARRIVE_BEHAVIOR = 1;

    private final Body body;

    private final float boundingRadius;
    private boolean tagged;
    private float maxLinearAcceleration;
    private float maxAngularAcceleration;
    private float maxLinearSpeed;
    private float maxAngularSpeed;

    private float zeroLinearSpeedThreshold;

    private SteeringBehavior<Vector2> steeringBehavior;
    private final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());

    private Wander<Vector2> wanderBehavior;
    private Arrive<Vector2> arriveBehavior;

    public GhostAI(Body body, float boundingRadius) {
        this.body = body;
        this.boundingRadius = boundingRadius;

        tagged = false;
        maxAngularAcceleration = 1.0f;
        maxLinearAcceleration = 1.0f;
        maxLinearSpeed = 2f;
        maxAngularSpeed = 1.0f;

        createWanderBehavior();
    }

    private void createWanderBehavior() {
        wanderBehavior = new Wander<>(this)
                .setEnabled(true)
                .setWanderRadius(2f)
                .setWanderRate(MathUtils.PI2 * 4);
    }

    public void setBehavior(int behavior) {
        switch (behavior) {
            case ARRIVE_BEHAVIOR:
                if (arriveBehavior == null) {
                    if (GameManager.instance.playerLocation != null) {
                        arriveBehavior = new Arrive<>(this, GameManager.instance.playerLocation)
                                .setEnabled(true)
                                .setTimeToTarget(0.1f)
                                .setArrivalTolerance(0.5f);
                        steeringBehavior = arriveBehavior;
                    }
                } else {
                    steeringBehavior = arriveBehavior;
                }
                break;
            case WANDER_BEHAVIOR:
                steeringBehavior = wanderBehavior;
            default:
                break;
        }
    }

    public void update(float deltaTime) {
        if (steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            applyingSteering(deltaTime);
        }
    }

    public void applyingSteering(float deltaTime) {
        boolean anyAcceleration = false;

        if (!steeringOutput.linear.isZero()) {
            body.applyForceToCenter(steeringOutput.linear, true);
            anyAcceleration = true;
        }

        if (anyAcceleration) {

            // cap the linear speed
            Vector2 velocity = body.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
                body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
            }
        }
    }

    public SteeringBehavior<Vector2> getSteeringBehavior() {
        return steeringBehavior;
    }

    public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) {
        this.steeringBehavior = steeringBehavior;
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
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

    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroLinearSpeedThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        this.zeroLinearSpeedThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

}
