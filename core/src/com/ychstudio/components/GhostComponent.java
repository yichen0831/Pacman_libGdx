package com.ychstudio.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.ychstudio.ai.GhostAI;

public class GhostComponent implements Component {

    // state
    public static final int MOVE_UP = 0;
    public static final int MOVE_DOWN = 1;
    public static final int MOVE_LEFT = 2;
    public static final int MOVE_RIGHT = 3;
    public static final int ESCAPE = 4;
    public static final int DIE = 5;

    public GhostAI ai;

    public GhostComponent(Body body, float boundingRadius) {

        ai = new GhostAI(body, boundingRadius);
    }
}
