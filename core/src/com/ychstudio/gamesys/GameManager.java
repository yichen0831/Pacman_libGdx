package com.ychstudio.gamesys;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class GameManager implements Disposable {
    
    public static final GameManager instance = new GameManager();
    
    public static final float PPM = 16f;
    
    public static final short NOTHING_BIT = 0;
    public static final short WALL_BIT = 1;
    public static final short PLAYER_BIT = 1 << 1;
    public static final short PILL_BIT = 1 << 2;
    public static final short GHOST_BIT = 1 << 3;
    public static final short GATE_BIT = 1 << 4;
    
    public AssetManager assetManager;
    
    public Vector2 playerSpawnPos;
    public Vector2 ghostSpawnPos;
    
    public int totalPills = 0;
    
    private GameManager() {
        assetManager = new AssetManager();
        assetManager.load("images/actors.pack", TextureAtlas.class);
        
        assetManager.finishLoading();
        
        playerSpawnPos = new Vector2();
        ghostSpawnPos = new Vector2();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
