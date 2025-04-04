package service

import entity.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*
import kotlin.concurrent.schedule
import kotlin.test.Test

/**
 * Test class for [PlayerActionService.meditate] for a growth card.
 */
class MeditateGrowthCardTest {

    //The game state before meditate action
    private fun setUp() : RootService {
        val rootService = RootService()

        //SETUP for game
        val players = mutableListOf(
            Player("Alice", PlayerType.HUMAN, true, ColorType.RED),
            Player("Bob", PlayerType.HUMAN, true, ColorType.BLUE),
            Player("Tomi", PlayerType.HUMAN, true, ColorType.BLACK)
        )

        val zenDeck = mutableListOf(
            MasterCard(mutableListOf(TileType.WOOD, TileType.WOOD), 21),
            ParchmentCard(null, CardType.MASTERCARD, 2, 34),
            GrowthCard(TileType.WOOD, 0),
            GrowthCard(TileType.LEAF, 3)
        )

        val faceUpCards = mutableListOf(
            ToolCard(44), //Position 0
            HelperCard(TileType.LEAF, 35), //Position 1
            MasterCard(mutableListOf(TileType.WOOD, TileType.LEAF, TileType.FRUIT), 33), //Position 2
            GrowthCard(TileType.WOOD, 1), //Position 3
        )

        val gameState = BonsaiGameState(
            currentPlayer = players.first(),
            players = players,
            botSpeed = 1,
            currentState = States.START_TURN
        )

        gameState.zenDeck.addAll(zenDeck)
        gameState.faceUpCards.addAll(faceUpCards)

        //SETUP for player
        gameState.currentPlayer.tileCapacity = 5

        gameState.currentPlayer.personalSupply = mutableListOf(Tile(null, null, TileType.WOOD))

        gameState.currentPlayer.collectedCards = mutableListOf(
            MasterCard(mutableListOf(TileType.LEAF, TileType.FRUIT), 27)
        )

        val game = BonsaiGame()
        game.currentBonsaiGameState = gameState

        rootService.currentGame = game

        return rootService
    }

    /**
     * card position 3 -> player receives a leaf tile and fruit tile in personal supply
     * a GrowthCard(TileType.WOOD, 1) is drawn
     * the player's playable tiles is increased by a Wood Tile
     * and the drawn card is moved to the personal collected card stack
     */
    @Test
    fun testMeditateGrowthCardPosition3() {
        val rootService = setUp()

        val game = rootService.currentGame
        checkNotNull(game)

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        assertEquals(5, gameState.currentPlayer.tileCapacity)
        assertEquals(3, gameState.currentPlayer.playableTiles.size)
        assertEquals(1, gameState.currentPlayer.personalSupply.size)
        assertEquals(1, gameState.currentPlayer.collectedCards.size)
        repeat(3) {
            gameState.currentPlayer.personalSupply.add(Tile(null, null, TileType.WOOD))
        }

        //println("personal supply:" + gameState.currentPlayer.personalSupply)

        rootService.playerActionService.meditate(3, null)
        //println("personal supply:" + gameState.currentPlayer.personalSupply)
        //println("capacity: " + gameState.currentPlayer.tileCapacity)
        assertEquals(5, gameState.currentPlayer.tileCapacity)
        assertEquals(4, gameState.currentPlayer.playableTiles.size)
        assertEquals(6, gameState.currentPlayer.personalSupply.size)
        assertEquals(2, gameState.currentPlayer.collectedCards.size)
        Timer().schedule(1000){ println("im here")
            assertEquals(States.DISCARDING , gameState.currentState)}

    }

}
