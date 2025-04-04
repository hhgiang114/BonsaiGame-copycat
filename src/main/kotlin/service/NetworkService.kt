package service

import edu.udo.cs.sopra.ntf.*
import entity.GoalTileType
import gui.Refreshable
import entity.*
import util.ZenCardLoader

/**
 * Service layer class that realizes the necessary logic for sending and receiving messages
 * in multiplayer network games. Bridges between the [BonsaiNetworkClient] and the other services.
 */
class NetworkService(private val rootService: RootService) : AbstractRefreshingService() {

    /** Network client. Nullable for offline games. */
    var client: BonsaiNetworkClient? = null

    /** current state of the connection in a network game. */
    var connectionState: ConnectionState = ConnectionState.DISCONNECTED
        private set

    // our own name
    var myName : String = ""

    var toBeSentMeditateMessage = MutableMeditateMessage(
        mutableListOf(), 4, mutableListOf(),
        mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
    )
    var toBeSentCultivateMessage = MutableCultivateMessage(
        mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
    )

    var hasCultivated = false
    var hasMeditated = false


    /**
     * Connects to server and creates a new game session.
     *
     * @param secret Server secret.
     * @param name Player name.
     * @param sessionID identifier of the hosted session (to be used by guest on join)
     *
     * @throws IllegalStateException if already connected to another game or connection attempt fails
     */
    fun createGame(secret: String, name: String, sessionID: String?) {
        if (!connect(secret, name)) {
            error("Connection failed")
        }
        updateConnectionState(ConnectionState.CONNECTED)

        if (sessionID.isNullOrBlank()) {
            client?.createGame(GAME_ID, "Welcome!^_^")
        } else {
            client?.createGame(GAME_ID, sessionID, "Welcome!^_^")
        }
        onAllRefreshables { refreshAfterPlayerJoined(name) }
        updateConnectionState(ConnectionState.WAITING_FOR_HOST_CONFIRMATION)
    }

    /**
     * Connects to server and joins a game session as guest player.
     *
     * @param secret Server secret.
     * @param name Player name.
     * @param sessionID identifier of the joined session (as defined by host on create)
     *
     * @throws IllegalStateException if already connected to another game or connection attempt fails
     */
    fun joinGame(secret: String, name: String, sessionID: String) {
        if (!connect(secret, name)) {
            error("Connection failed")
        }
        updateConnectionState(ConnectionState.CONNECTED)

        client?.joinGame(sessionID, "Welcome!^o^")

        updateConnectionState(ConnectionState.WAITING_FOR_JOIN_CONFIRMATION)
    }

    /**
     * set up the game using [GameService.startNewGame] and send the game init message
     * to the guest players. [connectionState] needs to be [ConnectionState.WAITING_FOR_GUEST].
     * This method should be called when the host(we) decide to start the game and
     * when there are 2 to 4 players in the lobby
     *
     * @throws IllegalStateException if [connectionState] != [ConnectionState.WAITING_FOR_GUEST]
     * @throws IllegalStateException if player size < 2 or player size > 4
     */
    fun sendStartGameMessage(playerOrder: MutableList<Player>, goalTilesEntries: MutableList<GoalTileType>) {
        check(connectionState == ConnectionState.WAITING_FOR_GUEST)
        { "currently not prepared to start a new hosted game." }

        val playerNames = client?.otherPlayerNames
        checkNotNull(playerNames)

        check(playerOrder.size in 2..4) {"there should be 2 to 4 players"}

        rootService.gameService.startNewGame(playerOrder, true, goalTilesEntries)
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game) { "game should not be null right after starting it." }

        val nameColorPair = playerOrder.map {
            Pair(it.name, it.color.toColorMessage())
        }

        val chosenGoalTiles = goalTilesEntries.map {
            it.toGoalTileTypeMessage()
        }

        val zenDeckList = game.zenDeck.map {
            Pair(it.cardType.toCardTypeMessage(), it.id)
        }
        val faceUpCardsList = game.faceUpCards.map {
            Pair(it.cardType.toCardTypeMessage(), it.id)
        }

        val zenDeckMessage = zenDeckList + faceUpCardsList

        val message = StartGameMessage(nameColorPair, chosenGoalTiles, zenDeckMessage)

        myName = client?.playerName.toString()

        if (myName == playerOrder.first().name) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
            client?.sendGameActionMessage(message)
        } else {
            updateConnectionState(ConnectionState.WAITING_FOR_OPPONENT)
            client?.sendGameActionMessage(message)
        }
        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     * Holds the message to be sent for a Meditate action.
     */
    fun sendMeditateMessage() {
        // --------------- prologue: state check ---------------
        check(connectionState == ConnectionState.PLAYING_MY_TURN)
        { "currently not expecting your turn." }

        // --------------- main functionality ---------------
        val message = MeditateMessage(
                toBeSentMeditateMessage
                    .removedTilesAxialCoordinates.map { it },
                toBeSentMeditateMessage
                    .chosenCardPosition,
                toBeSentMeditateMessage
                    .playedTiles.map { (it.first.toTileTypeMessage() to it.second) },
                toBeSentMeditateMessage
                    .drawnTiles.map { it.toTileTypeMessage() },
                toBeSentMeditateMessage
                    .claimedGoals.map { (it.first.toGoalTileTypeMessage() to it.second + 1) },
                toBeSentMeditateMessage
                    .renouncedGoals.map { (it.first.toGoalTileTypeMessage() to it.second + 1) },
                toBeSentMeditateMessage
                    .discardedTiles.map { it.toTileTypeMessage() },
            )

        // --------------- epilogue: state update ---------------
        toBeSentMeditateMessage.removedTilesAxialCoordinates.clear()
        toBeSentCultivateMessage.removedTilesAxialCoordinates.clear()
        toBeSentMeditateMessage.playedTiles.clear()
        toBeSentMeditateMessage.claimedGoals.clear()
        toBeSentMeditateMessage.renouncedGoals.clear()
        toBeSentMeditateMessage.drawnTiles.clear()
        toBeSentMeditateMessage.discardedTiles.clear()
        toBeSentMeditateMessage.chosenCardPosition = 4
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENT)
        client?.sendGameActionMessage(message)
    }

    /**
     * Holds the message to be sent for a Cultivate action.
     */
    fun sendCultivateMessage() {
        check(connectionState == ConnectionState.PLAYING_MY_TURN)
        { "currently not expecting your turn." }

        val message = CultivateMessage(
            toBeSentCultivateMessage.removedTilesAxialCoordinates.map { it },
            toBeSentCultivateMessage.playedTiles.map {
                (it.first.toTileTypeMessage() to it.second)
            },
            toBeSentCultivateMessage.claimedGoals.map {
                (it.first.toGoalTileTypeMessage() to it.second + 1)
            },
            toBeSentCultivateMessage.renouncedGoals.map {
                (it.first.toGoalTileTypeMessage() to it.second + 1)
            }
        )

        // at the end clear the toBeSentMessage
        toBeSentMeditateMessage.removedTilesAxialCoordinates.clear()
        toBeSentCultivateMessage.removedTilesAxialCoordinates.clear()
        toBeSentCultivateMessage.playedTiles.clear()
        toBeSentCultivateMessage.claimedGoals.clear()
        toBeSentCultivateMessage.renouncedGoals.clear()

        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENT)
        client?.sendGameActionMessage(message)
    }

    /**
     * Initializes the entity structure with the data given by the [StartGameMessage] sent by the host.
     * [connectionState] needs to be [ConnectionState.WAITING_FOR_INIT].
     * This method should be called from the [BonsaiNetworkClient] when the host sends the init message.
     * See [BonsaiNetworkClient.onStartGameMessageReceived].
     *
     * @throws IllegalStateException if not currently waiting for an init message
     */
    fun receiveStartGameMessage(message: StartGameMessage) {
        check(connectionState == ConnectionState.WAITING_FOR_INIT)
        { "not waiting for game init message" }

        //decode message
        val orderedPair = message.orderedPlayerNames
        val goalTileTypes = message.chosenGoalTiles.map {
            it.toGoalTileType()
        }
        val players = mutableListOf<Player>()
        myName = client?.playerName.toString()

        orderedPair.forEach {
            val isLocal = (myName == it.first)
            players.add(Player(it.first, PlayerType.HUMAN, isLocal, it.second.toColor()))
        }


        //initialise game
        rootService.gameService.startNewGame(players, true, goalTileTypes.toMutableList())

        //construct game
        val standardZenDeck = ZenCardLoader().readAllZenCards(4)
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        game.zenDeck =
            message.orderedCards.map {
                standardZenDeck[it.second]
            }.toMutableList()
        game.faceUpCards.addAll(game.zenDeck.takeLast(4))
        repeat(4) {game.zenDeck.removeLast()}

        if (myName == orderedPair.first().first) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else {
            updateConnectionState(ConnectionState.WAITING_FOR_OPPONENT)
        }
        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     * Receives and processes the Meditate action message from another player.
     *
     * @param message The Meditate message received from the opponent.
     */
    fun receiveMeditateMessage(message: MeditateMessage) {
        // --------------- prologue: state check ---------------
        check(connectionState == ConnectionState.WAITING_FOR_OPPONENT)
            { "currently not expecting an opponent's turn." }
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)

        // --------------- main functionality ---------------
        message.removedTilesAxialCoordinates
            .forEach { rootService.treeService.removeFromTree(it) }
        val chosenCard = game.faceUpCards[message.chosenCardPosition]

        if (chosenCard is HelperCard) {
            game.currentPlayer.playableTilesCopy.addAll(chosenCard.tileTypes)
        }

            // processing `chosenCardPosition` in message
        when (message.chosenCardPosition) {
            1 -> rootService.playerActionService
                .meditate (
                        cardPosition = 1,
                        chosenTile = message.drawnTiles[0].toTileType(),
                    )
            else -> rootService.playerActionService
                .meditate (
                    cardPosition = message.chosenCardPosition,
                    chosenTile = null,
                )
        }

        if (chosenCard is HelperCard && message.playedTiles.isNotEmpty()){
            message.playedTiles
                .forEach {
                    val tile = Tile(null, null, it.first.toTileType())
                    rootService.treeService.playTile(tile, it.second)
                }
            message.claimedGoals
                .forEach { rootService.playerActionService
                    .claimOrRenounceGoal(true, it.first.toGoalTileType(), it.second) }
            message.renouncedGoals
                .forEach { rootService.playerActionService
                    .claimOrRenounceGoal(false, it.first.toGoalTileType(), it.second) }
        }

        // --------------- epilogue: state update ---------------
        val currentIndex = game.players.indexOf(game.currentPlayer)
        val nextIndex = (currentIndex + 1) % game.players.size
        if (game.players[nextIndex].name == myName) updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        hasMeditated = false
        game.currentPlayer.hasPlayed = true
        rootService.playerActionService.endTurn()
    }

    /**
     * Receives and processes the Cultivate action message from another player.
     *
     * @param message The Cultivate message received from the opponent.
     */
    fun receiveCultivateMessage(message: CultivateMessage) {
        check(connectionState == ConnectionState.WAITING_FOR_OPPONENT)
        { "currently not expecting an opponent's turn." }

        // reproduce what the other player has done
        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        //val otherPlayer = game.currentPlayer
        message.removedTilesAxialCoordinates.forEach {
            rootService.treeService.removeFromTree(it)
        }

        rootService.playerActionService.cultivate()


        if (message.playedTiles.isNotEmpty()){
            message.playedTiles.forEach {
                val tile = Tile(null, null, it.first.toTileType())
                rootService.treeService.playTile(tile, it.second)
            }
        }

        message.claimedGoals.forEach {
            rootService.playerActionService.claimOrRenounceGoal(true, it.first.toGoalTileType(), it.second)
        }
        message.renouncedGoals.forEach {
            rootService.playerActionService.claimOrRenounceGoal(false, it.first.toGoalTileType(), it.second)
        }

        val currentIndex = game.players.indexOf(game.currentPlayer)
        // get next player in the list, looping back to the first player at end of the list
        val nextIndex = (currentIndex + 1) % game.players.size

        if (game.players[nextIndex].name == myName) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        }
        hasCultivated = false
        rootService.playerActionService.endTurn()
    }

    /**
     * Updates the [connectionState] to [newState] and notifies
     * all refreshables via [Refreshable.refreshConnectionState]
     */
    fun updateConnectionState(newState: ConnectionState) {
        this.connectionState = newState
        onAllRefreshables {
            refreshConnectionState(newState)
        }
    }
    /**
     * Notifies all refreshables when a player joins.
     * @param playerName The name of the player who joined.
     */
    fun receivePlayerJoinedMessage(playerName: String) {
        onAllRefreshables { refreshAfterPlayerJoined(playerName) }
    }

    /**
     * Attempts to connect to the server with the given secret and player name.
     *
     * @param secret The server secret for authentication.
     * @param name The player's name.
     *
     * @return True if the connection was successful, false otherwise.
     */
    private fun connect(secret: String, name: String): Boolean {
        require(connectionState == ConnectionState.DISCONNECTED && client == null)
        { "already connected to another game" }

        require(secret.isNotBlank()) { "server secret must be given" }
        require(name.isNotBlank()) { "player name must be given" }

        val newClient =
            BonsaiNetworkClient(
                playerName = name,
                host = SERVER_ADDRESS,
                secret = secret,
                networkService = this
            )

        return if (newClient.connect()) {
            this.client = newClient
            true
        } else {
            false
        }
    }

    /**
     * Disconnects the [client] from the server, nulls it and updates the
     * [connectionState] to [ConnectionState.DISCONNECTED]. Can safely be called
     * even if no connection is currently active.
     */
    fun disconnect() {
        client?.apply {
            if (sessionID != null) leaveGame("Goodbye!")
            if (isOpen) disconnect()
        }
        client = null
        updateConnectionState(ConnectionState.DISCONNECTED)
    }

    /**
     * used for network tests
     */
    fun setConnectionStateTest(newState: ConnectionState){
        updateConnectionState(newState)
    }

    /** URL of the BGW net server hosted for SoPra participants
     * Name of the game as registered with the server
     * */
    companion object {

        const val SERVER_ADDRESS = "sopra.cs.tu-dortmund.de:80/bgw-net/connect"

        const val GAME_ID = "Bonsai"
    }
}
