package com.game.States;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.game.Enuns.GameEvent;
import com.game.GameObjects.Characters.Player;
import com.game.GameObjects.Collectibles.FragmentOfLight;
import com.game.GameObjects.Interactables.Door;
import com.game.GameObjects.Interactables.Terminal;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Components.InteractionPromptComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Core.GameStateManager;
import com.jdstudio.engine.Dialogue.ActionManager;
import com.jdstudio.engine.Dialogue.ConditionManager;
import com.jdstudio.engine.Dialogue.DialogueManager;
import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Events.InteractionEventData;
import com.jdstudio.engine.Events.WorldLoadedEventData;
import com.jdstudio.engine.Graphics.AssetManager;
import com.jdstudio.engine.Graphics.Layers.IRenderable;
import com.jdstudio.engine.Graphics.Layers.RenderLayer;
import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Graphics.Layers.StandardLayers;
import com.jdstudio.engine.Graphics.Lighting.Light;
import com.jdstudio.engine.Graphics.Lighting.LightingManager;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.Sprite.Spritesheet;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animation;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Graphics.UI.Managers.ThemeManager;
import com.jdstudio.engine.Graphics.UI.Managers.UIManager;
import com.jdstudio.engine.Graphics.WSUI.InteractionElements.UIInteractionPrompt;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Sound.Sound;
import com.jdstudio.engine.States.EnginePlayingState;
import com.jdstudio.engine.World.Camera;
import com.jdstudio.engine.World.IMapLoaderListener;
import com.jdstudio.engine.World.Tile;
import com.jdstudio.engine.World.World;
import com.game.Tiles.*;
import com.jdstudio.engine.Graphics.UI.*;
import com.jdstudio.engine.Graphics.UI.Elements.*;
import com.jdstudio.engine.Tutorial.*;
import com.jdstudio.engine.Utils.PropertiesReader;


import com.jdstudio.engine.Object.PreBuildObjcts.EngineDoor;


public class PlayingState extends EnginePlayingState implements IMapLoaderListener {

	// Referências estáticas para fácil acesso
	public static AssetManager assets;
	public static Player player;
	public static World world;

	// Managers específicos deste estado
	private UIManager uiManager;
	private DialogueBox dialogueBox;
	// Adicione aqui outros managers que você usa, como ProjectileManager,
	// LightingManager, etc.

	
	private TutorialBox tutorialBox;
	private UIInteractionPrompt interactionPrompt;
	Light lightPlayer;

	private GameObject interactableObjectInRange = null;
	
	public static int countFragLight = 0;
	private List<UIImage> healthHearts;
	
	private enum InputMode { GAMEPLAY, UI }
    private InputMode currentInputMode = InputMode.GAMEPLAY;
	
	
	public PlayingState() {
		super();

		// --- ORDEM DE INICIALIZAÇÃO CORRIGIDA E SEGURA ---

		// 1. Limpa os managers de estados anteriores.
		RenderManager.getInstance().clear();
		GameStateManager.getInstance().clearFlags();
		EventManager.getInstance().reset();
		DialogueManager.getInstance().reset();
		DialogueManager.getInstance().setDialogueCooldown(2000);


		// 2. Inicializa os managers principais.
		ThemeManager.getInstance().setTheme(UITheme.MEDIEVAL);
		assets = new AssetManager();
		uiManager = new UIManager();
		
		// 3. Carrega os recursos visuais e sonoros.
		loadAssets();
		
		// 4. Carrega o mundo. Este passo é CRÍTICO e irá criar o 'player'
	    // através do método onObjectFound.
		world = new World("/Levels/level1.json", this);
		EventManager.getInstance().trigger(EngineEvent.WORLD_LOADED, new WorldLoadedEventData(world,gameObjects));
		// A partir deste ponto, a variável 'player' já não é nula.

		// 5. Agora que o player existe, podemos configurar tudo o que depende dele.


		for (GameObject go : this.gameObjects) {

			if (go instanceof Door) {
				((Door) go).setGameObjects(this.gameObjects);
			}
		}
		


		setupUI();
		setupDialogueConditions();
		setupDialogueActions();
		setupEventListeners();

		// 6. Carrega os sistemas que podem depender da UI ou dos assets.
	    // A tutorialBox já foi criada dentro de setupUI().
		TutorialManager.getInstance().loadTutorials("/Dialogues/tutorials.json", this.tutorialBox);
		
		// 7. Configura a câmera para seguir o jogador.
		Engine.camera.applyProfile(Camera.PROFILE_GAMEPLAY, player);

		// 8. Regista os sistemas de renderização.
		registerRenderSystems();
		
		// 9. Inicia o áudio e a iluminação.
		Sound.loop("/Sounds/music.wav");
		Sound.setMusicVolume(0.01f);
		LightingManager.getInstance().setAmbientColor(new Color(0, 0, 0, 40));
		lightPlayer= new Light(player.getCenterX(), player.getCenterY(), 30, new Color(255, 255, 200, 50));
		LightingManager.getInstance().addLight(lightPlayer);
		
		// 10. Dispara o evento de início de jogo para acionar os tutoriais iniciais.
		EventManager.getInstance().trigger(GameEvent.GAME_STARTED, null);
	}

	

	private void loadAssets() {

		Spritesheet mainSpritesheet = new Spritesheet("/Spritesheets/MainSpritesheet.png");
		assets.loadSpritesFromSpritesheetJson("/Spritesheets/TileSetMain.json");
		assets.registerSprite("grass_v_1", mainSpritesheet.getSprite(0, 160, 32, 32));

		//ssets.registerSprite("fragmento_de_luz", mainSpritesheet.getSprite(0, 0, 32, 32));
		
		Spritesheet fragluzSpritesheet = new Spritesheet("/Spritesheets/fragmentodeluz.png");
		assets.registerSprite("frag_de_luz_1", fragluzSpritesheet.getSprite(0, 0, 32, 32));
		assets.registerSprite("frag_de_luz_2", fragluzSpritesheet.getSprite(32, 0, 32, 32));
		assets.registerSprite("frag_de_luz_3", fragluzSpritesheet.getSprite(64, 0, 32, 32));

		Spritesheet doorSpritesheet = new Spritesheet("/Spritesheets/doorSpritesheet.png");
		assets.registerSprite("door_frame_1", doorSpritesheet.getSprite(0, 0, 32, 32));
		assets.registerSprite("door_frame_2", doorSpritesheet.getSprite(32, 0, 32, 32));
		assets.registerSprite("door_frame_3", doorSpritesheet.getSprite(64, 0, 32, 32));
			
	}

	private void setupUI() {

		this.tutorialBox = new TutorialBox();
		this.uiManager.addElement(this.tutorialBox);
		createDialogueBox();
		uiManager.addElement(dialogueBox);
		this.healthHearts = new ArrayList<>();
		int maxHearts = (int) Math.ceil(player.maxLife / 40.0); // Ex: 1 coração para cada 40 de vida

		//int maxHearts = 3;
		for (int i = 0; i < maxHearts; i++) {
			UIImage heart = new UIImage(5 + (i * 18), 5, ThemeManager.getInstance().get(UISpriteKey.HEART_FULL));
			healthHearts.add(heart);
			uiManager.addElement(heart);
		}
		
		
		
		
		
		uiManager.addElement(new UIImage(10, 30, assets.getSprite("fragmento_de_luz")));

		// 2. Adiciona o texto do contador (dinâmico)
		uiManager.addElement(
				new UIText(
						30, 
						42, 
						new Font("arial", Font.BOLD, 14), 
						Color.YELLOW, 
						() -> String.valueOf(countFragLight)));
		
		this.interactionPrompt = new UIInteractionPrompt();
		this.uiManager.addElement(this.interactionPrompt);
	}
	
	private void createDialogueBox() {
		dialogueBox = new DialogueBox(10, 85, Engine.getWIDTH() - 20, 70);
		dialogueBox.setFonts(new Font("Courier New", Font.BOLD, 12), new Font("Courier New", Font.PLAIN, 10));
		dialogueBox.setColors(new Color(20, 20, 80, 230), Color.WHITE, Color.YELLOW, Color.CYAN);
		dialogueBox.setPadding(5);
		dialogueBox.setLineSpacing(12);
		dialogueBox.setSectionSpacing(8);
		dialogueBox.setTypewriterSpeed(2);
		
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
		    if (event.zone().type.equals(InteractionZone.TYPE_TRIGGER) || event.zone().type.equals(InteractionZone.TYPE_DIALOGUE)) {
		        // Apenas guarda a referência do objeto. Nenhuma ação é executada.
		        this.interactableObjectInRange = event.zoneOwner();
		        // Opcional: Mostrar uma dica na UI, como um "[E]" a piscar.
		    }
		    InteractionPromptComponent promptComp = interactableObjectInRange.getComponent(InteractionPromptComponent.class);
		    if (promptComp != null) {
		        interactionPrompt.setTarget(interactableObjectInRange, promptComp.promptText);
		    }
		});
		EventManager.getInstance().subscribe(EngineEvent.TARGET_EXITED_ZONE, (data)->{
			InteractionEventData event = (InteractionEventData) data;
		    // Se estamos a sair da zona do objeto guardado, limpamos a referência.
		    if (event.zoneOwner() == this.interactableObjectInRange) {
		        this.interactableObjectInRange = null;
		        
		        // Opcional: Esconder a dica da UI.
		    }
		    interactionPrompt.setTarget(null, "");
		});
		
		
		 // **A LÓGICA DE CONTROLE DE MODO ESTÁ AQUI**
        EventManager.getInstance().subscribe(EngineEvent.DIALOGUE_STARTED, (data) -> {
            // Quando um diálogo começa, muda para o modo UI
            this.currentInputMode = InputMode.UI;
        });

        EventManager.getInstance().subscribe(EngineEvent.DIALOGUE_ENDED, (data) -> {
            // Quando o diálogo termina, volta para o modo Gameplay
            this.currentInputMode = InputMode.GAMEPLAY;
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
				LightingManager.getInstance().render(g);
			}
			
			@Override
			public boolean isVisible() {

				return true;
			}
			
			@Override
			public RenderLayer getRenderLayer() {
				return StandardLayers.LIGHTING;
			}
		});

		// Adicione aqui o registo de outros sistemas, como Partículas e Iluminação
	}
	
	private void setupDialogueActions() {
	    ActionManager manager = ActionManager.getInstance();
	    GameStateManager stateManager = GameStateManager.getInstance();
	    
	    manager.registerAction("COLETAR_FRAGMENTO", (player, item) -> {
	        // Esta ação é chamada tanto pelo diálogo como pela interação direta.
	        
	        // 1. Garante que a flag do tutorial seja definida (não faz mal chamar várias vezes)
	        stateManager.setFlag("FLAG_JOGADOR_VIU_ITEM_TUTORIAL");
	        
	        // 2. Executa a lógica de coleta
	        if (item instanceof FragmentOfLight && !item.isDestroyed) {
	            ((FragmentOfLight) item).coleted();
	        }
	    });
	    
	    
	    
	    
	}

	
	private void setupDialogueConditions() {
	    ConditionManager manager = ConditionManager.getInstance();
	    GameStateManager stateManager = GameStateManager.getInstance();
	    
	    // Regista a lógica para a condição "PRIMEIRA_VEZ_ITEM_TUTORIAL"
	    manager.registerCondition("PRIMEIRA_VEZ_ITEM_TUTORIAL", (interactor) -> {
	        // Esta condição retorna 'true' se a flag AINDA NÃO existir.
	        return !stateManager.hasFlag("FLAG_JOGADOR_VIU_ITEM_TUTORIAL");
	    });

	    // Adicione aqui o registo de outras condições do seu jogo...
	}
	
	public void handleInput() {
		if (InputManager.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
			Engine.transitionToState(new MenuState());
		}
		
		if(InputManager.isActionJustPressed("TOGGLE_DEBUG")) {
			Engine.isDebug = !Engine.isDebug;
		}
		
		if (InputManager.isActionJustPressed("INTERACT")) {
	        if (this.interactableObjectInRange != null) {
	            
	            if (this.interactableObjectInRange instanceof FragmentOfLight) {
	                FragmentOfLight fragment = (FragmentOfLight) this.interactableObjectInRange;
	                
	                boolean isFirstTime = ConditionManager.getInstance().checkCondition("PRIMEIRA_VEZ_ITEM_TUTORIAL", player);
	                
	                if (isFirstTime) {
	                    // Se for a primeira vez, apenas inicia o diálogo.
	                    // A action "COLETAR_FRAGMENTO" será chamada quando o jogador confirmar.
	                    fragment.startFilteredDialogue(player);
	                } else {
	                    // Se NÃO for a primeira vez, chama a ação de coleta diretamente.
	                    // O mesmo código da action é executado, garantindo consistência.
	                    ActionManager.getInstance().executeAction("COLETAR_FRAGMENTO", player, fragment);
	                }
	            }else if (this.interactableObjectInRange instanceof Door) {
	                // Se for uma porta, inicia o diálogo de abertura
	                Door door = (Door) this.interactableObjectInRange;
					System.out.println("interagiu com a porta");
					door.interact();
	            } 
				else if(this.interactableObjectInRange instanceof Terminal){
					
					
					//start dialog
					Terminal terminal = (Terminal) this.interactableObjectInRange;
					terminal.interact();
					terminal.startFilteredDialogue(player);
					//DialogueManager.getInstance().startDialogue(terminal.getDialogue(), terminal, player);
					
				}

					
				}
	        }
	    
		
		if(InputManager.isKeyJustPressed(KeyEvent.VK_I)) {
			player.takeDamage((int)player.maxLife/6);
		}
		
	}
	
	@Override
	public void tick() {
		 // 1. Atualiza sempre os sistemas de UI que precisam de funcionar
	    //    mesmo durante os diálogos (como a própria caixa de diálogo).
	    uiManager.tick();
	    TutorialManager.getInstance().update();
	    dialogueBox.tick(); // Permite que a caixa de diálogo processe o seu próprio input.

	    // 2. Se um diálogo estiver ativo, a lógica de jogabilidade é pausada.
	    //    O 'return' impede que o resto do método seja executado.
	    if (DialogueManager.getInstance().isActive()) {
	        return;
	    }
	    
	    if (dialogueBox.inputConsumedThisFrame) {
	        return;
	    }

	 // 2. A lógica de jogabilidade SÓ é executada se estivermos no modo GAMEPLAY
        if (currentInputMode == InputMode.GAMEPLAY) {
            super.tick(); // Atualiza todos os GameObjects (Jogador, Inimigos)
            handleInput(); // Processa o input de interação com o mundo
            collisionsUpdate();
        }
	    // 4. Atualiza a câmara e outros sistemas de jogabilidade.
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

		case "parede":
			
			String spriteKeyS = "stone_v_" + tileId;
			Sprite tileSpriteS = assets.getSprite(spriteKeyS);
			// Se o sprite para um ID específico não for encontrado, usa um padrão.
			if (tileSpriteS == null) {
				System.err.println("Aviso: Sprite para o tile de parede com id '" + tileId + "' não encontrado. A usar o padrão.");
				tileSpriteS = assets.getSprite("stone_v_221");
			}
			createdTile = new WallTile(x, y, tileSpriteS);
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
					super.initialize(properties);
					
					Sprite spr1 = assets.getSprite("frag_de_luz_1");
					Sprite spr2 = assets.getSprite("frag_de_luz_3");
					Sprite spr3 = assets.getSprite("frag_de_luz_2");
					
					Animation Anim = new Animation(10, true, spr1,spr2,spr3);

					// 2. Adiciona a animação criada a um componente Animator
					Animator animator = new Animator();
					animator.addAnimation("idle", Anim);

					// 3. Adiciona o componente Animator ao GameObject
					this.addComponent(animator);
					
				}
				@Override
					public void tick() {
						super.tick();
						getComponent(Animator.class).play("idle");
					}
				};
				
				addGameObject(gm);
				
			}
		}else if("door".equals(type)) {
			GameObject door = new Door(properties, player);
			addGameObject(door);
		}
	}

	//method add gameObjetc to the gameObjects list
	public static void addGameObjectToList(GameObject gameObject, JSONObject properties) {
		gameObject.initialize(properties);
		gameObjects.add(gameObject);
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
	
	private void collisionsUpdate() {
		for (int i = 0; i < gameObjects.size(); i++) {
			GameObject obj1 = gameObjects.get(i);
			// Pula objetos destruídos
			if (obj1.isDestroyed)
				continue;

			for (int j = i + 1; j < gameObjects.size(); j++) {
				GameObject obj2 = gameObjects.get(j);
				// Pula objetos destruídos
				if (obj2.isDestroyed)
					continue;

				if (GameObject.isColliding(obj1, obj2)) {
					obj1.onCollision(obj2);
					obj2.onCollision(obj1);
				}
			}
		}
	}
	
}