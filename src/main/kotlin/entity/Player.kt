package entity

import kotlinx.serialization.Serializable

/**
 * This class represents a human or non-human [Player] for this game
 * and stores all the players assets and actions
 *
 * @property name is name of the player
 * @property playerType distinguishes between human and bot (and their difficulty levels)
 * @property isLocal signifies if this is a hot seat game or via network
 * @property color the color of the player
 * @property bonsaiTree is tree the player owns
 * @property personalSupply contains bonsai tiles that the player owns
 * @property collectedCards contains zen cards that the player draws from zen stack
 * @property claimedGoals contains goal tiles that the player claimed
 * @property renouncedGoals contains goal tiles that the player renounced
 * @property playableTiles represents bonsai tiles that the player can place as default at beginning of action
 * @property playableTilesCopy represents bonsai tiles that the player can place during his turn
 * @property tileCapacity is capacity of personal supply
 * @property score is total points that the player gained
 * @property hasPlayed signifies if the player has taken his turn
 */

@Serializable
data class Player(
    val name: String,
    val playerType: PlayerType,
    val isLocal: Boolean,
    val color: ColorType
) {
    var bonsaiTree: MutableMap<Pair<Int, Int>, Tile> = mutableMapOf((0 to 0) to Tile(0, 0, TileType.WOOD))

    var personalSupply: MutableList<Tile> = mutableListOf()
    var collectedCards: MutableList<Card> = mutableListOf()

    var claimedGoals: MutableList<GoalTile> = mutableListOf()
    var renouncedGoals: MutableList<GoalTile> = mutableListOf()

    var playableTiles: MutableList<TileType> = mutableListOf(TileType.ANY, TileType.WOOD, TileType.LEAF)
    var playableTilesCopy: MutableList<TileType> = mutableListOf()

    var tileCapacity = 5
    var score = 0
    var hasPlayed: Boolean = false
}
