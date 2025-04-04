package entity

import kotlinx.serialization.Serializable

/**
 * Represents a ParchmentCard in the game.
 * extends the [Card] class and is categorized under [CardType.PARCHMENTCARD]
 *
 * @param parchmentTileType is the [TileType] shown on parchment card
 * @param parchmentCardType is the other [CardType] shown on parchment card
 * @param basePoints is the point multiplier parchment card gives
 * @param id is ID for the card
 */

@Serializable
class ParchmentCard(
    val parchmentTileType: TileType?,
    val parchmentCardType: CardType?,
    val basePoints: Int,
    override val id: Int
) : Card(id, CardType.PARCHMENTCARD)
