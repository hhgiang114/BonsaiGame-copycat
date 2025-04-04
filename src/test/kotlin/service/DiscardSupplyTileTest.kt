package service

import entity.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Tests if method discardSupplyTile works correctly, checks:
 * - player can only discard supply tile(s) if it's over his tile capacity limit
 * - throws error if player tries to discard tiles, when not over capacity limit
 */
class DiscardSupplyTileTest {

    /**
     * Tests if player can discard bonsai tiles
     * if his supply size is over the tile capacity limit
     */
    @Test
    fun testDiscardSupplyTileIfOverCapacity(){
        val rootService =  RootService()
        val playerActionService = PlayerActionService(rootService)
        val game = BonsaiGame()
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.BLUE)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE)
        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        player1.tileCapacity = 5
        player1.personalSupply.addAll(
            listOf(
                Tile(1,1, TileType.LEAF),
                Tile(1,2, TileType.LEAF),
                Tile(1,3, TileType.LEAF),
                Tile(1,4, TileType.LEAF),
                Tile(1,5, TileType.LEAF),
                Tile(1,6, TileType.LEAF),
                )
        )
        val tileToDiscard = Tile(1,6, TileType.LEAF)
        val history = History()
        history.currentPosition = 1
        history.gameStates.add(gameState1)
        game.currentBonsaiGameState = gameState1
        game.history = history
        rootService.currentGame = game
        assertTrue(player1.personalSupply.size > player1.tileCapacity)
        playerActionService.discardSupplyTile(tileToDiscard)
        assertEquals(5, player1.personalSupply.size)
        // tests if selected tile to discard was deleted from players personal supply
        assertFalse(player1.personalSupply.contains(tileToDiscard))
        assertEquals(States.END_TURN, gameState1.currentState)
        assertTrue(player1.hasPlayed)
    }

    /**
     * Tests if player can not discard bonsai tiles
     * if his supply size is not over the tile capacity limit
     */
    @Test
    fun testDiscardSupplyTileIfNotOverCapacity(){
        val rootService =  RootService()
        val playerActionService = PlayerActionService(rootService)
        val game = BonsaiGame()
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.BLUE)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE)
        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        player1.tileCapacity = 5
        player1.personalSupply.addAll(
            listOf(
                Tile(1,1, TileType.LEAF),
                Tile(1,2, TileType.LEAF),
                Tile(1,3, TileType.LEAF),
                Tile(1,4, TileType.LEAF),
            )
        )
        val tileToDiscard = Tile(1,4, TileType.LEAF)
        val history = History()
        history.currentPosition = 1
        history.gameStates.add(gameState1)
        game.currentBonsaiGameState = gameState1
        game.history = history
        rootService.currentGame = game
        assertTrue(player1.personalSupply.size < player1.tileCapacity)
        val exception =
            assertThrows<IllegalStateException> { playerActionService.discardSupplyTile(tileToDiscard) }

        assertEquals("The personal supply tiles hasn't reached the capacity.", exception.message)
    }

}
