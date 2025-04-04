package entity

import kotlinx.serialization.Serializable

/**
 * Represents the history of game states in a Bonsai game.
 *
 * This class is responsible for tracking the current position in the history
 * and storing all game states that have occurred during the game.
 *
 * @property currentPosition The current position in the history .
 * @property gameStates A list of all game states that have been recorded in the history.
 */
@Serializable
class History {
    var currentPosition = 0
    val gameStates = mutableListOf<BonsaiGameState>()
}
