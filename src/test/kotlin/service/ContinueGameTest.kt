package service

import kotlin.test.*
import entity.*
import org.junit.jupiter.api.assertThrows
import java.io.File

/**
 * Tests if function continueGame works correctly, by checking if all gameStates are saved correct.
 * Saved game should be the same as the gameState played with
 */
class ContinueGameTest {

    /**
     * Tests if continueGame throws error if there is no saved game
     */
    @Test
    fun testContinueGameInvalid() {
        val rootService = RootService()
        val historyService = HistoryService(rootService)
        val savedGameState = File("./savedGameState.json")
        if (savedGameState.exists()) {
            savedGameState.delete()
        }
        val exception =
            assertThrows<IllegalStateException> { historyService.continueGame() }

        assertEquals("There is no saved gameState", exception.message)
    }

    /**
     * Tests if saveGame and continueGame works correctly
     */
    @Test
    fun testContinueGame() {
        val rootService = RootService()
        val historyService = HistoryService(rootService)

        val game = BonsaiGame()

        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.RED)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE)
        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        val gameState2 = BonsaiGameState(player2, mutableListOf(player1, player2), 2, States.CULTIVATE)
        val history = History()
        history.currentPosition = 1
        history.gameStates.add(gameState1)
        history.gameStates.add(gameState2)
        game.currentBonsaiGameState = gameState2
        game.history = history
        rootService.currentGame = game
        historyService.saveGame()
        rootService.currentGame = null
        historyService.continueGame()
        val loadedGame = rootService.currentGame
        assertNotNull(loadedGame)
        assertEquals(game.history?.currentPosition, loadedGame.history?.currentPosition)
        assertEquals(game.history?.gameStates, loadedGame.history?.gameStates)
    }

    /**
     * Tests if player can not save if not played local
     */
    @Test
    fun testSaveGameIfPlayerNotLocal() {
        val rootService = RootService()
        val game = BonsaiGame()
        val historyService = HistoryService(rootService)
        val player1 = Player("Tom", PlayerType.HUMAN, false, ColorType.RED)
        val player2 = Player("Tomy", PlayerType.HUMAN, false, ColorType.BLUE)
        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        val gameState2 = BonsaiGameState(player2, mutableListOf(player1, player2), 2, States.CULTIVATE)
        val history = History()
        history.currentPosition = 1
        history.gameStates.add(gameState1)
        history.gameStates.add(gameState2)
        game.currentBonsaiGameState = gameState2
        game.history = history
        rootService.currentGame = game
        val exception =
            assertThrows<IllegalStateException> { historyService.saveGame() }

        assertEquals("Can only be saved if played local", exception.message)

    }

}
