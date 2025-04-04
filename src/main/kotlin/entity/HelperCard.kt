package entity

import kotlinx.serialization.Serializable

/**
 * A helper card in the game, which lets a player play tiles at acquisition
 *
 * @property tileTypes A list containing `TileType.ANY` and one other [TileType]
 *
 * @throws IllegalArgumentException If [id] is not a non-negative int
 */

@Serializable
class HelperCard(val tileType: TileType,override val id: Int) : Card(id, CardType.HELPERCARD) {
    val tileTypes = mutableListOf(TileType.ANY, tileType)
}