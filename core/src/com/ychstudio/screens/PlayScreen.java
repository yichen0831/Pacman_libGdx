package com.ychstudio.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.PacMan;
import com.ychstudio.b2dworldutils.WorldContactListener;
import com.ychstudio.builders.WorldBuilder;
import com.ychstudio.gamesys.GameManager;
import com.ychstudio.systems.AnimationSystem;
import com.ychstudio.systems.MovementSystem;
import com.ychstudio.systems.PillSystem;
import com.ychstudio.systems.PlayerSystem;
import com.ychstudio.systems.RenderSystem;
import com.ychstudio.systems.StateSystem;

public class PlayScreen implements Screen {

    private final float WIDTH = 19.0f;
    private final float HEIGHT = 23.0f;

    private PacMan game;
    private SpriteBatch batch;
    private AssetManager assetManager;

    private FitViewport viewport;
    private OrthographicCamera camera;

    private Engine engine;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private boolean showBox2DDebuggerRenderer;

    public PlayScreen(PacMan game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {
        assetManager = GameManager.instance.assetManager;
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        camera.translate(WIDTH / 2, HEIGHT / 2);
        camera.update();

        batch = new SpriteBatch();
        engine = new Engine();
        engine.addSystem(new PlayerSystem());
        engine.addSystem(new PillSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new StateSystem());
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new RenderSystem(batch));

        world = new World(Vector2.Zero, true);
        world.setContactListener(new WorldContactListener(engine));
        box2DDebugRenderer = new Box2DDebugRenderer();
        showBox2DDebuggerRenderer = true;

        // load map
        tiledMap = new TmxMapLoader().load("map/map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 16f, batch);

        new WorldBuilder(tiledMap, engine, world).buildAll();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBox2DDebuggerRenderer = !showBox2DDebuggerRenderer;
        }
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        world.step(1 / 60f, 8, 3);

        batch.setProjectionMatrix(camera.combined);
        engine.update(delta);

        if (showBox2DDebuggerRenderer) {
            box2DDebugRenderer.render(world, camera.combined);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        tiledMapRenderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
    }

}
