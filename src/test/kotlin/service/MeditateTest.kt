package service

import entity.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*
import kotlin.concurrent.schedule
import kotlin.test.Test
import kotlin.test.assertFails

/**
 * Test class for [PlayerActionService.meditate]
 */
class MeditateTest {

    //The game state before meditate action
    private fun setUpGame(): RootService {
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
            MasterCard(mutableListOf(TileType.LEAF, TileType.LEAF), 26), //Position 2
            MasterCard(mutableListOf(TileType.ANY), 25), //Position 3
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
     * in this case is tool card, player receives no extra tile,
     * but the personal supply capacity is expanded to 7
     * and the drawn card is moved to the personal collected card stack
     */

    @Test
    fun `test draw the card in position 0`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState

        checkNotNull(gameState) { "No active game state." }

        assertEquals(5, gameState.currentPlayer.tileCapacity)
        assertEquals(3, gameState.currentPlayer.playableTiles.size)
        assertEquals(1, gameState.currentPlayer.collectedCards.size)

        rootService.playerActionService.meditate(0, null)
        assertEquals(7, gameState.currentPlayer.tileCapacity)
        assertEquals(3, gameState.currentPlayer.playableTiles.size)
        assertEquals(2, gameState.currentPlayer.collectedCards.size)
    }

    /**
     * in this case : Position 1 : the player should choose to have WOOD or LEAF Tile
     * the drawnCard is [HelperCard]
     * checked if : the card is added to the collectedCards of the currentPlayer
     * and if the state is Using_Helper
     * and if the chosen Tile of type WOOD is added to the personalSupply
     * and checked also : call meditate with invalid ChosenTile or with null value
     * TODO : Check refreshables
     */
    @Test
    fun `test draw the card in position 1`() {
        val rootService = setUpGame()
        val game = rootService.currentGame
        checkNotNull(game)
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        assertEquals(5, gameState.currentPlayer.tileCapacity)
        assertEquals(3, gameState.currentPlayer.playableTiles.size)
        assertEquals(1, gameState.currentPlayer.personalSupply.size)
        assertEquals(1, gameState.currentPlayer.collectedCards.size)

        rootService.playerActionService.meditate(1, TileType.WOOD)
        assertEquals(TileType.WOOD, gameState.currentPlayer.personalSupply[1].tileType)
        assertEquals(35, gameState.currentPlayer.collectedCards[1].id)
        assertEquals(CardType.HELPERCARD, gameState.currentPlayer.collectedCards[1].cardType)
        assertEquals(States.USING_HELPER, gameState.currentState)
        Timer().schedule(1000){assert(gameState.currentPlayer.hasPlayed)}

        //rootService.treeService.playTile(Tile(null, null, TileType.WOOD), Pair(0, -1))
        assertFails { rootService.playerActionService.meditate(1, null) }
        assertFails { rootService.playerActionService.meditate(1, TileType.FLOWER) }

    }

    /**
     *  in this case: position 2 -> player receives a wood tile and flower tile in personal supply
     *  drawn card is master card, player receives the tile(s) whose type is shown on master.
     *  in this situation MasterCard(mutableListOf(TileType.LEAF, TileType. LEAF ) -> 2 leaf tiles
     *  and the drawn card is moved to the personal collected card stack
     */

    @Test
    fun `test draw the card in position 2`() {
        val rootService = setUpGame()

        val game = rootService.currentGame
        checkNotNull(game)

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        assertEquals(5, gameState.currentPlayer.tileCapacity)
        assertEquals(3, gameState.currentPlayer.playableTiles.size)
        assertEquals(1, gameState.currentPlayer.personalSupply.size)
        assertEquals(1, gameState.currentPlayer.collectedCards.size)

        rootService.playerActionService.meditate(2, null)
        assertEquals(5, gameState.currentPlayer.tileCapacity)
        assertEquals(3, gameState.currentPlayer.playableTiles.size)
        assertEquals(5, gameState.currentPlayer.personalSupply.size)
        assertEquals(2, gameState.currentPlayer.collectedCards.size)

        assertEquals(TileType.LEAF, gameState.currentPlayer.personalSupply.last().tileType)
        assertEquals(TileType.LEAF, gameState.currentPlayer.personalSupply[3].tileType)
        assertEquals(TileType.FLOWER, gameState.currentPlayer.personalSupply[2].tileType)
        assertEquals(TileType.WOOD, gameState.currentPlayer.personalSupply[1].tileType)
        assertEquals(TileType.WOOD, gameState.currentPlayer.personalSupply[0].tileType)
    }


    /**
     * in this case: position 3 -> player receives a leaf tile and fruit tile in personal supply
     * drawn card is growth card, player's playable tiles is increased
     * in this situation GrowthCard(TileType.FLOWER, 9)
     * and the drawn card is moved to the personal collected card stack
     */
    @Test
    fun `test draw the card in position 3`() {
        val rootService = setUpGame()

        val game = rootService.currentGame
        checkNotNull(game)

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        assertEquals(5, gameState.currentPlayer.tileCapacity)
        assertEquals(3, gameState.currentPlayer.playableTiles.size)
        assertEquals(1, gameState.currentPlayer.personalSupply.size)
        assertEquals(1, gameState.currentPlayer.collectedCards.size)
        println(gameState.currentPlayer.playableTiles)

        rootService.playerActionService.meditate(3, null)
        assertEquals(5, gameState.currentPlayer.tileCapacity)
        //assertEquals(4, gameState.currentPlayer.playableTiles.size)
        println(gameState.currentPlayer.playableTiles)
        assertEquals(3, gameState.currentPlayer.personalSupply.size)
        //println(gameState.currentPlayer.personalSupply)
        assertEquals(2, gameState.currentPlayer.collectedCards.size)
        gameState.currentPlayer.personalSupply.addAll(
            listOf(
                Tile(null, null, TileType.WOOD), Tile(null, null, TileType.WOOD)
            )
        )
        rootService.playerActionService.chooseTile(TileType.FRUIT)
        //assertEquals(false, gameState.currentPlayer.hasPlayed)
        Timer().schedule(1000){
            assertEquals(States.DISCARDING, gameState.currentState)
        }

        assertEquals(6, gameState.currentPlayer.personalSupply.size)
        gameState.currentPlayer.personalSupply.removeAll(
            listOf(
                Tile(null, null, TileType.WOOD), Tile(null, null, TileType.WOOD)
            )
        )
        //assertFails { rootService.playerActionService.chooseTile(TileType.FRUIT) }
       // assertEquals(TileType.FRUIT, gameState.currentPlayer.personalSupply.last().tileType)

    }
}
