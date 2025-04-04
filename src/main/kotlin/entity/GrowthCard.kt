package entity

import kotlinx.serialization.Serializable


/**
 * A growth card in the game, which lets a player play more tiles in the cultivate step
 *
 * @property tileType [TileType] associated with this [GrowthCard]
 *
 * @throws IllegalArgumentException If [id] is not a non-negative int
 */
@Serializable
class GrowthCard(val tileType: TileType, override val id: Int)
    : Card(id, CardType.GROWTHCARD)
