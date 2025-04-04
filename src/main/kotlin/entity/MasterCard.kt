package entity

import kotlinx.serialization.Serializable

/**
 * Represents a MasterCard in the game.
 * extends the [Card] class and is categorized under [CardType.MASTERCARD]
 *
 * @param tileTypes is a list of tile types that Master card grants when activated
 * @param id is ID for the card
 */

@Serializable
class MasterCard(val tileTypes: MutableList<TileType>, override val id: Int) : Card(id, CardType.MASTERCARD)
