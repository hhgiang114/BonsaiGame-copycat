package service

import entity.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Tests if method cultivate works correctly
 */
class CultivateTest {

    /**
     * Initialises a new game for to do some tests with
     *
     * @return A [RootService] with initialised game
     */
    private fun setUpGame(): RootService {
        val rootService = RootService()
        val game = BonsaiGame()
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.BLUE)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE)
        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        val history = History()
        history.currentPosition = 1
        history.gameStates.add(gameState1)
        player1.playableTiles.add(TileType.LEAF)
        game.currentBonsaiGameState = gameState1
        game.history = history
        rootService.currentGame = game

        return rootService
    }

    /**
     * Tests if method throws error if he has already played
     * and tries so cultivate
     */
    @Test
    fun testCultivateHasPlayedAlready() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val gameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        gameState.currentPlayer.hasPlayed = true
        val exception =
            assertThrows<IllegalStateException> { playerActionService.cultivate() }

        assertEquals("Player has already meditated or cultivated.", exception.message)
    }

    /**
     * Tests if playableTiles get copied into playableTilesCopy
     */
    @Test
    fun testCultivatePlayableTilesCopy() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val gameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        val playTiles = gameState.currentPlayer.playableTiles.toMutableList()
        playerActionService.cultivate()
        assertEquals(playTiles, gameState.currentPlayer.playableTilesCopy)
    }

    /**
     * Tests if hasPlayed is set to true after cultivate
     */
    @Test
    fun testCultivateSetHasPlayed() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val gameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        playerActionService.cultivate()
        assertTrue(gameState.currentPlayer.hasPlayed)
    }

    /**
     * Tests if State gets changed after cultivate
     */
    @Test
    fun testCultivateStateChange() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val gameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        playerActionService.cultivate()
        assertEquals(gameState.currentState, States.CULTIVATE)
    }

}
