package com.ychstudio.builders;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.ai.astar.AStarMap;
import com.ychstudio.ai.astar.AStartPathFinding;
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
    private final RayHandler rayHandler;
    private final Engine engine;

    private final AssetManager assetManager;
    private final TextureAtlas actorAtlas;

    private boolean wall;

    public WorldBuilder(TiledMap tiledMap, Engine engine, World world, RayHandler rayHandler) {
        this.tiledMap = tiledMap;
        this.engine = engine;
        this.world = world;
        this.rayHandler = rayHandler;

        assetManager = GameManager.instance.assetManager;
        actorAtlas = assetManager.get("images/actors.pack", TextureAtlas.class);
    }

    public void buildAll() {
        buildMap();
    }

    private void buildMap() {
        MapLayers mapLayers = tiledMap.getLayers();

        int mapWidth = ((TiledMapTileLayer) mapLayers.get(0)).getWidth();
        int mapHeight = ((TiledMapTileLayer) mapLayers.get(0)).getHeight();

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
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.GHOST_BIT | GameManager.LIGHT_BIT;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }

        // create map for A* path finding
        AStarMap aStarMap = new AStarMap(mapWidth, mapHeight);

        QueryCallback queryCallback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                wall = fixture.getFilterData().categoryBits == GameManager.WALL_BIT;
                return false; // stop finding other fixtures in the query area
            }
        };

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                wall = false;
                world.QueryAABB(queryCallback, x + 0.2f, y + 0.2f, x + 0.8f, y + 0.8f);
                if (wall) {
                   aStarMap.getNodeAt(x, y).isWall = true;
                }
            }
        }
        GameManager.instance.pathfinder = new AStartPathFinding(aStarMap);

        // Gate
        MapLayer gateLayer = mapLayers.get("Gate"); // gate layer
        for (MapObject mapObject : gateLayer.getObjects()) {
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
            fixtureDef.filter.categoryBits = GameManager.GATE_BIT;
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.GHOST_BIT;
            fixtureDef.isSensor = true;
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
                textureRegion = new TextureRegion(actorAtlas.findRegion("Pill"), 16, 0, 16, 16);
            } else {
                textureRegion = new TextureRegion(actorAtlas.findRegion("Pill"), 0, 0, 16, 16);
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
            entity.add(new TransformComponent(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2, 5));
            entity.add(new TextureComponent(textureRegion));
            entity.add(new MovementComponent(body));

            engine.addEntity(entity);
            body.setUserData(entity);

            GameManager.instance.totalPills++;
        }

        // ghosts
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

        // box2d light
        PointLight pointLight = new PointLight(rayHandler, 50, new Color(0.5f, 0.5f, 0.5f, 1.0f), 12f, 0, 0);
        pointLight.setContactFilter(GameManager.LIGHT_BIT, GameManager.NOTHING_BIT, GameManager.WALL_BIT);
        pointLight.setSoft(true);
        pointLight.setSoftnessLength(2.0f);
        pointLight.attachToBody(body);

        TextureRegion textureRegion = new TextureRegion(actorAtlas.findRegion("Pacman"), 0, 0, 16, 16);

        PlayerComponent player = new PlayerComponent(body);
        GameManager.instance.playerLocation = player.ai;

        Entity entity = new Entity();
        entity.add(player);
        entity.add(new TransformComponent(x, y, 1));
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

        for (int i = 0; i < 10; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), i * 16, 16, 16, 16));
        }
        keyFrames.add(new TextureRegion(actorAtlas.findRegion("Pacman"), 9 * 16, 0, 16, 16)); // invisible
        animation = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        animationComponent.animations.put(PlayerComponent.DIE, animation);

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
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        fixtureDef.filter.categoryBits = GameManager.GHOST_BIT;
        fixtureDef.filter.maskBits = GameManager.WALL_BIT | GameManager.GATE_BIT;
        fixtureDef.isSensor = false;
        body.createFixture(fixtureDef);

        // box2d light
        PointLight pointLight = new PointLight(rayHandler, 50, new Color(0.2f, 0.2f, 0.2f, 1.0f), 12f, 0, 0);
        pointLight.setContactFilter(GameManager.LIGHT_BIT, GameManager.NOTHING_BIT, GameManager.WALL_BIT);
        pointLight.setSoft(true);
        pointLight.setSoftnessLength(2.0f);
        pointLight.attachToBody(body);

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

        keyFrames.clear();

        // die
        for (int i = 4; i < 8; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 16 * 4, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(GhostComponent.DIE, animation);

        GhostComponent ghostComponent = new GhostComponent(body);

        Entity entity = new Entity();
        entity.add(ghostComponent);
        entity.add(new TransformComponent(x, y, 3));
        entity.add(new MovementComponent(body));
        entity.add(new StateComponent());
        entity.add(new TextureComponent(new TextureRegion(textureRegion, 0, 0, 16, 16)));
        entity.add(anim);

        engine.addEntity(entity);
        body.setUserData(entity);
    }
}
