package service

import entity.*
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

/**
 * Test class for [TreeService.playTile], [TreeService.canPlayTile]
 */
class PlayTileTest {
    private fun setUpGame(): RootService {
        val rootService = RootService()

        //SETUP for game
        val players = mutableListOf(
            Player("Alice", PlayerType.HUMAN, true, ColorType.RED),
            Player("Bob", PlayerType.HUMAN, true, ColorType.BLUE),
            Player("Tomi", PlayerType.HUMAN, true, ColorType.BLACK)
        )

        ///val zenDeck = mutableListOf()

        val faceUpCards = mutableListOf(
            MasterCard(mutableListOf(TileType.LEAF, TileType.LEAF), 26),
            MasterCard(mutableListOf(TileType.ANY), 26)
        )

        val gameState = BonsaiGameState(
            currentPlayer = players.first(),
            players = players,
            botSpeed = 1,
            currentState = States.CULTIVATE
        )

        gameState.zenDeck.isEmpty()
        gameState.faceUpCards.addAll(faceUpCards)

        //SETUP for player


        val playerBonsaiTree = mutableMapOf(
            (0 to 0) to Tile(null, null, TileType.WOOD), //ROOT
            (0 to -1) to Tile(null, null, TileType.WOOD),
            //(-1 to -1) to Tile(null, null, TileType.LEAF),

            //(-1 to -2) to Tile(null, null, TileType.FLOWER),
            (0 to -2) to Tile(null, null, TileType.WOOD),
            (1 to -2) to Tile(null, null, TileType.WOOD),
            //(2 to -2) to Tile(null, null, TileType.LEAF),
            //(3 to -2) to Tile(null, null, TileType.FLOWER),

            //(0 to -3) to Tile(null, null, TileType.FLOWER),
            (1 to -3) to Tile(null, null, TileType.LEAF),
            (2 to -3) to Tile(null, null, TileType.LEAF),

//            (2 to -4) to Tile(null, null, TileType.FRUIT)
        )

        val playerCollectedCard = mutableListOf(
            ToolCard(41),
            MasterCard(mutableListOf(TileType.LEAF, TileType.FRUIT), 27),
            HelperCard(TileType.LEAF, 35),
            GrowthCard(TileType.LEAF, 3),
            MasterCard(mutableListOf(TileType.ANY), 24),
        )


        gameState.currentPlayer.collectedCards = playerCollectedCard
        gameState.currentPlayer.bonsaiTree = playerBonsaiTree
        //   gameState.currentPlayer.claimedGoals = playerClaimedGoal

        val game = BonsaiGame()
        game.currentBonsaiGameState = gameState

        rootService.currentGame = game

        return rootService
    }

    /**
     * test canPlayTile returns true for valid placement of leaf, wood, flower, fruit
     */
    @Test
    fun `test canPlayTile returns true for valid play`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)

        val tileWood = Tile(null, null, TileType.WOOD)
        val tileLeaf = Tile(null, null, TileType.LEAF)
        val tileFlower = Tile(null, null, TileType.FLOWER)
        val tileFruit = Tile(null, null, TileType.FRUIT)

        gameState.currentPlayer.personalSupply.add(tileWood)
        gameState.currentPlayer.personalSupply.add(tileLeaf)
        gameState.currentPlayer.personalSupply.add(tileFlower)
        gameState.currentPlayer.personalSupply.add(tileFruit)

        gameState.currentPlayer.playableTilesCopy.add(TileType.LEAF)
        gameState.currentPlayer.playableTilesCopy.add(TileType.WOOD)
        gameState.currentPlayer.playableTilesCopy.add(TileType.FLOWER)
        gameState.currentPlayer.playableTilesCopy.add(TileType.FRUIT)


        assertTrue(rootService.treeService.canPlayTile(tileWood, Pair(1, -1)))
        assertTrue(rootService.treeService.canPlayTile(tileLeaf, Pair(2, -2)))

        assertTrue(rootService.treeService.canPlayTile(tileFlower, Pair(0, -3)))
        assertTrue(rootService.treeService.canPlayTile(tileFruit, Pair(2, -4)))
    }

    /**
     * test canPlayTile(tile) fails
     */
    @Test
    fun `test when tile not in hand`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)

        val tile = Tile(null, null, TileType.FLOWER)
        gameState.currentPlayer.playableTilesCopy.add(TileType.FLOWER)

        assertFails { rootService.treeService.canPlayTile(tile) }
    }

    /**
     * test canPlayTile(tile, position) fails
     */
    @Test
    fun `test when position is occupied`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)

        val tile = Tile(null, null, TileType.WOOD)

        gameState.currentPlayer.personalSupply.add(tile)
        gameState.currentPlayer.playableTilesCopy.add(TileType.WOOD)

        assertFails { rootService.treeService.canPlayTile(tile, Pair(0, -2)) }
    }

    /**
     * test playTile(tile, position) successfully
     */

    @Test
    fun `test place tile`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)

        val tile = Tile(null, null, TileType.FLOWER)
        gameState.currentPlayer.personalSupply.add(tile)
        gameState.currentPlayer.playableTilesCopy.add(TileType.FLOWER)

        assertTrue(gameState.currentPlayer.personalSupply.contains(tile))
        assertTrue(gameState.currentPlayer.playableTilesCopy.contains(TileType.FLOWER))

        rootService.treeService.playTile(tile, Pair(3, -3))

        assertEquals(tile, gameState.currentPlayer.bonsaiTree[Pair(3, -3)])
        assertFalse(gameState.currentPlayer.personalSupply.contains(tile))
        assertFalse(gameState.currentPlayer.playableTilesCopy.contains(TileType.FLOWER))

    }

    /**
     * test playTile(tile, position) fails
     */
    @Test
    fun `test place tile fail`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)

        val tile = Tile(null, null, TileType.FLOWER)
        gameState.currentPlayer.personalSupply.add(tile)
        gameState.currentPlayer.playableTilesCopy.add(TileType.FLOWER)

        assertFails { rootService.treeService.playTile(tile, Pair(-1, -1)) }
    }

    /**
     * Tests the playTile function if a player to lay a tile, that it not playable, on his tree.
     *
     * If the player has a tile type Any in his playable tile list
     * If the tile is played remove the tile type Any from the personal playable tile copy.
     */
    @Test
    fun `test PlayTile tile type Any`() {
        val rootService = setUpGame()

        val game = rootService.currentGame
        checkNotNull(game)

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)

        //println(gameState.currentPlayer.playableTiles)
        val tileToPlay = Tile(null, null, TileType.FLOWER)

        gameState.currentPlayer.personalSupply.add(tileToPlay)
        gameState.currentPlayer.playableTilesCopy = mutableListOf(TileType.ANY, TileType.WOOD, TileType.LEAF)

        assertTrue(gameState.currentPlayer.personalSupply.contains(tileToPlay))
        assertTrue(gameState.currentPlayer.playableTilesCopy.contains(TileType.ANY))

        rootService.treeService.playTile(tileToPlay, Pair(3, -3))

        assertEquals(tileToPlay, gameState.currentPlayer.bonsaiTree[Pair(3, -3)])
        assertFalse(gameState.currentPlayer.playableTilesCopy.contains(TileType.ANY))
        assertFalse(gameState.currentPlayer.personalSupply.contains(tileToPlay))

    }

    /**
     * Tests for canPlayTile, tests if both neighbor tiles are leafs, fruit can be placed
     */
    @Test
    fun testNeighbourTilesForFruit() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)
        val tile = Tile(null, null, TileType.FRUIT)
        gameState.currentPlayer.personalSupply.add(tile)
        gameState.currentPlayer.playableTilesCopy.add(
            TileType.FRUIT
        )
        assertTrue(rootService.treeService.canPlayTile(tile, Pair(2, -4)))
        assertFalse(rootService.treeService.canPlayTile(tile, Pair(1, -4)))
    }

    /**
     * Test for network
     */
    @Test
    fun testPlayTileMessagesIfConnected() {
        val rootService = setUpGame()
        val treeService = TreeService(rootService)
        rootService.networkService.setConnectionStateTest(ConnectionState.PLAYING_MY_TURN)
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)
        val tile = Tile(2, -2, TileType.LEAF)
        gameState.currentPlayer.personalSupply.add(tile)
        gameState.currentPlayer.playableTilesCopy.add(
            TileType.LEAF
        )

        rootService.networkService.hasMeditated = true
        treeService.playTile(tile, Pair(2, -2))

        //println("Meditate Message Tiles: ${rootService.networkService.toBeSentMeditateMessage.playedTiles}")
        //println("Cultivate Message Tiles: ${rootService.networkService.toBeSentCultivateMessage.playedTiles}")
        assertTrue(
            rootService.networkService.toBeSentMeditateMessage.playedTiles.contains(
                TileType.LEAF to Pair(2, -2)
            )
        )
        assertFalse(
            rootService.networkService.toBeSentCultivateMessage.playedTiles.contains(
                TileType.LEAF to Pair(2, -2)
            )
        )
    }
    /**
     * Test for network
     */
    @Test
    fun testPlayTileMessagesIfDisConnected() {
        val rootService = setUpGame()
        val treeService = TreeService(rootService)
        rootService.networkService.setConnectionStateTest(ConnectionState.DISCONNECTED)
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)
        val tile = Tile(2, -2, TileType.LEAF)
        gameState.currentPlayer.personalSupply.add(tile)
        gameState.currentPlayer.playableTilesCopy.add(
            TileType.LEAF
        )

        treeService.playTile(tile, Pair(2, -2))

        //println("Meditate Message Tiles: ${rootService.networkService.toBeSentMeditateMessage.playedTiles}")
        //println("Cultivate Message Tiles: ${rootService.networkService.toBeSentCultivateMessage.playedTiles}")

        assertFalse(
            rootService.networkService.toBeSentMeditateMessage.playedTiles.contains(
                TileType.LEAF to Pair(2, -2)
            )
        )
        assertFalse(
            rootService.networkService.toBeSentCultivateMessage.playedTiles.contains(
                TileType.LEAF to Pair(2, -2)
            )
        )
    }

    /**
     * Test for network
     */
    @Test
    fun testCanPlayIfNoTilesInHand() {
        val rootService = setUpGame()
        val treeService = TreeService(rootService)
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState)
        val tile = Tile(2, 2, TileType.LEAF)
        gameState.currentPlayer.personalSupply.clear()
        assertFails { treeService.canPlayTile(tile) }
    }
}






