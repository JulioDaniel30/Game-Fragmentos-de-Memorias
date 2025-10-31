package com.game.GameObjects.Characters;

import org.json.JSONObject;

import com.game.States.PlayingState;
import com.jdstudio.engine.Object.Character;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Components.Moviments.*;

public class EnemyBase extends Character {
    public EnemyBase(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(org.json.JSONObject properties) {
        super.initialize(properties);
        setCollisionType(CollisionType.CHARACTER_TRIGGER);
        life = 50; // Vida do inimigo
        maxLife = 50; // Vida máxima do inimigo
        this.addComponent(new MovementComponent(2));
    }

    @Override
    public void tick() {
        super.tick();

    }



    @Override
    public void onCollision(GameObject other) {
        super.onCollision(other);

        if (other instanceof Player) {
            // Lógica de dano ao jogador ou outra interação
            System.out.println("EnemyBase collided with Player!");
            life -= 10; // Exemplo de dano recebido
            if (life <= 0) {
                // Lógica de morte do inimigo
                PlayingState.KillCount++;// Incrementa o contador de inimigos derrotados
                this.destroy();
            }
        }

    }

}