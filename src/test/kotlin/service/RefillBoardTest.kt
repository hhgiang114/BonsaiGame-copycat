package service

import entity.*
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import kotlin.test.assertFails

/**
 * test the gameService method refillBoard with different cases
 */
class RefillBoardTest {

    /**
     * set up the game and fill the zenDeck and the faceUpCards
     */
    private fun setUpGame(): RootService {
        val rootService = RootService()

        val players = mutableListOf(
            Player("Tom", PlayerType.HUMAN, true, ColorType.RED),
            Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE),
            Player("Tomi", PlayerType.HUMAN, true, ColorType.BLACK)
        )

        val zenDeck = mutableListOf(
            MasterCard(mutableListOf(TileType.WOOD, TileType.WOOD), 5),
            ParchmentCard(null, CardType.MASTERCARD, 2, 1),
            GrowthCard(TileType.WOOD, 1),
            GrowthCard(TileType.LEAF, 1)
        )

        val faceUpCards = mutableListOf(
            MasterCard(mutableListOf(TileType.LEAF, TileType.LEAF), 4),
            ParchmentCard(null, CardType.GROWTHCARD, 2, 7),
            GrowthCard(TileType.FRUIT, 8),
            GrowthCard(TileType.FLOWER, 9)
        )

        val gameState = BonsaiGameState(
            currentPlayer = players.first(),
            players = players,
            botSpeed = 1,
            currentState = States.START_TURN
        )

        gameState.zenDeck.addAll(zenDeck)
        gameState.faceUpCards.addAll(faceUpCards)

        val game = BonsaiGame()
        game.currentBonsaiGameState = gameState

        rootService.currentGame = game

        return rootService
    }

    /**
     * test to refill the board when removing the first face up Card
     */
    @Test
    fun `test refillBoard when removing the first faceUp Card`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        gameState.faceUpCards.removeAt(0)
        rootService.gameService.refillBoard()
        assertEquals(7 , gameState.faceUpCards[1].id)
        assertEquals(1 , gameState.faceUpCards.first().id)
        assertEquals(8 , gameState.faceUpCards[2].id)
        assertEquals(9 , gameState.faceUpCards[3].id)
        assertEquals( CardType.MASTERCARD, gameState.zenDeck.first().cardType)
    }

    /**
     * test to refillBoard when removing the second face up Card
     */
    @Test
    fun `test refillBoard when removing the second faceUp Card`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        gameState.faceUpCards.removeAt(1)
        rootService.gameService.refillBoard()
        assertEquals(1 , gameState.faceUpCards.first().id)
        assertEquals(4 , gameState.faceUpCards[1].id)
        assertEquals(8 , gameState.faceUpCards[2].id)
        assertEquals(9 , gameState.faceUpCards[3].id)
        assertEquals( CardType.MASTERCARD, gameState.zenDeck.first().cardType)
    }

    /**
     * test to refill the board when removing the third one
     */
    @Test
    fun `test refillBoard when removing the third faceUp Card`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        gameState.faceUpCards.removeAt(2)
        rootService.gameService.refillBoard()
        assertEquals(1 , gameState.faceUpCards.first().id)
        assertEquals(4 , gameState.faceUpCards[1].id)
        assertEquals(7 , gameState.faceUpCards[2].id)
        assertEquals(9 , gameState.faceUpCards[3].id)
        assertEquals( CardType.MASTERCARD, gameState.zenDeck.first().cardType)
    }

    /**
     * test to refillBoard when removing the fourth Card
     */
    @Test
    fun `test refillBoard when removing the fourth faceUp Card`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        gameState.faceUpCards.removeAt(3)
        rootService.gameService.refillBoard()
        assertEquals( CardType.MASTERCARD, gameState.zenDeck.first().cardType)
        assertEquals(1 , gameState.faceUpCards.first().id)
        assertEquals(4 , gameState.faceUpCards[1].id)
        assertEquals(7 , gameState.faceUpCards[2].id)
        assertEquals(8 , gameState.faceUpCards[3].id)

    }

    /**
     * Test refillBoard when the ZenDeck empty
     */
//    @Test
//    fun `test refillBoard with empty zenDeck`(){
//        val rootService = setUpGame()
//        val game = rootService.currentGame
//        checkNotNull(game)
//        val gameState = game.currentBonsaiGameState
//        checkNotNull(gameState) { "No active game state." }
//        gameState.zenDeck = mutableListOf()
//        assertFails { rootService.gameService.refillBoard() }
//    }


}

