package entity

import kotlinx.serialization.Serializable

/**
 * Represents a ToolCard in the game.
 * extends the [Card] class and is categorized under [CardType.TOOLCARD]
 *
 * @param id is ID for the card
 */
@Serializable
class ToolCard(override val id: Int) : Card(id, CardType.TOOLCARD)
