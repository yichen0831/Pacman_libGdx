package com.ychstudio.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ychstudio.components.AnimationComponent;
import com.ychstudio.components.StateComponent;
import com.ychstudio.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {
    
    private ComponentMapper<TextureComponent> textureM = ComponentMapper.getFor(TextureComponent.class);
    private ComponentMapper<AnimationComponent> animationM = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    public AnimationSystem() {
        super(Family.all(TextureComponent.class, AnimationComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextureComponent tex = textureM.get(entity);
        AnimationComponent anim = animationM.get(entity);
        StateComponent state = stateM.get(entity);
        
        tex.region.setRegion(anim.animations.get(state.getState()).getKeyFrame(state.getStateTime()));
    }
    
}
