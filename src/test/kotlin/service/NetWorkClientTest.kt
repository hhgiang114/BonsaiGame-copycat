package service

import entity.*
import org.junit.jupiter.api.Assertions.assertTrue
import util.SECRET_KEY
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * Test class for [BonsaiNetworkClient]
 */
class NetWorkClientTest {

    /**
     * Test if correctly received StartGameMessage
     */
    @Test
    fun onStartGameMessageReceived() {
        val rootService1 = RootService()
        assertEquals(rootService1.networkService.client, null)
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

        rootService1.networkService.sendStartGameMessage(
            mutableListOf(player1,player2),
            mutableListOf(
                GoalTileType.BROWN, GoalTileType.PINK, GoalTileType.GREEN)
        )
        Thread.sleep(1000)

        val game1 = rootService1.currentGame?.currentBonsaiGameState
        val game2 = rootService2.currentGame?.currentBonsaiGameState
        checkNotNull(game1)
        checkNotNull(game2)
        assertEquals(game1.zenDeck.size, game2.zenDeck.size)
    }

    /**
     * Test if correctly received Meditate Message
     */
    @Test
    fun onMeditateMessageReceived() {
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

        rootService1.networkService.sendStartGameMessage(
            mutableListOf(player1,player2),
            mutableListOf(
                GoalTileType.BROWN, GoalTileType.PINK, GoalTileType.GREEN)
        )
        Thread.sleep(1000)

        val game1 = rootService1.currentGame?.currentBonsaiGameState
        val game2 = rootService2.currentGame?.currentBonsaiGameState
        checkNotNull(game1)
        checkNotNull(game2)
        game1.faceUpCards[0] = ToolCard(41)
        game2.faceUpCards[0] = ToolCard(41)
        rootService1.playerActionService.meditate(0, null)
        rootService1.networkService.sendMeditateMessage()
        Thread.sleep(1000)
        assertEquals(game1.players[0].tileCapacity, game2.players[0].tileCapacity)
        game1.faceUpCards[1] = ParchmentCard(null,null,2,2)
        game2.faceUpCards[1] = ParchmentCard(null,null,2,2)
        rootService2.gameService.switchPlayerTurn()
        rootService1.playerActionService.meditate(1,TileType.LEAF)
        game1.currentPlayer.hasPlayed = true
        rootService1.playerActionService.endTurn()
        Thread.sleep(1000)
        assertEquals(game1.players[1].personalSupply.last().tileType, TileType.LEAF)
    }

    /**
     * Test if correctly received Cultivate Message
     */
    @Test
    fun onCultivateMessageReceived() {
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

        rootService1.networkService.sendStartGameMessage(
            mutableListOf(player1,player2),
            mutableListOf(
                GoalTileType.BROWN, GoalTileType.PINK, GoalTileType.GREEN)
        )
        Thread.sleep(1000)

        val game1 = rootService1.currentGame?.currentBonsaiGameState
        val game2 = rootService2.currentGame?.currentBonsaiGameState
        checkNotNull(game1)
        checkNotNull(game2)
        rootService1.playerActionService.cultivate()
        for (i in 0..12) {
            game1.currentPlayer.bonsaiTree[i to -1] = Tile(null, null,TileType.WOOD)
            game2.currentPlayer.bonsaiTree[i to -1] = Tile(null, null,TileType.WOOD)
        }
        //rootService1.treeService.playTile(game1.currentPlayer.personalSupply.last(), (0 to -1))
        rootService1.playerActionService.claimOrRenounceGoal(true, GoalTileType.BROWN, 2)
        for (i in 0..8) {
            game1.currentPlayer.bonsaiTree[i to -2] = Tile(null, null,TileType.LEAF)
            game2.currentPlayer.bonsaiTree[i to -2] = Tile(null, null,TileType.LEAF)
        }
        rootService1.playerActionService.claimOrRenounceGoal(false, GoalTileType.GREEN, 2)
        rootService1.playerActionService.endTurn()
        Thread.sleep(1000)
        assertEquals(game2.currentPlayer, game2.players[1])
        assertTrue(game2.players[0].claimedGoals.isNotEmpty())
        assertTrue(game2.players[0].renouncedGoals.isNotEmpty())

    }

    /**
     * Test if it fails
     */
    @Test
    fun testWhenFails() {

    }
}
