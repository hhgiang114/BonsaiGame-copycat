package gui

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import java.io.File

//import util.PRIMARY_COLOUR

/**
 * The [MainMenuScene] is a [MenuScene] that is shown at the start of the application
 * and lets the user choose to start a new game in ether modus,
 * to load and continue a game of bonsai or
 * to quit the application
 */
class MainMenuScene(
    private val rootService: RootService,
    private val bonsaiApplication: BonsaiApplication
) : MenuScene(
    1920, 1080, ImageVisual("Backgrounds/background_main.jpg", 1920, 1080)
), Refreshable {

    private val titleLabel = Label(
        560,
        100,
        800,
        300,
        "BONSAI",
        font = Font(200, Color(0x000000), "Osake Regular", Font.FontWeight.NORMAL),
    )

    private val startButton = ButtonStyle1(735, 480, "START GAME").apply {
        onMouseClicked = {
            bonsaiApplication.hideMenuScene()
            bonsaiApplication.showConfigScene()
        }
    }

    private val continueButton = ButtonStyle1(735, 595, "CONTINUE GAME").apply {
        val savedGameState = File("./savedGameState.json")
        onMouseClicked = {
            if (savedGameState.exists()) {
                rootService.historyService.continueGame()
                bonsaiApplication.hideMenuScene()
            }
        }
    }

    private val hostGameButton = ButtonStyle1(735, 710, "HOST GAME").apply {
        onMouseClicked = {
            bonsaiApplication.hideMenuScene()
            bonsaiApplication.showStartSessionScene()
        }
    }

    private val joinGameButton = ButtonStyle1(735, 825, "JOIN GAME").apply {
        onMouseClicked = {
            bonsaiApplication.hideMenuScene()
            bonsaiApplication.showJoinScene()
        }
    }

    private val quitButton = ButtonStyle1(735, 940, "QUIT").apply {
        onMouseClicked = {
            bonsaiApplication.exit()
        }
    }

    init {
        addComponents(
            titleLabel,
            startButton,
            continueButton,
            hostGameButton,
            joinGameButton,
            quitButton,
        )
    }
}
