package service

import entity.*
import kotlin.test.Test
import kotlin.test.assertFalse

/**
 * Class to test the [GameService.createGoalTiles] function in [GameService].
 */
class CreateGoalTilesTest {

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
        val playerOrder = mutableListOf(player1, player2)
        val goalTiles = mutableListOf(GoalTileType.BROWN, GoalTileType.ORANGE, GoalTileType.GREEN)
        gameService.startNewGame(playerOrder, false, goalTiles)
        return rootService
    }

    /**
     * Checks the goal tile list for a Bonsai game with two players.
     *
     * if two players then remove the middle goal Tile from each chosen goal Tile categories
     */
    @Test
    fun testGoalTilesForTwoPlayers() {
        val rootService = setUpGame()
        val goalTiles = rootService.gameService.createGoalTiles(
            mutableListOf(GoalTileType.BROWN, GoalTileType.ORANGE, GoalTileType.GREEN), 2)

        assertFalse(goalTiles.contains(GoalTile(GoalTileType.ORANGE, 4, 11)))
        assertFalse(goalTiles.contains(GoalTile(GoalTileType.BROWN, 10, 10)))
        assertFalse(goalTiles.contains(GoalTile(GoalTileType.GREEN, 7, 9)))
    }
}
