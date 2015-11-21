package com.ychstudio.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.PacMan;
import com.ychstudio.b2dworldutils.WorldContactListener;
import com.ychstudio.builders.WorldBuilder;
import com.ychstudio.gamesys.GameManager;
import com.ychstudio.systems.AnimationSystem;
import com.ychstudio.systems.GhostSystem;
import com.ychstudio.systems.MovementSystem;
import com.ychstudio.systems.PillSystem;
import com.ychstudio.systems.PlayerSystem;
import com.ychstudio.systems.RenderSystem;
import com.ychstudio.systems.StateSystem;

public class PlayScreen implements Screen {

    private final float WIDTH = 19.0f;
    private final float HEIGHT = 23.0f;

    private final PacMan game;
    private SpriteBatch batch;

    private FitViewport viewport;
    private OrthographicCamera camera;

    private Engine engine;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private BitmapFont font;
    private FitViewport stageViewport;
    private Stage stage;

    private Label scoreLabel;
    private Label highScoreLabel;

    private Label gameOverLabel;

    private StringBuilder stringBuilder;

    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private boolean showBox2DDebuggerRenderer;

    private boolean changeScreen;

    public PlayScreen(PacMan game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        camera.translate(WIDTH / 2, HEIGHT / 2);
        camera.update();

        batch = new SpriteBatch();
        engine = new Engine();
        engine.addSystem(new PlayerSystem());
        engine.addSystem(new GhostSystem());
        engine.addSystem(new PillSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new StateSystem());
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new RenderSystem(batch));

        world = new World(Vector2.Zero, true);
        world.setContactListener(new WorldContactListener());
        box2DDebugRenderer = new Box2DDebugRenderer();
        showBox2DDebuggerRenderer = false;

        // load map
        tiledMap = new TmxMapLoader().load("map/map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 16f, batch);

        new WorldBuilder(tiledMap, engine, world).buildAll();

        stageViewport = new FitViewport(WIDTH * 20, HEIGHT * 20);
        stage = new Stage(stageViewport, batch);

        font = new BitmapFont(Gdx.files.internal("fonts/army_stencil.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Label scoreTextLabel = new Label("SCORE", labelStyle);
        scoreTextLabel.setPosition(WIDTH * 1, HEIGHT * 19);
        stage.addActor(scoreTextLabel);

        Label hightScoreTextLabel = new Label("High Score", labelStyle);
        hightScoreTextLabel.setPosition(WIDTH * 14, HEIGHT * 19);
        stage.addActor(hightScoreTextLabel);

        scoreLabel = new Label("0", labelStyle);
        scoreLabel.setPosition(WIDTH * 1.5f, HEIGHT * 18.2f);
        stage.addActor(scoreLabel);

        highScoreLabel = new Label("0", labelStyle);
        highScoreLabel.setPosition(WIDTH * 16.5f, HEIGHT * 18.2f);
        stage.addActor(highScoreLabel);

        gameOverLabel = new Label("              - Game Over -\n Press Enter to continue", labelStyle);
        gameOverLabel.setPosition(WIDTH * 4.3f, HEIGHT * 8f);
        gameOverLabel.setVisible(false);
        stage.addActor(gameOverLabel);

        stringBuilder = new StringBuilder();

        changeScreen = false;
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBox2DDebuggerRenderer = !showBox2DDebuggerRenderer;
        }

        if (GameManager.instance.isGameOver() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            changeScreen = true;
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

        // update score
        stringBuilder.setLength(0);
        scoreLabel.setText(stringBuilder.append(GameManager.instance.score).toString());
        stringBuilder.setLength(0);
        highScoreLabel.setText(stringBuilder.append(GameManager.instance.highScore).toString());
        if (GameManager.instance.isGameOver()) {
            gameOverLabel.setVisible(true);
        }
        stage.draw();

        if (changeScreen) {
            GameManager.instance.resetGame();
            game.setScreen(new PlayScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stageViewport.update(width, height);
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
        font.dispose();
        stage.dispose();
        tiledMap.dispose();
        tiledMapRenderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
    }

}
