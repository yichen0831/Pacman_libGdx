package com.ychstudio.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {
    
    public TextureRegion region;

    public TextureComponent(TextureRegion textureRegion) {
        region = new TextureRegion(textureRegion);
    }
}
