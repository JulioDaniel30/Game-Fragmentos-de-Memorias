package com.game.GameObjects.Collectibles;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;

import org.json.JSONObject;

import com.game.States.PlayingState;
import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionPromptComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Graphics.Lighting.Light;
import com.jdstudio.engine.Graphics.Lighting.LightingManager;
import com.jdstudio.engine.Object.DialogableGameObject;

public class FragmentOfLight extends DialogableGameObject{

	
	Light light;
	
	public FragmentOfLight(JSONObject properties) {
		super(properties);
	}
	@Override
	public void initialize(JSONObject properties) {
			super.initialize(properties);
			InteractionComponent interaction = new InteractionComponent();

	        // 2. Cria uma zona de interação circular com o TIPO "DIALOGUE" e o mesmo raio de antes
	        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_TRIGGER, 50));

	        // 3. Adiciona o componente ao GameObject
	        this.addComponent(interaction);
	        
	        light = new Light(getCenterX(),getCenterY(), 50, new Color(50,50,255,70));
	        
	        LightingManager.getInstance().addLight(light);
	        this.addComponent(new InteractionPromptComponent("[E] Coletar"));
	}

	@Override
	public void tick() {
		super.tick();
		this.getComponent(InteractionComponent.class).checkInteractions(Collections.singletonList(PlayingState.player));
	}
	
	public void coleted() {
		
		destroy();
		PlayingState.countFragLight+= 1;
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		getComponent(InteractionComponent.class).render(g);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		LightingManager.getInstance().removeLight(light);
	}
	
}
