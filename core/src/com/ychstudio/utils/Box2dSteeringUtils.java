package com.ychstudio.utils;

import com.badlogic.gdx.math.Vector2;

public final class Box2dSteeringUtils {
    
    private Box2dSteeringUtils() {
        
    }
    
    public static float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }
    
    public static Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);
        
        return outVector;
    }
}
