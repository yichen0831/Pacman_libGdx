package com.ychstudio.ai.fsm;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.ychstudio.components.GhostComponent;

public enum GhostState implements State<GhostAgent> {

    MOVE_UP() {
        @Override
        public void update(GhostAgent entity) {
            entity.ghostComponent.currentState = GhostComponent.MOVE_UP;
            changeStateUponVelocity(entity);
        }

    },
    MOVE_DOWN() {
        @Override
        public void update(GhostAgent entity) {
            entity.ghostComponent.currentState = GhostComponent.MOVE_DOWN;
            changeStateUponVelocity(entity);
        }
    },
    MOVE_LEFT() {
        @Override
        public void update(GhostAgent entity) {
            entity.ghostComponent.currentState = GhostComponent.MOVE_LEFT;
            changeStateUponVelocity(entity);
        }
    },
    MOVE_RIGHT() {
        @Override
        public void update(GhostAgent entity) {
            entity.ghostComponent.currentState = GhostComponent.MOVE_RIGHT;
            changeStateUponVelocity(entity);
        }
    },
    ESCAPE() {
        @Override
        public void update(GhostAgent entity) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    },
    DIE() {
        @Override
        public void update(GhostAgent entity) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    };

    private static void changeStateUponVelocity(GhostAgent entity) {
        Vector2 velocity = entity.ghostComponent.getBody().getLinearVelocity();

        if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
            if (velocity.x > 0.1f) {
                entity.ghostComponent.currentState = GhostComponent.MOVE_RIGHT;

            } else if (velocity.x < -0.1f) {
                entity.ghostComponent.currentState = GhostComponent.MOVE_LEFT;

            }
        } else if (velocity.y > 0.1f) {
            entity.ghostComponent.currentState = GhostComponent.MOVE_UP;

        } else if (velocity.y < -0.1f) {
            entity.ghostComponent.currentState = GhostComponent.MOVE_DOWN;

        }
    }

    @Override
    public void enter(GhostAgent entity) {
    }

    @Override
    public void exit(GhostAgent entity) {
    }

    @Override
    public boolean onMessage(GhostAgent entity, Telegram telegram) {
        return false;
    }

}
