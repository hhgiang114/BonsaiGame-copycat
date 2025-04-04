package service

import kotlin.test.*
import entity.*

/**
 * Tests if startNewGame works correctly:
 * - Game is not null
 * - Player size should be the same as players set
 * - Player order should be as selected
 * - Player names should be the same as selected
 * - FaceUp cards should be 4
 */
class StartNewGameTest {

    /**
     * Initialises a new game for to do some tests with
     *
     * @return A [RootService] with initialised game
     */
    private fun setUpGame(): RootService {
        val rootService = RootService()
        val gameService = GameService(rootService)
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.RED)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLACK)
        val player3 = Player("Tomi", PlayerType.HUMAN, true, ColorType.BLUE)
        val player4 = Player("Tomii", PlayerType.HUMAN, true, ColorType.PURPLE)
        val playerOrder = mutableListOf(player1, player2, player3, player4)
        val goalTiles = mutableListOf(GoalTileType.BLUE, GoalTileType.PINK, GoalTileType.GREEN)
        gameService.startNewGame(playerOrder, false, goalTiles)
        return rootService
    }

    /**
     * Tests if game was initialised
     */
    @Test
    fun testIfGameIsNotNull() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        assertNotNull(game)
    }

    /**
     * Tests if player size is correct
     */
    @Test
    fun testPlayerSize() {
        val rootService = setUpGame()
        val game = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        val players = game.players
        assertEquals(4, players.size)
    }

    /**
     * Tests if player order is correct as set
     */
    @Test
    fun testPlayerOrder() {
        val rootService = setUpGame()
        val bonsaiGameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        assertEquals("Tom", bonsaiGameState.currentPlayer.name)
        assertNotEquals("Tomy", bonsaiGameState.currentPlayer.name)
    }

    /**
     * Tests if names got initialised correctly
     */
    @Test
    fun testIfNamesCorrect() {
        val rootService = setUpGame()
        val bonsaiGameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        val players = bonsaiGameState.players
        assertEquals("Tom", players.first().name)
        assertEquals("Tomii", players.last().name)
    }

    /**
     * Tests if faceUp cards are size 4
     */
    @Test
    fun testFaceUpCardsSize() {
        val rootService = setUpGame()
        val bonsaiGameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        assertEquals(4, bonsaiGameState.faceUpCards.size)
    }

    /**
     * Tests starting tiles
     */
    @Test
    fun testStartingTiles() {
        val rootService = setUpGame()
        val bonsaiGameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        val players = bonsaiGameState.players
        assertEquals(players[0].personalSupply.map { it.tileType }, mutableListOf(TileType.WOOD))
        assertEquals(
            players[1].personalSupply.map { it.tileType },
            mutableListOf(TileType.WOOD, TileType.LEAF)
        )
        assertEquals(
            players[2].personalSupply.map { it.tileType },
            mutableListOf(TileType.WOOD, TileType.LEAF, TileType.FLOWER)
        )
        assertEquals(
            players[3].personalSupply.map { it.tileType },
            mutableListOf(TileType.WOOD, TileType.LEAF, TileType.FLOWER, TileType.FRUIT)
        )
    }

}
