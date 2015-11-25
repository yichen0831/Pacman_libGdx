package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.components.TextureComponent;
import com.ychstudio.components.TransformComponent;
import com.ychstudio.gamesys.GameManager;
import java.util.Comparator;

public class RenderSystem extends IteratingSystem {

    private final Array<Entity> renderArray;

    private final SpriteBatch batch;

    private final ComponentMapper<TransformComponent> transformM = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<TextureComponent> rendererM = ComponentMapper.getFor(TextureComponent.class);

    public RenderSystem(SpriteBatch batch) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get());

        this.batch = batch;

        renderArray = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        renderArray.sort(new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                TransformComponent transform1 = transformM.get(o1);
                TransformComponent transform2 = transformM.get(o2);
                return transform2.zIndex - transform1.zIndex;
            }

        });

        batch.begin();
        for (Entity entity : renderArray) {
            TransformComponent transform = transformM.get(entity);
            TextureComponent tex = rendererM.get(entity);
            float width = tex.region.getRegionWidth() / GameManager.PPM;
            float height = tex.region.getRegionHeight() / GameManager.PPM;
            float originX = width * 0.5f;
            float originY = height * 0.5f;
            batch.draw(tex.region,
                    transform.pos.x - originX, transform.pos.y - originY,
                    originX, originY,
                    width, height,
                    transform.scale.x, transform.scale.y,
                    transform.rotation * MathUtils.radiansToDegrees);
        }

        batch.end();
        renderArray.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderArray.add(entity);
    }

}
