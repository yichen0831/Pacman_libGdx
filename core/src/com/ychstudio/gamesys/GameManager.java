package com.ychstudio.gamesys;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.ychstudio.ai.astar.AStartPathFinding;

public class GameManager implements Disposable {

    public static final GameManager instance = new GameManager();

    public static final float PPM = 16f;

    public static final short NOTHING_BIT = 0;
    public static final short WALL_BIT = 1;
    public static final short PLAYER_BIT = 1 << 1;
    public static final short PILL_BIT = 1 << 2;
    public static final short GHOST_BIT = 1 << 3;
    public static final short GATE_BIT = 1 << 4;
    public static final short LIGHT_BIT = 1 << 5;

    public AssetManager assetManager;

    public Vector2 playerSpawnPos;
    public Vector2 ghostSpawnPos;

    public int totalPills = 0;

    public int highScore = 0;
    public int score = 0;

    public int displayScore = 0;
    public int displayHighScore = 0;

    public int playerLives = 4;
    public boolean playerIsInvincible = true;

    public boolean bigPillEaten = false;
    public boolean playerIsAlive = true;
    private boolean gameOver = false;

    public AStartPathFinding pathfinder;

    public Location<Vector2> playerLocation;

    private GameManager() {
        assetManager = new AssetManager();
        assetManager.load("images/actors.pack", TextureAtlas.class);
        assetManager.load("sounds/pill.ogg", Sound.class);
        assetManager.load("sounds/big_pill.ogg", Sound.class);
        assetManager.load("sounds/ghost_die.ogg", Sound.class);
        assetManager.load("sounds/pacman_die.ogg", Sound.class);
        assetManager.load("sounds/clear.ogg", Sound.class);

        assetManager.finishLoading();

        playerSpawnPos = new Vector2();
        ghostSpawnPos = new Vector2();
    }

    public void decreasePlayerLives() {
        playerLives--;
    }

    public void resetPlayerLives() {
        playerLives = 3;
    }

    public void makeGameOver() {
        gameOver = true;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void resetGame(boolean restart) {
        if (restart) {
            score = 0;
            displayScore = 0;
            resetPlayerLives();
        }
        totalPills = 0;
        playerIsAlive = true;
        bigPillEaten = false;
        gameOver = false;
    }

    public void addScore(int score) {
        this.score += score;
        if (this.score > highScore) {
            highScore = this.score;
        }
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
