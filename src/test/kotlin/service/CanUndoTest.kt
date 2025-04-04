package service

import entity.*
import kotlin.test.*

/**
 * Tests if canUndo works correctly:
 * - should be true, if there game state size greater than 0
 * - should be false, if the game has just started
 */
class CanUndoTest {

    /**
     * Initialises a new game for to do some tests with
     *
     * @return A [RootService] with initialised game
     */
    private fun setUpGame(currentPos: Int): RootService {
        val rootService = RootService()
        val game = BonsaiGame()
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.RED)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE)
        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        val gameState2 = BonsaiGameState(player2, mutableListOf(player1, player2), 2, States.CULTIVATE)
        val history = History()
        history.currentPosition = currentPos
        history.gameStates.add(gameState1)
        history.gameStates.add(gameState2)
        game.currentBonsaiGameState = gameState2
        game.history = history
        rootService.currentGame = game
        return rootService

    }

    /**
     * Tests if player can undo if there are previous game states
     */
    @Test
    fun testCanUndoIfHistoryPosGreater0() {
        val rootService = setUpGame(1)
        val historyService = HistoryService(rootService)
        assertTrue(historyService.canUndo())
    }

    /**
     * Tests if player can not undo if game has just started
     */
    @Test
    fun testCanNotUndoIfHistoryPos0() {
        val rootService = setUpGame(0)
        val historyService = HistoryService(rootService)
        assertFalse(historyService.canUndo())
    }

    /**
     * Tests if player can not undo if there are no game states yet
     */
    @Test
    fun testCanNotUndoIfGameStateEmpty() {
        val rootService = RootService()
        val game = BonsaiGame()
        val historyService = HistoryService(rootService)
        val history = History()
        history.currentPosition = 0
        game.history = history
        rootService.currentGame = game
        assertFalse(historyService.canUndo())
    }

    /**
     * Tests the [HistoryService.undo] function.
     *
     * tests if the current position and the current game state are updated after an undo action.
     */
    @Test
    fun testUndoValid() {
        val rootService = RootService()
        val game = BonsaiGame()
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.RED)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE)

        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        val gameState2 = BonsaiGameState(player2, mutableListOf(player1, player2), 2, States.CULTIVATE)
        val history = History()
        history.currentPosition = 1
        history.gameStates.add(gameState1)
        history.gameStates.add(gameState2)
        game.currentBonsaiGameState = history.gameStates[history.currentPosition]
        game.history = history
        rootService.currentGame = game

        val historyService = HistoryService(rootService)


        assertEquals(1, history.currentPosition)
        assertEquals(history.gameStates[1], game.currentBonsaiGameState)

        historyService.undo()

        assertEquals(0, history.currentPosition)
        assertEquals(history.gameStates[0], game.currentBonsaiGameState)

    }

}
