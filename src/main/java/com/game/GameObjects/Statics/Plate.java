package com.game.GameObjects.Statics;

import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Utils.PropertiesReader;

import java.awt.Graphics;
import java.util.Properties;

import com.game.Loaders.AssetsLoader;
import com.game.States.PlayingState;
import com.jdstudio.engine.Engine;

import org.json.JSONObject;
@SuppressWarnings("unused")
public class Plate extends GameObject {

    protected String Text;

    protected int positionX;
    protected int positionY;
    


    public Plate(JSONObject properties) {
        super(properties);
    }
    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        setCollisionType(CollisionType.NO_COLLISION);
        PropertiesReader reader = new PropertiesReader(properties);
        this.name = reader.getString("name", "Plate");
        this.Text = reader.getString("text", "Text");
        
    };

    @Override
    public void tick() {
        //super.tick();
        positionX = this.getX() - Engine.camera.getX();
        positionY = this.getY() - Engine.camera.getY();
        
    }

    @Override

    public void render(Graphics g) {
        super.render(g);

        // 2. Define as propriedades da "área branca" (conforme nossa análise da imagem)
        int innerX = 4;        // O X relativo da área branca dentro do sprite
        int innerY = 4;        // O Y relativo da área branca dentro do sprite
        int innerWidth = 24;   // A largura da área branca
        int innerHeight = 8;   // A altura da área branca
        g.setFont(AssetsLoader.fontePixelPequena);

        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        g2d.setRenderingHint(
            java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
            java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
        );

        // 3. Pega as métricas da fonte atual
        java.awt.FontMetrics fm = g.getFontMetrics();

        // 4. Calcula o X para centralizar horizontalmente
        int textWidth = fm.stringWidth(Text);
        int centeredX = positionX + innerX + (innerWidth - textWidth) / 2;

        // 5. Calcula o Y para centralizar verticalmente
        // Esta fórmula alinha a linha de base do texto de forma que 
        // o texto fique visualmente centrado na vertical.
        int textHeight = fm.getHeight();
        int centeredY = positionY + innerY + (innerHeight - textHeight) / 2 + fm.getAscent();

        // 6. Desenha o texto na tela nas posições calculadas
        g.drawString(Text, centeredX, centeredY);
    }

}
