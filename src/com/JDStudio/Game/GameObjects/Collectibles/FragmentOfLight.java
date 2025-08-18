package com.JDStudio.Game.GameObjects.Collectibles;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;

import org.json.JSONObject;

import com.JDStudio.Engine.Components.InteractionComponent;
import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Graphics.Lighting.Light;
import com.JDStudio.Engine.Graphics.Lighting.LightingManager;
import com.JDStudio.Engine.Object.DialogableGameObject;
import com.JDStudio.Game.States.PlayingState;

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
	        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_TRIGGER, 30));

	        // 3. Adiciona o componente ao GameObject
	        this.addComponent(interaction);
	        
	        light = new Light(getCenterX(),getCenterY(), 50, new Color(50,50,255,70));
	        
	        LightingManager.getInstance().addLight(light);
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		super.tick();
		this.getComponent(InteractionComponent.class).checkInteractions(Collections.singletonList(PlayingState.player));
	}
	
	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		super.render(g);
		getComponent(InteractionComponent.class).render(g);
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		LightingManager.getInstance().removeLight(light);
	}
	
}
