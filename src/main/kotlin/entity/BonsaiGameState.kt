package entity

import kotlinx.serialization.Serializable

/**
 * The state of the game at a given moment
 *
 * @property currentPlayer Player from players list whose turn it is
 * @property players List of all players participating in the game. should have two to four players
 * @property botSpeed Speed at which the bot plays. must be a positive int
 * @property currentState Current state of the game
 * @property endGameCounter Tracks how many turn are left in the game, once the game end condition was met
 * @property zenDeck Deck of cards available for the game. empty till assigned cards at game start
 * @property faceUpCards Set of face-up cards available for selection during meditate.
 * empty till assigned cards at game start
 * @property goalTiles List of goal tiles used in the game. empty till assigned six or nine cards at game start
 *
 * @throws IllegalArgumentException If [players] is empty or [currentPlayer] is not in the list
 * @throws IllegalArgumentException If [botSpeed] is negative
 */
@Serializable
data class BonsaiGameState(
    var currentPlayer: Player,
    val players: MutableList<Player>,
    val botSpeed: Int,
    var currentState: States
) {
    var endGameCounter = 0
    var zenDeck: MutableList<Card> = mutableListOf()
    var faceUpCards: MutableList<Card> = mutableListOf()
    var goalTiles: MutableList<GoalTile> = mutableListOf()
}
