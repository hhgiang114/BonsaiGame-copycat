package service

import entity.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests the [TreeService.canPlayWood] function
 */
class CanPlayWoodTest {

    /**
     * Test can place wood tiles
     */
    @Test
    fun `test canPlayWood returns true for valid play`() {
        val rootService = RootService()

        //SETUP for game
        val players = mutableListOf(
            Player("Alice", PlayerType.HUMAN, true, ColorType.RED),
            Player("Bob", PlayerType.HUMAN, true, ColorType.BLUE),
            Player("Tomi", PlayerType.HUMAN, true, ColorType.BLACK)
        )

        val gameState = BonsaiGameState(
            currentPlayer = players.first(),
            players = players,
            botSpeed = 1,
            currentState = States.DISCARDING
        )

        //SETUP for player
        val playerBonsaiTree = mutableMapOf(
            (0 to 0) to Tile(null, null, TileType.WOOD), //ROOT
            (0 to -1) to Tile(null, null, TileType.WOOD),
            (-1 to -1) to Tile(null, null, TileType.LEAF),
            (1 to -1) to Tile(null, null, TileType.LEAF),

            (0 to -2) to Tile(null, null, TileType.WOOD),
            (1 to -2) to Tile(null, null, TileType.LEAF),
            (2 to -2) to Tile(null, null, TileType.LEAF),

            (0 to -3) to Tile(null, null, TileType.FLOWER),
            (1 to -3) to Tile(null, null, TileType.LEAF),
            (2 to -3) to Tile(null, null, TileType.LEAF),
        )

        val playerCollectedCard = mutableListOf(
            ToolCard(41),
            MasterCard(mutableListOf(TileType.LEAF, TileType.FRUIT), 27)
        )

        gameState.currentPlayer.collectedCards = playerCollectedCard
        gameState.currentPlayer.bonsaiTree = playerBonsaiTree

        val game = BonsaiGame()
        game.currentBonsaiGameState = gameState
        rootService.currentGame = game

        assertTrue(rootService.treeService.canPlayWood())
    }

    /**
     * Test can place wood tiles fails
     */
    @Test
    fun `test canPlayWood fails`() {
        val rootService = RootService()

        //SETUP for game
        val players = mutableListOf(
            Player("Alice", PlayerType.HUMAN, true, ColorType.RED),
            Player("Bob", PlayerType.HUMAN, true, ColorType.BLUE),
            Player("Tomi", PlayerType.HUMAN, true, ColorType.BLACK)
        )

        val gameState = BonsaiGameState(
            currentPlayer = players.first(),
            players = players,
            botSpeed = 1,
            currentState = States.DISCARDING
        )

        //SETUP for player
        val playerBonsaiTree = mutableMapOf(
            (0 to 0) to Tile(null, null, TileType.WOOD), //ROOT
            (0 to -1) to Tile(null, null, TileType.WOOD),
            (-1 to -1) to Tile(null, null, TileType.LEAF),
            (1 to -1) to Tile(null, null, TileType.LEAF),

            (-1 to -2) to Tile(null, null, TileType.FLOWER),
            (0 to -2) to Tile(null, null, TileType.WOOD),
            (1 to -2) to Tile(null, null, TileType.LEAF),
            (2 to -2) to Tile(null, null, TileType.LEAF),

            (0 to -3) to Tile(null, null, TileType.FLOWER),
            (1 to -3) to Tile(null, null, TileType.LEAF),
            (2 to -3) to Tile(null, null, TileType.LEAF),
        )

        val playerCollectedCard = mutableListOf(
            ToolCard(41),
            MasterCard(mutableListOf(TileType.LEAF, TileType.FRUIT), 27),
            HelperCard(TileType.LEAF, 35),
            GrowthCard(TileType.LEAF, 3),
            MasterCard(mutableListOf(TileType.ANY), 24)
        )

        gameState.currentPlayer.collectedCards = playerCollectedCard
        gameState.currentPlayer.bonsaiTree = playerBonsaiTree

        val game = BonsaiGame()
        game.currentBonsaiGameState = gameState
        rootService.currentGame = game

        assertFalse(rootService.treeService.canPlayWood())
    }

}
