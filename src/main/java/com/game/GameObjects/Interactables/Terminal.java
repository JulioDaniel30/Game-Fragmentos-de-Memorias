package com.game.GameObjects.Interactables;

import java.awt.Graphics;
import java.util.Collections;

import org.json.JSONObject;

import com.game.States.PlayingState;
import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionPromptComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Dialogue.DialogueManager;
import com.jdstudio.engine.Object.DialogableGameObject;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Object.GameObject.CollisionType;

@SuppressWarnings("unused")
public class Terminal extends DialogableGameObject {
    // Terminal specific properties and methods can be added here

    protected double interactionRange = 50.0; // Example property for interaction range


    // Constructor to initialize the terminal with properties
    public Terminal(JSONObject properties) {
        super(properties);
        setCollisionType(CollisionType.TRIGGER);        
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);

        InteractionComponent interactionComponent = new InteractionComponent();

        interactionComponent.addZone(new InteractionZone(this,InteractionZone.TYPE_DIALOGUE, interactionRange));
        this.addComponent(interactionComponent);
        this.addComponent(new InteractionPromptComponent("Pressione 'E' para interagir com o terminal"));
        sprite = PlayingState.assets.getSprite("door_frame_2");
    }

    public void interact() {
        // Logic for interacting with the terminal
        System.out.println("Interagindo com o terminal: " + this.name);
    }

    @Override
    public void tick() {
        super.tick();
        this.getComponent(InteractionComponent.class).checkInteractions(Collections.singletonList(PlayingState.player));
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        this.getComponent(InteractionComponent.class).render(g);
    }
    
}
