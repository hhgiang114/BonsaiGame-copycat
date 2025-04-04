package gui

import entity.ColorType
import entity.GoalTileType
import entity.PlayerType
import service.RootService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import util.PRIMARY_COLOUR
import util.SECONDARY_COLOUR
import util.TERTIARY_COLOUR
import util.*


/**
 * The [ConfigureGameMenuScene] is a [MenuScene] to configure the starting parameters
 * of a game of bonsai in hot seat mode
 */
class HostScene(
    private val bonsaiApplication: BonsaiApplication,
    private val rootService: RootService
) : MenuScene(
    1920, 1080, ImageVisual("Backgrounds/background_main.jpg", 1920, 1080)
), Refreshable {

    private val playerColors = mutableListOf(
        ColorType.RED,
        ColorType.PURPLE,
        ColorType.BLACK,
        ColorType.BLUE
    )

    private val availableColors = mutableListOf(
        ColorType.RED,
        ColorType.PURPLE,
        ColorType.BLACK,
        ColorType.BLUE
    )

    private val colorMapping = mapOf(
        ColorType.RED to COLOUR_RED,
        ColorType.PURPLE to COLOUR_PURPLE,
        ColorType.BLACK to COLOUR_BLACK,
        ColorType.BLUE to COLOUR_BLUE
    )

    private val selectedGoalTiles = mutableListOf<GoalTileType>()

    private val contentPlayerPane =
        Pane<UIComponent>(
            posX = 80,
            posY = 40,
            width = 1100,
            height = 1000,
            visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            }
        )

    private val contentGoalTilePane =
        Pane<UIComponent>(
            posX = 1260,
            posY = 40,
            width = 580,
            height = 1000,
            visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            }
        )

    private val titleLabel =
        Label(
            posX = 20,
            posY = 20,
            width = 1000,
            height = 100,
            text = "HOSTING CONFIG",
            font = Font(72, Color(0x000000), "ARIAL BLACK"),
        )

    private val titleGoalTileLabel =
        Label(
            posX = 20,
            posY = 20,
            width = 500,
            height = 100,
            text = "CHOOSE 3",
            font = Font(42, Color(0x000000), "ARIAL BLACK"),
        )

    private val titleGoalTileLabel2 =
        Label(
            posX = 20,
            posY = 80,
            width = 500,
            height = 100,
            text = "GOAL TILES",
            font = Font(42, Color(0x000000), "ARIAL BLACK"),

            )

    private val woodGoalTileLabel =
        LabelStyle2(
            posX = 20,
            posY = 200,
            text = "WOOD"
        )

    private val woodGoalTileButton = CheckBoxButton(
        posX = 390,
        posY = 200
    ).apply {
        onMouseClicked = {
            change()
            toggleGoalTile(GoalTileType.BROWN, this)
        }
    }

    private val leafGoalTileLabel =
        LabelStyle2(
            posX = 20,
            posY = 330,
            text = "LEAF"
        )

    private val leafGoalTileButton = CheckBoxButton(
        posX = 390,
        posY = 330
    ).apply {
        onMouseClicked = {
            change()
            toggleGoalTile(GoalTileType.GREEN, this)
        }
    }

    private val fruitGoalTileLabel =
        LabelStyle2(
            posX = 20,
            posY = 460,
            text = "FRUIT"
        )

    private val fruitGoalTileButton = CheckBoxButton(
        posX = 390,
        posY = 460
    ).apply {
        onMouseClicked = {
            change()
            toggleGoalTile(GoalTileType.ORANGE, this)
        }
    }

    private val flowerGoalTileLabel =
        LabelStyle2(
            posX = 20,
            posY = 590,
            text = "FLOWER"
        )

    private val flowerGoalTileButton = CheckBoxButton(
        posX = 390,
        posY = 590
    ).apply {
        onMouseClicked = {
            change()
            toggleGoalTile(GoalTileType.PINK, this)
        }
    }

    private val positionGoalTileLabel =
        LabelStyle2(
            posX = 20,
            posY = 720,
            text = "POSITION"
        )

    private val positionGoalTileButton = CheckBoxButton(
        posX = 390,
        posY = 720
    ).apply {
        onMouseClicked = {
            change()
            toggleGoalTile(GoalTileType.BLUE, this)
        }
    }

    private val randomGoalTileLabel =
        LabelStyle2(
            posX = 20,
            posY = 870,
            text = "RANDOM"
        )

    /**
     * deselects all goal tiles and chooses and selects three goal tiles at random
     */
    private val randomGoalTileButton = CheckBoxButton(
        posX = 390,
        posY = 870
    ).apply {
        onMouseClicked = {
            clearSelectedGoalTiles()
            selectRandomGoalTiles()
        }
    }

    private val startButton = ButtonStyle2(
        posX = 715,
        posY = 830,
        text = "START"
    )

    private val backButton = ButtonStyle2(
        posX = 175,
        posY = 830,
        text = "BACK"
    ).apply {
        onMouseClicked = {
            rootService.networkService.disconnect()
            bonsaiApplication.hideMenuScene()
            bonsaiApplication.showMainMenuScene()
        }
    }

    private val playerOrderButton = ButtonStyle2(
        posX = 445,
        posY = 830,
        text = "RANDOM ORDER"
    ).apply {
        onMouseClicked = {
            randomizePlayerOrder()
        }
    }

    // Group all player inputs in lists to easily manage them
    private val playerTurns: MutableList<Label> = mutableListOf()
    private val playerInputs: MutableList<TextField> = mutableListOf()
    private val playerColours: MutableList<Button> = mutableListOf()


    init {
        contentPlayerPane.addAll(
            titleLabel,
            backButton,
            playerOrderButton,
            startButton.apply {
                onMouseClicked = {
                    val guiPlayer = playerInputs.mapIndexed { index, playerInput ->
                        val playerNameInLobby = playerInput.text.trim()
                        val color = playerColors[index]
                        val client = rootService.networkService.client
                        checkNotNull(client)
                        val isLocal = (playerNameInLobby == client.playerName)
                        var human = PlayerType.HUMAN
                        if (StartSessionScene.easyBotButton.isChecked) {
                            human = PlayerType.EASYBOT
                        } else if (StartSessionScene.hardBotButton.isChecked) {
                            human = PlayerType.HARDBOT
                        }


                        entity.Player(playerNameInLobby, human, isLocal, color)
                    }.toMutableList()

                    rootService.networkService
                        .sendStartGameMessage(
                            guiPlayer,
                            selectedGoalTiles
                        )
                    bonsaiApplication.hideMenuScene()
                    bonsaiApplication.showGameScene()
                }
            },
        )
        contentGoalTilePane.addAll(
            titleGoalTileLabel,
            titleGoalTileLabel2,
            woodGoalTileLabel,
            woodGoalTileButton,
            leafGoalTileLabel,
            leafGoalTileButton,
            fruitGoalTileLabel,
            fruitGoalTileButton,
            flowerGoalTileLabel,
            flowerGoalTileButton,
            positionGoalTileLabel,
            positionGoalTileButton,
            randomGoalTileLabel,
            randomGoalTileButton,
        )
        addComponents(contentPlayerPane, contentGoalTilePane)

        assignColorButtonFunctionality()

    }

    private fun addPlayer(playerName: String) {
        val currentIndex = playerInputs.size
        if (currentIndex >= 4) return

        val newPlayerTurn = getTurnLabel(currentIndex)

        val newPlayerInput = TextFieldStyle1(
            posX = 190,
            posY = 270 + 140 * currentIndex,
            prompt = playerName
        ).apply {
            text = playerName
            isDisabled = true
        }

        val newPlayerColour = getColourButton(currentIndex)


        // Add components to the pane
        contentPlayerPane.addAll(
            newPlayerInput,
            newPlayerTurn,
            newPlayerColour,
        )

        // Add to respective lists
        playerTurns.add(newPlayerTurn)
        playerInputs.add(newPlayerInput)
        playerColours.add(newPlayerColour)
    }

    private fun getTurnLabel(currentIndex: Int): TurnLabel {
        return TurnLabel(
            posX = 40,
            posY = 270 + 140 * currentIndex,
        ).apply {
            text = "${currentIndex + 1}"
            onMouseClicked = {
                swapPlayerWithNext(currentIndex)
            }
            onMouseEntered = {
                highlightPlayers(currentIndex)
            }
            onMouseExited = {
                removeHighlight()
            }
        }
    }

    private fun getColourButton(currentIndex: Int): ColourButton {
        return ColourButton(
            posX = 600,
            posY = 280 + 140 * currentIndex,
        ).apply {
            onMouseClicked = {
                val currentColor = playerColors[currentIndex]
                val newColor = nextColor(currentColor)
                playerColors[currentIndex] = newColor

                this.visual = ColorVisual(Color(colorMapping[newColor] ?: COLOUR_BLACK)).apply {
                    style.borderRadius = BorderRadius(20.0)
                }
            }

            visual = ColorVisual(Color(colorMapping[playerColors[currentIndex]] ?: COLOUR_BLACK)).apply {
                style.borderRadius = BorderRadius(20.0)
            }
        }
    }

    private fun highlightPlayers(index: Int) {
        val nextIndex = if (index == playerInputs.size - 1) 0 else index + 1

        playerInputs[index].visual = ColorVisual(Color(0xFFFF00)).apply { // Yellow color for highlight
            style.borderRadius = BorderRadius(20.0)
        }

        playerInputs[nextIndex].visual = ColorVisual(Color(0x00FF00)).apply { // Green color for highlight
            style.borderRadius = BorderRadius(20.0)
        }
    }

    private fun removeHighlight() {
        playerInputs.forEach { textField ->
            textField.visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            }
        }
    }

    override fun refreshAfterPlayerJoined(playerName: String) {
        addPlayer(playerName)
    }

    /**
     * determines which color should be chosen for new player
     */
    private fun nextColor(currentColor: ColorType): ColorType {
        val currentIndex = availableColors.indexOf(currentColor)
        return if (currentIndex == -1 || currentIndex == availableColors.lastIndex)
            availableColors.first()
        else
            availableColors[currentIndex + 1]
    }

    /**
     * changes color to next one on click
     */
    private fun assignColorButtonFunctionality() {
        playerColours.forEachIndexed { index, button ->
            button.onMouseClicked = {
                val currentColor = playerColors[index]
                val newColor = nextColor(currentColor)
                playerColors[index] = newColor

                // Update the button visual and ensure it stays rounded
                button.visual = ColorVisual(Color(colorMapping[newColor] ?: COLOUR_BLACK)).apply {
                    style.borderRadius = BorderRadius(20.0)
                }
            }
        }
    }

    /**
     * chooses and selects three goal tiles at random
     */
    private fun selectRandomGoalTiles() {
        val allGoalTiles = GoalTileType.entries.shuffled()
        val selectedTiles = allGoalTiles.take(3)

        for (goalTile in selectedTiles) {
            val button = getGoalTileButton(goalTile)
            button.change()

            selectedGoalTiles.add(goalTile)
        }
    }

    /**
     * clears all goal tiles is used in randomGoalTileButton
     */
    private fun clearSelectedGoalTiles() {
        for (goalTile in selectedGoalTiles) {
            val button = getGoalTileButton(goalTile)
            if (button.isChecked) {
                button.change()
            }
        }
        selectedGoalTiles.clear()
    }

    /**
     * returns checkbox status of goals
     */
    private fun getGoalTileButton(goalTileType: GoalTileType): CheckBoxButton {
        return when (goalTileType) {
            GoalTileType.BROWN -> woodGoalTileButton
            GoalTileType.GREEN -> leafGoalTileButton
            GoalTileType.ORANGE -> fruitGoalTileButton
            GoalTileType.PINK -> flowerGoalTileButton
            GoalTileType.BLUE -> positionGoalTileButton
        }
    }

    /**
     * toggles selected goals and adds them to a list used in start game
     */
    private fun toggleGoalTile(goalTileType: GoalTileType, button: CheckBoxButton) {
        if (button.isChecked) {
            if (selectedGoalTiles.size >= 3) {
                // If 3 goal tiles are already selected, we deselect the first one
                val firstSelected = selectedGoalTiles.first()
                val firstButton = when (firstSelected) {
                    GoalTileType.BROWN -> woodGoalTileButton
                    GoalTileType.GREEN -> leafGoalTileButton
                    GoalTileType.ORANGE -> fruitGoalTileButton
                    GoalTileType.PINK -> flowerGoalTileButton
                    GoalTileType.BLUE -> positionGoalTileButton
                }
                firstButton.change()
                selectedGoalTiles.remove(firstSelected)
            }
            selectedGoalTiles.add(goalTileType)
        } else {
            // If the button is being unchecked, remove the goal tile from the selected list
            selectedGoalTiles.remove(goalTileType)
        }
    }

    /**
     * randomizes the order of the player
     */
    private fun randomizePlayerOrder() {
        val indices = playerInputs.indices.toList().shuffled()

        val shuffledInputs = indices.map { playerInputs[it] }.toMutableList()
        val shuffledColors = indices.map { playerColors[it] }.toMutableList()
        val shuffledTurns = indices.map { playerTurns[it] }.toMutableList()
        val shuffledColours = indices.map { playerColours[it] }.toMutableList()

        playerInputs.clear()
        playerInputs.addAll(shuffledInputs)

        playerColors.clear()
        playerColors.addAll(shuffledColors)

        playerTurns.clear()
        playerTurns.addAll(shuffledTurns)

        playerColours.clear()
        playerColours.addAll(shuffledColours)

        updatePlayerPositions()
    }

    /**
     * current players starting position and next players starting position is switched
     * player 4 is switched with player 1
     */
    private fun swapPlayerWithNext(index: Int) {
        // for player 4
        val nextIndex = if (index == playerInputs.size - 1) 0 else index + 1

        println("Tausche Spieler ${index + 1} mit Spieler ${nextIndex + 1}")

        // Swap the players in all lists
        swapInList(playerInputs, index, nextIndex)
        swapInList(playerColors, index, nextIndex)
        swapInList(playerTurns, index, nextIndex)
        swapInList(playerColours, index, nextIndex)

        // update the positions and click handlers
        updatePlayerPositions()
        updatePlayerClickHandlers()

        // highlight the newly swapped players
        highlightPlayers(index)
    }

    /**
     * updates where the player boxes have to be after swapping the players
     */
    private fun updatePlayerPositions() {
        for (i in playerInputs.indices) {

            playerTurns[i].posY = 270.0 + 140 * i
            playerInputs[i].posY = 270.0 + 140 * i
            playerColours[i].posY = 280.0 + 140 * i
            playerTurns[i].text = "${i + 1}"
        }
    }

    /**
     * help funktion for swap players and highlighting players
     */
    private fun updatePlayerClickHandlers() {
        for (i in playerTurns.indices) {
            playerTurns[i].onMouseClicked = {
                swapPlayerWithNext(i)
            }
            playerTurns[i].onMouseEntered = {
                highlightPlayers(i)
            }
            playerTurns[i].onMouseExited = {
                removeHighlight()
            }
        }
    }

    private fun <T> swapInList(list: MutableList<T>, index1: Int, index2: Int) {
        val temp = list[index1]
        list[index1] = list[index2]
        list[index2] = temp
    }
}
