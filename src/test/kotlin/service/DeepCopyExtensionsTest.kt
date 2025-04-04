package service

import entity.*
import kotlin.test.*

/**
 * Tests the deep copies in deepCopyExtensions
 */

class DeepCopyExtensionsTest {

    /**
     * Tests deep copy functionality of BonsaiGameState
     *
     * tests if copied state is not the same instance as the original and checks the score
     */

    @Test
    fun testBonsaiGameStateDeepCopy() {

        val originalGameState = BonsaiGameState(
            currentPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED),
            players = mutableListOf(Player("Bob", PlayerType.HUMAN, isLocal = true, ColorType.BLUE)),
            botSpeed = 1,
            currentState = States.START_TURN
        ).apply {
            zenDeck = mutableListOf(GrowthCard(TileType.WOOD, 1))
            faceUpCards = mutableListOf(HelperCard(TileType.WOOD, 2))
            goalTiles = mutableListOf(GoalTile(GoalTileType.GREEN, 1, 10))
        }

        val copiedGameState = originalGameState.deepCopy()

        assertNotSame(originalGameState, copiedGameState)

        assertEquals(originalGameState, copiedGameState)

        originalGameState.currentPlayer.score = 100
        assertNotEquals(originalGameState.currentPlayer.score, copiedGameState.currentPlayer.score)
    }

    /**
     * Tests deep copy functionality of Player
     *
     * tests if copied state is not the same instance as the original and checks the score
     */

    @Test
    fun testPlayerDeepCopy() {
        val originalPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED).apply {
            bonsaiTree = mutableMapOf((0 to 0) to Tile(0, 0, TileType.WOOD))
            personalSupply = mutableListOf(Tile(1, 1, TileType.LEAF))
            collectedCards = mutableListOf(GrowthCard(TileType.WOOD, 1))
            claimedGoals = mutableListOf(GoalTile(GoalTileType.GREEN, 1, 10))
            score = 50
        }

        val copiedPlayer = originalPlayer.deepCopy()

        assertNotSame(originalPlayer, copiedPlayer)

        assertEquals(originalPlayer, copiedPlayer)

        originalPlayer.score = 100
        assertNotEquals(originalPlayer.score, copiedPlayer.score)
    }

    /**
     * Tests deep copy functionality of a players bonsai tree
     *
     * tests if copied state is not the same instance as the original
     */

    @Test
    fun testPlayerBonsaiTreeDeepCopy() {
        val originalPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED).apply {
            bonsaiTree = mutableMapOf((0 to 0) to Tile(0, 0, TileType.WOOD))
        }

        val copiedPlayer = originalPlayer.deepCopy()

        originalPlayer.bonsaiTree[0 to 0] = Tile(0, 0, TileType.LEAF)

        assertNotEquals(originalPlayer.bonsaiTree[0 to 0], copiedPlayer.bonsaiTree[0 to 0])
    }

    /**
     * Tests deep copy functionality of a players personal supply
     *
     * tests if copied state is not the same instance as the original
     */

    @Test
    fun testPlayerPersonalSupplyDeepCopy() {
        val originalPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED).apply {
            personalSupply = mutableListOf(Tile(1, 1, TileType.LEAF))
        }

        val copiedPlayer = originalPlayer.deepCopy()

        originalPlayer.personalSupply[0] = Tile(1, 1, TileType.FLOWER)

        assertNotEquals(originalPlayer.personalSupply[0], copiedPlayer.personalSupply[0])
    }

    /**
     * Tests deep copy functionality of a players collected cards
     *
     * tests if copied state is not the same instance as the original
     */
    @Test
    fun testPlayerCollectedCardsDeepCopy() {
        val originalPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED).apply {
            collectedCards = mutableListOf(GrowthCard(TileType.WOOD, 1))
        }

        val copiedPlayer = originalPlayer.deepCopy()

        originalPlayer.collectedCards[0] = GrowthCard(TileType.LEAF, 1)

        assertNotEquals(originalPlayer.collectedCards[0], copiedPlayer.collectedCards[0])
    }


    /**
     * Tests deep copy functionality of a players claimed goals
     *
     * tests if copied state is not the same instance as the original
     */
    @Test
    fun testPlayerClaimedGoalsDeepCopy() {
        val originalPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED).apply {
            claimedGoals = mutableListOf(GoalTile(GoalTileType.GREEN, 1, 10))
        }

        val copiedPlayer = originalPlayer.deepCopy()

        originalPlayer.claimedGoals[0] = GoalTile(GoalTileType.PINK, 1, 10)

        assertNotEquals(originalPlayer.claimedGoals[0], copiedPlayer.claimedGoals[0])
    }

    /**
     * Tests deep copy functionality of a players renounced goals
     *
     * tests if copied state is not the same instance as the original
     */
    @Test
    fun testPlayerRenouncedGoalsDeepCopy() {
        val originalPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED).apply {
            renouncedGoals = mutableListOf(GoalTile(GoalTileType.GREEN, 1, 10))
        }

        val copiedPlayer = originalPlayer.deepCopy()

        originalPlayer.renouncedGoals[0] = GoalTile(GoalTileType.PINK, 1, 10)

        assertNotEquals(originalPlayer.renouncedGoals[0], copiedPlayer.renouncedGoals[0])
    }


    /**
     * Tests deep copy functionality of a players playable tiles and its copy
     *
     * tests if copied state is not the same instance as the original
     */
    @Test
    fun testPlayerPlayableTilesShallowCopy() {
        val originalPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED).apply {
            playableTiles = mutableListOf(TileType.WOOD, TileType.LEAF)
            playableTilesCopy = mutableListOf(TileType.FLOWER, TileType.FRUIT)
        }

        val copiedPlayer = originalPlayer.deepCopy()

        originalPlayer.playableTiles[0] = TileType.FLOWER

        assertNotEquals(originalPlayer.playableTiles[0], copiedPlayer.playableTiles[0])

        originalPlayer.playableTilesCopy[0] = TileType.ANY

        assertNotEquals(originalPlayer.playableTilesCopy[0], copiedPlayer.playableTilesCopy[0])
    }

    /**
     * Tests deep copy functionality of a games zen deck
     *
     * tests if copied state is not the same instance as the original
     */
    @Test
    fun testBonsaiGameStateZenDeckDeepCopy() {
        val originalGameState = BonsaiGameState(
            currentPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED),
            players = mutableListOf(Player("Bob", PlayerType.HUMAN, isLocal = true, ColorType.BLUE)),
            botSpeed = 1,
            currentState = States.START_TURN
        ).apply {
            zenDeck = mutableListOf(GrowthCard(TileType.WOOD, 1))
        }

        val copiedGameState = originalGameState.deepCopy()

        originalGameState.zenDeck.add(GrowthCard(TileType.LEAF, 2))

        assertNotEquals(originalGameState.zenDeck.size, copiedGameState.zenDeck.size)

    }


    /**
     * Tests deep copy functionality of a games zen deck
     *
     * tests if copied state is not the same instance as the original
     */
    @Test
    fun testBonsaiGameStateFaceUpCardsDeepCopy() {
        val originalGameState = BonsaiGameState(
            currentPlayer = Player("Alice", PlayerType.HUMAN, isLocal = true, ColorType.RED),
            players = mutableListOf(Player("Bob", PlayerType.HUMAN, isLocal = true, ColorType.BLUE)),
            botSpeed = 1,
            currentState = States.START_TURN
        ).apply {
            faceUpCards = mutableListOf(HelperCard(TileType.WOOD, 2))
        }

        val copiedGameState = originalGameState.deepCopy()

        originalGameState.faceUpCards.add(HelperCard(TileType.LEAF, 3))

        assertNotEquals(originalGameState.faceUpCards.size, copiedGameState.faceUpCards.size)
    }

    /**
     * Tests deep copy functionality of a games mastercard
     *
     * tests if copied state is not the same instance as the original
     */
    @Test
    fun testMasterCardDeepCopy() {

        val tileTypes = mutableListOf(TileType.LEAF, TileType.FLOWER)
        val masterCard = MasterCard(tileTypes, id = 1)

        // Act
        val copiedMasterCard = masterCard.deepCopy()

        // Assert
        assertEquals(masterCard.id, copiedMasterCard.id)
        assertEquals(masterCard.tileTypes, copiedMasterCard.tileTypes)
        assert(copiedMasterCard.tileTypes !== masterCard.tileTypes)
    }

    /**
     * Tests deep copy functionality of a games tool card
     *
     * tests if copied state is not the same instance as the original
     */
    @Test
    fun testToolCardDeepCopy() {
        // Arrange
        val toolCard = ToolCard(id = 3)

        // Act
        val copiedToolCard = toolCard.deepCopy()

        // Assert
        assertEquals(toolCard.id, copiedToolCard.id, "IDs should match")
    }

    /**
     * Tests deep copy functionality of a games parchment card
     *
     * tests if copied state is not the same instance as the original
     */
    @Test
    fun testParchmentCardDeepCopy() {
        // Arrange
        val parchmentCard = ParchmentCard(
            TileType.LEAF,
             null,
             2,
             2
        )

        // Act
        val copiedParchmentCard = parchmentCard.deepCopy()

        // Assert
        assertEquals(parchmentCard.id, copiedParchmentCard.id, "IDs should match")
    }

}









