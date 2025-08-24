package com.game.GameObjects.Interactables;


import java.util.List;

import org.json.JSONObject;

import com.game.States.PlayingState;
import com.jdstudio.engine.Components.InteractionPromptComponent;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animation;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Object.PreBuildObjcts.EngineDoor;

public class Door extends EngineDoor {

    Terminal terminal;

    public Door(JSONObject properties, GameObject target) {
        super(properties, target);
        setCollisionMask(0, 22, 32, 9);
        this.addComponent(new InteractionPromptComponent("Pressione 'E' para interagir com a porta"));
        terminal = new Terminal(properties);
        terminal.setX(x- width - 10);
        //create new properties for the terminal
        JSONObject terminalProperties = new JSONObject();
        terminalProperties.put("x", terminal.getX());
        terminalProperties.put("y", terminal.getY());
        terminalProperties.put("width", terminal.getWidth());
        terminalProperties.put("height", terminal.getHeight());
        terminalProperties.put("name", "Terminal");
        terminalProperties.put("dialogueFile","/Dialogues/terminal.json");

        PlayingState.addGameObjectToList(terminal, terminalProperties);
    }


    @Override
    public void tick() {
        super.tick();
        
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

    @Override
    public void setGameObjects(List<GameObject> gameObjects) {
        // TODO Auto-generated method stub
        super.setGameObjects(gameObjects);
    }

}