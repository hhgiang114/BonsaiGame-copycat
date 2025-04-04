package service


import entity.*
import kotlin.math.max
import util.SIDE_VECTORS


// todo: `msgToBeSent` should even be handled in the `refreshAfter` methods.

/**
 * The service layer class which contains the player's action functions.
 */
class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Meditate to receive a zen card:
     * - tool card
     * - growth card
     * - helper card
     * - master card
     * - parchment card
     * and also receive play tiles based on the position of the card chosen.
     *
     * preconditions:
     * - The game has started and is currently running.
     *
     * post conditions:
     * - Player has received a zen card.
     * - Empty spot in board is filled by pushing every card to the right
     * and filling the left empty spot with a new card from the zen deck.
     * - updates that the current player has done an action during his turn.
     * - sets the current state to cultivate
     *
     * @param cardPosition The position of the selected zen card.
     * @param chosenTile The wood or leaf tile if the card is drawn from position 1
     * @throws IllegalStateException if player has already done an action during his current turn.
     */
    fun meditate(cardPosition: Int, chosenTile: TileType?) {
        require(cardPosition in 0..3)
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        val net = rootService.networkService
        val msg = net.toBeSentMeditateMessage

        // net stuff
        if (net.connectionState != ConnectionState.DISCONNECTED &&
            gameState.currentPlayer.isLocal){
            rootService.networkService.hasMeditated = true
            msg.chosenCardPosition = cardPosition
        }

        gameState.currentState = States.MEDITATE
        val actPlayer = gameState.currentPlayer

        // Player draw 1 card among face up cards in the board
        val drawnCard = gameState.faceUpCards.removeAt(cardPosition)
        actPlayer.collectedCards.add(drawnCard)

        // Refill the board
        rootService.gameService.refillBoard()


        onAllRefreshables { refreshAfterChooseCard() }

        when (cardPosition) {

            1 -> {
                checkNotNull(chosenTile)
                require(chosenTile == TileType.WOOD || chosenTile == TileType.LEAF) { "Please choose WOOD or LEAF" }
                actPlayer.personalSupply.add(Tile(null, null, chosenTile))
                if (net.connectionState != ConnectionState.DISCONNECTED &&
                    gameState.currentPlayer.isLocal) {
                    msg.drawnTiles += chosenTile
                }
            }

            2 -> {
                actPlayer.personalSupply.add(Tile(null, null, TileType.WOOD))
                actPlayer.personalSupply.add(Tile(null, null, TileType.FLOWER))
            }

            3 -> {
                actPlayer.personalSupply.add(Tile(null, null, TileType.LEAF))
                actPlayer.personalSupply.add(Tile(null, null, TileType.FRUIT))
            }
        }

        when (drawnCard) {

            is ToolCard -> {
                actPlayer.tileCapacity += 2
            }

            is GrowthCard -> {
                actPlayer.playableTiles.add(drawnCard.tileType)
            }

            is MasterCard -> {
                gameState.currentState = States.USING_MASTER
                playMasterCard(drawnCard, cardPosition)
                return
            }

            is HelperCard -> {
                gameState.currentState = States.USING_HELPER
                playHelperCard(drawnCard, cardPosition)
                return
            }
            is ParchmentCard -> {}
        }
        onAllRefreshables { refreshAfterApplyCardEffects(cardPosition) }
    }

    /**
     * apply MasterCard effects
     * @param drawnCard : the drawn MasterCard
     */
    private fun playMasterCard(drawnCard: MasterCard, cardPosition: Int) {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        require(gameState.currentState == States.USING_MASTER)
        val actPlayer = gameState.currentPlayer

        when (drawnCard.tileTypes.size) {
            1 -> {
                if (actPlayer.isLocal) {
                    onAllRefreshables { refreshAfterDrawingMasterCardAny() }
                } else {
                    onAllRefreshables { refreshAfterApplyCardEffects(cardPosition) }
                }
                return
            }

            2 -> {
                actPlayer.personalSupply.add(Tile(null, null, drawnCard.tileTypes[0]))
                actPlayer.personalSupply.add(Tile(null, null, drawnCard.tileTypes[1]))
            }

            3 -> {
                actPlayer.personalSupply.add(Tile(null, null, drawnCard.tileTypes[0]))
                actPlayer.personalSupply.add(Tile(null, null, drawnCard.tileTypes[1]))
                actPlayer.personalSupply.add(Tile(null, null, drawnCard.tileTypes[2]))
            }
        }
            onAllRefreshables { refreshAfterApplyCardEffects(cardPosition) }
    }

    /**
     * chose ANY Type
     * @param tileType : the TileType that the player has chosen
     */
    fun chooseTile(tileType: TileType) {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        val actPlayer = gameState.currentPlayer

        require(
            gameState.currentState == States.USING_MASTER
        )

        actPlayer.personalSupply.add(Tile(null, null, tileType))

        onAllRefreshables { refreshAfterApplyCardEffects(null) }

    }

    /**
     * apply HelperCard effects
     * @param drawnCard : the drawn MasterCard
     */
    private fun playHelperCard(drawnCard: HelperCard, cardPosition: Int) {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        if (!gameState.currentPlayer.isLocal) {
            onAllRefreshables { refreshAfterApplyCardEffects(cardPosition) }
        } else {
            onAllRefreshables { refreshAfterDrawingHelperCard(drawnCard.tileTypes) }
        }
    }
    /**
     *
     */
    fun checkSupply(){
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        val actPlayer = gameState.currentPlayer
        if (actPlayer.personalSupply.size > actPlayer.tileCapacity) {
            gameState.currentState = States.DISCARDING

            onAllRefreshables { refreshAfterReceivedTile(true) }
            return
        }

        actPlayer.hasPlayed = true
        onAllRefreshables { refreshAfterApplyCardEffects(null) }
    }

    /**
     * Action to place a tile from personal supply on bonsai tree.
     *
     * preconditions:
     * - The game has started and is currently running.
     *
     * post conditions:
     * - updates that the current player has done an action during his turn.
     * - sets the current state to cultivate
     *
     * @throws IllegalStateException if player has already done an action during his current turn.
     */
    fun cultivate() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        val currentPlayer = gameState.currentPlayer

        if (currentPlayer.hasPlayed) {
            throw IllegalStateException("Player has already meditated or cultivated.")
        }

        // create mutable list of the tiles that are playable this turn
        currentPlayer.playableTilesCopy = currentPlayer.playableTiles.toMutableList()

        // enable the player to click the end turn button at any time from now
        currentPlayer.hasPlayed = true

        // change game state to CULTIVATE
        gameState.currentState = States.CULTIVATE

        // let the game know at endTurn() that it should send cultivateMessage
        if (rootService.networkService.connectionState != ConnectionState.DISCONNECTED &&
            currentPlayer.isLocal
        ) {
            rootService.networkService.hasCultivated = true
        }

        onAllRefreshables { refreshAfterCultivateStart() }

    }

    /**
     * Ends the turn of the current player.
     *
     * preconditions:
     * - player has played an action (meditate or cultivate).
     *
     * post conditions:
     * - The current player switches to the next player.
     *
     * @throws IllegalStateException if the current player has not played an action yet.
     */
    fun endTurn() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        require(canEndTurn())


        // Trigger end game by counting the turn of player
        if (gameState.zenDeck.isEmpty()) {
            gameState.endGameCounter++
        }

        val net = rootService.networkService
        // if it's online and we're the local player
        if (net.connectionState != ConnectionState.DISCONNECTED &&
            getCurrentPlayer().isLocal
        ) {
            // if we cultivated then send cultivateMessage
            if (net.hasCultivated &&
                net.connectionState == ConnectionState.PLAYING_MY_TURN) {
                net.sendCultivateMessage()
                net.hasCultivated = false
            } else if (net.hasMeditated &&
                net.connectionState == ConnectionState.PLAYING_MY_TURN) {
                net.sendMeditateMessage()
                net.hasMeditated = false
            }
        }

        // When the counter = the number of players -> all players finish their last turn
        if (gameState.endGameCounter == gameState.players.size) {
            // Calculate score for all players before showing the winner
            gameState.players.forEach { player ->
                player.score = rootService.gameService.calculateScore(player)[5]
            }
            rootService.gameService.showWinner()
        } else {
            gameState.currentPlayer.score = rootService.gameService.calculateScore(gameState.currentPlayer)[5]

            rootService.gameService.switchPlayerTurn()
        }

        gameState.currentState = States.START_TURN
        gameState.currentPlayer.hasPlayed = false


        getCurrentPlayer().hasPlayed = false
        // save history
        val copiedState = gameState.deepCopy()
        game.history?.gameStates?.add(copiedState)
        game.history?.currentPosition = game.history?.gameStates?.lastIndex ?: 0

        onAllRefreshables { refreshAfterEndTurn() }

    }



    /**
     * Player claims or renounces goal tile.
     *
     * preconditions:
     * - player has reached conditions to claim a goal tile.
     *
     * post conditions:
     * - player has accepted the goal tile or
     * - goal tile gets locked from player
     *
     * @param claim true if player accepts goal tile, otherwise false.
     *
     */
    fun claimOrRenounceGoal(claim: Boolean, goalTileType: GoalTileType, tier: Int) {
        val game = checkNotNull(rootService.currentGame) { "No game was started." }

        val gameState = checkNotNull(game.currentBonsaiGameState) { "No active game state." }

        val player = getCurrentPlayer()

        require(canClaimOrRenounceGoal(goalTileType, tier))


        // if the goal tile requirement is reached, get goal tile based on the claim
        if (claim) {
            // remove the claimed goal tile from the list
            gameState.goalTiles.removeIf { tile ->
                if (tile.goalTileType == goalTileType && tile.tier == tier) {
                    player.claimedGoals.add(tile)
                    true
                } else false
            }
            // update message
            writeGoalInMessage(goalTileType, tier, true)
        } else {
            gameState.goalTiles.forEach { tile ->
                if (tile.goalTileType == goalTileType && tile.tier == tier) {
                    player.renouncedGoals.add(tile)
                }
            }
            // update message
            writeGoalInMessage(goalTileType, tier, false)
        }

        if(!getCurrentPlayer().isLocal && claim) onAllRefreshables {
            refreshAfterClaimGoal(goalTileType, tier) }
    }

    private fun writeGoalInMessage(goalTileType: GoalTileType, tier: Int, claim: Boolean) {
        val net = rootService.networkService

        if (net.connectionState != ConnectionState.DISCONNECTED &&
            getCurrentPlayer().isLocal && claim
        ) {
            if (net.hasCultivated) {
                net.toBeSentCultivateMessage.claimedGoals.add((goalTileType to tier - 1))
            } else if (net.hasMeditated) {
                net.toBeSentMeditateMessage.claimedGoals.add((goalTileType to tier - 1))
            }
        } else if (net.connectionState != ConnectionState.DISCONNECTED &&
            getCurrentPlayer().isLocal && !claim) {
            if (net.hasCultivated) {
                net.toBeSentCultivateMessage.renouncedGoals.add((goalTileType to tier - 1))

            } else if (net.hasMeditated) {
                net.toBeSentMeditateMessage.renouncedGoals.add((goalTileType to tier - 1))
            }
        }
    }


    /**
     * Checks if the given goal can be claimed
     *
     * preconditions:
     * - the game must be active,
     *
     * post conditions:
     * - returned true if the goal can be claimed
     * - returned false otherwise
     */

    fun canClaimOrRenounceGoal(goalTileType: GoalTileType, tier: Int): Boolean {

        val player = getCurrentPlayer()
        val playersBonsaiTree = player.bonsaiTree

        if (player.claimedGoals.any { it.goalTileType == goalTileType }) {
            return false
        }
        if (player.renouncedGoals.any { it.goalTileType == goalTileType && it.tier == tier }) {
            return false
        }

        return when (goalTileType) {
            GoalTileType.BROWN -> hasReachedBrownGoal(playersBonsaiTree, tier)
            GoalTileType.GREEN -> hasReachedGreenGoal(playersBonsaiTree, tier)
            GoalTileType.PINK -> hasReachedPinkGoal(playersBonsaiTree, tier)
            GoalTileType.ORANGE -> hasReachedOrangeGoal(playersBonsaiTree, tier)
            GoalTileType.BLUE -> hasReachedBlueGoal(playersBonsaiTree, tier)
        }
    }

    /**
     * count the amount of wood tiles in the players tree and compare it do the necessary amount for the tier.
     *
     * @param bonsaiTree is the bonsai tree of the active player
     * @param tier the tier of the brown goal tile
     *
     * @return whether the goal tile can be claimed or not
     */
    private fun hasReachedBrownGoal(bonsaiTree: MutableMap<Pair<Int, Int>, Tile>, tier: Int): Boolean {
        val brownTiles = bonsaiTree.keys.count { bonsaiTree[it]?.tileType == TileType.WOOD }
        return when (tier) {
            0 -> brownTiles >= 8
            1 -> brownTiles >= 10
            2 -> brownTiles >= 12
            else -> false
        }
    }

    /**
     * count the amount of adjacent leaf tiles in the players tree and compare it do the necessary amount for the tier.
     *
     * @param bonsaiTree is the bonsai tree of the active player
     * @param tier the tier of the green goal tile
     *
     * @return whether the goal tile can be claimed or not
     */
    private fun hasReachedGreenGoal(bonsaiTree: MutableMap<Pair<Int, Int>, Tile>, tier: Int): Boolean {
        // Set to keep track of visited tiles so that there are no redundant calculations
        val visited = mutableSetOf<Pair<Int, Int>>()

        // Find the size of the largest cluster of LEAF tiles
        var maxLeafCluster = 0
        for ((pos, tile) in bonsaiTree) {
            if (tile.tileType == TileType.LEAF && pos !in visited) {
                maxLeafCluster = maxOf(maxLeafCluster, dfs(pos, visited, bonsaiTree))
            }
        }

        // Check if the largest cluster meets the tier requirement
        return when (tier) {
            0 -> maxLeafCluster >= 5
            1 -> maxLeafCluster >= 7
            2 -> maxLeafCluster >= 9
            else -> false
        }
    }

    // Function to perform depth-first search and count the size of a cluster
    private fun dfs(tilePos: Pair<Int, Int>,
                    visited: MutableSet<Pair<Int, Int>>,
                    bonsaiTree: MutableMap<Pair<Int, Int>, Tile>): Int {
        //stack to keep track of all the tiles
        val stack = mutableListOf(tilePos)
        var count = 0

        while (stack.isNotEmpty()) {
            val (q, r) = stack.removeLast()
            // Skip already visited tiles
            if (!visited.add(Pair(q, r))) continue

            count++
            // find adjacent tiles
            for ((dq, dr) in SIDE_VECTORS) {
                val neighbor = Pair(q + dq, r + dr)
                if (neighbor in bonsaiTree && bonsaiTree[neighbor]?.tileType ==
                    TileType.LEAF && neighbor !in visited) {
                    stack.add(neighbor)
                }
            }
        }
        return count
    }

    /**
     * count the amount of flower tiles in the players tree and compare it do the necessary amount for the tier.
     *
     * @param bonsaiTree is the bonsai tree of the active player
     * @param tier the tier of the pink goal tile
     *
     * @return whether the goal tile can be claimed or not
     */
    private fun hasReachedPinkGoal(bonsaiTree: MutableMap<Pair<Int, Int>, Tile>, tier: Int): Boolean {
        val leftProtrude = bonsaiTree.keys.count { it.first <= -2 && bonsaiTree[it]?.tileType == TileType.FLOWER }
        val rightProtrude =
            bonsaiTree.keys.count {
                (it.first >= 4 || it.first == 3 && (it.second) % 2 == 0)
                        && bonsaiTree[it]?.tileType == TileType.FLOWER
            }

        val pinkProtruding = max(leftProtrude, rightProtrude)

        return when (tier) {
            0 -> pinkProtruding >= 3
            1 -> pinkProtruding >= 4
            2 -> pinkProtruding >= 5
            else -> false
        }
    }

    /**
     * count the amount of fruit tiles in the players tree and compare it do the necessary amount for the tier.
     *
     * @param bonsaiTree is the bonsai tree of the active player
     * @param tier the tier of the orange goal tile
     *
     * @return whether the goal tile can be claimed or not
     */
    private fun hasReachedOrangeGoal(bonsaiTree: MutableMap<Pair<Int, Int>, Tile>, tier: Int): Boolean {
        val orangeTiles = bonsaiTree.keys.count { bonsaiTree[it]?.tileType == TileType.FRUIT }
        return when (tier) {
            0 -> orangeTiles >= 3
            1 -> orangeTiles >= 4
            2 -> orangeTiles >= 5
            else -> false
        }
    }

    /**
     * checks if a player has reached one of the blue goal tiles.
     *
     * @param bonsaiTree is the bonsai tree of the active player
     * @param tier the tier of the blue goal tile
     *
     * return true if the [tier] of the blue goal tile is reached, otherwise false
     */
    private fun hasReachedBlueGoal(bonsaiTree: MutableMap<Pair<Int, Int>, Tile>, tier: Int): Boolean {

        val leftProtrude = bonsaiTree.keys.any { it.second <= -2 * (it.first + 2) }
        val rightProtrude = bonsaiTree.keys.any { it.second >= -2 * (it.first - 3) }

        val bellowLeftProtrude = bonsaiTree.keys.any { leftProtrude && it.second >= 2 }
        val bellowRightProtrude = bonsaiTree.keys.any { rightProtrude && it.second >= 2 }

        return when (tier) {
            0 -> rightProtrude
            1 -> leftProtrude && rightProtrude
            2 -> (leftProtrude && bellowRightProtrude) || (rightProtrude && bellowLeftProtrude)
            else -> false
        }
    }

    /**
     * Checks if player has played an action before ending his turn.
     *
     * preconditions:
     * - Player must play an action (cultivate or meditate).
     *
     *  @return true if player can end his turn.
     */
    fun canEndTurn(): Boolean {
        val player = getCurrentPlayer()
        return player.hasPlayed
    }

    /**
     * Discards tile(s) from supply if the personal capacity limit has been exceeded.
     *
     * preconditions:
     * - There are more tiles in personal supply than allowed.
     *
     * post conditions:
     * - bonsai tile(s) are removed from players personal supply.
     *
     * @throws IllegalStateException if personal supply is not over capacity limit.
     */
    fun discardSupplyTile(tileToDiscard: Tile) {
        val msg = rootService.networkService.toBeSentMeditateMessage
        val player = getCurrentPlayer()
        check(player.personalSupply.size > player.tileCapacity)
        { "The personal supply tiles hasn't reached the capacity." }
        player.personalSupply.remove(tileToDiscard)
        msg.discardedTiles.add(tileToDiscard.tileType)
        rootService.currentGame?.currentBonsaiGameState?.currentState = States.END_TURN
        player.hasPlayed = true
    }

    // returns the current player
    private fun getCurrentPlayer(): Player {
        return checkNotNull(rootService.currentGame?.currentBonsaiGameState?.currentPlayer)
    }
}
