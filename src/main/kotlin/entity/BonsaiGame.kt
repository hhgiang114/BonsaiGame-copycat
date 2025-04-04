package entity

import kotlinx.serialization.Serializable

/**
 * Game instance that manages the [BonsaiGameState] of a game and the saved states of the game
 * that are saved in [History]
 *
 * @property history Stores [History] of game actions. `null` at start
 * a [History] needs to be recorded at game start
 */

@Serializable
class BonsaiGame {
    var history: History? = null
    var currentBonsaiGameState: BonsaiGameState? = null
}
