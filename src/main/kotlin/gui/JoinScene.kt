package gui

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import util.SECRET_KEY


/**
 * The [JoinScene] is a [MenuScene] to join an online session for a game of bonsai
 *
 * @param bonsaiApplication is the application
 * @property titleLabel shows the title of the game
 * @property sessionTextField is a text field that is used as an input for the session id
 * @property nameTextField is a text field that is used as an input for the players name
 *
 *
 */
class JoinScene(bonsaiApplication: BonsaiApplication, rootService: RootService) : MenuScene(
    1920, 1080, ImageVisual("Backgrounds/background_main.jpg", 1920, 1080)
), Refreshable {

    private val titleLabel = Label(
        0,
        0,
        800,
        300,
        "BONSAI",
        font = Font(200, Color(0x000000), "Osake Regular", Font.FontWeight.NORMAL)
    )

    private val sessionTextField =
        TextFieldStyle2(
            posX = 875,
//            posY = 275,
            posY = 150,
            prompt = "SESSION ID",
        )


    private val nameTextField =
        TextFieldStyle2(
            posX = 875,
            posY = 285,
            prompt = "NAME",
        )

    private val easyBotButton =
        CheckBoxButton2(
            posX = 1500,
            posY = 285,
            text = "EASY BOT",
        ).apply {
            onMouseClicked = {
                change()
            }
        }

    private val hardBotButton =
        CheckBoxButton2(
            posX = 1635,
            posY = 285,
            text = "HARD BOT",
        ).apply {
            onMouseClicked = {
                change()
            }
        }

    private val backButton =
        ButtonStyle1(
            posX = 875,
            posY = 420,
            width = 280,
            height = 110,
            text = "BACK",
        ).apply {
            onMouseClicked = {
                bonsaiApplication.hideMenuScene()
                bonsaiApplication.showMainMenuScene()
            }
        }

    private val joinButton =
        ButtonStyle1(
            posX = 1175,
            posY = 420,
            width = 280,
            height = 110,
            text = "JOIN",
        ).apply {
            onMouseClicked = {
                rootService.networkService
                    .joinGame(
                        SECRET_KEY,
                        nameTextField.text,
                        sessionTextField.text,
                    )
                bonsaiApplication.hideMenuScene()
                bonsaiApplication.showWaitingScene()
            }
        }

    init {
        addComponents(
            titleLabel,
            sessionTextField,
            nameTextField,
            easyBotButton,
            hardBotButton,
            joinButton,
            backButton,
        )
    }
}
