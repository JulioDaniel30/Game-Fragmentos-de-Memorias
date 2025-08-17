# Game Design Document: Fragmentos de Memória

## 1. Informações Gerais

- **Título do Jogo:** Fragmentos de Memória
- **Gênero:** Aventura / Ação Leve (Top-Down)
- **Premissa:** Um pequeno autômato acorda sem memória em uma antiga biblioteca abandonada e tomada pela natureza. Guiado por uma luz piscante em seu peito, ele deve coletar "Fragmentos de Luz" para restaurar suas memórias e descobrir seu propósito, enquanto enfrenta construtos de sucata defeituosos que protegem o local.

## 2. Aproveitando os Recursos da Engine

| Recurso da Engine | Aplicação no Jogo |
| :--- | :--- |
| **Arquitetura de Componentes** | O jogador (autômato) começa com componentes básicos (`MovementComponent`) e adquire novos ao longo do jogo (`ShieldComponent`, `DashComponent`), simulando uma "recalibração" de suas habilidades. |
| **Sistema de Inventário e Itens** | O foco da coleta. O jogador gerencia:<ul><li>**Fragmentos de Luz:** Itens de missão para recuperar memórias.</li><li>**Engrenagens Antigas:** Moeda para possíveis upgrades.</li><li>**Núcleos de Energia:** Consumíveis utilizados para curar.</li></ul> |
| **Mapa (Tiled)** | O mundo do jogo é uma grande biblioteca interligada, construída com Tiled, dividida em quatro seções principais:<ul><li>O Saguão Principal</li><li>A Ala dos Jardins Botânicos</li><li>O Arquivo Subterrâneo</li><li>O Observatório</li></ul> |
| **Inimigos e IA (AIMovementComponent)** | Os "Construtos de Sucata" patrulham rotas fixas ou guardam áreas. A IA define seus comportamentos, variando entre inimigos lentos e fortes e outros rápidos e frágeis. |
| **Objetos Interativos (Interactable)** | Terminais de computador espalhados pelo cenário permitem que o jogador leia logs e trechos da história da biblioteca, utilizando o `DialogueManager` para exibir o texto. |
| **Sistema de Salvamento (ISavable)** | O progresso do jogador (fragmentos coletados, áreas desbloqueadas, habilidades adquiridas) é salvo em "Pontos de Recalibração", que funcionam como checkpoints. |

## 3. Roteiro (Dividido em 3 Atos)

---

### **Ato I: O Despertar**

1. **Início:**
    - **Local:** Centro do Saguão Principal.
    - **Evento:** O jogador acorda. Um tutorial ensina os controles básicos de movimento. A luz em seu peito pisca fracamente, servindo como guia inicial.

2. **Primeira Missão:**
    - **Objetivo:** Coletar o primeiro Fragmento de Luz.
    - **Evento:** Uma interface aponta para o fragmento, visível em uma plataforma próxima. Ao coletá-lo, o `DialogueManager` exibe uma memória curta: *"...energia falhando... selar as alas..."*.

3. **O Obstáculo:**
    - **Desafio:** A porta para a Ala dos Jardins está trancada e sem energia.
    - **Pista:** Um terminal ao lado exibe a mensagem: "ENERGIA INSUFICIENTE. INSIRA NÚCLEO DE ENERGIA PRIMÁRIO".

4. **Exploração e Combate:**
    - **Objetivo:** Encontrar o Núcleo de Energia Primário.
    - **Mecânicas:** O jogador explora o Saguão, enfrentando Construtos de Sucata simples com seu ataque básico (um pulso de energia de curto alcance). O núcleo é encontrado em uma sala lateral.

5. **Conclusão do Ato:**
    - **Ação:** O jogador insere o núcleo no terminal.
    - **Resultado:** A porta para a Ala dos Jardins se abre, permitindo o avanço.

---

### **Ato II: Os Jardins e as Profundezas**

1. **A Ala dos Jardins:**
    - **Local:** Uma área semi-aberta, com vegetação densa cobrindo as estruturas.
    - **Objetivo:** Encontrar 3 Fragmentos de Memória espalhados pela área.
    - **Inimigos:** Novos construtos são introduzidos, incluindo alguns que se escondem na vegetação para criar emboscadas.

2. **Nova Habilidade:**
    - **Descoberta:** O jogador encontra um "Módulo de Escudo" quebrado em um canto da ala.
    - **Resultado:** Ao interagir, ele ganha um `ShieldComponent`, que concede uma habilidade de escudo temporário. Esta habilidade consome uma barra de energia (mana).

3. **Diálogos e História:**
    - **Lore:** Terminais nos Jardins contêm logs do antigo bibliotecário-chefe. Os textos revelam que um experimento energético deu errado e falam sobre a necessidade de desligar a "Fonte Principal", localizada no Arquivo Subterrâneo.

4. **O Caminho para Baixo:**
    - **Evento:** Após coletar os 3 fragmentos, uma nova memória é revelada: *"...o Arquivo... a fonte precisa ser contida..."*.
    - **Resultado:** Uma passagem secreta para o Arquivo Subterrâneo se abre.

5. **Conclusão do Ato:**
    - **Ação:** O jogador desce para a nova área, concluindo a exploração dos jardins.

---

### **Ato III: O Coração da Biblioteca**

1. **O Arquivo Subterrâneo:**
    - **Local:** Uma área escura e labiríntica. O uso do `LightingManager` é fundamental aqui, com a própria luz do autômato iluminando o caminho.
    - **Desafio:** Inimigos mais fortes e resistentes patrulham os corredores.

2. **A Fonte Principal:**
    - **Local:** O centro do Arquivo.
    - **Evento:** O jogador encontra a fonte de energia sobrecarregada, que está corrompendo a biblioteca e gerando os construtos defeituosos.
    - **Chefe:** Um "Guardião" (inimigo chefe maior e mais resistente) protege a fonte.

3. **Batalha do Chefe:**
    - **Objetivo:** Derrotar o Guardião.
    - **Mecânicas:** O jogador deve usar todas as suas habilidades (pulso de energia e escudo) para vencer. A batalha pode incluir fases ou momentos em que o chefe fica vulnerável após realizar um ataque específico.

4. **A Escolha:**
    - **Evento:** Após a derrota do Guardião, o jogador coleta o último e maior Fragmento de Luz. A memória final é revelada: **seu propósito era conter a sobrecarga**.
    - **Interação:** Um terminal final aparece, apresentando duas opções através do `DialogueChoice`:
        1. **[Reiniciar Sistema]:** Tenta consertar a fonte, com o risco de uma nova sobrecarga no futuro.
        2. **[Desligar Permanentemente]:** Desliga toda a energia da biblioteca para sempre, garantindo a segurança definitiva.

5. **Final:**
    - **Resultado:** Uma pequena cutscene em texto descreve o desfecho com base na escolha do jogador.
        - **Se "Reiniciar":** A biblioteca volta à vida, as luzes se acendem e a natureza floresce em harmonia com a tecnologia.
        - **Se "Desligar":** Todas as luzes se apagam. O autômato fica em silêncio no escuro, com sua missão cumprida e seu propósito finalizado.
    -