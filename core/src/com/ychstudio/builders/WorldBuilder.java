package com.ychstudio.builders;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import com.badlogic.gdx.utils.Array;
import com.ychstudio.ai.GhostAI;
import com.ychstudio.components.AnimationComponent;
import com.ychstudio.components.GhostComponent;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.PillComponent;
import com.ychstudio.components.PlayerComponent;
import com.ychstudio.components.StateComponent;
import com.ychstudio.components.TextureComponent;
import com.ychstudio.components.TransformComponent;
import com.ychstudio.gamesys.GameManager;

public class WorldBuilder {

    private final TiledMap tiledMap;
    private final World world;
    private final Engine engine;

    private final AssetManager assetManager;
    private final TextureAtlas actorAtlas;

    public WorldBuilder(TiledMap tiledMap, Engine engine, World world) {
        this.tiledMap = tiledMap;
        this.engine = engine;
        this.world = world;

        assetManager = GameManager.instance.assetManager;
        actorAtlas = assetManager.get("images/actors.pack", TextureAtlas.class);
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

        // pills
        MapLayer pillLayer = mapLayers.get("Pill"); // pill layer
        for (MapObject mapObject : pillLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            correctRectangle(rectangle);

            boolean isBig = false;
            float radius = 0.1f;
            TextureRegion textureRegion;

            if (mapObject.getProperties().containsKey("big")) {
                isBig = true;
                radius = 0.2f;
                textureRegion = new TextureRegion(actorAtlas.findRegion("Pill"), 8, 0, 8, 8);
            } else {
                textureRegion = new TextureRegion(actorAtlas.findRegion("Pill"), 0, 0, 8, 8);
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
            Body body = world.createBody(bodyDef);

            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(radius);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circleShape;
            fixtureDef.filter.categoryBits = GameManager.PILL_BIT;
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT;
            fixtureDef.isSensor = true;
            body.createFixture(fixtureDef);

            circleShape.dispose();

            Entity entity = new Entity();
            entity.add(new PillComponent(isBig));
            entity.add(new TransformComponent(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2));
            entity.add(new TextureComponent(textureRegion));
            entity.add(new MovementComponent(body));

            engine.addEntity(entity);
            body.setUserData(entity);

            GameManager.instance.totalPills++;
        }

        // ghost
        MapLayer ghostLayer = mapLayers.get("Ghost");
        for (MapObject mapObject : ghostLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

            correctRectangle(rectangle);

            GameManager.instance.ghostSpawnPos.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);

            // create four ghosts
            for (int i = 0; i < 4; i++) {
                createGhost(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2, i);
            }
        }

        // player
        MapLayer playerLayer = mapLayers.get("Player"); // player layer
        for (MapObject mapObject : playerLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

            correctRectangle(rectangle);

            GameManager.instance.playerSpawnPos.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);

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

        TextureRegion textureRegion = new TextureRegion(actorAtlas.findRegion("Pacman"), 0, 0, 16, 16);

        PlayerComponent player = new PlayerComponent(body);
        GameManager.instance.playerLocation = player.ai;
        
        Entity entity = new Entity();
        entity.add(player);
        entity.add(new TransformComponent(x, y));
        entity.add(new MovementComponent(body));
        entity.add(new StateComponent(PlayerComponent.IDLE_RIGHT));
        entity.add(new TextureComponent(textureRegion));

        AnimationComponent animationComponent = new AnimationComponent();
        Animation animation;
        Array<TextureRegion> keyFrames = new Array<>();

        // idle
        keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), 16 * 1, 0, 16, 16));
        animation = new Animation(0.2f, keyFrames);
        animationComponent.animations.put(PlayerComponent.IDLE_RIGHT, animation);

        keyFrames.clear();

        keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), 16 * 3, 0, 16, 16));
        animation = new Animation(0.2f, keyFrames);
        animationComponent.animations.put(PlayerComponent.IDLE_LEFT, animation);

        keyFrames.clear();

        keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), 16 * 5, 0, 16, 16));
        animation = new Animation(0.2f, keyFrames);
        animationComponent.animations.put(PlayerComponent.IDLE_UP, animation);

        keyFrames.clear();

        keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), 16 * 7, 0, 16, 16));
        animation = new Animation(0.2f, keyFrames);
        animationComponent.animations.put(PlayerComponent.IDLE_DOWN, animation);

        keyFrames.clear();

        // move
        for (int i = 1; i < 3; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), i * 16, 0, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        animationComponent.animations.put(PlayerComponent.MOVE_RIGHT, animation);

        keyFrames.clear();

        for (int i = 3; i < 5; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), i * 16, 0, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        animationComponent.animations.put(PlayerComponent.MOVE_LEFT, animation);

        keyFrames.clear();

        for (int i = 5; i < 7; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), i * 16, 0, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        animationComponent.animations.put(PlayerComponent.MOVE_UP, animation);

        keyFrames.clear();

        for (int i = 7; i < 9; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), i * 16, 0, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        animationComponent.animations.put(PlayerComponent.MOVE_DOWN, animation);

        keyFrames.clear();

        entity.add(animationComponent);

        engine.addEntity(entity);
        body.setUserData(entity);
    }

    private void createGhost(float x, float y, int index) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.4f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.GHOST_BIT;
        fixtureDef.filter.maskBits = GameManager.WALL_BIT | GameManager.GATE_BIT | GameManager.PLAYER_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        circleShape.dispose();

        TextureRegion textureRegion = actorAtlas.findRegion("Ghost");

        AnimationComponent anim = new AnimationComponent();
        Animation animation;
        Array<TextureRegion> keyFrames = new Array<>();
        // move right
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, index * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(GhostComponent.MOVE_RIGHT, animation);

        keyFrames.clear();

        // move left
        for (int i = 2; i < 4; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, index * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(GhostComponent.MOVE_LEFT, animation);

        keyFrames.clear();

        // move up
        for (int i = 4; i < 6; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, index * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(GhostComponent.MOVE_UP, animation);

        keyFrames.clear();

        // move down
        for (int i = 6; i < 8; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, index * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(GhostComponent.MOVE_DOWN, animation);

        keyFrames.clear();

        // escape
        for (int i = 0; i < 4; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 16 * 4, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(GhostComponent.ESCAPE, animation);

        // die
        for (int i = 4; i < 8; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 16 * 4, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(GhostComponent.DIE, animation);
        
        GhostComponent ghostComponent = new GhostComponent(body, 0.5f);
        ghostComponent.ai.setBehavior(GhostAI.WANDER_BEHAVIOR);
        
        Entity entity = new Entity();
        entity.add(ghostComponent);
        entity.add(new TransformComponent(x, y));
        entity.add(new MovementComponent(body));
        entity.add(new StateComponent());
        entity.add(new TextureComponent(new TextureRegion(textureRegion, 0, 0, 16, 16)));
        entity.add(anim);

        engine.addEntity(entity);
        body.setUserData(entity);
    }
}
