package service

import entity.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertNotSame

/**
 * Tests if method endTurn works correctly:
 * - player must require canEndTurn
 * - if zenDeck is empty, endgameCounter increases by 1
 * - otherwise the score gets updated and the turn switches to the next player
 */
class EndTurnTest {

    /**
    * Initialises a new game for to do some tests with
    *
    * @return A [RootService] with initialised game
    */
    private fun setUpGame(): RootService {
        val rootService = RootService()
        val game = BonsaiGame()
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.BLUE)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.RED)
        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        gameState1.zenDeck.add(0, HelperCard(TileType.LEAF, 3))
        val history = History()
        history.currentPosition = 0
        history.gameStates.add(gameState1)
        game.currentBonsaiGameState = gameState1
        game.history = history
        rootService.currentGame = game

        return rootService
    }

    /**
     * Tests if endTurn switches to the next player and updates score
     */
    @Test
    fun testEndTurnNotLastRound() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val gameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        gameState.currentPlayer.hasPlayed = true
        val otherPlayer = gameState.currentPlayer
        playerActionService.endTurn()
        assertTrue(gameState.currentPlayer.score >= 0.0)
        assertNotEquals(gameState.currentPlayer.name, otherPlayer.name)
        assertEquals("Tomy", gameState.currentPlayer.name)
    }

    /**
     * Tests if endgameCounter increases by 1 if zenDeck is empty
     */
    @Test
    fun testEndTurnZenDeckEmpty() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val gameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        gameState.zenDeck.clear()
        gameState.currentPlayer.hasPlayed = true
        playerActionService.endTurn()
        assertEquals(1, gameState.endGameCounter)
    }

    /**
     * Tests if endgameCounter equals player size
     */
    @Test
    fun testEndTurnEndGame(){
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val gameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        gameState.zenDeck.clear()
        gameState.endGameCounter = 1
        gameState.currentPlayer.hasPlayed = true
        playerActionService.endTurn()
        assertEquals(gameState.endGameCounter, gameState.players.size)
    }

    /**
     * Tests if endgameCounter not equals player size
     */
    @Test
    fun testEndTurnCounterNotEqualPlayerSize(){
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val gameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        gameState.zenDeck.clear()
        gameState.currentPlayer.hasPlayed = true
        playerActionService.endTurn()
        assertNotEquals(gameState.endGameCounter, gameState.players.size)
    }

    /**
     * Tests if currentState changes to start turn after player has ended his turn
     */
    @Test
    fun testEndTurnStateChange() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val gameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        gameState.currentPlayer.hasPlayed = true
        playerActionService.endTurn()
        assertEquals(gameState.currentState, States.START_TURN)
    }

    /**
     * Tests if history is created correctly after endTurn
     */
    @Test
    fun testEndTurnHistory() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val game = checkNotNull(rootService.currentGame)
        val gameState = checkNotNull(game.currentBonsaiGameState)


        assertEquals(1, game.history?.gameStates?.size)
        assertEquals(0, game.history?.currentPosition)

        gameState.currentPlayer.hasPlayed = true
        playerActionService.endTurn()

         assertEquals(2, game.history?.gameStates?.size)
         assertEquals(1, game.history?.currentPosition)

         val newState = game.history?.gameStates?.get(1)
         assertNotSame(gameState, newState)
         assertEquals(gameState, newState)
    }
}
