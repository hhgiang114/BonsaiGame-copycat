package gui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.CheckBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import util.*

/**
 * Class design button style
 */
class ButtonStyle1(
    posX: Int = 0, posY: Int = 0, text: String = "Button", width: Int = 450, height: Int = 100,
) : Button(
    posX = posX,
    posY = posY,
    width = width,
    height = height,
    text = text,
    font = Font(24.0, Color(TERTIARY_COLOUR), "Okashi Regular", Font.FontWeight.NORMAL),
    visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)

/**
 * Class design button style
 */
class ButtonStyle2(
    posX: Int = 0, posY: Int = 0, text: String = "Button"
) : Button(
    posX = posX,
    posY = posY,
    width = 250,
    height = 100,
    text = text,
    font = Font(24.0, Color.WHITE,"Okashi Regular", Font.FontWeight.BOLD),
    visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)

/**
 * Class design label
 */
class LabelStyle1(
    posX: Int = 0, posY: Int = 0, text: String = "Label"
) : Label(
    posX = posX,
    posY = posY,
    width = 450,
    height = 100,
    text = text,
    font = Font(24.0, Color(PRIMARY_COLOUR), "Okashi italic", Font.FontWeight.BOLD),
    visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)

/**
 * Class design label
 */
class LabelStyle2(
    posX: Int = 0, posY: Int = 0, text: String = "Label"
) : Label(
    posX = posX,
    posY = posY,
    width = 330,
    height = 110,
    text = text,
    font = Font(24.0, Color(PRIMARY_COLOUR), "Okashi italic", Font.FontWeight.LIGHT),
    visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)

/**
 * Class design label
 */
class TextFieldStyle1(
    posX: Int = 0, posY: Int = 0, prompt: String = ""
) : TextField(
    posX = posX,
    posY = posY,
    width = 600,
    height = 110,
    prompt = prompt,
    font = Font(48.0, Color(PRIMARY_COLOUR), "Okashi italic", Font.FontWeight.LIGHT),
    visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)

/**
 * Class design text
 */
class TextFieldStyle2(
    posX: Int = 0, posY: Int = 0, prompt: String = ""
) : TextField(
    posX = posX,
    posY = posY,
    width = 600,
    height = 110,
    prompt = prompt,
    font = Font(48.0, Color(TERTIARY_COLOUR), "Okashi italic", Font.FontWeight.LIGHT),
    visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)

/**
 * Class design label
 */
class TurnLabel(
    posX: Int = 0, posY: Int = 0, text: String = "1"
) : Label(
    posX = posX,
    posY = posY,
    width = 110,
    height = 110,
    text = text,
    font = Font(48.0, Color.WHITE, "Okashi italic", Font.FontWeight.NORMAL),
    visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)

/**
 * Class design check box
 */
class CheckBoxStyle1(
    posX: Int = 0, posY: Int = 0,
) : CheckBox(
    posX = posX,
    posY = posY,
    width = 110,
    height = 110,
    font = Font(48.0, Color(PRIMARY_COLOUR), "Arial Black", Font.FontWeight.BOLD),
    visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)

/**
 * Class design button
 */
class SquareButton(
    posX: Int = 0, posY: Int = 0,
) : Button(
    posX = posX,
    posY = posY,
    width = 110,
    height = 110,
    text = "X",
    font = Font(48.0, Color(0x000000), "Arial Black", Font.FontWeight.BOLD),
    visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)

/**
 * Class design check box button
 */
class CheckBoxButton(
    posX: Int = 0, posY: Int = 0,
) : Button(
    posX = posX,
    posY = posY,
    width = 110,
    height = 110,
    visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
) {
    var isChecked: Boolean = false

    /**
     * function for design
     */
    fun change() {
        if (isChecked) {
            visual = ColorVisual(Color(TERTIARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            }
            isChecked = false
        } else {
            visual = CompoundVisual(
                ColorVisual(Color(TERTIARY_COLOUR)).apply {
                    style.borderRadius = BorderRadius(20.0)
                },
                ImageVisual("remove.png")
            )
            isChecked = true
        }
    }
}

/**
 * Class design check box button
 */
class CheckBoxButton2(
    posX: Int = 0, posY: Int = 0, text: String = ""
) : Button(
    posX = posX,
    posY = posY,
    width = 110,
    height = 110,
    text = text,
    font = Font(16.0, Color(TERTIARY_COLOUR), "Okashi Italic", Font.FontWeight.BOLD),
    visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
) {
    var isChecked: Boolean = false

    /**
     * function for design
     */
    fun change() {
        if (isChecked) {
            visual = ColorVisual(Color(SECONDARY_COLOUR)).apply {
                style.borderRadius = BorderRadius(20.0)
            }
            isChecked = false
        } else {
            visual = CompoundVisual(
                ColorVisual(Color(SECONDARY_COLOUR)).apply {
                    style.borderRadius = BorderRadius(20.0)
                },
                ImageVisual("remove.png")
            )
            isChecked = true
        }
    }


}

/**
 * Class design color button
 */
class ColourButton(
    posX: Int = 0, posY: Int = 0,
) : Button(
    posX = posX,
    posY = posY,
    width = 90,
    height = 90,
    text = "",
    font = Font(48.0, Color(0x000000), "Arial Black", Font.FontWeight.BOLD),
    visual = ColorVisual(Color(COLOUR_RED)).apply {
        style.borderRadius = BorderRadius(20.0)
    }
)
