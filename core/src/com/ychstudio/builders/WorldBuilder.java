package com.ychstudio.builders;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.gamesys.GameManager;

public class WorldBuilder {

    private TiledMap tiledMap;
    private World world;
    private Engine engine;
    
    public WorldBuilder(TiledMap tiledMap,Engine engine, World world) {
        this.tiledMap = tiledMap;
        this.engine = engine;
        this.world = world;
    }
    
    public void buildAll() {
        buildMap();
    }

    private void buildMap() {
        MapLayers mapLayers = tiledMap.getLayers();
        
        // build walls
        MapLayer wallLayer = mapLayers.get(2); // wall layer
        for (MapObject mapObject : wallLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            
            rectangle.x = rectangle.x / GameManager.PPM;
            rectangle.y = rectangle.y / GameManager.PPM;
            rectangle.width = rectangle.width / GameManager.PPM;
            rectangle.height = rectangle.height / GameManager.PPM;
            
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
            
            Body body = world.createBody(bodyDef);
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(rectangle.width / 2, rectangle.height / 2);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }
        
    }
}
