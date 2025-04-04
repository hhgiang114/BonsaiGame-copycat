package gui

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import util.DEFAULT_NAME
import util.SECRET_KEY

/**
 * The [StartSessionScene] for network play
 */
class StartSessionScene(bonsaiApplication: BonsaiApplication, rootService: RootService) : MenuScene(
    1920, 1080, ImageVisual("Backgrounds/Hintergrund2.png", 1920, 1080)
), Refreshable {
    private val titleLabel = Label(
        0,
        0,
        800,
        210,
        "BONSAI",
        font = Font(164.0, Color(0x000000), "Arial Black", Font.FontWeight.BOLD),
    )

    private val sessionTextField =
        TextFieldStyle2(
            posX = 875,
            posY = 275,
            prompt = "SESSION ID",
        )

    private val nameTextField =
        TextFieldStyle2(
            posX = 875,
            posY = 150,
            prompt = "PLAYER NAME",
        )

    private val backButton =
        ButtonStyle1(
            posX = 875,
            posY = 410,
            width = 280,
            height = 110,
            text = "BACK",
        ).apply {
            onMouseClicked = {
                bonsaiApplication.hideMenuScene()
                bonsaiApplication.showMainMenuScene()
            }
        }

    private val startSessionButton =
        ButtonStyle1(
            posX = 1195,
            posY = 410,
            width = 280,
            height = 110,
            text = "START SESSION",
        ).apply {
            onMouseClicked = {
                bonsaiApplication.hideMenuScene()
                rootService.networkService
                    .createGame (
                        SECRET_KEY,
                        if (nameTextField.text!="") nameTextField.text else DEFAULT_NAME,
                        sessionTextField.text
                    )
                bonsaiApplication.showHostScene()
            }
        }

    init {
        addComponents(
            titleLabel,
            nameTextField,
            sessionTextField,
            startSessionButton,
            backButton,
            easyBotButton,
            hardBotButton
        )
    }

    /**
     * Button for bot
     */
    companion object{
        val easyBotButton =
            CheckBoxButton2(
                posX = 1500,
                posY = 150,
                text = "EASY BOT",
            ).apply {
                onMouseClicked = {
                    change()
                }
            }

        val hardBotButton =
            CheckBoxButton2(
                posX = 1635,
                posY = 150,
                text = "HARD BOT",
            ).apply {
                onMouseClicked = {
                    change()
                }
            }
    }

}
