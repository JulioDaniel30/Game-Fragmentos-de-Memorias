package com.JDStudio.Game.GameObjects.Characters;
//package com.meujogo;

import java.util.Map;

import org.json.JSONObject;
import com.JDStudio.Engine.Components.Moviments.MovementComponent;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Graphics.Sprite.Spritesheet;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
import com.JDStudio.Engine.Graphics.Sprite.Animations.AnimationLoader;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.Character;
import com.JDStudio.Engine.Utils.PropertiesReader;
import com.JDStudio.Game.Enuns.GameEvent;

public class Player extends Character {

	public double lastDx = 1;
	public double lastDy = 0;
	public Player(JSONObject properties) {
		super(properties);
	}

	@Override
	public void initialize(JSONObject properties) {
		super.initialize(properties);
		PropertiesReader reader = new PropertiesReader(properties);
		//pega o nome diretamente das propriedates/atributos do Tiled
		this.name = reader.getString("name", "Player");
		
		this.addComponent(new Animator());
		this.addComponent(new MovementComponent(1.5));

		// Configurações (podem ser movidas para o Tiled depois)
		setCollisionMask(2, 2, 12, 14);
		this.maxLife = 100;
		this.life = this.maxLife;
		setupAnimations(properties);
	}
	
	/**
	 * O método setupAnimations agora lê os nomes dos sprites a partir das propriedades.
	 */
	private void setupAnimations(JSONObject properties) {
		
		Animator animator = getComponent(Animator.class);
		if (animator == null) return;	
		Spritesheet playerSheet = new Spritesheet("/playerRobot32x32.png"); // Use o caminho correto

        // Carrega TODAS as animações de uma vez a partir do JSON!
        // O 'true' no final diz para criar as versões "_left" automaticamente.
        Map<String, Animation> playerAnims = AnimationLoader.loadFromAsepriteJson(
            "/playerRobot32x32.json", playerSheet, true);

        // Adiciona as animações carregadas ao Animator do jogador
        for (Map.Entry<String, Animation> entry : playerAnims.entrySet()) {
            animator.addAnimation(entry.getKey(), entry.getValue());
        }
        animator.play("idle");

        
	}
	

	@Override
	public void tick() {
		super.tick(); // Atualiza todos os componentes
		handleMovementInput();
	}

	@Override
	public void takeDamage(double amount) {
		// TODO Auto-generated method stub
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