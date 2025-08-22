package com.game.GameObjects.Characters;
//package com.meujogo;

import java.util.Map;

import org.json.JSONObject;

import com.game.Enuns.GameEvent;
import com.jdstudio.engine.Components.Moviments.MovementComponent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Graphics.Sprite.Spritesheet;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animation;
import com.jdstudio.engine.Graphics.Sprite.Animations.AnimationLoader;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.Object.Character;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Utils.PropertiesReader;

public class Player extends Character {
	

	public double lastDx = 1;
	public double lastDy = 0;
	public Player(JSONObject properties) {
		super(properties);
		//setCollisionMask(0, 0, 32, 32);
	}

	@Override
	public void initialize(JSONObject properties) {
		
		super.initialize(properties);
		setCollisionType(CollisionType.CHARACTER_SOLID);
		PropertiesReader reader = new PropertiesReader(properties);
		//pega o nome diretamente das propriedates/atributos do Tiled
		this.name = reader.getString("name", "Player");
		
		this.addComponent(new Animator());
		this.addComponent(new MovementComponent(1.5));

		// Configurações (podem ser movidas para o Tiled depois)
		//setCollisionMask(2, 2, 12, 14);
		setCollisionMask(2, 3, 28, 27);
		this.maxLife = 120;
		this.life = this.maxLife;
		setupAnimations(properties);
	}
	
	/**
	 * O método setupAnimations agora lê os nomes dos sprites a partir das propriedades.
	 */
	private void setupAnimations(JSONObject properties) {
		
		Animator animator = getComponent(Animator.class);
		if (animator == null) return;	
		Spritesheet playerSheet = new Spritesheet("/Spritesheets/playerRobot32x32.png"); // Use o caminho correto

        // Carrega TODAS as animações de uma vez a partir do JSON!
        // O 'true' no final diz para criar as versões "_left" automaticamente.
        Map<String, Animation> playerAnims = AnimationLoader.loadFromAsepriteJson(
            "/Animations/playerRobot32x32.json", playerSheet, true);

        // Adiciona as animações carregadas ao Animator do jogador
        for (Map.Entry<String, Animation> entry : playerAnims.entrySet()) {
            animator.addAnimation(entry.getKey(), entry.getValue());
        }
        animator.play("idle");

        
	}
	
	@Override
	public void onCollision(GameObject other) {
		super.onCollision(other);
	}
	

	@Override
	public void tick() {
		super.tick(); // Atualiza todos os componentes
		handleMovementInput();
	}

	@Override
	public void takeDamage(double amount) {
		super.takeDamage(amount);
		EventManager.getInstance().trigger(GameEvent.PLAYER_TAKE_DAMAGE, amount);
	}

	private void handleMovementInput() {
		MovementComponent playerMovement = getComponent(MovementComponent.class);
		if (playerMovement == null)
			return;

		double dx = 0, dy = 0;
		if (InputManager.isActionPressed("MOVE_LEFT")) {
			dx = -1;
			this.lastDx = dx;
		} else if (InputManager.isActionPressed("MOVE_RIGHT")) {
			dx = 1;
			this.lastDx = dx;
		}
		if (InputManager.isActionPressed("MOVE_UP")) {
			dy = -1;
			this.lastDy = dy;
		} else if (InputManager.isActionPressed("MOVE_DOWN")) {
			dy = 1;
			this.lastDy = dy;
		}
		playerMovement.setDirection(dx, dy);
		Animator animator = getComponent(Animator.class);
		if (animator == null) return;
		if (dx > 0) {
			animator.play("right");
		} else if (dx < 0) {
			animator.play("left");
		} else if (dy>0) {
			animator.play("down");
		}else if(dy<0) {
			animator.play("up");
		}
		else {
			
			if(lastDx > 0) animator.play("idle");
			else if (lastDx < 0) animator.play("idle");
			else animator.play("idle");
			
		}
		
		
		
	}
}