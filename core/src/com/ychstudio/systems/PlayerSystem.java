package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.PlayerComponent;
import com.ychstudio.components.StateComponent;
import com.ychstudio.gamesys.GameManager;

public class PlayerSystem extends IteratingSystem {

    private final ComponentMapper<PlayerComponent> playerM = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<MovementComponent> movementM = ComponentMapper.getFor(MovementComponent.class);
    private final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    private final Vector2 tmpV1 = new Vector2();
    private final Vector2 tmpV2 = new Vector2();
    private boolean canMove;

    private enum MoveDir {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public PlayerSystem() {
        super(Family.all(PlayerComponent.class, MovementComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerM.get(entity);
        StateComponent state = stateM.get(entity);
        MovementComponent movement = movementM.get(entity);
        Body body = movement.body;

        if (player.hp > 0) {
            if ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) && checkMovable(body, MoveDir.RIGHT)) {
                body.applyLinearImpulse(tmpV1.set(movement.speed, 0).scl(body.getMass()), body.getWorldCenter(), true);

            } else if ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) && checkMovable(body, MoveDir.LEFT)) {
                body.applyLinearImpulse(tmpV1.set(-movement.speed, 0).scl(body.getMass()), body.getWorldCenter(), true);

            } else if ((Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) && checkMovable(body, MoveDir.UP)) {
                body.applyLinearImpulse(tmpV1.set(0, movement.speed).scl(body.getMass()), body.getWorldCenter(), true);

            } else if ((Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))&& checkMovable(body, MoveDir.DOWN)) {
                body.applyLinearImpulse(tmpV1.set(0, -movement.speed).scl(body.getMass()), body.getWorldCenter(), true);

            }

            if (body.getLinearVelocity().len2() > movement.speed * movement.speed) {
                body.setLinearVelocity(body.getLinearVelocity().scl(movement.speed / body.getLinearVelocity().len()));
            }
        }

        // player is invincible for 3 seconds when spawning
        if (GameManager.instance.playerIsInvincible) {
            player.invincibleTimer += deltaTime;
            if (player.invincibleTimer >= 3.0f) {
                GameManager.instance.playerIsInvincible = false;
                player.invincibleTimer = 0;
            }
        }

        player.playerAgent.update(deltaTime);
        state.setState(player.currentState);
    }

    private boolean checkMovable(Body body, MoveDir dir) {
        canMove = true;
        World world = body.getWorld();

        RayCastCallback rayCastCallback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getFilterData().categoryBits == GameManager.WALL_BIT || fixture.getFilterData().categoryBits == GameManager.GATE_BIT) {
                    canMove = false;
                    return 0;
                }
                return 0;
            }
        };

        for (int i = 0; i < 2; i++) {
            tmpV1.set(body.getPosition());
            switch (dir) {
                case UP:
                    tmpV2.set(body.getPosition().x - (i - 0.5f) * 0.2f, body.getPosition().y + 0.6f);
                    break;
                case DOWN:
                    tmpV2.set(body.getPosition().x - (i - 0.5f) * 0.2f, body.getPosition().y - 0.6f);
                    break;
                case LEFT:
                    tmpV2.set(body.getPosition().x - 0.6f, body.getPosition().y - (i - 0.5f) * 0.2f);
                    break;
                case RIGHT:
                    tmpV2.set(body.getPosition().x + 0.6f, body.getPosition().y - (i - 0.5f) * 0.2f);
                    break;
                default:
                    break;
            }

            world.rayCast(rayCastCallback, tmpV1, tmpV2);
        }

        return canMove;
    }
}
