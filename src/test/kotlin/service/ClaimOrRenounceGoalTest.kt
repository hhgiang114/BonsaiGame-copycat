package service

import entity.*
import kotlin.test.*


/**
 * Tests if method claimOrRenounceGoal works correctly
 */
class ClaimOrRenounceGoalTest {

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
        val goalTileBrownT0 = GoalTile(GoalTileType.BROWN, 0, 5)
        val goalTileBrownT1 = GoalTile(GoalTileType.BROWN, 1, 10)
        val goalTileBrownT2 = GoalTile(GoalTileType.BROWN, 2, 15)
        val goalTileGreenT0 = GoalTile(GoalTileType.GREEN, 0, 6)
        val goalTileGreenT1 = GoalTile(GoalTileType.GREEN, 1, 9)
        val goalTileGreenT2 = GoalTile(GoalTileType.GREEN, 2, 12)
        val goalTilePinkT0 = GoalTile(GoalTileType.PINK, 0, 8)
        val goalTilePinkT1 = GoalTile(GoalTileType.PINK, 1, 12)
        val goalTilePinkT2 = GoalTile(GoalTileType.PINK, 2, 16)
        val goalTileOrangeT0 = GoalTile(GoalTileType.ORANGE, 0, 9)
        val goalTileOrangeT1 = GoalTile(GoalTileType.ORANGE, 1, 11)
        val goalTileOrangeT2 = GoalTile(GoalTileType.ORANGE, 2, 13)
        val goalTileBlueT0 = GoalTile(GoalTileType.BLUE, 0, 7)
        val goalTileBlueT1 = GoalTile(GoalTileType.BLUE, 1, 10)
        val goalTileBlueT2 = GoalTile(GoalTileType.BLUE, 2, 14)
        val gameState1 = BonsaiGameState(player1, mutableListOf(player1, player2), 2, States.MEDITATE)
        gameState1.goalTiles.add(goalTileBrownT0)
        gameState1.goalTiles.add(goalTileBrownT1)
        gameState1.goalTiles.add(goalTileBrownT2)
        gameState1.goalTiles.add(goalTileOrangeT0)
        gameState1.goalTiles.add(goalTileOrangeT1)
        gameState1.goalTiles.add(goalTileOrangeT2)
        gameState1.goalTiles.add(goalTileGreenT0)
        gameState1.goalTiles.add(goalTileGreenT1)
        gameState1.goalTiles.add(goalTileGreenT2)
        gameState1.goalTiles.add(goalTilePinkT0)
        gameState1.goalTiles.add(goalTilePinkT1)
        gameState1.goalTiles.add(goalTilePinkT2)
        gameState1.goalTiles.add(goalTileBlueT0)
        gameState1.goalTiles.add(goalTileBlueT1)
        gameState1.goalTiles.add(goalTileBlueT2)
        val history = History()
        history.currentPosition = 1
        history.gameStates.add(gameState1)
        game.currentBonsaiGameState = gameState1
        game.history = history
        rootService.currentGame = game

        return rootService
    }

    // help function to get current player
    private fun getCurrentPlayer(rootService: RootService): Player {
        val currentGameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        return currentGameState.currentPlayer
    }


    /**
     * Tests if player can claim brown tier 0 goal tile
     */
    @Test
    fun testGoalTileBrownT0() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        for (i in 0..7) {
            player.bonsaiTree[Pair(1, -i)] = Tile(1, -i, TileType.WOOD)
        }

        playerActionService.claimOrRenounceGoal(true, GoalTileType.BROWN, 0)
        assertTrue(player.claimedGoals.isNotEmpty())
        assertEquals(GoalTileType.BROWN, player.claimedGoals.first().goalTileType)
    }

    /**
     * Tests if player can claim tier 0 orange goal tile
     */
    @Test
    fun testGoalTileOrangeT0() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        for (i in 0..2) {
            player.bonsaiTree[Pair(i, -i)] = Tile(i, -i, TileType.FRUIT)
        }
        playerActionService.claimOrRenounceGoal(true, GoalTileType.ORANGE, 0)
        assertTrue(player.claimedGoals.isNotEmpty())
        assertEquals(GoalTileType.ORANGE, player.claimedGoals.first().goalTileType)
    }

    /**
     * Tests if player can claim tier 0 green goal tile
     */
    @Test
    fun testGoalTileGreenT0() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        for (i in 0..4) {
            player.bonsaiTree[Pair(i, 3)] = Tile(i, 3, TileType.LEAF)
        }
        playerActionService.claimOrRenounceGoal(true, GoalTileType.GREEN, 0)
        assertTrue(player.claimedGoals.isNotEmpty())
        assertEquals(GoalTileType.GREEN, player.claimedGoals.first().goalTileType)
    }

    /**
     * Tests if player can claim tier 1 green goal tile after renouncing the tier 0 version
     */
    @Test
    fun testGoalTileGreenT1() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        for (i in 1..5) {
            player.bonsaiTree[Pair(1, -i)] = Tile(1, -i, TileType.LEAF)
        }
        playerActionService.claimOrRenounceGoal(false, GoalTileType.GREEN, 0)
        assertTrue(player.claimedGoals.isEmpty())
        for (i in 7..10) {
            player.bonsaiTree[Pair(1, -i)] = Tile(1, -i, TileType.LEAF)
        }
        player.bonsaiTree[Pair(1, -6)] = Tile(1, -6, TileType.LEAF)
        playerActionService.claimOrRenounceGoal(true, GoalTileType.GREEN, 1)
        assertTrue(player.claimedGoals.isNotEmpty())
        assertEquals(GoalTileType.GREEN, player.claimedGoals.first().goalTileType)
    }

    /**
     * Tests if player can claim tier 1 green goal tile after claiming the tier 0 version
     */
    @Test
    fun testGoalTileGreenT1withT0() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        for (i in 1..5) {
            player.bonsaiTree[Pair(1, -i)] = Tile(1, -i, TileType.LEAF)
        }
        playerActionService.claimOrRenounceGoal(true, GoalTileType.GREEN, 0)
        assertTrue(player.claimedGoals.isNotEmpty())
        for (i in 7..10) {
            player.bonsaiTree[Pair(1, -i)] = Tile(1, -i, TileType.LEAF)
        }
        player.bonsaiTree[Pair(1, -6)] = Tile(1, -6, TileType.LEAF)
        assertFails { playerActionService .claimOrRenounceGoal(true, GoalTileType.GREEN, 1)}
        assertTrue(player.claimedGoals.size==1)
        assertEquals(GoalTileType.GREEN, player.claimedGoals.first().goalTileType)
    }

    /**
     * Tests if player can claim pink tier 0 goal tile
     */
    @Test
    fun testGoalTilePinkT0() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        for (i in 4..6) {
            player.bonsaiTree[Pair(i, 0)] = Tile(i, 0, TileType.FLOWER)
        }
        playerActionService.claimOrRenounceGoal(true, GoalTileType.PINK, 0)
        assertTrue(player.claimedGoals.isNotEmpty())
        assertEquals(GoalTileType.PINK, player.claimedGoals.first().goalTileType)
    }

    /**
     * Tests if player can claim blue tier 0 goal tile
     */
    @Test
    fun testGoalTileBlueT0() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        player.bonsaiTree[Pair(5, -3)] = Tile(5, -3, TileType.FLOWER)
        playerActionService.claimOrRenounceGoal(true, GoalTileType.BLUE, 0)
        assertTrue(player.claimedGoals.isNotEmpty())
        assertEquals(GoalTileType.BLUE, player.claimedGoals.first().goalTileType)
    }

    /**
     * Tests if player renounce goals get added in list
     */
    @Test
    fun testGoalTileRenounce() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        player.bonsaiTree[Pair(5, -2)] = Tile(5, -2, TileType.FLOWER)
        playerActionService.claimOrRenounceGoal(false, GoalTileType.BLUE, 0)
        assertTrue(player.renouncedGoals.isNotEmpty())
        assertTrue(player.claimedGoals.isEmpty())
    }

    /**
     * Tests if player renounce goals get added in list
     */
    @Test
    fun testClaimAfterRenounce() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        player.bonsaiTree[Pair(5, -2)] = Tile(5, -2, TileType.FLOWER)
        playerActionService.claimOrRenounceGoal(false, GoalTileType.BLUE, 0)
        assertFails { playerActionService.claimOrRenounceGoal(true, GoalTileType.BLUE, 0) }
        assertTrue(player.renouncedGoals.isNotEmpty())
        assertTrue(player.claimedGoals.isEmpty())
    }


    /**
     * Tests the case if the tiers in [PlayerActionService.canClaimOrRenounceGoal] are not 0 to 2
     * in this case the function should return false.
     */
    @Test
    fun testClaimTilesInvalid() {
        val rootService = setUpGame()
        val playerActionService = PlayerActionService(rootService)
        val player = getCurrentPlayer(rootService)
        val goalTilePink = GoalTile(GoalTileType.PINK, 0, 8)
        val goalTileBrown = GoalTile(GoalTileType.BROWN, 0, 5)
        val goalTileBlue = GoalTile(GoalTileType.BLUE, 0, 7)
        val goalTileOrange = GoalTile(GoalTileType.ORANGE, 0, 9)
        val goalTileGreen = GoalTile(GoalTileType.GREEN, 0, 6)

        val pinkTest = playerActionService.canClaimOrRenounceGoal(goalTilePink.goalTileType, 4)
        val brownTest = playerActionService.canClaimOrRenounceGoal(goalTileBrown.goalTileType, 4)
        val blueTest = playerActionService.canClaimOrRenounceGoal(goalTileBlue.goalTileType, 4)
        val orangeTest = playerActionService.canClaimOrRenounceGoal(goalTileOrange.goalTileType, 4)
        val greenTest = playerActionService.canClaimOrRenounceGoal(goalTileGreen.goalTileType, 4)

        assertEquals(false, pinkTest)
        assertEquals(false, brownTest)
        assertEquals(false, blueTest)
        assertEquals(false, orangeTest)
        assertEquals(false, greenTest)

        assertTrue(player.claimedGoals.isEmpty())
        assertTrue(player.renouncedGoals.isEmpty())
    }

}
