package gui

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
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
    1920,1080, ImageVisual("Backgrounds/Hintergrund.png",1920,1080)
), Refreshable {

    private val titleLabel = Label(
        0,
        0,
        800,
        210,
        "BONSAI",
        font = Font(164.0, Color(0x000000), "Arial Black", Font.FontWeight.BOLD),)

    private val startButton = ButtonStyle1(350,500, "START GAME").apply {
        onMouseClicked = {
            bonsaiApplication.hideMenuScene()
            bonsaiApplication.showConfigScene()
        }
    }

    private val continueButton = ButtonStyle1(350,615, "CONTINUE GAME").apply {
        onMouseClicked = {
            rootService.historyService.continueGame()
            bonsaiApplication.hideMenuScene()
        }
    }

    private val hostGameButton  = ButtonStyle1(350,730, "HOST GAME").apply {
        onMouseClicked = {
            bonsaiApplication.hideMenuScene()
            bonsaiApplication.showStartSessionScene()
        }
    }

    private val joinGameButton  = ButtonStyle1(350,845, "JOIN GAME").apply {
        onMouseClicked = {
            bonsaiApplication.hideMenuScene()
            bonsaiApplication.showJoinScene()
        }
    }

    private val quitButton = ButtonStyle1(350,960, "QUIT").apply {
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
            quitButton, )
    }
}
