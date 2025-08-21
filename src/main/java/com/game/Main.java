package com.game;

import java.awt.event.KeyEvent;

import com.game.States.MenuState;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.ResolutionProfile;
import com.jdstudio.engine.Input.InputManager;



public class Main {

	public static void setupInputBindings() {
		InputManager manager = InputManager.instance;
		// Movimento
		manager.bindKey("MOVE_UP", KeyEvent.VK_W);
		manager.bindKey("MOVE_UP", KeyEvent.VK_UP);
		manager.bindKey("MOVE_DOWN", KeyEvent.VK_S);
		manager.bindKey("MOVE_DOWN", KeyEvent.VK_DOWN);
		manager.bindKey("MOVE_LEFT", KeyEvent.VK_A);
		manager.bindKey("MOVE_LEFT", KeyEvent.VK_LEFT);
		manager.bindKey("MOVE_RIGHT", KeyEvent.VK_D);
		manager.bindKey("MOVE_RIGHT", KeyEvent.VK_RIGHT);
		// Ações
		manager.bindKey("INTERACT", KeyEvent.VK_E);
		// manager.loadAndMergeBindings("keybinding.json");
	}

	public static void main(String[] args) {
		// 1. Configura os controlos
		setupInputBindings();
		
        //ResolutionProfile profile = StandardResolutions.MODERN_16_9_HIGH.getProfile();
        ResolutionProfile profile = new ResolutionProfile(640, 360, 2);

        Engine engine = new Engine(
				profile.width(), //width
				profile.height(), //height
				profile.recommendedScale(),//Scale 
				false, //Redimencionavel
				"Meu Novo Jogo", //Titulo
				60.0//FPS
				);

		Engine.setInitialGameState(MenuState.class);	

		engine.start();
	}
}