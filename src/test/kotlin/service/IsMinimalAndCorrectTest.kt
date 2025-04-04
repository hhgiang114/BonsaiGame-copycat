package service

import entity.*
import kotlin.test.*

/**
 * Tests the [TreeService.isMinimalAndCorrect] function
 */
class IsMinimalAndCorrectTest {

    /**
     * test minimal removal
     */
    @Test
    fun `test isMinimal`() {
        val rootService = RootService()

        //SETUP for game
        val players = mutableListOf(
            Player("Alice", PlayerType.HUMAN, true, ColorType.RED),
            Player("Bob", PlayerType.HUMAN, true, ColorType.BLUE),
        )

        val gameState = BonsaiGameState(
            currentPlayer = players.first(),
            players = players,
            botSpeed = 1,
            currentState = States.DISCARDING
        )

        //SETUP for player
        val playerBonsaiTree = mutableMapOf(
            (0 to 0) to Tile(null, null, TileType.WOOD), (-2 to -1) to Tile(null, null, TileType.FLOWER),
            (-1 to -1) to Tile(null, null, TileType.LEAF), (0 to -1) to Tile(null, null, TileType.WOOD),
            (1 to -1) to Tile(null, null, TileType.LEAF),

            (-2 to -2) to Tile(null, null, TileType.FRUIT), (-1 to -2) to Tile(null, null, TileType.LEAF),
            (0 to -2) to Tile(null, null, TileType.LEAF), (1 to -2) to Tile(null, null, TileType.WOOD),
            (2 to -2) to Tile(null, null, TileType.LEAF), (3 to -2) to Tile(null, null, TileType.FLOWER),

            (-1 to -3) to Tile(null, null, TileType.LEAF), (0 to -3) to Tile(null, null, TileType.WOOD),
            (1 to -3) to Tile(null, null, TileType.WOOD), (2 to -3) to Tile(null, null, TileType.WOOD),
            (3 to -3) to Tile(null, null, TileType.LEAF), (4 to -3) to Tile(null, null, TileType.FLOWER),

            (0 to -4) to Tile(null, null, TileType.LEAF), (1 to -4) to Tile(null, null, TileType.WOOD),
            (2 to -4) to Tile(null, null, TileType.LEAF), (3 to -4) to Tile(null, null, TileType.WOOD),

            (0 to -5) to Tile(null, null, TileType.FLOWER), (1 to -5) to Tile(null, null, TileType.WOOD),
            (2 to -5) to Tile(null, null, TileType.LEAF), (3 to -5) to Tile(null, null, TileType.LEAF),
            (4 to -5) to Tile(null, null, TileType.LEAF), (5 to -5) to Tile(null, null, TileType.FLOWER),

            (1 to -6) to Tile(null, null, TileType.LEAF), (2 to -6) to Tile(null, null, TileType.LEAF),
            (3 to -6) to Tile(null, null, TileType.FLOWER), (4 to -6) to Tile(null, null, TileType.LEAF),

            (2 to -7) to Tile(null, null, TileType.FRUIT)
        )
        gameState.currentPlayer.bonsaiTree = playerBonsaiTree
        val game = BonsaiGame()
        game.currentBonsaiGameState = gameState
        rootService.currentGame = game
         assert( rootService.treeService.isMinimalAndCorrect(Pair(1, -1)) )
         assert( rootService.treeService.isMinimalAndCorrect(Pair(-1, -1)) )
         assert( rootService.treeService.isMinimalAndCorrect(Pair(2, -2)) )
         assert( rootService.treeService.isMinimalAndCorrect(Pair(0, -4)) )
         assert( rootService.treeService.isMinimalAndCorrect(Pair(4, -6)) )
         assertFalse( rootService.treeService.isMinimalAndCorrect(Pair(-1, -3)) )
         assertFalse( rootService.treeService.isMinimalAndCorrect(Pair(3, -3)) )
         assertFalse( rootService.treeService.isMinimalAndCorrect(Pair(4, -5)) )
         assertFalse( rootService.treeService.isMinimalAndCorrect(Pair(1, -6)) )
         assertFalse( rootService.treeService.isMinimalAndCorrect(Pair(2, -6)) )
    }

    /**
     * other test case minimal removal
     */
    @Test
    fun `test another time `(){
        val rootService = RootService()

        //SETUP for game
        val players = mutableListOf(
            Player("Alice", PlayerType.HUMAN, true, ColorType.RED),
            Player("Bob", PlayerType.HUMAN, true, ColorType.BLUE),
        )

        val gameState = BonsaiGameState(
            currentPlayer = players.first(), players = players,
            botSpeed = 1, currentState = States.DISCARDING
        )

        val playerBonsaiTree = mutableMapOf(
            (0 to 0) to Tile(null, null, TileType.WOOD), (-2 to -1) to Tile(null, null, TileType.FRUIT),
            (-1 to -1) to Tile(null, null, TileType.LEAF), (0 to -1) to Tile(null, null, TileType.WOOD),
            (1 to -1) to Tile(null, null, TileType.LEAF), (2 to -1) to Tile(null, null, TileType.FRUIT),

            (-2 to -2) to Tile(null, null, TileType.FRUIT), (-1 to -2) to Tile(null, null, TileType.LEAF),
            (0 to -2) to Tile(null, null, TileType.LEAF), (1 to -2) to Tile(null, null, TileType.WOOD),
            (2 to -2) to Tile(null, null, TileType.LEAF), (3 to -2) to Tile(null, null, TileType.FLOWER),

            (-1 to -3) to Tile(null, null, TileType.LEAF), (0 to -3) to Tile(null, null, TileType.WOOD),
            (1 to -3) to Tile(null, null, TileType.WOOD), (2 to -3) to Tile(null, null, TileType.WOOD),
            (3 to -3) to Tile(null, null, TileType.LEAF), (4 to -3) to Tile(null, null, TileType.FLOWER),

            (-1 to -4) to Tile(null, null, TileType.FRUIT), (0 to -4) to Tile(null, null, TileType.LEAF),
            (1 to -4) to Tile(null, null, TileType.WOOD), (2 to -4) to Tile(null, null, TileType.LEAF),
            (3 to -4) to Tile(null, null, TileType.WOOD), (4 to -4) to Tile(null, null, TileType.LEAF),
            (5 to -4) to Tile(null, null, TileType.FLOWER),

            (0 to -5) to Tile(null, null, TileType.FLOWER), (1 to -5) to Tile(null, null, TileType.WOOD),
            (2 to -5) to Tile(null, null, TileType.LEAF), (3 to -5) to Tile(null, null, TileType.LEAF),
            (4 to -5) to Tile(null, null, TileType.LEAF), (5 to -5) to Tile(null, null, TileType.FLOWER),

            (1 to -6) to Tile(null, null, TileType.LEAF), (2 to -6) to Tile(null, null, TileType.LEAF),
            (3 to -6) to Tile(null, null, TileType.FLOWER), (4 to -6) to Tile(null, null, TileType.FRUIT),

            (2 to -7) to Tile(null, null, TileType.FRUIT)

        )
        gameState.currentPlayer.bonsaiTree = playerBonsaiTree
        val game = BonsaiGame()
        game.currentBonsaiGameState = gameState
        rootService.currentGame = game
        assertEquals(true, rootService.treeService.isMinimalAndCorrect(Pair(-1, -1)) )
        assertEquals(true, rootService.treeService.isMinimalAndCorrect(Pair(1, -1)) )
        assertEquals(true, rootService.treeService.isMinimalAndCorrect(Pair(-1, -3)) )
        assertEquals(true, rootService.treeService.isMinimalAndCorrect(Pair(4, -5)) )
        assertEquals(true, rootService.treeService.isMinimalAndCorrect(Pair(1, -6)) )
        assertEquals(true, rootService.treeService.isMinimalAndCorrect(Pair(2, -6)) )
        assertFalse( rootService.treeService.isMinimalAndCorrect(Pair(5, -5)))
    }
}
