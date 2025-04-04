package service

import entity.*
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import kotlin.test.*

/**
 * Tests if method canEndTurn works correctly, checks:
 * - player can only end turn if he has played an action
 * - return false if no play action was done before
 */
class CanEndTurnTest {

    /**
     * Tests if player can end turn if he has played an action before
     */
    @Test
    fun testCanEndTurnIfHasPlayed() {
        val rootService = RootService()
        val game = BonsaiGame()
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.BLUE)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE)
        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        val history = History()
        history.currentPosition = 1
        player1.hasPlayed = true
        history.gameStates.add(gameState1)
        game.currentBonsaiGameState = gameState1
        game.history = history
        rootService.currentGame = game
        val playerActionService = PlayerActionService(rootService)
        assertTrue(playerActionService.canEndTurn())
    }

    /**
     * Tests if player can not end his turn if he has not played an action before
     */
    @Test
    fun testCanEndTurnIfHasNotPlayed() {
        val rootService = RootService()
        val game = BonsaiGame()
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.BLUE)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE)
        val gameState2 = BonsaiGameState(player2, mutableListOf(player1, player2), 2, States.MEDITATE)
        val history = History()
        history.currentPosition = 1
        player2.hasPlayed = false
        history.gameStates.add(gameState2)
        game.currentBonsaiGameState = gameState2
        game.history = history
        rootService.currentGame = game
        val playerActionService = PlayerActionService(rootService)
        assertFalse(playerActionService.canEndTurn())
    }

}
