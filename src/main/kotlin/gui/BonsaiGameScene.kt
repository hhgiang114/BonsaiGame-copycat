package gui

import entity.*
import service.ConnectionState
import service.RootService
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.container.Area
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.layoutviews.CameraPane
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.*
import util.*


/**
 * The [BonsaiGameScene] is a [BoardGameScene] that displays the whole game and
 * lets the user play the bonsai game
 */
class BonsaiGameScene(private val rootService: RootService, private val bonsaiApplication: BonsaiApplication) :
    BoardGameScene(1920, 1080, ColorVisual(Color(PRIMARY_COLOUR))), Refreshable {

    private val treeTileMaps: MutableList<BidirectionalMap<Tile, HexagonView>> = mutableListOf()
    private val supplyTileMaps: MutableList<BidirectionalMap<Tile, HexagonView>> = mutableListOf()
    private val zenCardMap: BidirectionalMap<Card, CardView> = BidirectionalMap()

    private val playerPanes: MutableList<Pane<ComponentView>> = mutableListOf()
    private val treeHexagonGrids: MutableList<HexagonGrid<HexagonView>> = mutableListOf()
    private val targetLayouts: MutableList<Pane<ComponentView>> = mutableListOf()
    private val treePanes: MutableList<ComponentView> = mutableListOf()
    private val woodSupplyDecks: MutableList<Area<HexagonView>> = mutableListOf()
    private val leafSupplyDecks: MutableList<Area<HexagonView>> = mutableListOf()
    private val flowerSupplyDecks: MutableList<Area<HexagonView>> = mutableListOf()
    private val fruitSupplyDecks: MutableList<Area<HexagonView>> = mutableListOf()
    private val playerButtons: MutableList<Button> = mutableListOf()
    private val goalButtons: MutableList<Button> = mutableListOf()
    private var goalTileList: MutableList<GoalTile> = mutableListOf()

    private val woodTileImageVisual = ImageVisual("wood.png")
    private val leafTileImageVisual = ImageVisual("leaf.png")
    private val flowerTileImageVisual = ImageVisual("flower.png")
    private val fruitTileImageVisual = ImageVisual("fruit.png")
    private val anyTileImageVisual = ImageVisual("any.png")
    private val zenCardsBack = ImageVisual("ZenCards/cardsBack.png")
    private val backgrounds = ImageVisual("Backgrounds/holz.png")


    // button for cultivate
    private val cultivateButton =
        Button(
            posX = 200, posY = 250,
            width = 150,
            height = 50,
            visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            },
            text = "Cultivate",
            font = Font(30, Color(0x000000), "Okashi Regular", fontWeight = Font.FontWeight.NORMAL)
        ).apply {
            onMouseClicked = {
                val game = rootService.currentGame?.currentBonsaiGameState
                checkNotNull(game)
                if (game.currentState == States.START_TURN ||
                    game.currentState == States.CHOOSE_ACTION ||
                    game.currentState == States.REMOVE_TILES
                ) {
                    removeButton.isVisible = false
                    rootService.playerActionService.cultivate()
                }
            }
        }

    // button for meditate
    private val endTurnButton =
        Button(
            posX = 380,
            posY = 250,
            width = 150,
            height = 50,
            visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            },
            text = "EndTurn",
            font = Font(30, Color(0x000000), "Okashi Regular", fontWeight = Font.FontWeight.NORMAL)
        ).apply {
            onMouseClicked = {
                if (rootService.playerActionService.canEndTurn()) {
                    rootService.playerActionService.endTurn()
                }
            }
        }

    // button for remove from tree
    private val removeButton =
        Button(
            posX = 20,
            posY = 250,
            width = 160,
            height = 50,
            visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            },
            text = "Remove",
            font = Font(30, Color(0x000000), "Okashi Regular", fontWeight = Font.FontWeight.NORMAL)
        ).apply {
            onMouseClicked = {
                // Testing to skip a player's turn
                val game = rootService.currentGame?.currentBonsaiGameState
                checkNotNull(game)
                interactionText.text = "Click on tile to remove from the tree 🌴"
                game.currentState = States.REMOVE_TILES
                makeRemoval(game.currentPlayer)
            }
        }


    // text for how many cards there are
    private val cardSumText = Label(
        posX = 82, posY = 200,
        width = 50, height = 40,
        visual = ColorVisual(Color(0, 0, 0, 0)),
        text = "",
        font = Font(40, Color.WHITE, "Okashi Regular", fontWeight = Font.FontWeight.SEMI_BOLD)
    ).apply {
        isFocusable = true
    }

    // pane for the cards
    private val zenCardPane =
        Pane<ComponentView>(
            posX = 20, posY = 20,
            width = 700,
            height = 220,
            visual = ImageVisual("zenBoard.png").apply {
                style.borderRadius = BorderRadius(20.0)
            }
        )

    private val nameText = Label(
        posY = 338, posX = 272,
        width = 134,
        height = 34,
        text = "",
        font = Font(30, Color(0xFFFFFFF), "Okashi Regular")
    )

    // Label for player's tile capacity
    private val capacityLabel = Pane<UIComponent>(
        posY = 170,
        posX = 272,
        width = 134,
        height = 208,
        visual = ImageVisual("Pot/seishi.png")
    )

    private val woodPlayable = HexagonView(
        posX = 285,
        posY = 75,
        size = 30,
        visual = CompoundVisual(
            woodTileImageVisual,
            TextVisual(
                text = "0",
                font = Font(20, Color.WHITE)
            )
        )
    )

    private val anyPlayable = HexagonView(
        posX = 215,
        posY = 75,
        size = 30,
        visual = CompoundVisual(
            anyTileImageVisual,
            TextVisual(
                text = "0",
                font = Font(20, Color.WHITE)
            )
        )
    )

    private val fruitPlayable = HexagonView(
        posX = 320,
        posY = 135,
        size = 30,
        visual = CompoundVisual(
            fruitTileImageVisual,
            TextVisual(
                text = "0",
                font = Font(20, Color.WHITE)
            )
        )
    )

    private val flowerPlayable = HexagonView(
        posX = 250,
        posY = 135,
        size = 30,
        visual = CompoundVisual(
            flowerTileImageVisual,
            TextVisual(
                text = "0",
                font = Font(20, Color.WHITE)
            )
        )
    )

    private val leafPlayable = HexagonView(
        posX = 180,
        posY = 135,
        size = 30,
        visual = CompoundVisual(
            leafTileImageVisual,
            TextVisual(
                text = "0",
                font = Font(20, Color.WHITE)
            )
        )
    )

    //Collected cards
    private val collectedCards = LinearLayout<CardView>(
        posX = 0,
        posY = 0,
        width = 315,
        height = 170,
        spacing = 0
    )

    private val growthCards = LinearLayout<CardView>(
        posX = 393, posY = 170,
        width = 319, height = 170,
        spacing = -89
    )
    private val toolCards = LinearLayout<CardView>(
        posX = 165,
        posY = 170,
        width = 287, height = 170,
        spacing = -154

    )

//    private val masterLabel = Label(
//        posX = 0,
//        posY = 110,
//        width = 200,
//        height = 50,
//        text = "Master Cards: 0",
//        alignment = Alignment.CENTER,
//        font = Font(25, Color.BLACK)
//    ).apply {
//        isWrapText = true
//    }

//    private val collectedCardPane = Pane<ComponentView>(
//        posX = 1154,
//        posY = 20,
//        width = 403,
//        height = 220,
//        visual = ColorVisual(255, 255, 255, 0.5).apply {
//            style.borderRadius = BorderRadius(20.0)
//        }
//    ).apply {
//        this.add(collectedCards)
//        this.add(helperLabel)
//        this.add(masterLabel)
//    }

    // pane for player info
    // tiles capacity
    // play capacity
    private val infoPane =
        Pane<ComponentView>(
            posX = 740,
            posY = 20,
            width = 710,
            height = 420,
            visual = ColorVisual(Color.TRANSPARENT)
        ).apply {
            this.add(capacityLabel)
            this.add(nameText)
//            this.add(anyPlayable)
//            this.add(leafPlayable)
//            this.add(woodPlayable)
//            this.add(flowerPlayable)
//            this.add(fruitPlayable)
            this.add(collectedCards)
            this.add(growthCards)
            this.add(toolCards)
            //this.add(helperLabel)
            //this.add(masterLabel)
        }


    private val interactionText = Label(
        width = 700,
        height = 70,
        text = "",
        font = Font(30, Color(COLOUR_BLACK), "Okashi Italic"),
        alignment = Alignment.CENTER
    )

    // pane for the interaction
    private val interactionPane =
        Pane<ComponentView>(
            posX = 20,
            posY = 310,
            width = 700,
            height = 70,
            visual = ColorVisual(255, 255, 255, 0.5).apply {
                style.borderRadius = BorderRadius(20.0)
            },
        ).apply {
            this.add(interactionText)
        }

    //zenDeck
    private val zenDeckView = CardStack<CardView>(
        posX = 47, posY = 30,
        width = 120, height = 170
    )

    //face-up cards
    private val faceUpCards = LinearLayout<CardView>(
        posX = 195,
        posY = 30,
        width = 510,
        height = 170,
        spacing = 6
    )

    private val undoButton =
        Button(
            posX = -410,
            posY = 0,
            width = 50,
            height = 50,
            visual = ColorVisual(Color(0xffffff)).apply {
                style.borderRadius = BorderRadius(20.0)
            },
            text = "⬅",
            font = Font(36)
        ).apply {
            onMouseClicked = {
                if (rootService.historyService.canUndo()) {
                    rootService.historyService.undo()
                }
            }
            //hide the button in network games
            isVisible = rootService.currentGame?.currentBonsaiGameState?.currentPlayer?.isLocal == true
        }

    /**
     * This just does not work at all, probable because relevant parts of the history are not deep copies
     * ToDo make redo and undo not broken
     */
    private val redoButton =
        Button(
            posX = -70,
            posY = 0,
            width = 50,
            height = 50,
            visual = ColorVisual(Color(0xffffff)).apply {
                style.borderRadius = BorderRadius(20.0)
            },
            text = "➡",
            font = Font(36)
        ).apply {
            onMouseClicked = {
                if (rootService.historyService.canRedo()) {
                    rootService.historyService.redo()
                }
            }
            //hide the button in network games
            isVisible = rootService.currentGame?.currentBonsaiGameState?.currentPlayer?.isLocal == true
        }

    private val saveButton =
        Button(
            posX = -345, // Adjusted to fit within the buttonPane
            posY = 0, // Adjusted to fit within the buttonPane
            width = 260,
            height = 50,
            visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            },
            text = "Save & Quit",
            font = Font(30, Color(0x000000), "Okashi Regular", fontWeight = Font.FontWeight.NORMAL)
        ).apply {
            onMouseClicked = {
                val isLocal = rootService.currentGame?.currentBonsaiGameState?.currentPlayer?.isLocal ?: false
                isVisible = isLocal
                onMouseClicked = {
                    bonsaiApplication.showMainMenuScene()
                    rootService.historyService.saveGame()
                }
            }
        }


    // right side pane
    private val buttonPane = Pane<UIComponent>(
        posX = 1470,
        posY = 20,
        width = 420,
        height = 1040,
        visual = ColorVisual(Color.TRANSPARENT)

    ).apply {
        this.add(undoButton)
        this.add(redoButton)
        this.add(saveButton)
    }

    // Overlay the game scene to prompt the player to choose tile
    private val overlayPane = Pane<ComponentView>(
        posX = 0, posY = 0,
        width = 1920, height = 1080,
    ).apply {
        isVisible = false
    }

    private val overlayPaneDiscard = Pane<ComponentView>(
        posX = 0, posY = 0,
        width = 1920, height = 1080,
    ).apply {
        isVisible = false
    }


    private val claimButton = Button(
        posX = 710,
        posY = 660,
        width = 150,
        height = 80,
        visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
            style.borderRadius = BorderRadius(20.0)
        },
        text = "Claim",
        font = Font(30, Color(0x000000), "Okashi Regular", fontWeight = Font.FontWeight.NORMAL)
    )

    private val renounceButton = Button(
        posX = 1060,
        posY = 660,
        width = 150,
        height = 80,
        visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
            style.borderRadius = BorderRadius(20.0)
        },
        text = "Renounce",
        font = Font(30, Color(0x000000), "Okashi Regular", fontWeight = Font.FontWeight.NORMAL)
    )

    private val goalTileText = Label(
        text = "You can claim this goal 🎉",
        posX = 460,
        posY = 300,
        height = 100, width = 1000,
        font = Font(70, Color(0x000000), "Okashi Regular", fontWeight = Font.FontWeight.BOLD)
    )

    // pane for claim or renounce goal tile
    private val goalTilePane =
        Pane<ComponentView>(
//            posX = 1920,
//            posY = 1080,
            width = 1920,
            height = 1080,
            visual = ColorVisual(Color.WHITE).apply {
                style.borderRadius = BorderRadius(20.0)
                transparency = 0.8
            }
            //0xC4C8AC
        ).apply {
            zIndex = 1
            isVisible = false
            isDisabled = true
            this.add(claimButton)
            this.add(renounceButton)
            this.add(goalTileText)

        }

    private val titleText = Label(
        text = "Choose a tile to claim: ",
        posX = 460,
        posY = 300,
        height = 100, width = 1000,
        font = Font(70, Color(0x000000), "Okashi Regular", fontWeight = Font.FontWeight.BOLD)
    )

    private val woodTile = HexagonView(
        posY = 480,
        posX = 720,
        size = 60,
        visual = woodTileImageVisual
    )

    private val leafTile = HexagonView(
        posY = 480,
        posX = 840,
        size = 60,
        visual = leafTileImageVisual
    )


    private val flowerTile = HexagonView(
        posY = 480,
        posX = 960,
        size = 60,
        visual = flowerTileImageVisual
    )

    private val fruitTile = HexagonView(
        posY = 480,
        posX = 1080,
        size = 60,
        visual = fruitTileImageVisual
    )

    // pane for claim or renounce goal tile
    private val choseAnyTilePane =
        Pane<ComponentView>(
//            posX = 535,
//            posY = 240,
            width = 1920,
            height = 1080,
            visual = ColorVisual(Color.WHITE).apply {
                style.borderRadius = BorderRadius(20.0)
                transparency = 0.8
            }
        ).apply {
            zIndex = 1
            isVisible = false
            isDisabled = true
            this.add(leafTile)
            this.add(woodTile)
            this.add(fruitTile)
            this.add(flowerTile)
            this.add(titleText)
        }


    init {
        background = backgrounds
        addComponents(
            zenCardPane, infoPane, interactionPane,
            removeButton, cultivateButton, endTurnButton,
            zenDeckView, faceUpCards, cardSumText,
            overlayPane, goalTilePane, choseAnyTilePane,
            overlayPaneDiscard,
        )
    }

    override fun refreshAfterRedoOrUndo() {
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState) { "Game state is not initialized." }

        // Refresh player name and supply amount
        nameText.text = "Player: ${gameState.currentPlayer.name}"
        updateSupplyAmount(gameState.currentPlayer)

        // Update player pane visibility
        val currentPlayerIndex = getOrder(gameState.currentPlayer)
        val lastPlayerIndex = when (currentPlayerIndex) {
            0 -> gameState.players.size - 1
            else -> currentPlayerIndex - 1
        }

        // Hide the last player's pane and supply tiles
        playerPanes[lastPlayerIndex].isVisible = false

        // Show the current player's pane and supply tiles
        playerPanes[currentPlayerIndex].isVisible = true

        newZenBoardForRedoOrUndo()

        // Refresh supply tiles
        updateSupply(gameState.currentPlayer)

        // Refresh playable tiles
        updatePlayableTiles(gameState.currentPlayer)

        // Refresh collected cards
        updateCollectedCards(gameState.currentPlayer)
        updateGrowthCards(gameState.currentPlayer)
        updateToolCards(gameState.currentPlayer)

        // Refresh tree tiles
        createEmptyHex(gameState.currentPlayer)

        // Refresh interaction text
        interactionText.text = "Undo/Redo performed. Current player: ${gameState.currentPlayer.name}"

        // Update tile capacity label
        //TODO()
        //capacityLabel.text = "Capacity: ${gameState.currentPlayer.tileCapacity}"

        // Update remove button visibility
        removeButton.isVisible = !rootService.treeService.canPlayWood()
        interactionText.text = if (removeButton.isVisible) {
            "You can Cultivate / Meditate / Remove"
        } else {
            "You can Cultivate / Meditate"
        }

        // Refresh goal tile buttons
        goalButtons.forEachIndexed { index, button ->
            val goalTile = goalTileList[index % goalTileList.size]
            button.text = "Tier: ${goalTile.tier}"
        }

        // Refresh player buttons
        playerButtons.forEachIndexed { index, button ->
            button.text = gameState.players[index].name
        }

        // Hide overlay panes if they are visible
        overlayPane.isVisible = false
        overlayPaneDiscard.isVisible = false
        goalTilePane.isVisible = false
        choseAnyTilePane.isVisible = false

        // Reapply event handlers for face-up cards
        applyCardPosition()

        // Update undo/redo button visibility
        undoButton.isVisible = gameState.currentPlayer.isLocal
        redoButton.isVisible = gameState.currentPlayer.isLocal
        saveButton.isVisible = gameState.currentPlayer.isLocal
    }

    private fun newZenBoardForRedoOrUndo() {
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState) { "Game state is not initialized." }

        // Refresh the zen board
        faceUpCards.clear()
        zenDeckView.clear()
        zenCardMap.clear()

        gameState.faceUpCards.forEach { card ->
            val cardView = CardView(
                height = 170,
                width = 120,
                front = CompoundVisual(ColorVisual.WHITE, TextVisual("${card.id}")),
                back = ColorVisual.BLACK,
            )
            cardFrontSetter(card, cardView)
            cardView.showFront()
            faceUpCards.add(cardView)
            zenCardMap.add(card to cardView)
        }

        gameState.zenDeck.forEach { card ->
            val cardView = CardView(
                height = 170,
                width = 120,
                front = CompoundVisual(ColorVisual.WHITE, TextVisual("${card.id}")),
                back = ColorVisual.BLACK,
            )
            cardFrontSetter(card, cardView)
            cardView.showBack()
            zenDeckView.add(cardView)
            zenCardMap.add(card to cardView)
        }
    }

    //components for the tree boards

    private fun initPot(player: Player) {
        val treeHexagonGrid = treeHexagonGrids[getOrder(player)]

        POT.forEach {
            var color = getColorForPot(player.color)
            val visual: SingleLayerVisual?
            if (it.second == 0) {
                color = ColorVisual.TRANSPARENT
            }
            visual = if (it.first == 0 && it.second == 0) {
                woodTileImageVisual
            } else {
                color
            }
            val hexagon = HexagonView(
                visual = CompoundVisual(
                    visual
                ),
                size = 30
            )
            treeHexagonGrid[it.first, it.second] = hexagon
            if (it.first == 0 && it.second == 0) {
                val root = player.bonsaiTree[0 to 0]
                checkNotNull(root) { "not root" }
                treeTileMaps[getOrder(player)].add(
                    (root to hexagon)
                )
            }
        }
        // get the empty tiles
        if (player.isLocal) {
            createEmptyHex(player)
        }
    }

    private fun initTreePane(player: Player) {
        val playerPane = playerPanes[getOrder(player)]
        val treeHexagonGrid = HexagonGrid<HexagonView>(
            posX = 1000,
            posY = 1000,
            coordinateSystem = HexagonGrid.CoordinateSystem.AXIAL
        )
        treeHexagonGrids.add(treeHexagonGrid)

        val targetLayout = Pane<ComponentView>(
            width = 2000,
            height = 2000,
            posX = 0, posY = 0,
            visual = ColorVisual(Color.WHITE).apply {
                style.borderRadius = BorderRadius(20.0)
                transparency = 0.5
            }
        ).apply {
            this.add(treeHexagonGrid)
        }
        targetLayouts.add(targetLayout)

        //HIHI
        val treePane = CameraPane(
            posX = 140,
            posY = 40,
            width = 1300,
            height = 630,
            target = targetLayout,
            limitBounds = true
        ).apply {
            this.interactive = true
            pan(1000, 1000)
        }
        treePanes.add(treePane)
        playerPane.add(treePane)
    }

    private fun initSupply(player: Player) {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        val playerIndex = getOrder(player)
        val supplyTileMap = supplyTileMaps[playerIndex]
        val woodSupplyDeck = woodSupplyDecks[playerIndex]
        val leafSupplyDeck = leafSupplyDecks[playerIndex]
        val flowerSupplyDeck = flowerSupplyDecks[playerIndex]
        val fruitSupplyDeck = fruitSupplyDecks[playerIndex]

        player.personalSupply.forEachIndexed { index, tile ->
            val supplyHex = HexagonView(
                visual = CompoundVisual(
                    getTileImageVisualForTileType(tile.tileType)
                ),
                size = 60
            ).apply {
                this.isDraggable = true
                this.onDragGestureEnded = { _, success ->
                    if (success) {
                        this.isDraggable = false
                    }
                }
            }

            tile.q = index
            if (supplyTileMap.containsForward(tile)) {
                supplyTileMap.removeForward(tile)
            }
            supplyTileMap.add(tile to supplyHex)

            when (tile.tileType) {
                TileType.WOOD -> woodSupplyDeck.add(supplyHex)
                TileType.LEAF -> leafSupplyDeck.add(supplyHex)
                TileType.FLOWER -> flowerSupplyDeck.add(supplyHex)
                else -> fruitSupplyDeck.add(supplyHex)
            }
        }
        updateSupplyAmount(player)
    }

    private fun initSupplyDecks(player: Player) {
        val playerPane = playerPanes[getOrder(player)]
        val woodSupplyDeck = Area<HexagonView>(
            posX = 30, posY = 120,
            width = 60, height = 60,
            visual = ColorVisual(Color.TRANSPARENT)
        )
        woodSupplyDecks.add(woodSupplyDeck)
        playerPane.add(woodSupplyDeck)
        // leaf supply deck
        val leafSupplyDeck = Area<HexagonView>(
            posX = 30, posY = 250,
            width = 60, height = 60,
            visual = ColorVisual(Color.TRANSPARENT)
        )
        leafSupplyDecks.add(leafSupplyDeck)
        playerPane.add(leafSupplyDeck)
        // flower supply deck
        val flowerSupplyDeck = Area<HexagonView>(
            posX = 30, posY = 380,
            width = 60, height = 60,
            visual = ColorVisual(Color.TRANSPARENT)
        )
        flowerSupplyDecks.add(flowerSupplyDeck)
        playerPane.add(flowerSupplyDeck)
        // fruit supply deck
        val fruitSupplyDeck = Area<HexagonView>(
            posX = 30, posY = 510,
            width = 60, height = 60,
            visual = ColorVisual(Color.TRANSPARENT)
        )
        fruitSupplyDecks.add(fruitSupplyDeck)
        playerPane.add(fruitSupplyDeck)
    }

    /**
     *
     */
    private fun updateSupply(player: Player) {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        val supplyTileMap = supplyTileMaps[getOrder(player)]
        val woodSupplyDeck = woodSupplyDecks[getOrder(player)]
        val leafSupplyDeck = leafSupplyDecks[getOrder(player)]
        val flowerSupplyDeck = flowerSupplyDecks[getOrder(player)]
        val fruitSupplyDeck = fruitSupplyDecks[getOrder(player)]
        supplyTileMap.clear()
        woodSupplyDeck.clear()
        leafSupplyDeck.clear()
        flowerSupplyDeck.clear()
        fruitSupplyDeck.clear()

        player.personalSupply.forEachIndexed { index, tile ->
            val supplyHex = HexagonView(
                visual = CompoundVisual(
                    getTileImageVisualForTileType(tile.tileType)
                ),
                size = 50
            ).apply {
                this.isDraggable = false
                this.onDragGestureEnded = { _, success ->
                    if (success) {
                        this.isDraggable = false
                    }
                }
            }

            tile.q = index
            supplyTileMap.add(tile to supplyHex)

            when (tile.tileType) {
                TileType.WOOD -> woodSupplyDeck.add(supplyHex)
                TileType.LEAF -> leafSupplyDeck.add(supplyHex)
                TileType.FLOWER -> flowerSupplyDeck.add(supplyHex)
                else -> fruitSupplyDeck.add(supplyHex)
            }
        }
        updateSupplyAmount(player)
    }

    private fun initZenBoard() {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        game.zenDeck.forEach {
            val cardView = CardView(
                height = 170,
                width = 120,
                front = CompoundVisual(ColorVisual.TRANSPARENT),
                back = zenCardsBack,

                )
            cardFrontSetter(it, cardView)
            cardView.showBack()
            zenDeckView.add(cardView)
            zenCardMap.add(it to cardView)
        }
        cardSumText.text = "${game.zenDeck.size}"

        game.faceUpCards.forEach {
            val cardView = CardView(
                height = 170,
                width = 120,
                front = CompoundVisual(ColorVisual.TRANSPARENT),
                back = ColorVisual.TRANSPARENT,
            )
            cardFrontSetter(it, cardView)
            cardView.showFront()
            faceUpCards.add(cardView)
            zenCardMap.add(it to cardView)
        }
        applyCardPosition()
    }

    private fun updateZenBoard() {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        if (game.zenDeck.isNotEmpty()) {
            val newCardView = zenDeckView.pop()
            newCardView.showFront()
            val oldCardViews = faceUpCards.components.toMutableList()
            faceUpCards.clear()
            faceUpCards.add(newCardView)
            faceUpCards.addAll(oldCardViews)
            applyCardPosition()
            cardSumText.text = "${game.zenDeck.size}"
        } else {
            val oldCardViews = faceUpCards.components.toMutableList()
            faceUpCards.clear()
            faceUpCards.addAll(oldCardViews)
            applyCardPosition()
            cardSumText.text = "${game.zenDeck.size}"
        }
    }

    private fun createRightSidePane() {
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState) { "Game state is not initialized." }

        // sorted goal tiles in order: brown, green, orange, pink, blue
        val sortedGoalTiles = gameState.goalTiles.sortedBy {
            when (it.goalTileType) {
                GoalTileType.BROWN -> 0
                GoalTileType.GREEN -> 1
                GoalTileType.ORANGE -> 2
                GoalTileType.PINK -> 3
                GoalTileType.BLUE -> 4
            }
        }
        goalTileList = sortedGoalTiles.toMutableList()


        createPlayerButtons()

        var currentY = 0

        // need 6 goal tiles for 2 players and 9 for 3
        val numberOfButtons = if (gameState.players.size == 2) 6 else 9

        // create goal tile buttons in correct order
        for (i in 0 until numberOfButtons) {
            val goalTile = sortedGoalTiles[i % sortedGoalTiles.size]
            val goalButton = Button(
                posX = 93,
                posY = currentY,
                width = 234,
                height = 110,
//                width = ImageVisual("GoalTiles/${goalTile.goalTileType}_${goalTile.tier}.png").width,
//                height = ImageVisual("GoalTiles/${goalTile.goalTileType}_${goalTile.tier}.png").height,
                visual = ImageVisual("GoalTiles/${goalTile.goalTileType}_${goalTile.tier}.png").apply {
                    style.borderRadius = BorderRadius(10.0)
                },
                //text = "Tier: ${goalTile.tier} Score: ${goalTile.score}",
                font = Font(40, Color(0xFFFFFF), "Okashi Regular", fontWeight = Font.FontWeight.NORMAL)
            )

            goalButtons.add(goalButton)
            buttonPane.add(goalButton)
            currentY += 110 + 5
        }

        // add panel to the scene
        removeComponents(buttonPane)
        addComponents(buttonPane)
    }

    private fun createPlayerButtons() {
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState)

        // number of necessary player buttons
        val numberOfPlayerButtons = gameState.players.size

        // created player buttons
        for (i in 0 until numberOfPlayerButtons) {
            var visual = ColorVisual(Color(0xffffff)).apply {
                style.borderRadius = BorderRadius(20.0)
            }
            var font = Font(15)
            if (gameState.players[i].isLocal) {
                visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
                    style.borderRadius = BorderRadius(10.0)
                }
                font = Font(30, Color.WHITE, "Okashi italic")
            }
            // current player needs no buttons
            val player = gameState.players[i]
            val playerButton = Button(
                posX = -410 + (90 + 10) * i,
                posY = 80,
                width = 90,
                height = 40,
                visual = visual,
                text = player.name,
                font = font
            ).apply {
                playerButtons.add(this)
                onMouseClicked = {
                    playerPanes.forEachIndexed { index, playerPane ->
                        if (index == i) {
                            playerPane.isVisible = true
                        } else {
                            playerPane.isVisible = false
                        }
                    }
                    // show player's playable tiles
                    updatePlayableTiles(player)

                    //update player's collected cards
                    updateCollectedCards(player)
                    updateGrowthCards(player)

                    //update player's name
                    nameText.text = gameState.players[i].name

                    // update player's capacity
                    updateToolCards(player)
                }
            }
            buttonPane.add(playerButton)
        }
    }

    // Remove tiles from tree
    private fun makeRemoval(player: Player) {
        val treeTileMap = treeTileMaps[getOrder(player)]
        val treePlayer = treeHexagonGrids[getOrder(player)]

        treePlayer.components.forEach { hexagonView ->
            hexagonView.onMouseClicked = {
                if (!rootService.treeService.canPlayWood()) {
                    val tile = treeTileMap.backward(hexagonView)

                    val q = tile.q ?: throw IllegalStateException("Tile q coordinate is null.")
                    val r = tile.r ?: throw IllegalStateException("Tile r coordinate is null.")

                    if (rootService.treeService.isMinimalAndCorrect(q to r)) {
                        rootService.treeService.removeFromTree(q to r)
                        interactionText.text = "Tile removed successfully"

                        // Check if more removals are needed
                        checkCanPlayWood()

                        hexagonView.removeFromParent()
                        createEmptyHex(player)
                        treeTileMap.removeForward(tile) // Remove the tile from the tile map
                        treePlayer.isVisible = true
                        playerPanes[getOrder(player)].isVisible = true
                    }
                }
            }

        }
    }

    private fun checkCanPlayWood() {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        if (rootService.treeService.canPlayWood() &&
            game.currentPlayer.isLocal
        ) {
            removeButton.isVisible = false
            interactionText.text = "You can now Cultivate or Meditate"
        }
    }

    //refresher do something
    override fun refreshAfterGameStart() {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)

        // update tile capacity
        //capacityLabel.text = "Capacity: ${game.currentPlayer.tileCapacity}"

        //invisible buttons in network game
        val isLocalPlayer = game.currentPlayer.isLocal
        undoButton.isVisible = isLocalPlayer
        redoButton.isVisible = isLocalPlayer
        saveButton.isVisible = isLocalPlayer

        initZenBoard()
        game.players.forEach {
            createPlayerPane(it)
            treeTileMaps.add(BidirectionalMap())
            supplyTileMaps.add(BidirectionalMap())
            initTreePane(it)
            initPot(it)
            initSupplyDecks(it)
            initSupply(it)
        }
        createRightSidePane()



        nameText.text = "Player: " + game.players[0].name
        updateSupply(game.players[0])
        updatePlayableTiles(game.currentPlayer)
        if (!rootService.treeService.canPlayWood()) {
            removeButton.isVisible = true
            interactionText.text = "You need to remove tiles on your tree 🌴"
        } else {
            removeButton.isVisible = false
            if (game.currentPlayer.isLocal) {
                interactionText.text = "You can now Cultivate or Meditate"
            }
        }

        // update playable tiles
        updatePlayableTiles(game.currentPlayer)

        // update collected cards
        updateCollectedCards(game.currentPlayer)
        updateGrowthCards(game.currentPlayer)
        updateToolCards(game.currentPlayer)

        // update tree for real this time bro
        drawTreeFRFR()
        updateAllSuspiciously()

        val currentPlayerType = rootService.currentGame?.currentBonsaiGameState?.currentPlayer?.playerType
        checkNotNull(currentPlayerType)
        if (currentPlayerType != PlayerType.HUMAN) {
            rootService.botService.makeRandomMove()
        }
    }

    override fun refreshAfterApplyCardEffects(position: Int?) {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        val actPlayer = game.currentPlayer
        if (actPlayer.personalSupply.size > actPlayer.tileCapacity &&
            game.currentPlayer.isLocal
        ) {
            game.currentState = States.DISCARDING
            refreshAfterReceivedTile(true)
            return
        } else {
            actPlayer.hasPlayed = true
            if (game.currentState != States.USING_HELPER) {
                updateSupply(game.currentPlayer)
                updatePlayableTiles(game.currentPlayer)
                updateCollectedCards(game.currentPlayer)
                updateGrowthCards(game.currentPlayer)
                updateToolCards(game.currentPlayer)
            }

            // update tile capacity
            //capacityLabel.text = "Capacity: ${game.currentPlayer.tileCapacity}"

            // update playable tiles
            updatePlayableTiles(game.currentPlayer)

            // update collected cards
            updateCollectedCards(game.currentPlayer)
            updateGrowthCards(game.currentPlayer)
            updateToolCards(game.currentPlayer)

            if (rootService.networkService.connectionState != ConnectionState.DISCONNECTED &&
                !game.currentPlayer.isLocal
            ) {
                checkNotNull(position)
                faceUpCards.components[position].removeFromParent()
                updateZenBoard()
            }
        }
    }

    override fun refreshAfterReceivedTile(discard: Boolean) {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)

        if (discard) {
            interactionText.text = "Choose tiles to remove until you're within capacity."


            playerPanes[getOrder(game.currentPlayer)].isVisible = false
            overlayPaneDiscard.clear()
            overlayPaneDiscard.isVisible = true

            val tilesToRemove = mutableListOf<Tile>()

            game.currentPlayer.personalSupply.forEachIndexed { index, tile ->
                val hexagonView = HexagonView(
                    posX = 585 + (index % 4) * 200,
                    posY = 500 + (index / 4) * 200,
                    visual = CompoundVisual(
                        getTileImageVisualForTileType(tile.tileType)
                    ),
                    size = 60,
                ).apply {
                    onMouseClicked = {
                        if (tilesToRemove.contains(tile)) {
                            tilesToRemove.remove(tile)
                            this.visual = CompoundVisual(getTileImageVisualForTileType(tile.tileType))
                        } else {
                            tilesToRemove.add(tile)
                            this.visual = CompoundVisual(ColorVisual(Color.TRANSPARENT))

                        }

                        if (game.currentPlayer.personalSupply.size - tilesToRemove.size
                            <= game.currentPlayer.tileCapacity
                        ) {
                            tilesToRemove.forEach { rootService.playerActionService.discardSupplyTile(it) }
                            updateSupply(game.currentPlayer)
                            updatePlayableTiles(game.currentPlayer)
                            overlayPaneDiscard.isVisible = false
                            playerPanes[getOrder(game.currentPlayer)].isVisible = true
                            interactionText.text = "Tiles removed. You can end turn!"

                        }
                    }
                }
                overlayPaneDiscard.add(hexagonView)
            }
        }

        // update tile capacity
        //capacityLabel.text = "Capacity: ${game.currentPlayer.tileCapacity}"

        // update playable tiles
        updatePlayableTiles(game.currentPlayer)

        // update collected cards
        updateCollectedCards(game.currentPlayer)
        updateGrowthCards(game.currentPlayer)
        updateToolCards(game.currentPlayer)
    }


    override fun refreshAfterDrawingHelperCard(playableTilesCopy: MutableList<TileType>) {

        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        val playerIndex = getOrder(game.currentPlayer)
        val supplyTileMap = supplyTileMaps[playerIndex]
        updateSupply(game.currentPlayer)
        updatePlayableTiles(game.currentPlayer)
        game.currentPlayer.playableTilesCopy.addAll(playableTilesCopy)
        // make the supply tiles draggable after cultivate start
        game.players[playerIndex].personalSupply.forEach { supplyTile ->
            supplyTileMap[supplyTile].isDraggable = true

        }
        interactionText.text = "You can place your tiles or end turn"

        // update tile capacity
        //capacityLabel.text = "Capacity: ${game.currentPlayer.tileCapacity}"

        // update collected cards
        updateCollectedCards(game.currentPlayer)
        updateGrowthCards(game.currentPlayer)
        updateToolCards(game.currentPlayer)

        val confirmButton = Button(
            posX = 540,
            posY = -60,
            width = 150,
            height = 50,
            visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            },
            font = Font(30, Color(0x000000), "Okashi Regular", fontWeight = Font.FontWeight.NORMAL),
            text = "Finished"
        ).apply {
            onMouseClicked = {
                this.isVisible = false
                rootService.playerActionService.checkSupply()
                game.players[playerIndex].personalSupply.forEach { supplyTile ->
                    supplyTileMap[supplyTile].apply {
                        isDraggable = false
                        game.currentPlayer.hasPlayed = true
                    }
                }
            }
        }
        // Button in infoPane hinzufügen
        interactionPane.add(confirmButton)
    }

    override fun refreshAfterDrawingMasterCardAny() {

        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)

        choseAnyTilePane.isVisible = true

        leafTile.onMouseClicked = {
            rootService.playerActionService.chooseTile(TileType.LEAF)
            updateSupply(game.currentPlayer)
            updatePlayableTiles(game.currentPlayer)
            interactionText.text = "You have received a leaf tile 🌿"
            game.currentPlayer.hasPlayed = true
            choseAnyTilePane.isVisible = false
        }
        woodTile.onMouseClicked = {
            rootService.playerActionService.chooseTile(TileType.WOOD)
            updateSupply(game.currentPlayer)
            updatePlayableTiles(game.currentPlayer)
            choseAnyTilePane.isVisible = false
            interactionText.text = "You have received a wood tile 🪵"
            game.currentPlayer.hasPlayed = true
        }
        fruitTile.onMouseClicked = {
            rootService.playerActionService.chooseTile(TileType.FRUIT)
            updateSupply(game.currentPlayer)
            updatePlayableTiles(game.currentPlayer)
            choseAnyTilePane.isVisible = false
            interactionText.text = "You have received a fruit tile 🍊"
            game.currentPlayer.hasPlayed = true
        }
        flowerTile.onMouseClicked = {
            rootService.playerActionService.chooseTile(TileType.FLOWER)
            updateSupply(game.currentPlayer)
            updatePlayableTiles(game.currentPlayer)
            choseAnyTilePane.isVisible = false
            interactionText.text = "You have received a flower tile 🌸"
            game.currentPlayer.hasPlayed = true
        }


        // update tile capacity
        //capacityLabel.text = "Capacity: ${game.currentPlayer.tileCapacity}"

        // update collected cards
        updateCollectedCards(game.currentPlayer)
        updateGrowthCards(game.currentPlayer)
        updateToolCards(game.currentPlayer)
    }

    override fun refreshAfterCultivateStart() {
        // refresh information telling the player to pick a tile
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        val playerIndex = getOrder(game.currentPlayer)
        val supplyTileMap = supplyTileMaps[playerIndex]

        // make the supply tiles draggable after cultivate start
        game.players[playerIndex].personalSupply.forEach { supplyTile ->
            supplyTileMap[supplyTile].isDraggable = true
        }
        interactionText.text = "You may now place your tiles or end turn"

        // update tile capacity
        // capacityLabel.text = "Capacity: ${game.currentPlayer.tileCapacity}"

        //update playable tiles
        updatePlayableTiles(game.currentPlayer)

        updateGrowthCards(game.currentPlayer)
        updateCollectedCards(game.currentPlayer)
        updateToolCards(game.currentPlayer)
    }

    override fun refreshAfterPlayTile(goalTileType: GoalTileType?, tier: Int, tilePosition: Pair<Int, Int>) {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)

        if (rootService.networkService.connectionState != ConnectionState.DISCONNECTED &&
            !game.currentPlayer.isLocal
        ) {
            drawTree(game.currentPlayer, tilePosition)
        } else {
            // update playable tiles
            updatePlayableTiles(game.currentPlayer)
        }

        // update tile capacity
        //capacityLabel.text = "Capacity: ${game.currentPlayer.tileCapacity}"


        if (goalTileType != null) {
            updateGoals(goalTileType, tier)
        }

        updateCollectedCards(game.currentPlayer)
        updateGrowthCards(game.currentPlayer)
        updateToolCards(game.currentPlayer)
    }

    /**
     * it's only for non-local player
     */
    override fun refreshAfterClaimGoal(goalTileType: GoalTileType, tier: Int) {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
//        var goalTileScore = 0
//
//        for (goalTile in goalTileList) {
//            if (goalTile.goalTileType == goalTileType && goalTile.tier == tier) {
//                goalTileScore = goalTile.score
//            }
//        }

        goalTileList.forEach {
            if (goalTileType == it.goalTileType && tier == it.tier) {
                goalButtons[goalTileList.indexOf(it)].text =
                    game.currentPlayer.name

//                goalButtons[goalTileList.indexOf(it)].visual.apply {
//                    opacity = 0.7
//                }
            }
        }
    }

    override fun refreshAfterDiscardTile() {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        if (game.currentState != States.USING_HELPER) {
            updateSupply(game.currentPlayer)
            updatePlayableTiles(game.currentPlayer)
        }

        // update tile capacity
        //  capacityLabel.text = "Capacity: ${game.currentPlayer.tileCapacity}"

        // update playable tiles
        updatePlayableTiles(game.currentPlayer)
    }

    override fun refreshAfterEndTurn() {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        val currentPlayerIndex = getOrder(game.currentPlayer)
        val lastPlayerIndex = when (currentPlayerIndex) {
            0 -> game.players.size - 1
            else -> currentPlayerIndex - 1
        }
        nameText.text = game.currentPlayer.name
        updateSupply(game.currentPlayer)
        updatePlayableTiles(game.currentPlayer)
        playerPanes[currentPlayerIndex].isVisible = true

        playerPanes[lastPlayerIndex].isVisible = false


        //updateSupply(game.currentPlayer)
        updatePlayableTiles(game.currentPlayer)

        if (!rootService.treeService.canPlayWood()) {
            removeButton.isVisible = true
            if (game.currentPlayer.isLocal) {
                interactionText.text = "You can Cultivate/ Meditate/ Remove"
            }
        } else {
            removeButton.isVisible = false
            if (game.currentPlayer.isLocal) {
                interactionText.text = "You can Cultivate/ Meditate"
            }
        }

        // update tile capacity
        // capacityLabel.text = "Capacity: ${game.currentPlayer.tileCapacity}"

        // update playable tiles
        updatePlayableTiles(game.currentPlayer)

        // update collected cards
        updateCollectedCards(game.currentPlayer)
        updateGrowthCards(game.currentPlayer)
        updateToolCards(game.currentPlayer)

        val currentPlayerType = rootService.currentGame?.currentBonsaiGameState?.currentPlayer?.playerType
        checkNotNull(currentPlayerType)
        if (currentPlayerType != PlayerType.HUMAN) {
            rootService.botService.makeRandomMove()
        }


        if (!game.currentPlayer.isLocal) {
            faceUpCards.forEach {
                it.onMouseClicked = null
            }
            cultivateButton.isVisible = false
            endTurnButton.isVisible = false
            interactionText.text = "It's ${game.currentPlayer.name}'s turn"
            updateSupply(game.players[lastPlayerIndex])
        } else {
            cultivateButton.isVisible = true
            endTurnButton.isVisible = true
            applyCardPosition()
        }
    }


    private fun cardFrontSetter(generalCard: Card, cardView: CardView) {
        when (generalCard.cardType) {
            CardType.TOOLCARD -> setToolCardFront(generalCard as ToolCard, cardView)
            CardType.GROWTHCARD -> setGrowthCardFront(generalCard as GrowthCard, cardView)
            CardType.HELPERCARD -> setHelperCardFront(generalCard as HelperCard, cardView)
            CardType.MASTERCARD -> setMasterCardFront(generalCard as MasterCard, cardView)
            CardType.PARCHMENTCARD -> setParchmentCardFront(generalCard as ParchmentCard, cardView)
        }
    }

    private fun setToolCardFront(card: ToolCard, cardView: CardView) {
        cardView.frontVisual = CompoundVisual(
            ImageVisual("ZenCards/${card.id}.png").apply {
                style.borderRadius = BorderRadius(10.0)
            }
        )
    }

    private fun setGrowthCardFront(card: GrowthCard, cardView: CardView) {
        cardView.frontVisual = CompoundVisual(
            ImageVisual("ZenCards/${card.id}.png").apply {
                style.borderRadius = BorderRadius(10.0)
            }
        )
    }

    private fun setHelperCardFront(card: HelperCard, cardView: CardView) {
        cardView.frontVisual = CompoundVisual(
            ImageVisual("ZenCards/${card.id}.png").apply {
                style.borderRadius = BorderRadius(10.0)
            }
        )
    }

    private fun setMasterCardFront(card: MasterCard, cardView: CardView) {
        cardView.frontVisual = when (card.tileTypes.size) {
            3 -> CompoundVisual(
                ImageVisual("ZenCards/${card.id}.png").apply {
                    style.borderRadius = BorderRadius(10.0)
                }
            )

            2 -> CompoundVisual(
                ImageVisual("ZenCards/${card.id}.png").apply {
                    style.borderRadius = BorderRadius(10.0)
                }
            )

            else -> CompoundVisual(
                ImageVisual("ZenCards/${card.id}.png").apply {
                    style.borderRadius = BorderRadius(10.0)
                }
            )
        }
    }

    private fun setParchmentCardFront(card: ParchmentCard, cardView: CardView) {
        cardView.frontVisual = if (card.parchmentCardType != null) {
            CompoundVisual(
                ImageVisual("ZenCards/${card.id}.png").apply {
                    style.borderRadius = BorderRadius(10.0)
                }
            )
        } else {
            CompoundVisual(
                ImageVisual("ZenCards/${card.id}.png").apply {
                    style.borderRadius = BorderRadius(10.0)
                }
            )
        }
    }

    private fun applyCardPosition() {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        faceUpCards.forEachIndexed { index, card ->
            card.onMouseClicked = {
                if (canClickCard()) {
                    handleCardClick(index, card, game)
                }
            }
        }
    }

    private fun handleCardClick(index: Int, card: CardView, game: BonsaiGameState) {
        removeButton.isVisible = false
        card.removeFromParent()

        when (index) {
            0 -> {
                interactionText.text = "No extra tiles!"
                rootService.playerActionService.meditate(0, null)
            }

            1 -> {
                interactionText.text = "Choose tile to claim: "
                overlayPane.clear()
                overlayPane.isVisible = true
                showChooseWoodOrLeafPane()
            }

            2 -> {
                interactionText.text = "You have received a wood 🪵 and flower 🌸 tile"
                rootService.playerActionService.meditate(2, null)
            }

            else -> {
                interactionText.text = "You have received a leaf 🌿 and fruit 🍊 tile"
                rootService.playerActionService.meditate(3, null)
            }
        }

        if (game.currentState != States.USING_HELPER) {
            updateSupply(game.currentPlayer)
            updatePlayableTiles(game.currentPlayer)
        }
        updateZenBoard()
    }

    private fun showChooseWoodOrLeafPane() {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)

        overlayPane.add(
            HexagonView(
                posX = 600, posY = 315,
                size = 30,
                visual = leafTileImageVisual
            )
                .apply {
                    onMouseClicked = {
                        rootService.playerActionService.meditate(1, TileType.LEAF)
                        if (game.currentState != States.USING_HELPER) {
                            updateSupply(game.currentPlayer)
                            updatePlayableTiles(game.currentPlayer)
                        }
                        overlayPane.isVisible = false
                        interactionText.text = "You have received a leaf tile 🌿 "

                    }
                })
        overlayPane.add(
            HexagonView(
                posX = 530, posY = 315,
                size = 30,
                visual = woodTileImageVisual
            ).apply {
                onMouseClicked = {
                    rootService.playerActionService.meditate(1, TileType.WOOD)
                    if (game.currentState != States.USING_HELPER) {
                        updateSupply(game.currentPlayer)
                        updatePlayableTiles(game.currentPlayer)
                    }
                    overlayPane.isVisible = false
                    interactionText.text = "You have received a wood tile 🪵"
                }
            })
    }

    private fun canClickCard(): Boolean {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        return game.currentState == States.START_TURN ||
                game.currentState == States.CHOOSE_ACTION ||
                game.currentState == States.REMOVE_TILES
    }

    /**
     * really important function for play tile
     */
    private fun createEmptyHex(player: Player) {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        val treeTileMap = treeTileMaps[getOrder(player)]
        val treeHexagonGrid = treeHexagonGrids[getOrder(player)]

        player.bonsaiTree.getEmptyTiles().forEach {
            val hexagon = HexagonView(
                visual = CompoundVisual(
                    ColorVisual(255, 255, 255, 0.3),
                    TextVisual(
                        text = "",
                        font = Font(10.0, Color(0x000000))
                    )
                ),
                size = 30
            ).apply {
                val emptyTile = Tile(it.first, it.second, TileType.EMPTY)
                if (treeTileMap.contains(emptyTile to this)) {
                    treeTileMap.remove(emptyTile to this)
                    treeTileMap.add(emptyTile to this)
                } else {
                    treeTileMap.add(emptyTile to this)
                }
                setEmptyHexDraggedCondition(this, it, player)
                setEmptyHexDroppedEvent(this, it, emptyTile, player)
            }
            treeHexagonGrid[it.first, it.second] = hexagon
        }
    }

    private fun setEmptyHexDraggedCondition(emptyHex: HexagonView, position: Pair<Int, Int>, player: Player) {
        val supplyTileMap = supplyTileMaps[getOrder(player)]
        emptyHex.dropAcceptor = { dragEvent ->
            when (dragEvent.draggedComponent) {
                is HexagonView -> {
                    // If the card is valid, the card can be dropped and played
                    // some condition
                    val comp = dragEvent.draggedComponent as HexagonView
                    val tile = supplyTileMap.backward(comp)
                    rootService.treeService.canPlayTile(tile, position)
                }

                else -> false
            }
        }
    }

    private fun setEmptyHexDroppedEvent(
        emptyHex: HexagonView,
        position: Pair<Int, Int>,
        emptyTile: Tile,
        player: Player
    ) {
        val supplyTileMap = supplyTileMaps[getOrder(player)]
        val treeTileMap = treeTileMaps[getOrder(player)]
        emptyHex.onDragDropped = { dragEvent ->
            val comp = dragEvent.draggedComponent as HexagonView
            val tile = supplyTileMap.backward(comp)
            tile.q = position.first
            tile.r = position.second
            treeTileMap.remove(emptyTile to emptyHex)
            treeTileMap.add(tile to emptyHex)
            emptyHex.visual = CompoundVisual(
                getTileImageVisualForTileType(tile.tileType),
            )
            rootService.treeService.playTile(tile, position)
            val parentDeck = comp.parent //as Area<HexagonView>
            check(parentDeck is Area<*>)

            comp.removeFromParent()
            if (parentDeck.components.isNotEmpty()) {
                val lastHex = parentDeck.last()
                check(lastHex is HexagonView)
                val supplyTileType = supplyTileMap.backward(lastHex).tileType
                parentDeck.last().visual = CompoundVisual(
                    getTileImageVisualForTileType(supplyTileType),
                    TextVisual(
                        "${parentDeck.components.size}",
                        font = Font(30.0, Color(0xFFFFFF), "Okashi Regular")
                    )
                )
            }
            createEmptyHex(player)
        }
    }

    private fun updateSupplyAmount(player: Player) {
        if (woodSupplyDecks[getOrder(player)].components.isNotEmpty()) {
            woodSupplyDecks[getOrder(player)].components.last().visual = CompoundVisual(
                woodTileImageVisual,
                TextVisual(
                    "${woodSupplyDecks[getOrder(player)].components.size}",
                    font = Font(30.0, Color(0xFFFFFF), "Okashi Regular")
                )
            )
        }

        if (leafSupplyDecks[getOrder(player)].components.isNotEmpty()) {
            leafSupplyDecks[getOrder(player)].components.last().visual = CompoundVisual(
                leafTileImageVisual,
                TextVisual(
                    "${leafSupplyDecks[getOrder(player)].components.size}",
                    font = Font(30.0, Color(0xFFFFFF), "Okashi Regular")
                )
            )
        }

        if (flowerSupplyDecks[getOrder(player)].components.isNotEmpty()) {
            flowerSupplyDecks[getOrder(player)].components.last().visual = CompoundVisual(
                flowerTileImageVisual,
                TextVisual(
                    "${flowerSupplyDecks[getOrder(player)].components.size}",
                    font = Font(30.0, Color(0xFFFFFF), "Okashi Regular")
                )
            )
        }

        if (fruitSupplyDecks[getOrder(player)].components.isNotEmpty()) {
            fruitSupplyDecks[getOrder(player)].components.last().visual = CompoundVisual(
                fruitTileImageVisual,
                TextVisual(
                    "${fruitSupplyDecks[getOrder(player)].components.size}",
                    font = Font(30.0, Color(0xFFFFFF), "Okashi Regular")
                )
            )
        }
    }

    /**
     * this function is used only for online game non-local player
     */
    private fun drawTree(player: Player, tilePosition: Pair<Int, Int>) {
        val playerIndex = getOrder(player)
        val treeTileMap = treeTileMaps[playerIndex]
        val treeHexagonGrid = treeHexagonGrids[playerIndex]
        val q = tilePosition.first
        val r = tilePosition.second
        val playedTile = player.bonsaiTree[q to r]
        checkNotNull(playedTile) { "the tile does not exist" }
        playedTile.q = q
        playedTile.r = r

        val coloredHex = HexagonView(
            visual = CompoundVisual(
                getTileImageVisualForTileType(playedTile.tileType),
                TextVisual(
                    text = "",
                    font = Font(10.0, Color(0xFFFFFFF))
                )
            ),
            size = 30
        )
        treeTileMap.add(playedTile to coloredHex)
        treeHexagonGrid[q, r] = coloredHex
    }

    private fun updateGoals(goalTileType: GoalTileType, tier: Int) {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
//        var goalTileScore = 0
//
//        for (goalTile in goalTileList) {
//            if (goalTile.goalTileType == goalTileType && goalTile.tier == tier) {
//                goalTileScore = goalTile.score
//            }
//        }

        //addComponents(goalTilePane)
        goalTilePane.add(
            Label(
                posX = 784,
                posY = 450,
                width = 351,
                height = 165,
                visual = ImageVisual("GoalTiles/${goalTileType}_${tier}.png"),
            )

        )
        if (game.currentPlayer.isLocal) {
            goalTilePane.isVisible = true
            goalTilePane.isDisabled = false
        }
        claimButton.onMouseClicked = {
            rootService.playerActionService.claimOrRenounceGoal(true, goalTileType, tier)
            goalTilePane.isDisabled = true
            goalTilePane.isVisible = false

            goalTileList.forEach {
                if (goalTileType == it.goalTileType && tier == it.tier) {
                    goalButtons[goalTileList.indexOf(it)].text =
                        game.currentPlayer.name
                }
            }
        }
        renounceButton.onMouseClicked = {
            rootService.playerActionService.claimOrRenounceGoal(false, goalTileType, tier)
            goalTilePane.isDisabled = true
            goalTilePane.isVisible = false
        }
    }

//    private fun getColorForGoalTile(goalTileType: GoalTileType): Int {
//        return when (goalTileType) {
//            GoalTileType.BROWN -> COLOUR_WOOD
//            GoalTileType.GREEN -> COLOUR_LEAF
//            GoalTileType.ORANGE -> COLOUR_FRUIT
//            GoalTileType.PINK -> COLOUR_FLOWER
//            GoalTileType.BLUE -> COLOUR_BLUE
//        }
//    }

    private fun getTileImageVisualForTileType(tileType: TileType): ImageVisual {
        return when (tileType) {
            TileType.WOOD -> woodTileImageVisual
            TileType.LEAF -> leafTileImageVisual
            TileType.FLOWER -> flowerTileImageVisual
            else -> fruitTileImageVisual
        }
    }

//    private fun getColorForPot(color: ColorType): Int {
//        return when (color) {
//            ColorType.RED -> COLOUR_RED
//            ColorType.PURPLE -> COLOUR_PURPLE
//            ColorType.BLACK -> COLOUR_BLACK
//            ColorType.BLUE -> COLOUR_BLUE
//        }
//    }

    private fun getColorForPot(color: ColorType): ColorVisual {
        return when (color) {
            ColorType.RED -> ColorVisual(Color(COLOUR_RED))
            ColorType.PURPLE -> ColorVisual(Color(COLOUR_PURPLE))
            ColorType.BLACK -> ColorVisual(Color(COLOUR_BLACK))
            ColorType.BLUE -> ColorVisual(Color(COLOUR_BLUE))
        }
    }

    private fun getOrder(player: Player): Int {
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        game.players.forEachIndexed { index, playerInList ->
            if (player == playerInList) {
                return index
            }
        }
        return -1
    }

//Don't really need
//    private fun updateCapacity(player: Player) {
//        capacityLabel.text = "Capacity: ${player.tileCapacity}"
//    }

    private fun updatePlayableTiles(player: Player) {
        val tileCounts = player.playableTiles.groupingBy { it }.eachCount()
        val woodCount = tileCounts[TileType.WOOD] ?: 0
        val leafCount = tileCounts[TileType.LEAF] ?: 0
        val fruitCount = tileCounts[TileType.FRUIT] ?: 0
        val flowerCount = tileCounts[TileType.FLOWER] ?: 0
        val anyCount = tileCounts[TileType.ANY] ?: 0

        woodPlayable.visual = CompoundVisual(
            woodTileImageVisual,
            TextVisual(
                text = "$woodCount",
                font = Font(20, Color.WHITE)
            )
        )

        anyPlayable.visual = CompoundVisual(
            anyTileImageVisual,
            TextVisual(
                text = "$anyCount",
                font = Font(20, Color.WHITE)
            )
        )

        leafPlayable.visual = CompoundVisual(
            leafTileImageVisual,
            TextVisual(
                text = "$leafCount",
                font = Font(20, Color.WHITE)
            )
        )

        flowerPlayable.visual = CompoundVisual(
            flowerTileImageVisual,
            TextVisual(
                text = "$flowerCount",
                font = Font(20, Color.WHITE)
            )
        )

        fruitPlayable.visual = CompoundVisual(
            fruitTileImageVisual,
            TextVisual(
                text = "$fruitCount",
                font = Font(20, Color.WHITE)
            )
        )
    }


    private fun createPlayerPane(player: Player) {
        val playerPane =
            Pane<ComponentView>(
                posY = 380,
                width = 1920,
                height = 1080,
                visual = ColorVisual(Color(0, 0, 0, 0)),
            ).apply {
                val game = rootService.currentGame?.currentBonsaiGameState
                checkNotNull(game)
                isVisible = player == game.currentPlayer
            }
        playerPanes.add(playerPane)
        addComponents(playerPane)
    }


    private fun updateGrowthCards(player: Player) {
        growthCards.clear()

        val columns = 4

        player.collectedCards.forEachIndexed { index, card ->

            val row = index / columns
            val col = index % columns
            if (card.cardType == CardType.GROWTHCARD) {
                val cardView = CardView(
                    posX = (col * 75).toDouble(),
                    posY = (row * 100).toDouble(),
                    height = 170,
                    width = 121.62,
                    back = ImageVisual("ZenCards/${card.id}.png").apply {
                        style.borderRadius = BorderRadius(10.0)
                    },
                    front = zenCardsBack.apply {
                        style.borderRadius = BorderRadius(10.0)
                    },
                )
                growthCards.add(cardView)

            }
        }
    }

    private fun updateToolCards(player: Player) {
        toolCards.clear()

        player.collectedCards.forEachIndexed { _, card ->
            if (card.cardType == CardType.TOOLCARD) {
                val cardView = CardView(
                    height = 170,
                    width = 121.62,
                    back = ImageVisual("ZenCards/${card.id}.png").apply {
                        style.borderRadius = BorderRadius(10.0)
                    },
                    front = zenCardsBack.apply {
                        style.borderRadius = BorderRadius(10.0)
                    },
                )
                toolCards.add(cardView)
            }
        }
    }


    private fun updateCollectedCards(player: Player) {
        collectedCards.clear()

        val currentPlayer = rootService.currentGame?.currentBonsaiGameState?.currentPlayer
        val columns = 1

        player.collectedCards.forEachIndexed { index, card ->

            val row = index / columns
            //val col = index % columns
            if (card.cardType == CardType.PARCHMENTCARD
                || card.cardType == CardType.MASTERCARD
                || card.cardType == CardType.HELPERCARD
            ) {
                val cardView = CardView(
                    //posX = (col * 75).toDouble(),
                    posY = (row * 100).toDouble(),
                    height = 100,
                    width = 75,
                    front = ImageVisual("ZenCards/${card.id}.png").apply {
                        style.borderRadius = BorderRadius(10.0)
                    },
                    back = zenCardsBack.apply {
                        style.borderRadius = BorderRadius(10.0)
                    },
                ).apply {
                    showBack()
                    onMouseClicked = {
                        if (player.isLocal && player == currentPlayer) {
                            showFront()
                        }
                    }
                }
                collectedCards.add(cardView)
            }
        }
    }

    private fun drawTreeFRFR() {
        val players = checkNotNull(rootService.currentGame?.currentBonsaiGameState?.players)
        for (player in players) {
            val tree = player.bonsaiTree
            for (tilePosition in (tree traverseFrom ROOT)) {
                drawTree(player, tilePosition)
            }
        }
    }

    private fun updateAllSuspiciously() {
        val playerCount = checkNotNull(rootService.currentGame?.currentBonsaiGameState?.players?.size)
        repeat(playerCount) { refreshAfterEndTurn() }
    }
}
