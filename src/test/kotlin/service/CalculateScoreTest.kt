package service

import entity.*
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

/**
 * Test class for [GameService.calculateScore]
 */
class CalculateScoreTest {

    //The game state
    private fun setUpGame(): RootService {
        val rootService = RootService()

        //SETUP for game
        val players = mutableListOf(
            Player("Alice", PlayerType.HUMAN, true, ColorType.RED),
            Player("Bob", PlayerType.HUMAN, true, ColorType.BLUE),
        )

        val faceUpCards = mutableListOf(
            MasterCard(mutableListOf(TileType.LEAF, TileType.LEAF), 26),
            MasterCard(mutableListOf(TileType.ANY), 26)
        )

        val gameState = BonsaiGameState(
            currentPlayer = players.first(), players = players,
            botSpeed = 1, currentState = States.END_TURN
        )

        gameState.zenDeck.isEmpty()
        gameState.faceUpCards.addAll(faceUpCards)

        //SETUP for player
        val playerBonsaiTree = mutableMapOf(
            (0 to 0) to Tile(null, null, TileType.WOOD), (-2 to -1) to Tile(null, null, TileType.FLOWER),
            (-1 to -1) to Tile(null, null, TileType.LEAF), (0 to -1) to Tile(null, null, TileType.WOOD),

            (-2 to -2) to Tile(null, null, TileType.FRUIT), (-1 to -2) to Tile(null, null, TileType.LEAF),
            (0 to -2) to Tile(null, null, TileType.LEAF), (1 to -2) to Tile(null, null, TileType.WOOD),
            (2 to -2) to Tile(null, null, TileType.LEAF),

            (-1 to -3) to Tile(null, null, TileType.LEAF), (0 to -3) to Tile(null, null, TileType.WOOD),
            (1 to -3) to Tile(null, null, TileType.WOOD), (2 to -3) to Tile(null, null, TileType.WOOD),
            (3 to -3) to Tile(null, null, TileType.LEAF), (4 to -3) to Tile(null, null, TileType.FLOWER),

            (0 to -4) to Tile(null, null, TileType.LEAF), (1 to -4) to Tile(null, null, TileType.WOOD),
            (2 to -4) to Tile(null, null, TileType.LEAF), (3 to -4) to Tile(null, null, TileType.WOOD),

            (0 to -5) to Tile(null, null, TileType.FLOWER), (1 to -5) to Tile(null, null, TileType.WOOD),
            (2 to -5) to Tile(null, null, TileType.LEAF), (3 to -5) to Tile(null, null, TileType.LEAF),
            (4 to -5) to Tile(null, null, TileType.LEAF), (5 to -5) to Tile(null, null, TileType.FLOWER),

            (1 to -6) to Tile(null, null, TileType.LEAF), (2 to -6) to Tile(null, null, TileType.LEAF),
            (3 to -6) to Tile(null, null, TileType.FLOWER), (2 to -7) to Tile(null, null, TileType.FRUIT)
        )

        val playerCollectedCard = mutableListOf(
            GrowthCard(TileType.LEAF, 3),
            MasterCard(mutableListOf(TileType.ANY), 24),
            ParchmentCard(TileType.LEAF, null, 1, 39),
            ParchmentCard(TileType.FLOWER, null, 2, 37),
            ParchmentCard(null, CardType.MASTERCARD, 2, 34)
        )

        val playerClaimedGoal = mutableListOf(
            GoalTile(GoalTileType.BROWN, 0, 5)
        )

        gameState.currentPlayer.collectedCards = playerCollectedCard
        gameState.currentPlayer.bonsaiTree = playerBonsaiTree
        gameState.currentPlayer.claimedGoals = playerClaimedGoal

        val game = BonsaiGame()
        game.currentBonsaiGameState = gameState

        rootService.currentGame = game

        return rootService
    }

    /**
     * Test for calculate score
     */
    @Test
    fun `test calculate score `() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState

        checkNotNull(gameState) { "No active game state." }

        assertEquals(
            intArrayOf(39, 14, 19, 5, 25, 102).toList(),
            rootService.gameService.calculateScore(player = gameState.currentPlayer)
        )
    }

}