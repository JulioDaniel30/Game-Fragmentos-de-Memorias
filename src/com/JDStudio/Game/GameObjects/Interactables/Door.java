package com.JDStudio.Game.GameObjects.Interactables;

import org.json.JSONObject;


import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Object.PreBuildObjcts.EngineDoor;
import com.JDStudio.Game.States.PlayingState;

public class Door extends EngineDoor {

    public Door(JSONObject properties) {
        super(properties);
    }

    /**
     * Esta é a nossa única responsabilidade: preencher as lacunas da classe base
     * com os assets específicos do nosso jogo.
     */
    @Override
    protected void setupAnimations(Animator animator) {
        // Carrega os sprites do AssetManager do nosso jogo
        Animation idleClosed = new Animation(1, PlayingState.assets.getSprite("door_frame_1"));
        Animation idleOpen = new Animation(1, PlayingState.assets.getSprite("door_frame_3"));
        Animation opening = new Animation(20, false, 
            PlayingState.assets.getSprite("door_frame_1"), 
            PlayingState.assets.getSprite("door_frame_2"), 
            PlayingState.assets.getSprite("door_frame_3"));
        Animation closing = new Animation(20, false, 
            PlayingState.assets.getSprite("door_frame_3"), 
            PlayingState.assets.getSprite("door_frame_2"), 
            PlayingState.assets.getSprite("door_frame_1"));

        // Adiciona as animações ao animator que foi passado pela classe base
        animator.addAnimation("idleClosed", idleClosed);
        animator.addAnimation("idleOpen", idleOpen);
        animator.addAnimation("opening", opening);
        animator.addAnimation("closing", closing);
    }
}