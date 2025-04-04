package service

import entity.BonsaiGame
import entity.BonsaiGameState
import entity.Player
import kotlinx.serialization.json.Json
import java.io.File

/**
 * The service layer class which contains all actions related to the game states
 * of the bonsai game, including:
 * - redo
 * - undo
 * - save game
 * - continue game
 */
class HistoryService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Restores the last action.
     *
     * preconditions:
     * - The game has started and is currently running.
     * - Last action(s) must be undone before.
     *
     * post conditions:
     * - Last action(s) is(are) restored.
     *
     * @throws IllegalStateException if there isn't a next game state.
     */
    fun redo() {
        require(canRedo())
        val game = checkNotNull(rootService.currentGame)
        val history = checkNotNull(rootService.currentGame?.history)
        history.currentPosition += 1
        game.currentBonsaiGameState = history.gameStates[history.currentPosition]
        onAllRefreshables { refreshAfterRedoOrUndo() }
    }

    /**
     * Reverses the last action(s).
     *
     * preconditions:
     * - A previous action must exist.
     *
     * post conditions:
     * - The last action(s) is(are) reversed.
     *
     * @throws IllegalStateException if no previous action exists (game has just started).
     */
    fun undo() {
        require(canUndo())
        val game = checkNotNull(rootService.currentGame)
        val history = checkNotNull(rootService.currentGame?.history)
        history.currentPosition -= 1
        game.currentBonsaiGameState = history.gameStates[history.currentPosition]
        onAllRefreshables { refreshAfterRedoOrUndo() }
    }

    /**
     * Saves the current game state.
     *
     * preconditions:
     * - The game must exist.
     *
     * post conditions:
     * - The game state is saved.
     *
     * @throws IllegalStateException if there is no existing game.
     */
    fun saveGame() {
        val game = checkNotNull(rootService.currentGame)
        val currentPlayer = getCurrentPlayer()

        check(currentPlayer.isLocal) { "Can only be saved if played local" }


        val json = Json {
            allowStructuredMapKeys = true
        }

        val encodedState = json.encodeToString(game)
        File("./savedGameState.json").writeText(encodedState)
    }

    /**
     * Checks if player can redo his turn.
     *
     * preconditions:
     * - Player has undone action(s).
     *
     * @return true if redo is available, otherwise false.
     */
    fun canRedo(): Boolean {
        val history = checkNotNull(rootService.currentGame?.history)
        return history.gameStates.isNotEmpty() && history.currentPosition < history.gameStates.size - 1
    }

    /**
     * Checks if player can undo his turn.
     *
     * preconditions:
     * - A previous player action exists.
     *
     * @return true if undo is available, otherwise false.
     */
    fun canUndo(): Boolean {
        val history = checkNotNull(rootService.currentGame?.history)
        return history.gameStates.isNotEmpty() && history.currentPosition > 0
    }

    /**
     * Continues a previously saved game state.
     *
     * preconditions:
     * - There is an existing game which was saved before.
     *
     * post conditions:
     * - Game has continued with old game state.
     *
     * @throws IllegalStateException if there is no previously saved game.
     */
    fun continueGame() {
        val savedGameState = File("./savedGameState.json")

        check(savedGameState.exists()) { "There is no saved gameState" }

        val jsonString = savedGameState.readText()

        val json = Json {
            allowStructuredMapKeys = true
        }

        val game = json.decodeFromString<BonsaiGame>(jsonString)
        rootService.currentGame = game
        onAllRefreshables { refreshAfterGameStart() }
    }

    // help function to get current player
    private fun getCurrentPlayer(): Player {
        return getCurrentGameState().currentPlayer
    }

    // help function to get current game state
    private fun getCurrentGameState(): BonsaiGameState {
        return checkNotNull(rootService.currentGame?.currentBonsaiGameState)
    }

}
