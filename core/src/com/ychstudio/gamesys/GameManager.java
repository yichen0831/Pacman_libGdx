package com.ychstudio.gamesys;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class GameManager implements Disposable {
    
    public static final GameManager instance = new GameManager();
    
    public static final float PPM = 16f;
    
    public AssetManager assetManager;
    
    private GameManager() {
        assetManager = new AssetManager();
        assetManager.load("images/actors.pack", TextureAtlas.class);
        
        assetManager.finishLoading();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
