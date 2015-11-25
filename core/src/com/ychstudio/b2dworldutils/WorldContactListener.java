package com.ychstudio.b2dworldutils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ychstudio.components.GhostComponent;
import com.ychstudio.components.PillComponent;
import com.ychstudio.components.PlayerComponent;
import com.ychstudio.gamesys.GameManager;

public class WorldContactListener implements ContactListener {

    private final ComponentMapper<PillComponent> pillM = ComponentMapper.getFor(PillComponent.class);
    private final ComponentMapper<GhostComponent> ghostM = ComponentMapper.getFor(GhostComponent.class);
    private final ComponentMapper<PlayerComponent> playerM = ComponentMapper.getFor(PlayerComponent.class);

    public WorldContactListener() {
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getFilterData().categoryBits == GameManager.PILL_BIT || fixtureB.getFilterData().categoryBits == GameManager.PILL_BIT) {
            // pill
            if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                Body body = fixtureB.getBody();
                Entity entity = (Entity) body.getUserData();
                PillComponent pill = pillM.get(entity);
                pill.eaten = true;
                GameManager.instance.bigPillEaten = pill.big;
            } else if (fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                Body body = fixtureA.getBody();
                Entity entity = (Entity) body.getUserData();
                PillComponent pill = pillM.get(entity);
                pill.eaten = true;
                GameManager.instance.bigPillEaten = pill.big;
            }
        } else if (fixtureA.getFilterData().categoryBits == GameManager.GHOST_BIT || fixtureB.getFilterData().categoryBits == GameManager.GHOST_BIT) {
            // ghost
            if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                PlayerComponent player = playerM.get((Entity) fixtureA.getBody().getUserData());
                GhostComponent ghost = ghostM.get((Entity) fixtureB.getBody().getUserData());

                if (ghost.currentState == GhostComponent.DIE) {
                    return;
                }

                if (ghost.weaken) {
                    // kill ghost
                    ghost.hp--;
                    GameManager.instance.addScore(800);
                    GameManager.instance.assetManager.get("sounds/ghost_die.ogg", Sound.class).play();
                } else // kill player if player is not invincible
                {
                    if (!GameManager.instance.playerIsInvincible) {
                        player.hp--;

                        if (GameManager.instance.playerIsAlive) {
                            GameManager.instance.assetManager.get("sounds/pacman_die.ogg", Sound.class).play();
                        }
                    }
                }

            } else if (fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                PlayerComponent player = playerM.get((Entity) fixtureB.getBody().getUserData());
                GhostComponent ghost = ghostM.get((Entity) fixtureA.getBody().getUserData());

                if (ghost.currentState == GhostComponent.DIE) {
                    return;
                }

                if (ghost.weaken) {
                    // kill ghost
                    ghost.hp--;
                    GameManager.instance.addScore(800);
                    GameManager.instance.assetManager.get("sounds/ghost_die.ogg", Sound.class).play();
                } else // kill player if player is not invincible
                 if (!GameManager.instance.playerIsInvincible) {
                        player.hp--;
                        if (GameManager.instance.playerIsAlive) {
                            GameManager.instance.assetManager.get("sounds/pacman_die.ogg", Sound.class).play();
                        }

                    }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
