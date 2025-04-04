package service

import edu.udo.cs.sopra.ntf.*
import entity.*
import util.SECRET_KEY
import util.ZenCardLoader
import kotlin.test.*

/**
 * Provides tests for the Network class in the Service layer.
 *
 * Includes game creation, joining a game and message handling.
 */
class NetworkServiceTest {

    /**
     * Initialises a new game for to do some tests with
     *
     * @return A [RootService] with initialised game
     */
    private fun setUpGame(): RootService {
        val rootService = RootService()
        val players = mutableListOf(
            Player("Tom", PlayerType.HUMAN, true, ColorType.RED),
            Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE),
            Player("Tomi", PlayerType.HUMAN, true, ColorType.BLACK)
        )
        val gameState = BonsaiGameState(
            currentPlayer = players.first(),
            players = players,
            botSpeed = 1,
            currentState = States.CULTIVATE
        )
        gameState.zenDeck = mutableListOf(
            HelperCard(TileType.WOOD, 1),
            HelperCard(TileType.WOOD, 2),
            HelperCard(TileType.WOOD, 3),
            HelperCard(TileType.WOOD, 4),
            HelperCard(TileType.WOOD, 5)
        )
        gameState.currentPlayer.bonsaiTree[Pair(0,0)] = Tile(0,0, TileType.WOOD)
        repeat(4){
            if (gameState.zenDeck.isNotEmpty()){
                gameState.faceUpCards.add(gameState.zenDeck.removeLast())
            }
        }
        val game = BonsaiGame()
        game.currentBonsaiGameState = gameState
        rootService.currentGame = game
        return rootService
    }

    /**
     * Tests the connection state is set correctly after a host has created a game.
     */
    @Test
    fun testCreateGame() {
        val rootService = setUpGame()
        val networkService = NetworkService(rootService)
        networkService.createGame("baum25", "Tom", "1234")
        assertEquals(ConnectionState.WAITING_FOR_HOST_CONFIRMATION, networkService.connectionState)
    }

    /**
     * Tests the connection state is set correctly after a guest has joined a game.
     */
    @Test
    fun testJoinGame() {
        val rootService = setUpGame()
        val networkService = NetworkService(rootService)
        networkService.joinGame("baum25", "Tom", "1234")
        assertEquals(ConnectionState.WAITING_FOR_JOIN_CONFIRMATION, networkService.connectionState)
    }



    @Test
    fun testSendStartGameMessage() {
        val rootService = RootService()
        val networkService = NetworkService(rootService)

        val players = mutableListOf(
            Player("Tom", PlayerType.HUMAN, true, ColorType.RED),
            Player("Tomy", PlayerType.HUMAN, true, ColorType.BLUE),
            Player("Tomi", PlayerType.HUMAN, true, ColorType.BLACK)
        )
        val goalTiles = mutableListOf(GoalTileType.BROWN, GoalTileType.PINK, GoalTileType.GREEN)

        networkService.myName = "Tom"

        // should not be connected already
        if (networkService.connectionState != ConnectionState.DISCONNECTED) {
            networkService.disconnect()
            Thread.sleep(100)
        }
        assertEquals(ConnectionState.DISCONNECTED, networkService.connectionState)

        // create the game
        networkService.createGame("baum25", "Tom", "1234")
        assertEquals(ConnectionState.WAITING_FOR_HOST_CONFIRMATION, networkService.connectionState)

        // simulate host confirmation
        networkService.updateConnectionState(ConnectionState.CONNECTED)
        assertEquals(ConnectionState.CONNECTED, networkService.connectionState, "timeout")

        // set the connection state back to WAITING_FOR_GUEST before sending the start game message
        networkService.setConnectionStateTest(ConnectionState.WAITING_FOR_GUEST)
        assertEquals(ConnectionState.WAITING_FOR_GUEST, networkService.connectionState)

        // send the start message
        networkService.sendStartGameMessage(players, goalTiles)

        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState)

        // check connection state
        val expectedConnectionState = if (networkService.myName == gameState.players.first().name) {
            ConnectionState.PLAYING_MY_TURN
        } else {
            ConnectionState.WAITING_FOR_OPPONENT
        }
        assertEquals(expectedConnectionState, networkService.connectionState)

        // check game state at the end
        assertEquals(players.size, gameState.players.size)
        assertEquals("Tom", gameState.players.first().name)
        assertEquals("Tomi", gameState.players.last().name)
        assertEquals(goalTiles.size * 3, gameState.goalTiles.size)
        assertTrue(gameState.zenDeck.isNotEmpty())
        assertTrue(gameState.faceUpCards.isNotEmpty())

        // clean up
        networkService.disconnect()
        Thread.sleep(100)
        assertEquals(ConnectionState.DISCONNECTED, networkService.connectionState)
    }





    /**
     * Tests sending a meditate message.
     * Ensures that the connection state is set to `WAITING_FOR_OPPONENT`
     * and that the list of played tiles is cleared.
     */
    @Test
    fun testSendMeditateMessage() {
        val rootService = setUpGame()
        val networkService = NetworkService(rootService)
        networkService.setConnectionStateTest(ConnectionState.PLAYING_MY_TURN)
        networkService.toBeSentMeditateMessage.playedTiles.add(TileType.LEAF to Pair(1, -1))
        networkService.sendMeditateMessage()
        assertEquals(ConnectionState.WAITING_FOR_OPPONENT, networkService.connectionState)
        assertTrue(networkService.toBeSentMeditateMessage.playedTiles.isEmpty())
    }

    /**
     * Tests sending a cultivate message.
     * Ensures that the connection state is set to `WAITING_FOR_OPPONENT`
     * and that the list of played tiles is cleared.
     */
    @Test
    fun testSendCultivateMessage() {
        val rootService = setUpGame()
        val networkService = NetworkService(rootService)
        networkService.setConnectionStateTest(ConnectionState.PLAYING_MY_TURN)
        networkService.toBeSentCultivateMessage.playedTiles.add(TileType.LEAF to Pair(1, -1))
        networkService.sendCultivateMessage()
        assertEquals(ConnectionState.WAITING_FOR_OPPONENT, networkService.connectionState)
        assertTrue(networkService.toBeSentCultivateMessage.playedTiles.isEmpty())
    }
    /**
     * Tests receiving a message after a game has started.
     * Verifies that the game is correctly initialized and that
     * the connection state is set correctly.
     */
    @Test
    fun testReceiveStartGameMessage() {
        val rootService = RootService()
        val networkService = NetworkService(rootService)

        rootService.currentGame = null
        rootService.gameService = GameService(rootService)
        networkService.myName = "Tom"
        networkService.setConnectionStateTest(ConnectionState.WAITING_FOR_INIT)
        val zenDeck = ZenCardLoader().readAllZenCards(3)
        val orderedCards = zenDeck.map { Pair(it.cardType.toCardTypeMessage(), it.id) }
        val message = StartGameMessage(
            orderedPlayerNames = listOf(
                Pair("Tom", ColorTypeMessage.RED),
                Pair("Tomy", ColorTypeMessage.BLUE),
                Pair("Tomi", ColorTypeMessage.BLACK)
            ),
            chosenGoalTiles = listOf(
                GoalTileTypeMessage.BROWN,
                GoalTileTypeMessage.PINK,
                GoalTileTypeMessage.GREEN
            ),
            orderedCards = orderedCards
        )
        networkService.receiveStartGameMessage(message)
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState)
        assertEquals(3, gameState.players.size)
        assertEquals("Tom", gameState.players.first().name)
        assertEquals("Tomi", gameState.players.last().name)
        assertEquals(9, gameState.goalTiles.size)
        val checkStateForHost = if (networkService.myName == gameState.players.first().name) {
            ConnectionState.PLAYING_MY_TURN
        } else {
            ConnectionState.WAITING_FOR_OPPONENT
        }
        assertEquals(checkStateForHost, networkService.connectionState)
    }

    /**
     * Tests receiving a meditate message.
     * Ensures that the game state is updated correctly and that
     * the connection state remains unchanged after processing.
     */
    @Test
    fun testReceiveMeditateMessage() {
        val rootService = setUpGame()
        val networkService = NetworkService(rootService)
        networkService.myName = "Tomy"
        networkService.setConnectionStateTest(ConnectionState.WAITING_FOR_OPPONENT)
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState)
        val tile = Tile(0, -1, TileType.WOOD)
        gameState.currentPlayer.personalSupply.add(tile)
        gameState.currentPlayer.playableTilesCopy.add(TileType.WOOD)
        gameState.currentPlayer.bonsaiTree[Pair(0, 0)] = Tile(0, 0, TileType.WOOD)
        val message = MeditateMessage(
            removedTilesAxialCoordinates = listOf(),
            chosenCardPosition = 2,
            playedTiles = listOf(Pair(TileTypeMessage.WOOD,Pair(0, -1))),
            drawnTiles = listOf(TileTypeMessage.WOOD),
            claimedGoals = listOf(),
            renouncedGoals = listOf(),
            discardedTiles = listOf()
        )
        networkService.receiveMeditateMessage(message)
        assertEquals(ConnectionState.PLAYING_MY_TURN, networkService.connectionState)
    }
    /**
     * Tests receiving a cultivate message.
     * Verifies that the game is updated correctly after receiving the message
     * and that the connection state is set properly.
     */
    @Test
    fun testReceiveCultivateMessage() {
        val rootService = setUpGame()
        val networkService = NetworkService(rootService)
        networkService.setConnectionStateTest(ConnectionState.WAITING_FOR_OPPONENT)
        val gameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(gameState)
        val tile = Tile(null, null, TileType.LEAF)
        gameState.currentPlayer.personalSupply.add(tile)
        gameState.currentPlayer.playableTilesCopy.add(TileType.LEAF)
        val message = CultivateMessage(
            removedTilesAxialCoordinates = listOf(),
            playedTiles = listOf(TileTypeMessage.LEAF to Pair(1, -1)),
            claimedGoals = listOf(),
            renouncedGoals = listOf()
        )
        networkService.receiveCultivateMessage(message)
        assertEquals(ConnectionState.WAITING_FOR_OPPONENT, networkService.connectionState)
    }
    /**
     * Tests updating the connection state.
     * Ensures that the state is correctly changed.
     */
    @Test
    fun testUpdateConnectionState() {
        val rootService = setUpGame()
        val networkService = NetworkService(rootService)
        networkService.updateConnectionState(ConnectionState.WAITING_FOR_GUEST)
        assertEquals(ConnectionState.WAITING_FOR_GUEST, networkService.connectionState)
    }
    /**
     * Tests disconnecting from the network.
     * Verifies that the connection state is set to `DISCONNECTED`.
     */
    @Test
    fun testDisconnect() {
        val rootService = setUpGame()
        val networkService = NetworkService(rootService)
        networkService.setConnectionStateTest(ConnectionState.CONNECTED)
        networkService.disconnect()
        assertEquals(ConnectionState.DISCONNECTED, networkService.connectionState)
    }


    /**
     * Test if it fails
     */
    @Test
    fun testFails() {
        val rootService1 = RootService()
        val rootService2 = RootService()
        val sessionId = (10001.. 50000).random().toString()
        rootService1.networkService.createGame (
            SECRET_KEY,
            "Gary",
            sessionId
        )

        Thread.sleep(2000)
        rootService2.networkService.joinGame(
            SECRET_KEY,
            "HIHI",
            sessionId
        )
        Thread.sleep(2000)

        val player1 = Player(
            "Gary", PlayerType.HUMAN, true, ColorType.RED)
        val player2 = Player(
            "HIHI", PlayerType.HUMAN, false, ColorType.PURPLE)

        rootService1.networkService.updateConnectionState(ConnectionState.DISCONNECTED)
        assertFails {
            rootService1.networkService.sendStartGameMessage(
                mutableListOf(player1,player2),
                mutableListOf(
                    GoalTileType.BROWN, GoalTileType.PINK, GoalTileType.GREEN)
            )
        }
        val rootService3 = RootService()
        val rootService4 = RootService()
        val sessionId2 = (10001.. 50000).random().toString()
        rootService3.networkService.createGame (
            SECRET_KEY,
            "Gary",
            sessionId2
        )

        Thread.sleep(2000)
        rootService4.networkService.joinGame(
            SECRET_KEY,
            "HIHI",
            sessionId2
        )
        Thread.sleep(2000)

        val player3 = Player(
            "Gary", PlayerType.HUMAN, true, ColorType.RED)
        val player4 = Player(
            "HIHI", PlayerType.HUMAN, false, ColorType.PURPLE)
        rootService3.networkService.sendStartGameMessage(
            mutableListOf(player3,player4),
            mutableListOf(
                GoalTileType.BROWN, GoalTileType.PINK, GoalTileType.GREEN)
        )
        Thread.sleep(1000)

        val game3 = rootService3.currentGame?.currentBonsaiGameState
        val game4 = rootService4.currentGame?.currentBonsaiGameState
        checkNotNull(game3)
        checkNotNull(game4)
        rootService3.playerActionService.cultivate()
        rootService3.networkService.updateConnectionState(ConnectionState.WAITING_FOR_OPPONENT)
        assertFails { rootService3.networkService.sendCultivateMessage() }
    }
}

