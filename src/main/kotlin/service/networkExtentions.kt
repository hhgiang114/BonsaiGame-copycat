package service

import edu.udo.cs.sopra.ntf.*
import entity.*

/**
 * Function to convert a [ColorTypeMessage] to a [ColorType].
 *
 * @return Corresponding [ColorType] value.
 */
fun ColorTypeMessage.toColor(): ColorType {
    return when (this) {
        ColorTypeMessage.BLUE -> ColorType.BLUE
        ColorTypeMessage.PURPLE -> ColorType.PURPLE
        ColorTypeMessage.BLACK -> ColorType.BLACK
        ColorTypeMessage.RED -> ColorType.RED
    }
}

/**
 * Function to convert a [ColorType] to a [ColorTypeMessage].
 *
 * @return Corresponding [ColorTypeMessage] value.
 */
fun ColorType.toColorMessage(): ColorTypeMessage {
    return when (this) {
        ColorType.BLUE -> ColorTypeMessage.BLUE
        ColorType.RED -> ColorTypeMessage.RED
        ColorType.PURPLE -> ColorTypeMessage.PURPLE
        ColorType.BLACK -> ColorTypeMessage.BLACK
    }
}

/**
 * Function to convert a [TileTypeMessage] to a [TileType].
 *
 * @return Corresponding [TileType] value.
 */
fun TileTypeMessage.toTileType(): TileType {
    return when (this) {
        TileTypeMessage.LEAF -> TileType.LEAF
        TileTypeMessage.WOOD -> TileType.WOOD
        TileTypeMessage.FLOWER -> TileType.FLOWER
        TileTypeMessage.FRUIT -> TileType.FRUIT
    }
}

/**
 * Function to convert a [TileType] to a [TileTypeMessage].
 *
 * @return Corresponding [TileTypeMessage] value.
 */
fun TileType.toTileTypeMessage(): TileTypeMessage {
    return when (this) {
        TileType.LEAF -> TileTypeMessage.LEAF
        TileType.WOOD -> TileTypeMessage.WOOD
        TileType.FLOWER -> TileTypeMessage.FLOWER
        else -> TileTypeMessage.FRUIT
    }
}

/**
 * Function to convert a [CardTypeMessage] to a [CardType].
 *
 * @return Corresponding [CardType] value.
 */
fun CardTypeMessage.toCardType(): CardType {
    return when (this) {
        CardTypeMessage.TOOL -> CardType.TOOLCARD
        CardTypeMessage.GROWTH -> CardType.GROWTHCARD
        CardTypeMessage.PARCHMENT -> CardType.PARCHMENTCARD
        CardTypeMessage.HELPER -> CardType.HELPERCARD
        CardTypeMessage.MASTER -> CardType.MASTERCARD
    }
}

/**
 * Extension function to convert a [CardType] to a [CardTypeMessage].
 *
 * @return Corresponding [CardTypeMessage] value.
 */
fun CardType.toCardTypeMessage(): CardTypeMessage {
    return when (this) {
        CardType.TOOLCARD -> CardTypeMessage.TOOL
        CardType.GROWTHCARD -> CardTypeMessage.GROWTH
        CardType.HELPERCARD -> CardTypeMessage.HELPER
        CardType.MASTERCARD -> CardTypeMessage.MASTER
        CardType.PARCHMENTCARD -> CardTypeMessage.PARCHMENT
    }
}

/**
 * Function to convert a [GoalTileTypeMessage] to a [GoalTileType].
 *
 * @return Corresponding [GoalTileType] value.
 */
fun GoalTileTypeMessage.toGoalTileType(): GoalTileType {
    return when (this) {
        GoalTileTypeMessage.BLUE -> GoalTileType.BLUE
        GoalTileTypeMessage.GREEN -> GoalTileType.GREEN
        GoalTileTypeMessage.BROWN -> GoalTileType.BROWN
        GoalTileTypeMessage.PINK -> GoalTileType.PINK
        GoalTileTypeMessage.ORANGE -> GoalTileType.ORANGE
    }
}

/**
 * Function to convert a [GoalTileType] to a [GoalTileTypeMessage].
 *
 * @return Corresponding [GoalTileTypeMessage] value.
 */
fun GoalTileType.toGoalTileTypeMessage(): GoalTileTypeMessage {
    return when (this) {
        GoalTileType.BLUE -> GoalTileTypeMessage.BLUE
        GoalTileType.BROWN -> GoalTileTypeMessage.BROWN
        GoalTileType.GREEN -> GoalTileTypeMessage.GREEN
        GoalTileType.PINK -> GoalTileTypeMessage.PINK
        GoalTileType.ORANGE -> GoalTileTypeMessage.ORANGE
    }
}

/**
 * Data class representing a mutable meditate message with the necessary parameters
 * for transmitting meditation-related actions in the game.
 */
data class MutableMeditateMessage(
    val removedTilesAxialCoordinates: MutableList<Pair<Int, Int>>,
    var chosenCardPosition: Int,
    val playedTiles: MutableList<Pair<TileType, Pair<Int, Int>>>,
    val drawnTiles: MutableList<TileType>,
    val claimedGoals: MutableList<Pair<GoalTileType, Int>>,
    val renouncedGoals: MutableList<Pair<GoalTileType, Int>>,
    val discardedTiles: MutableList<TileType>
)

/**
 * Data class representing a mutable cultivate message with the necessary parameters
 * for transmitting cultivation-related actions in the game.
 */
data class MutableCultivateMessage(
    val removedTilesAxialCoordinates: MutableList<Pair<Int, Int>>,
    val playedTiles: MutableList<Pair<TileType, Pair<Int, Int>>>,
    val claimedGoals: MutableList<Pair<GoalTileType, Int>>,
    val renouncedGoals: MutableList<Pair<GoalTileType, Int>>
)


