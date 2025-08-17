package com.JDStudio.Game.States;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Events.EngineEvent;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Events.WorldLoadedEventData;
import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.Layers.IRenderable;
import com.JDStudio.Engine.Graphics.Layers.RenderLayer;
import com.JDStudio.Engine.Graphics.Layers.RenderManager;
import com.JDStudio.Engine.Graphics.Layers.StandardLayers;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Spritesheet;
import com.JDStudio.Engine.Graphics.UI.UITheme;
import com.JDStudio.Engine.Graphics.UI.Managers.ThemeManager;
import com.JDStudio.Engine.Graphics.UI.Managers.UIManager;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Sound.Sound;
import com.JDStudio.Engine.States.EnginePlayingState;
import com.JDStudio.Engine.World.Camera;
import com.JDStudio.Engine.World.IMapLoaderListener;
import com.JDStudio.Engine.World.Tile;
import com.JDStudio.Engine.World.World;
import com.JDStudio.Game.Enuns.GameEvent;
import com.JDStudio.Game.GameObjects.Characters.Player;
import com.JDStudio.Game.Tiles.GrassTile;

@SuppressWarnings("static-access")
public class PlayingState extends EnginePlayingState implements IMapLoaderListener {

	// Referências estáticas para fácil acesso
	public static AssetManager assets;
	public static Player player;
	public static World world;

	// Managers específicos deste estado
	@SuppressWarnings("unused")
	private UIManager uiManager;
	// Adicione aqui outros managers que você usa, como ProjectileManager,
	// LightingManager, etc.

	public PlayingState() {
		super();

		// --- ORDEM DE INICIALIZAÇÃO CORRETA ---

		// 1. Limpa o RenderManager de qualquer lixo de um estado anterior
		RenderManager.getInstance().clear();

		// 2. Inicializa os managers
		ThemeManager.getInstance().setTheme(UITheme.MEDIEVAL);
		assets = new AssetManager();
		uiManager = new UIManager();
		// ... inicialize outros managers aqui ...

		// 3. Carrega os recursos visuais e sonoros
		loadAssets();

		// 4. Carrega o mundo. Durante este processo, os métodos onObjectFound e
		// onTileFound serão chamados, criando e registando os objetos e tiles.
		world = new World("/level1.json", this);
		
		EventManager.getInstance().trigger(EngineEvent.WORLD_LOADED, new WorldLoadedEventData(world,this.getGameObjects()));

		// 5. Regista os sistemas de renderização que não são GameObjects (fundo,
		// iluminação, etc.)
		registerRenderSystems();

		Engine.camera.applyProfile(Camera.PROFILE_GAMEPLAY, player);
		// 6. Configura a UI e os eventos
		setupUI();
		setupEventListeners();
		
		Sound.loop("/music.wav");
		Sound.setMusicVolume(0.01f);
	}

	private void loadAssets() {
		Spritesheet grassSpritesheet = new Spritesheet("./SpritesheetGrass.png");
		// Tamanho de cada tile na sua spritesheet
		final int TILE_SIZE = 32;
		// Número de colunas e linhas que você quer carregar
		final int NUM_COLS = 8;
		final int NUM_ROWS = 4;

		// Um contador para dar um número único a cada sprite (grass_v_1, grass_v_2, etc.)
		int spriteCounter = 1;

		// Loop aninhado: o loop externo percorre as LINHAS (y)
		for (int row = 0; row < NUM_ROWS; row++) {
		    // O loop interno percorre as COLUNAS (x)
		    for (int col = 0; col < NUM_COLS; col++) {
		        
		        // 1. Calcula a posição (x, y) do tile na spritesheet
		        int tileX = col * TILE_SIZE;
		        int tileY = row * TILE_SIZE;
		        
		        // 2. Cria a chave (key) dinâmica para o sprite
		        String spriteKey = "grass_v_" + spriteCounter;
		        
		        // 3. Recorta o sprite e o regista no AssetManager
		        assets.registerSprite(spriteKey, grassSpritesheet.getSprite(tileX, tileY, TILE_SIZE, TILE_SIZE));
		        
		        // 4. Incrementa o contador para o próximo sprite
		        spriteCounter++;
		    }
		}
		assets.registerSprite("grass_v_33", grassSpritesheet.getSprite(TILE_SIZE*0, TILE_SIZE*4, TILE_SIZE, TILE_SIZE));
		assets.registerSprite("grass_v_35", grassSpritesheet.getSprite(TILE_SIZE*2, TILE_SIZE*4, TILE_SIZE, TILE_SIZE));
		assets.registerSprite("grass_v_36", grassSpritesheet.getSprite(TILE_SIZE*3, TILE_SIZE*4, TILE_SIZE, TILE_SIZE));
		assets.registerSprite("grass_v_37", grassSpritesheet.getSprite(TILE_SIZE*4, TILE_SIZE*4, TILE_SIZE, TILE_SIZE));
		assets.registerSprite("grass_v_55", grassSpritesheet.getSprite(TILE_SIZE*6, TILE_SIZE*6, TILE_SIZE, TILE_SIZE));
		assets.registerSprite("grass_v_56", grassSpritesheet.getSprite(TILE_SIZE*7, TILE_SIZE*6, TILE_SIZE, TILE_SIZE));
		
	}

	private void setupUI() {
		// Crie e adicione aqui os elementos da sua UI (ex: HUD de vida)
		// uiManager.addElement(...);
	}

	private void setupEventListeners() {
		// Inscreva-se aqui nos eventos do jogo
		// EventManager.getInstance().subscribe(...);
		EventManager.getInstance().subscribe(GameEvent.PLAYER_TAKE_DAMAGE, (data) -> {
			System.out.println("player damager");
		});
	}

	@Override
	public void onExit() {
		super.onExit();
		EventManager.getInstance().unsubscribe(GameEvent.PLAYER_TAKE_DAMAGE, null);
	}

	/**
	 * Regista os sistemas de renderização (como o mundo e os efeitos) no
	 * RenderManager.
	 */
	private void registerRenderSystems() {
		RenderManager renderManager = RenderManager.getInstance();

		// Regista o renderizador do mundo de tiles
		renderManager.register(new IRenderable() {
			public void render(Graphics g) {
				if (world != null) {
					// O método render do mundo agora só desenha os tiles
					// (O ideal seria que os tiles se registassem individualmente)
				}
			}

			public RenderLayer getRenderLayer() {
				return StandardLayers.WORLD_BACKGROUND;
			}

			public boolean isVisible() {
				return true;
			}
		});

		// Adicione aqui o registo de outros sistemas, como Partículas e Iluminação
	}

	@Override
	public void tick() {
		super.tick(); // Atualiza todos os GameObjects registados

		if (InputManager.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
			Engine.transitionToState(new MenuState());
		}

		// Atualiza a câmara para seguir o jogador
		if (player != null && world != null) {
			Engine.camera.update(world); // A câmara já sabe quem seguir
		}
	}

	@Override
	public void render(Graphics g) {
		// A renderização agora é 100% controlada pelo RenderManager.
		// Ele irá desenhar tudo na ordem correta das camadas.
		RenderManager.getInstance().render(g);
	}

	// --- MÉTODOS DO IMapLoaderListener ---

	@Override
	public Tile onTileFound(String layerName, int tileId, int x, int y) {
		Tile createdTile = null;
		switch (layerName) {
		case "chao":
			String spriteKey = "grass_v_" + tileId;
		    Sprite tileSprite = assets.getSprite(spriteKey);

		    // Se o sprite para um ID específico não for encontrado, usa um padrão.
		    if (tileSprite == null) {
		        System.err.println("Aviso: Sprite para o tile de chão com id '" + tileId + "' não encontrado. A usar o padrão.");
		        tileSprite = assets.getSprite("grass_v_1");
		    }
		    
		    createdTile = new GrassTile(x, y, tileSprite);
			break;

		case "paredes":
			//createdTile = new WallTile(x, y, assets.getSprite("wall_1"));
			break;

		default:
			createdTile = null;
			break;
		}
		
		if (createdTile != null) {
            // ...nós o registamos no RenderManager para que ele seja desenhado.
            RenderManager.getInstance().register(createdTile);
        }
        
        // Retorna o tile para ser adicionado ao array 'tiles' do World (para colisões)
        return createdTile;
	}

	@Override
	public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
		// Lógica para criar e registar os seus GameObjects
		if ("player_start".equals(type)) {
			player = new Player(properties);
			this.addGameObject(player); // Adiciona à lista de tick
			// Os GameObjects já se registam no RenderManager nos seus construtores
		}
	}

	@Override
	public void onPathFound(String pathName, List<Point> pathPoints) {
		// Lógica para caminhos de patrulha
	}
}