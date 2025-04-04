package service

import kotlin.test.*
import entity.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows


/**
 * Tests if showWinner works correctly:
 * - if one player has the only highest score
 * - in a two player tie
 * - in a three player tie
 * - in a four player tie
 * - with no running game
 */

class ShowWinnerTest {
    private fun setUpGame(): RootService {
        val rootService = RootService()
        val gameService = GameService(rootService)
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.BLACK)
        val player2 = Player("Tom2", PlayerType.HUMAN, true, ColorType.RED)
        val player3 = Player("Tom3", PlayerType.HUMAN, true, ColorType.BLUE)
        val player4 = Player("Tom4", PlayerType.HUMAN, true, ColorType.PURPLE)
        val playerOrder = mutableListOf(player1, player2, player3, player4)
        val goalTiles = mutableListOf(GoalTileType.BLUE, GoalTileType.PINK, GoalTileType.GREEN)
        gameService.startNewGame(playerOrder, false, goalTiles)
        return rootService
    }

    /**
     * Tests if showWinner works correctly if the game is not over yet
     */
    @Test
    fun testGameOver() {
        val rootService = setUpGame()
        val gameService = rootService.gameService

        assertThrows<IllegalStateException> {
            gameService.showWinner()
        }
    }


    /**
     * Tests if player with the highest score wins
     */

    @Test
    fun testSingleWinner() {
        val rootService = setUpGame()
        val gameService = rootService.gameService
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState) { "Game state is null." }
        gameState.endGameCounter = gameState.players.size

        // Set scores
        gameState.players[0].score = 10
        gameState.players[1].score = 8
        gameState.players[2].score = 70
        gameState.players[3].score = 5

        gameService.showWinner()

        val winnerOrder = gameState.players.sortedWith(compareByDescending<Player> { it.score }
            .thenByDescending { gameState.players.indexOf(it) })

        val winnerNames = winnerOrder.map { it.name }

        val expectedWinnerNames = listOf("Tom3", "Tom", "Tom2", "Tom4")

        assertEquals(expectedWinnerNames, winnerNames)
    }

    /**
     * Tests if the winner order is correct in case of a two player tie
     */

    @Test
    fun testTieWithTwoPlayers() {
        val rootService = setUpGame()
        val gameService = rootService.gameService
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState) { "Game state is null." }
        gameState.endGameCounter = gameState.players.size

        // Set scores
        gameState.players[0].score = 10
        gameState.players[1].score = 70
        gameState.players[2].score = 70
        gameState.players[3].score = 5

        gameService.showWinner()

        val winnerOrder = gameState.players.sortedWith(compareByDescending<Player> { it.score }
            .thenByDescending { gameState.players.indexOf(it) })

        val winnerNames = winnerOrder.map { it.name }

        val expectedWinnerNames = listOf("Tom3", "Tom2", "Tom", "Tom4")

        assertEquals(expectedWinnerNames, winnerNames)
    }

    /**
     * Tests if the winner order is correct in case of a three player tie
     */

    @Test
    fun testTieWithThreePlayers() {
        val rootService = setUpGame()
        val gameService = rootService.gameService
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState) { "Game state is null." }
        gameState.endGameCounter = gameState.players.size

        // Set scores
        gameState.players[0].score = 70
        gameState.players[1].score = 8
        gameState.players[2].score = 70
        gameState.players[3].score = 70

        gameService.showWinner()

        val winnerOrder = gameState.players.sortedWith(compareByDescending<Player> { it.score }
            .thenByDescending { gameState.players.indexOf(it) })

        val winnerNames = winnerOrder.map { it.name }

        val expectedWinnerNames = listOf("Tom4", "Tom3", "Tom", "Tom2")

        assertEquals(expectedWinnerNames, winnerNames)
    }

    /**
     * Tests if the winner order is correct in case of a four player tie
     */

    @Test
    fun testTieWithAllPlayers() {
        val rootService = setUpGame()
        val gameService = rootService.gameService
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState) { "Game state is null." }
        gameState.endGameCounter = gameState.players.size

        // Set scores
        gameState.players[0].score = 10
        gameState.players[1].score = 10
        gameState.players[2].score = 10
        gameState.players[3].score = 10

        //gameService.showWinner()

//        val winnerOrder = gameState.players.sortedWith(compareByDescending<Player> { it.score }
//            .thenByDescending { gameState.players.indexOf(it) })
//
//        val winnerNames = winnerOrder.map { it.name }
//
//        println(winnerOrder)
        val expectedWinnerNames = listOf("Tom4", "Tom3", "Tom2", "Tom")
        println("Test: " + gameService.showWinner())
        assertEquals(expectedWinnerNames, gameService.showWinner().map { it.name })
    }

    /**
     * Tests if show winner fails if no game was started
     */

    @Test
    fun testEmptyGameState() {
        val rootService = RootService()
        val gameService = GameService(rootService)

        // No game state is set
        val exception = assertFailsWith<IllegalStateException> {
            gameService.showWinner()
        }
        assertEquals("No game was started.", exception.message)
    }


}
