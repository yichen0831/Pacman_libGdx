package com.ychstudio.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ychstudio.PacMan;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 428; // 570
        config.height = 518; // 690
        new LwjglApplication(new PacMan(), config);
    }
}
