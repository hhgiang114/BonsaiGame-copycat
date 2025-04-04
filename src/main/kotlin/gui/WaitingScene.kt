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
class WaitingScene (private val bonsaiApplication: BonsaiApplication) : MenuScene(
    1920,1080, ImageVisual("Backgrounds/Hintergrund2.png",1920,1080)
) , Refreshable {

    private val titleLabel = Label(
        0,
        0,
        800,
        210,
        "BONSAI",
        font = Font(164.0, Color(0x000000), "Arial Black", Font.FontWeight.BOLD),)

    private val textLabel =
        Label(
            posX = 875,
            posY = 275,
            width = 1000,
            height = 500,
            text = "WAITING FOR HOST TO START GAME" ,
            font = Font(48.0, Color(TERTIARY_COLOUR), "Arial Black", Font.FontWeight.BOLD),
            visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
            style.borderRadius = BorderRadius(20.0)
                }
        )

    init {
        addComponents(textLabel, titleLabel)
    }

    override fun refreshAfterGameStart() {
        bonsaiApplication.hideMenuScene()
        bonsaiApplication.showGameScene()
    }
}
