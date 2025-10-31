package com.game.Loaders;

import com.jdstudio.engine.Graphics.Sprite.*;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import com.jdstudio.engine.Graphics.AssetManager;
@SuppressWarnings("unused")
public class AssetsLoader {
    private AssetManager assets;

	public static Font fontePixelPequena;
    
    public AssetsLoader() {
        assets = new AssetManager();
        
    }

    public AssetManager loadAssets(){
        Spritesheet mainSpritesheet = new Spritesheet("/Spritesheets/MainSpritesheet.png");
		assets.loadSpritesFromSpritesheetJson("/Spritesheets/TileSetMain.json");
		assets.registerSprite("grass_v_1", mainSpritesheet.getSprite(0, 160, 32, 32));
		assets.registerSprite("enemy_base", mainSpritesheet.getSprite(224, 416, 32, 32));

		//ssets.registerSprite("fragmento_de_luz", mainSpritesheet.getSprite(0, 0, 32, 32));
		
		Spritesheet fragluzSpritesheet = new Spritesheet("/Spritesheets/fragmentodeluz.png");
		assets.registerSprite("frag_de_luz_1", fragluzSpritesheet.getSprite(0, 0, 32, 32));
		assets.registerSprite("frag_de_luz_2", fragluzSpritesheet.getSprite(32, 0, 32, 32));
		assets.registerSprite("frag_de_luz_3", fragluzSpritesheet.getSprite(64, 0, 32, 32));

		Spritesheet doorSpritesheet = new Spritesheet("/Spritesheets/doorSpritesheet.png");
		assets.registerSprite("door_frame_1", doorSpritesheet.getSprite(0, 0, 32, 32));
		assets.registerSprite("door_frame_2", doorSpritesheet.getSprite(32, 0, 32, 32));
		assets.registerSprite("door_frame_3", doorSpritesheet.getSprite(64, 0, 32, 32));

        assets.loadFont("pixi", "/Fonts/PIXY.ttf", 8f);
        fontePixelPequena = assets.getFont("pixi");

        return assets;
    }

    
}
