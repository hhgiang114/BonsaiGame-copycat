package service

import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import edu.udo.cs.sopra.ntf.*
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.net.client.NetworkLogging
import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.response.*

/**
 * [BoardGameClient] implementation for network communication.
 *
 * @param networkService the [NetworkService] to potentially forward received messages to.
 */
class BonsaiNetworkClient(
    playerName: String,
    host: String,
    secret: String,
    var networkService: NetworkService
) : BoardGameClient(playerName, host, secret, NetworkLogging.VERBOSE) {

    /** the identifier of this game session; can be null if no session started yet. */
    var sessionID: String? = null

    /** the name of other players; size = 0 if no message from other players received yet */
    var otherPlayerNames: MutableList<String> = mutableListOf()

    /**
     * Handle a [CreateGameResponse] sent by the server. Will await the guest player when its
     * status is [CreateGameResponseStatus.SUCCESS]. As recovery from network problems is not
     * implemented in NetWar, the method disconnects from the server and throws an
     * [IllegalStateException] otherwise.
     *
     * @throws IllegalStateException if status != success or currently not waiting for a game creation response.
     */
    override fun onCreateGameResponse(response: CreateGameResponse) {
        BoardGameApplication.run {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_HOST_CONFIRMATION)
            { "unexpected CreateGameResponse" }

            when (response.status) {
                CreateGameResponseStatus.SUCCESS -> {
                    networkService.updateConnectionState(ConnectionState.WAITING_FOR_GUEST)
                    sessionID = response.sessionID
                }

                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handle a [JoinGameResponse] sent by the server. Will await the init message when its
     * status is [JoinGameResponseStatus.SUCCESS]. As recovery from network problems is not
     * implemented in NetWar, the method disconnects from the server and throws an
     * [IllegalStateException] otherwise.
     *
     * @throws IllegalStateException if status != success or currently not waiting for a join game response.
     */
    override fun onJoinGameResponse(response: JoinGameResponse) {
        BoardGameApplication.run {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_JOIN_CONFIRMATION)
            { "unexpected JoinGameResponse" }

            when (response.status) {
                JoinGameResponseStatus.SUCCESS -> {
                    sessionID = response.sessionID
                    networkService.updateConnectionState((ConnectionState.WAITING_FOR_INIT))
                }

                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handle a [PlayerJoinedNotification] sent by the server. We receive the name from other player
     * as they joined our lobby
     * @throws IllegalStateException if not currently expecting any guests to join.
     * @throws IllegalStateException if there are more than other 3 players.
     */
    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        BoardGameApplication.run {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_GUEST)
            { "not awaiting any guests." }

            check(otherPlayerNames.size <= 3)
            { "more than 4 players." }

            otherPlayerNames.add(notification.sender)
            networkService.receivePlayerJoinedMessage(notification.sender)
        }
    }

    /**
     * handle a [StartGameMessage] sent by the server
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onStartGameMessageReceived(message: StartGameMessage, sender: String) {
        BoardGameApplication.run {
            networkService.receiveStartGameMessage(
                message = message
            )
        }
    }

    /**
     * handle a [CultivateMessage] sent by the server
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onCultivateMessage(message: CultivateMessage, sender: String) {
        BoardGameApplication.run {
            networkService.receiveCultivateMessage(message)
        }
    }

    /**
     * handle a [MeditateMessage] sent by the server
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onMeditateMessage(message: MeditateMessage, sender: String) {
        BoardGameApplication.run {
            networkService.receiveMeditateMessage(message)
        }
    }

    private fun disconnectAndError(message: Any) {
        networkService.disconnect()
        error(message)
    }
}
