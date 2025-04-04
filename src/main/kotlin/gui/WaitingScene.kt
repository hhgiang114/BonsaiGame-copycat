package gui

import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import util.SECONDARY_COLOUR
import util.TERTIARY_COLOUR

/**
 * The [WaitingScene] for network play
 */
class WaitingScene(private val bonsaiApplication: BonsaiApplication) : MenuScene(
    1920, 1080, ImageVisual("Backgrounds/background_main.jpg", 1920, 1080)
), Refreshable {

    private val titleLabel = Label(
        560,
        100,
        800,
        300,
        "BONSAI",
        font = Font(200, Color(0x000000), "Osake Regular", Font.FontWeight.NORMAL)
    )

    private val textLabel =
        Label(
            posX = 460,
            posY = 270,
            width = 1000,
            height = 500,
            text = "WAITING FOR HOST TO START GAME",
//            font = Font(48.0, Color(TERTIARY_COLOUR), "Okashi italic", Font.FontWeight.BOLD),
            font = Font(48.0, Color(0x141B1D), "Okashi italic", Font.FontWeight.BOLD),

            visual = ColorVisual(Color.TRANSPARENT)
        )

    init {
        addComponents(textLabel, titleLabel)
    }

    override fun refreshAfterGameStart() {
        bonsaiApplication.hideMenuScene()
        bonsaiApplication.showGameScene()
    }
}
