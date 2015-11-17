package com.ychstudio.b2dworldutils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ychstudio.components.PillComponent;
import com.ychstudio.gamesys.GameManager;

public class WorldContactListener implements ContactListener {

    private Engine engine;

    private ComponentMapper<PillComponent> pillM = ComponentMapper.getFor(PillComponent.class);

    public WorldContactListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getFilterData().categoryBits == GameManager.PILL_BIT || fixtureB.getFilterData().categoryBits == GameManager.PILL_BIT) {
            if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                Body body = fixtureB.getBody();
                Entity entity = (Entity) body.getUserData();
                PillComponent pill = pillM.get(entity);
                pill.eaten = true;
            } else if (fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                Body body = fixtureA.getBody();
                Entity entity = (Entity) body.getUserData();
                PillComponent pill = pillM.get(entity);
                pill.eaten = true;
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
