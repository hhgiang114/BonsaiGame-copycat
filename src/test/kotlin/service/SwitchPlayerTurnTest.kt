package service

import entity.ColorType
import entity.GoalTileType
import entity.Player
import entity.PlayerType
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

/**
 * test the function SwitchPlayerTurn
 */
class SwitchPlayerTurnTest {

    /**
     * set up the game with 3 players
     */
    private fun setUpGame(): RootService {
        val rootService = RootService()
        val gameService = GameService(rootService)
        val player1 = Player("Tom", PlayerType.HUMAN, true, ColorType.RED)
        val player2 = Player("Tomy", PlayerType.HUMAN, true, ColorType.BLACK)
        val player3 = Player("Tomi", PlayerType.HUMAN, true, ColorType.PURPLE)
        val playerOrder = mutableListOf(player1, player2, player3)
        val goalTiles = mutableListOf(GoalTileType.BLUE, GoalTileType.PINK, GoalTileType.GREEN)
        gameService.startNewGame(playerOrder, false, goalTiles)
        return rootService
    }

    /**
     * test if switching turn correct implemented
     */
    @Test
      fun `switchPlayerTurn should move to next player`() {
          val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        assertEquals("Tom", gameState.currentPlayer.name)

        rootService.gameService.switchPlayerTurn()
        assertEquals("Tomy", gameState.currentPlayer.name)

        rootService.gameService.switchPlayerTurn()
        assertEquals("Tomi", gameState.currentPlayer.name)

        rootService.gameService.switchPlayerTurn()
        assertEquals("Tom", gameState.currentPlayer.name)
    }

}
