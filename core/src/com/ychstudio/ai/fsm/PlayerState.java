package com.ychstudio.ai.fsm;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.ychstudio.components.PlayerComponent;
import com.ychstudio.gamesys.GameManager;

public enum PlayerState implements State<PlayerAgent> {

    MOVE_UP() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.MOVE_UP;
            if (entity.playerComponent.getBody().getLinearVelocity().y < SPEED_THRESHOLD) {
                entity.stateMachine.changeState(IDLE_UP);
            }

            if (entity.playerComponent.hp <= 0) {
                entity.stateMachine.changeState(DIE);
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

            if (entity.playerComponent.hp <= 0) {
                entity.stateMachine.changeState(DIE);
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

            if (entity.playerComponent.hp <= 0) {
                entity.stateMachine.changeState(DIE);
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

            if (entity.playerComponent.hp <= 0) {
                entity.stateMachine.changeState(DIE);
            }
        }

    },
    IDLE_UP() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.IDLE_UP;
            changeStateUponVelocity(entity);

            if (entity.playerComponent.hp <= 0) {
                entity.stateMachine.changeState(DIE);
            }
        }

    },
    IDLE_DOWN() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.IDLE_DOWN;
            changeStateUponVelocity(entity);

            if (entity.playerComponent.hp <= 0) {
                entity.stateMachine.changeState(DIE);
            }
        }

    },
    IDLE_LEFT() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.IDLE_LEFT;
            changeStateUponVelocity(entity);

            if (entity.playerComponent.hp <= 0) {
                entity.stateMachine.changeState(DIE);
            }
        }

    },
    IDLE_RIGHT() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.IDLE_RIGHT;
            changeStateUponVelocity(entity);

            if (entity.playerComponent.hp <= 0) {
                entity.stateMachine.changeState(DIE);
            }
        }

    },
    DIE() {
        @Override
        public void update(PlayerAgent entity) {
            entity.playerComponent.currentState = PlayerComponent.DIE;
            GameManager.instance.playerIsAlive = false;

            // re-spawn player
            if (entity.timer > 1.5f) {
                GameManager.instance.decreasePlayerLives();
                if (GameManager.instance.playerLives > 0) {
                    entity.playerComponent.getBody().setTransform(GameManager.instance.playerSpawnPos, 0);
                    entity.playerComponent.hp = 1;
                    entity.stateMachine.changeState(IDLE_RIGHT);
                    GameManager.instance.playerIsAlive = true;
                    GameManager.instance.playerIsInvincible = true;
                } else {
                    GameManager.instance.makeGameOver();
                }
            }
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
        entity.timer = 0;
    }

    @Override
    public void exit(PlayerAgent entity) {
    }

    @Override
    public boolean onMessage(PlayerAgent entity, Telegram telegram) {
        return false;
    }

}
