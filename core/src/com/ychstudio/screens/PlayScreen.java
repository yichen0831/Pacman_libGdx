package com.ychstudio.screens;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
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

    private AnimationSystem animationSystem;
    private GhostSystem ghostSystem;
    private MovementSystem movementSystem;
    private PillSystem pillSystem;
    private PlayerSystem playerSystem;
    private RenderSystem renderSystem;
    private StateSystem stateSystem;

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

    private Sprite pacmanSprite;

    private RayHandler rayHandler;

    private float ambientLight = 0.5f;

    private boolean changeScreen;
    private float changeScreenCountDown = 2.0f;

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

        playerSystem = new PlayerSystem();
        ghostSystem = new GhostSystem();
        movementSystem = new MovementSystem();
        pillSystem = new PillSystem();
        animationSystem = new AnimationSystem();
        renderSystem = new RenderSystem(batch);
        stateSystem = new StateSystem();

        engine = new Engine();
        engine.addSystem(playerSystem);
        engine.addSystem(ghostSystem);
        engine.addSystem(pillSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(stateSystem);
        engine.addSystem(animationSystem);
        engine.addSystem(renderSystem);

        // box2d
        world = new World(Vector2.Zero, true);
        world.setContactListener(new WorldContactListener());
        box2DDebugRenderer = new Box2DDebugRenderer();
        showBox2DDebuggerRenderer = false;

        // box2d light
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(ambientLight);

        // load map
        tiledMap = new TmxMapLoader().load("map/map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 16f, batch);

        new WorldBuilder(tiledMap, engine, world, rayHandler).buildAll();

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
        gameOverLabel.setPosition(WIDTH * 4.3f, HEIGHT * 9f);
        gameOverLabel.setVisible(false);
        stage.addActor(gameOverLabel);

        TextureAtlas textureAtlas = GameManager.instance.assetManager.get("images/actors.pack", TextureAtlas.class);
        pacmanSprite = new Sprite(new TextureRegion(textureAtlas.findRegion("Pacman"), 16, 0, 16, 16));
        pacmanSprite.setBounds(8f, 21.5f, 16 / GameManager.PPM, 16 / GameManager.PPM);

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

        batch.setProjectionMatrix(camera.combined);
        if (changeScreen) {
            playerSystem.setProcessing(false);
            ghostSystem.setProcessing(false);
            movementSystem.setProcessing(false);
        } else {
            world.step(1 / 60f, 8, 3);
        }
        engine.update(delta);

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        batch.begin();
        for (int i = 0; i < GameManager.instance.playerLives - 1; i++) {
            pacmanSprite.setPosition(8 + i, 21.5f);
            pacmanSprite.draw(batch);
        }
        batch.end();

        if (showBox2DDebuggerRenderer) {
            box2DDebugRenderer.render(world, camera.combined);
        }

        // update score
        stringBuilder.setLength(0);
        if (GameManager.instance.displayScore < GameManager.instance.score) {
            GameManager.instance.displayScore = Math.min(GameManager.instance.score, GameManager.instance.displayScore + (int) (600 * delta));
        }
        scoreLabel.setText(stringBuilder.append(GameManager.instance.displayScore));
        stringBuilder.setLength(0);
        if (GameManager.instance.displayHighScore < GameManager.instance.highScore) {
            GameManager.instance.displayHighScore = Math.min(GameManager.instance.highScore, GameManager.instance.displayHighScore + (int) (600 * delta));
        }
        highScoreLabel.setText(stringBuilder.append(GameManager.instance.displayHighScore));
        if (GameManager.instance.isGameOver() && !changeScreen) {
            gameOverLabel.setVisible(true);
        } else {
            gameOverLabel.setVisible(false);
        }
        stage.draw();

        if (GameManager.instance.totalPills <= 0 && !changeScreen) {
            GameManager.instance.assetManager.get("sounds/clear.ogg", Sound.class).play();
            changeScreen = true;
        }

        if (changeScreen) {
            changeScreenCountDown -= delta;
            if (changeScreenCountDown <= 1) {
                // fade out effect
                ambientLight -= delta;
                rayHandler.setAmbientLight(MathUtils.clamp(ambientLight, 0f, 1f));
                rayHandler.removeAll();
            }

            if (changeScreenCountDown <= 0) {
                GameManager.instance.resetGame(GameManager.instance.playerLives <= 0);
                game.setScreen(new PlayScreen(game));
            }
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
        rayHandler.dispose();
        tiledMap.dispose();
        tiledMapRenderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
    }

}
