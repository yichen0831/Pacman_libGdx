package com.ychstudio.ai.fsm;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.ychstudio.components.PlayerComponent;

public enum PlayerState implements State<PlayerAgent> {

    MOVE_UP() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.MOVE_UP;
            if (entity.playerComponent.getBody().getLinearVelocity().y < SPEED_THRESHOLD) {
                entity.stateMachine.changeState(IDLE_UP);
            }
        }
    },
    MOVE_DOWN() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.MOVE_DOWN;
            if (entity.playerComponent.getBody().getLinearVelocity().y > -SPEED_THRESHOLD) {
                entity.stateMachine.changeState(IDLE_DOWN);
            }
        }

    },
    MOVE_LEFT() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.MOVE_LEFT;
            if (entity.playerComponent.getBody().getLinearVelocity().x > -SPEED_THRESHOLD) {
                entity.stateMachine.changeState(IDLE_LEFT);
            }
        }

    },
    MOVE_RIGHT() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.MOVE_RIGHT;
            if (entity.playerComponent.getBody().getLinearVelocity().x < SPEED_THRESHOLD) {
                entity.stateMachine.changeState(IDLE_RIGHT);
            }
        }

    },
    IDLE_UP() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.IDLE_UP;
            changeStateUponVelocity(entity);
        }

    },
    IDLE_DOWN() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.IDLE_DOWN;
            changeStateUponVelocity(entity);
        }

    },
    IDLE_LEFT() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.IDLE_LEFT;
            changeStateUponVelocity(entity);
        }

    },
    IDLE_RIGHT() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.IDLE_RIGHT;
            changeStateUponVelocity(entity);
        }

    };

    private static final float SPEED_THRESHOLD = 0.5f;

    private static void changeStateUponVelocity(PlayerAgent entity) {
        Vector2 velocity = entity.playerComponent.getBody().getLinearVelocity();

        if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
            if (velocity.x >= SPEED_THRESHOLD) {
                entity.stateMachine.changeState(MOVE_RIGHT);
            } else if (velocity.x <= -SPEED_THRESHOLD) {
                entity.stateMachine.changeState(MOVE_LEFT);
            }
        } else if (velocity.y >= SPEED_THRESHOLD) {
            entity.stateMachine.changeState(MOVE_UP);
        } else if (velocity.y <= -SPEED_THRESHOLD) {
            entity.stateMachine.changeState(MOVE_DOWN);
        }
    }

    @Override
    public void enter(PlayerAgent entity) {
    }

    @Override
    public void exit(PlayerAgent entity) {
    }

    @Override
    public boolean onMessage(PlayerAgent entity, Telegram telegram) {
        return false;
    }

}
