package entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * general card in the game
 *
 * @property id Unique identifier of the card. Must be a non-negative int
 * @property cardType Type of the card, of [CardType]
 *
 * @throws IllegalArgumentException If [id] is negative or not an int
 */
@Serializable
sealed class Card(
    @Transient open val id: Int = -1,
    val cardType: CardType
)
