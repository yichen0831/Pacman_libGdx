package com.ychstudio.builders;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.PlayerComponent;
import com.ychstudio.components.StateComponent;
import com.ychstudio.components.TextureComponent;
import com.ychstudio.components.TransformComponent;
import com.ychstudio.gamesys.GameManager;

public class WorldBuilder {

    private TiledMap tiledMap;
    private World world;
    private Engine engine;
    
    private AssetManager assetManager;

    public WorldBuilder(TiledMap tiledMap, Engine engine, World world) {
        this.tiledMap = tiledMap;
        this.engine = engine;
        this.world = world;
        
        assetManager = GameManager.instance.assetManager;
    }

    public void buildAll() {
        buildMap();
    }

    private void buildMap() {
        MapLayers mapLayers = tiledMap.getLayers();

        // walls
        MapLayer wallLayer = mapLayers.get("Wall"); // wall layer
        for (MapObject mapObject : wallLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

            correctRectangle(rectangle);

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);

            Body body = world.createBody(bodyDef);
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(rectangle.width / 2, rectangle.height / 2);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.WALL_BIT;
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.GHOST_BIT;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }

        // player
        MapLayer playerLayer = mapLayers.get("Player"); // player layer
        for (MapObject mapObject : playerLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            
            correctRectangle(rectangle);
            
            createPlayer(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
        }
    }

    // make rectangle correct position and dimensions
    private void correctRectangle(Rectangle rectangle) {
        rectangle.x = rectangle.x / GameManager.PPM;
        rectangle.y = rectangle.y / GameManager.PPM;
        rectangle.width = rectangle.width / GameManager.PPM;
        rectangle.height = rectangle.height / GameManager.PPM;
    }

    private void createPlayer(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 16f;
        
        Body body = world.createBody(bodyDef);
        
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.45f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.PLAYER_BIT;
        fixtureDef.filter.maskBits = GameManager.WALL_BIT | GameManager.GATE_BIT | GameManager.GHOST_BIT | GameManager.PILL_BIT;
        body.createFixture(fixtureDef);
        circleShape.dispose();
        
        TextureAtlas textureAtlas = assetManager.get("images/actors.pack", TextureAtlas.class);
        
        TextureRegion textureRegion = new TextureRegion(textureAtlas.findRegion("Pacman"), 0, 0, 16, 16);
        
        Entity entity = new Entity();
        entity.add(new PlayerComponent());
        entity.add(new TransformComponent(x, y));
        entity.add(new MovementComponent(body));
        entity.add(new StateComponent());
        entity.add(new TextureComponent(textureRegion));
        
        engine.addEntity(entity);
        body.setUserData(entity);
    }
}
