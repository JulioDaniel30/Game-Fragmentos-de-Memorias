package com.JDStudio.Game.States;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Events.EngineEvent;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Events.InteractionEventData;
import com.JDStudio.Engine.Events.WorldLoadedEventData;
import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.Layers.IRenderable;
import com.JDStudio.Engine.Graphics.Layers.RenderLayer;
import com.JDStudio.Engine.Graphics.Layers.RenderManager;
import com.JDStudio.Engine.Graphics.Layers.StandardLayers;
import com.JDStudio.Engine.Graphics.Lighting.Light;
import com.JDStudio.Engine.Graphics.Lighting.LightingManager;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Spritesheet;
import com.JDStudio.Engine.Graphics.UI.UISpriteKey;
import com.JDStudio.Engine.Graphics.UI.UITheme;
import com.JDStudio.Engine.Graphics.UI.Elements.TutorialBox;
import com.JDStudio.Engine.Graphics.UI.Elements.UIImage;
import com.JDStudio.Engine.Graphics.UI.Elements.UIText;
import com.JDStudio.Engine.Graphics.UI.Managers.ThemeManager;
import com.JDStudio.Engine.Graphics.UI.Managers.UIManager;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Sound.Sound;
import com.JDStudio.Engine.States.EnginePlayingState;
import com.JDStudio.Engine.Tutorial.TutorialManager;
import com.JDStudio.Engine.Utils.PropertiesReader;
import com.JDStudio.Engine.World.Camera;
import com.JDStudio.Engine.World.IMapLoaderListener;
import com.JDStudio.Engine.World.Tile;
import com.JDStudio.Engine.World.World;
import com.JDStudio.Game.Enuns.GameEvent;
import com.JDStudio.Game.GameObjects.Characters.Player;
import com.JDStudio.Game.GameObjects.Collectibles.FragmentOfLight;
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
	
	private TutorialBox tutorialBox;
	
	Light lightPlayer;

	private GameObject interactableObjectInRange = null;
	
	public static int countFragLight = 0;
	private List<UIImage> healthHearts;
	
	
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
		TutorialManager.getInstance().loadTutorials("/tutorials.json", this.tutorialBox);
		Sound.loop("/music.wav");
		Sound.setMusicVolume(0.01f);
		LightingManager.getInstance().setAmbientColor(new Color(0, 0, 10, 80));
		lightPlayer= new Light(player.getCenterX(), player.getCenterY(), 30, new Color(255, 255, 200, 50));
		LightingManager.getInstance().addLight(lightPlayer);
		EventManager.getInstance().trigger(GameEvent.GAME_STARTED, null);
		
	}

	private void loadAssets() {

		Spritesheet mainSpritesheet = new Spritesheet("/MainSpritesheet.png");
		
		assets.loadSpritesFromSpritesheetJson("/TileSetGrass.json");
		assets.registerSprite("fragmento_de_luz", mainSpritesheet.getSprite(0, 0, 32, 32));
		
	}

	private void setupUI() {
		// Crie e adicione aqui os elementos da sua UI (ex: HUD de vida)
		// uiManager.addElement(...);
		
		this.healthHearts = new ArrayList<>();
		int maxHearts = (int) Math.ceil(player.maxLife / 40.0); // Ex: 1 coração para cada 20 de vida

		//int maxHearts = 3;
		for (int i = 0; i < maxHearts; i++) {
			UIImage heart = new UIImage(5 + (i * 18), 5, ThemeManager.getInstance().get(UISpriteKey.HEART_FULL));
			healthHearts.add(heart);
			uiManager.addElement(heart);
		}
		
		
		this.tutorialBox = new TutorialBox();
		this.uiManager.addElement(this.tutorialBox);
		
		
		uiManager.addElement(new UIImage(10, 30, assets.getSprite("fragmento_de_luz")));

		// 2. Adiciona o texto do contador (dinâmico)
		uiManager.addElement(
				new UIText(
						30, 
						42, 
						new Font("arial", Font.BOLD, 14), 
						Color.YELLOW, 
						() -> String.valueOf(countFragLight)));
		
		
	}

	private void setupEventListeners() {
		// Inscreva-se aqui nos eventos do jogo
		// EventManager.getInstance().subscribe(...);
		EventManager.getInstance().subscribe(GameEvent.PLAYER_TAKE_DAMAGE, (data) -> {
			System.out.println("player damager");
		});
		
		EventManager.getInstance().subscribe(EngineEvent.TARGET_ENTERED_ZONE, (data)->{
			InteractionEventData event = (InteractionEventData) data;
		    // Verifica se a zona é de um tipo interativo manual (ex: "DIALOGUE")
		    if (event.zone().type.equals(InteractionZone.TYPE_TRIGGER)) {
		        // Apenas guarda a referência do objeto. Nenhuma ação é executada.
		        this.interactableObjectInRange = event.zoneOwner();
		        // Opcional: Mostrar uma dica na UI, como um "[E]" a piscar.
		    }
		});
		EventManager.getInstance().subscribe(EngineEvent.TARGET_EXITED_ZONE, (data)->{
			InteractionEventData event = (InteractionEventData) data;
		    // Se estamos a sair da zona do objeto guardado, limpamos a referência.
		    if (event.zoneOwner() == this.interactableObjectInRange) {
		        this.interactableObjectInRange = null;
		        // Opcional: Esconder a dica da UI.
		    }
		});
		
		
		
		EventManager.getInstance().subscribe(GameEvent.PLAYER_TAKE_DAMAGE, (data) -> updateHealthUI());
		EventManager.getInstance().subscribe(GameEvent.PLAYER_HEALED, (data) -> updateHealthUI());
		// Chame-o uma vez no início para o estado inicial
		updateHealthUI();
		
	}

	@Override
	public void onExit() {
		super.onExit();
		EventManager.getInstance().unsubscribe(GameEvent.PLAYER_TAKE_DAMAGE, null);
		EventManager.getInstance().unsubscribe(EngineEvent.TARGET_ENTERED_ZONE, null);
		EventManager.getInstance().unsubscribe(EngineEvent.TARGET_EXITED_ZONE, null);
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
		
		renderManager.register(new IRenderable() {
			
			@Override
			public void render(Graphics g) {
				// TODO Auto-generated method stub
				LightingManager.getInstance().render(g);
			}
			
			@Override
			public boolean isVisible() {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public RenderLayer getRenderLayer() {
				// TODO Auto-generated method stub
				return StandardLayers.LIGHTING;
			}
		});

		// Adicione aqui o registo de outros sistemas, como Partículas e Iluminação
	}

	
	public void handleInput() {
		if (InputManager.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
			Engine.transitionToState(new MenuState());
		}
		
		if(InputManager.isActionJustPressed("TOGGLE_DEBUG")) {
			Engine.isDebug = !Engine.isDebug;
		}
		
		if (InputManager.isActionJustPressed("INTERACT")) {

			// Se a nossa variável de estado não for nula, o jogador está perto de algo E
			// pressionou a tecla.
			if (this.interactableObjectInRange != null) {

				// Verificamos que tipo de objeto é para decidir o que fazer.

				if (this.interactableObjectInRange instanceof FragmentOfLight) {
					countFragLight++;
					interactableObjectInRange.isDestroyed = true;
				}
				// else if (this.interactableObjectInRange instanceof Bau) { ... }
			}
		}
		
		if(InputManager.isKeyJustPressed(KeyEvent.VK_I)) {
			player.takeDamage((int)player.maxLife/6);
		}
		
	}
	
	@Override
	public void tick() {
		uiManager.tick();
		super.tick(); // Atualiza todos os GameObjects registados

		TutorialManager.getInstance().update();
		
		handleInput();

		// Atualiza a câmara para seguir o jogador
		if (player != null && world != null) {
			Engine.camera.update(world); // A câmara já sabe quem seguir
			lightPlayer.x = player.getCenterX();
			lightPlayer.y = player.getCenterY();
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
		}else if ("item".equals(type)) {
			
			PropertiesReader reader = new PropertiesReader(properties);
			
			if("fragmento de luz".equals(reader.getString("name",""))) {
				
				GameObject gm = new FragmentOfLight(properties) {@Override
				public void initialize(JSONObject properties) {
					// TODO Auto-generated method stub
					super.initialize(properties);
					sprite = assets.getSprite("fragmento_de_luz");
					
				}};
				
				LightingManager.getInstance().addLight(new Light(gm.getCenterX(), gm.getCenterY(), 50, new Color(50,50,255,70)));
				addGameObject(gm);
				
			}
		}
	}

	@Override
	public void onPathFound(String pathName, List<Point> pathPoints) {
		// Lógica para caminhos de patrulha
	}
	
	private void updateHealthUI() {
		double healthPerHeart = player.maxLife / healthHearts.size();

		for (int i = 0; i < healthHearts.size(); i++) {
			UIImage heart = healthHearts.get(i);
			double heartHealthThreshold = (i + 1) * healthPerHeart;

			if (player.life >= heartHealthThreshold) {
				heart.setSprite(ThemeManager.getInstance().get(UISpriteKey.HEART_FULL));
			} else if (player.life >= heartHealthThreshold - (healthPerHeart / 2)) {
				heart.setSprite(ThemeManager.getInstance().get(UISpriteKey.HEART_HALF));
			} else {
				heart.setSprite(ThemeManager.getInstance().get(UISpriteKey.HEART_EMPTY));
			}
		}
	}
	
}