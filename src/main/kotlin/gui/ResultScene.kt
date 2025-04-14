package gui

import entity.Player
import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual

/**
 * The [ResultScene] is a [MenuScene] to show the player the result of a game of bonsai
 * and lets the user return to the [MainMenuScene] or to quit the application
 *
 */
class ResultScene(
    private val bonsaiApplication: BonsaiApplication,
    private val rootService: RootService
) :
    MenuScene(1920, 1080, ImageVisual("Backgrounds/background_main.jpg", 1920, 1080)), Refreshable {

    // Display scene name
    private val titleLabel = Label(
        0,
        0,
        800,
        210,
        "RESULT",
        font = Font(164.0, Color(0x000000), "Okashi Regular", Font.FontWeight.BOLD),
    )

    // Display winner
    private val resultLabel = Label(
        text = "Winner",
        width = 680, height = 130, posX = 1060, posY = 850,
        alignment = Alignment.CENTER,
        font = Font(size = 70, Color(0x000000), "Okashi Regular", Font.FontWeight.NORMAL)
    )

    // Button to quit the game
    private val quitButton = ButtonStyle2(
        posX = 80,
        posY = 900,
        text = "QUIT",
    ).apply {
        onMouseClicked = {
            bonsaiApplication.exit()
        }
    }

    // Button to back to menu scene
    private val backToMenuButton = ButtonStyle2(
        posX = 460,
        posY = 900,
        text = "MENU",
    ).apply {
        onMouseClicked = {
            bonsaiApplication.showMainMenuScene()
        }
    }

    // Table Score
    private val scoreBoard = TableView<PlayerScore>(
        posX = 990,
        posY = 180,
        width = 870,
        height = 630,
        columns = listOf(
            // Column for Player Names
            TableColumn(
                title = "",
                width = 124,
                font = Font(40, Color(0x000000), "Okashi italic", Font.FontWeight.BOLD),
                formatFunction = { player -> player.name }
            ),
            // Column for leaf scores
            TableColumn(
                title = "",
                width = 124,
                font = Font(40, Color(0x000000), "Okashi italic", Font.FontWeight.NORMAL),
                formatFunction = { player -> player.eachScore[0].toString() }
            ),
            // Column for leaf scores
            TableColumn(
                title = "",
                width = 124,
                font = Font(40, Color(0x000000), "Okashi italic", Font.FontWeight.NORMAL),
                formatFunction = { player -> player.eachScore[1].toString() }

            ),
            // Column for leaf scores
            TableColumn(
                title = "",
                width = 124,
                font = Font(40, Color(0x000000), "Okashi italic", Font.FontWeight.NORMAL),
                formatFunction = { player -> player.eachScore[2].toString() }
            ),
            // Column for goal scores
            TableColumn(
                title = "",
                width = 124,
                font = Font(40, Color(0x000000), "Okashi italic", Font.FontWeight.NORMAL),
                formatFunction = { player -> player.eachScore[3].toString() }

            ),

            // Column for parchment scores
            TableColumn(
                title = "",
                width = 124,
                font = Font(40, Color(0x000000), "Okashi italic", Font.FontWeight.NORMAL),
                formatFunction = { player -> player.eachScore[4].toString() }

            ),
            // Column for parchment scores
            TableColumn(
                title = "",
                width = 124,
                font = Font(40, Color(0x000000), "Okashi italic", Font.FontWeight.BOLD),
                formatFunction = { player -> player.eachScore[5].toString() }
            )
        ),
        items = listOf(),
        visual = ImageVisual("ScoreBoard.png").apply {
                style.borderRadius = BorderRadius(20.0)
            },
        selectionMode = SelectionMode.SINGLE
    )

    // Initialize the scene
    init {
        addComponents(titleLabel, resultLabel, backToMenuButton, quitButton, scoreBoard)
    }

    /**
     * The refreshAfterShowWinner method is called by the service layer after a game has ended.
     * It sets the result of the game
     *
     * @param players is the list of players at the end
     */
    override fun refreshAfterShowWinner(players: List<Player>) {

        // Calculate scores for each player
        val playerScores = players.map { player ->
            PlayerScore(
                name = player.name,
                eachScore = rootService.gameService.calculateScore(player) // calculateScore returns List<Int>
            )
        }

        // Set winner name
        resultLabel.text = "Winner: ${players[0].name}"

        scoreBoard.items.addAll(playerScores)
    }

    /**
     * Needed for result table
     */
    data class PlayerScore(
        val name: String,
        val eachScore: List<Int>
    )
}
