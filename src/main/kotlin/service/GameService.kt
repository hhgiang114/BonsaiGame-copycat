package service

import entity.*
import util.ZenCardLoader

/**
 * Service layer class that provides the logic for actions taken by the System during the game.
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {
    /**
     * Starts a new game with:
     * - players
     * - specified player order and
     * - decide if network game or local game
     *
     * preconditions:
     * - There is no existing game.
     * - player must set their names and player order.
     * - must decide if played online or local.
     *
     * post conditions:
     * - Game started.
     *
     * @param playerOrder List of ordered players that are playing this game.
     * @param playerOrder A list of Players deciding the player order.
     * @param networkGame true if game is played online otherwise false.
     */
    fun startNewGame(
        playerOrder: MutableList<Player>,
        networkGame: Boolean,
        goalTilesEntries: MutableList<GoalTileType>
    ) {
        // check required conditions
        require(playerOrder.size >= 2) { "at least 2 Players" }
        require(goalTilesEntries.size == 3) { "Exactly 3 goal tiles must be selected." }

        val playerColors = playerOrder.map { it.color }
        require(playerColors.distinct().size == playerColors.size) { "All players must have unique colors." }

        var zenDeck = mutableListOf<Card>()
        val faceUpCards = mutableListOf<Card>()
        if (rootService.networkService.connectionState != ConnectionState.WAITING_FOR_INIT) {
            // create the zenDeck depending on the number of players
            zenDeck = ZenCardLoader().readAllZenCards(playerOrder.size).shuffled().toMutableList()
            // put the first 4 cards face up
            faceUpCards.addAll(zenDeck.takeLast(4))
            repeat(4) {zenDeck.removeLast()}
        }


        val gameState = BonsaiGameState(
            currentPlayer = playerOrder.first(),
            players = playerOrder,
            1,
            currentState = States.START_TURN
        )

        // create the list of goalTiles
        gameState.goalTiles = createGoalTiles(goalTilesEntries, playerOrder.size)
        // assign the created zenDeck to the zenDeck of the game
        gameState.zenDeck = zenDeck
        // assign the face-up cards list to the game face-up cards
        gameState.faceUpCards = faceUpCards

        // give each player the post Tiles depending on the order
        gameState.players.forEachIndexed { index, player ->
            val tiles = mutableListOf<TileType>()

            if (index == 0) {
                tiles.add(TileType.WOOD)
            } else {
                tiles.add(TileType.WOOD)
                tiles.add(TileType.LEAF)

                if (index >= 2) tiles.add(TileType.FLOWER)
                if (index >= 3) tiles.add(TileType.FRUIT)
            }
            player.personalSupply.addAll(tiles.map { Tile(null, null, it) })
        }

        // add the new game state to the history
        val setHistory = History().apply { gameStates.add(gameState) }

        val game = BonsaiGame().apply {
            history = setHistory
            currentBonsaiGameState = gameState
        }

        rootService.currentGame = game

        if (!networkGame) onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     * Switches the turn to the next player. Assuming player list is in correct turn order
     *
     * preconditions:
     * - Current player has ended his turn.
     *
     * post conditions:
     * - The current player switched to the next player.
     */
    fun switchPlayerTurn() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        val currentIndex = gameState.players.indexOf(gameState.currentPlayer)

        // get next player in the list, looping back to the first player at end of the list
        val nextIndex = (currentIndex + 1) % gameState.players.size
        gameState.currentPlayer = gameState.players[nextIndex]

        // refreshes here and not in endTurn
        onAllRefreshables { refreshAfterEndTurn() }
    }

    /**
     * refreshes with a list of the best placed players in order
     *
     * preconditions:
     * - Zen deck is empty and all players played their last action.
     *
     * post conditions:
     * - a refresh with the player order was used, where the winning player is at index 0,
     * second place is at index 1 etc.
     *
     * @throws IllegalStateException if game isn't over yet.
     */
    fun showWinner() : List<Player> {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        if (gameState.endGameCounter != gameState.players.size) {
            throw IllegalStateException("Game is not over yet.")
        }

        // Get the order of players by score with tiebreak included
        val winnerOrder = gameState.players.sortedWith(compareByDescending<Player> { it.score }
            .thenByDescending { gameState.players.indexOf(it) })

        onAllRefreshables { refreshAfterShowWinner(winnerOrder) }
        return winnerOrder.toList()
    }

    /**
     * Calculates the score of the current player.
     */
    fun calculateScore(player: Player): List<Int> {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        //val player = gameState.currentPlayer
        val playersBonsaiTree = player.bonsaiTree
        val playersCollectedCards = player.collectedCards
        val claimGoals = player.claimedGoals

        // Calculate score for each tile
        val leaf = countTilesType(playersBonsaiTree, TileType.LEAF)
        val fruit = countTilesType(playersBonsaiTree, TileType.FRUIT)

        val scoreOfLeaf = leaf * 3
        val scoreOfFlower = calculateFlowerPoints(playersBonsaiTree)
        val scoreOfFruit = fruit * 7

        val scoreTiles = scoreOfLeaf + scoreOfFlower + scoreOfFruit

        // Calculate score for parchment
        var scoreParchment = 0

        // Filter all parchment cards
        val parchmentCards = playersCollectedCards.filterIsInstance<ParchmentCard>()

        for (parchment in parchmentCards) {
            val basePoints = parchment.basePoints

            // If parchment is based on TileType
            parchment.parchmentTileType?.let { tileType ->
                val tileCount = countTilesType(playersBonsaiTree, tileType)
                val points = tileCount * basePoints
                scoreParchment += points
            }

            // If parchment is based on CardType
            parchment.parchmentCardType?.let { cardType ->
                val cardCount = countZenCardType(playersCollectedCards, cardType)
                val points = cardCount * basePoints
                scoreParchment += points
            }
        }

        // Calculate score for Goal
        val scoreOfGoal = claimGoals.sumOf { it.score }

        // Total
        val total = scoreTiles + scoreParchment + scoreOfGoal

        return listOf(scoreOfLeaf, scoreOfFruit, scoreOfFlower, scoreOfGoal, scoreParchment, total)
    }

    // Help functions for calculating score

    private fun countTilesType(bonsaiTree: MutableMap<Pair<Int, Int>, Tile>, type: TileType): Int {
        return bonsaiTree.values.count { it.tileType == type }
    }

    private fun countZenCardType(collectedZenCard: MutableList<Card>, type: CardType): Int {
        return collectedZenCard.count { it.cardType == type }
    }

    private fun calculateFlowerPoints(bonsaiTree: Map<Pair<Int, Int>, Tile>): Int {
        var totalPoints = 0

        for ((position, tile) in bonsaiTree) {
            if (tile.tileType == TileType.FLOWER) {
                val (q, r) = position

                // Define neighbor positions
                val neighbors = listOf(
                    Pair(q + 1, r),
                    Pair(q, r + 1),
                    Pair(q - 1, r + 1),
                    Pair(q - 1, r),
                    Pair(q, r - 1),
                    Pair(q + 1, r - 1)
                )

                // Count sides that are NOT touching other tiles
                val emptySides = neighbors.count { neighborPos -> !bonsaiTree.containsKey(neighborPos) }

                // Add points (1 point per empty side)
                totalPoints += emptySides
            }
        }

        return totalPoints
    }


    /**
     * Refills the board after a player has meditated.
     *
     * preconditions:
     * - player has meditated.
     * - zen deck is not empty.
     *
     * post conditions:
     * - all zen decks got shifted to the right side and
     * empty spot gets filled by zen deck.
     *
     * @throws IllegalStateException if zen deck is empty.
     */
    fun refillBoard() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }

        if (gameState.zenDeck.isEmpty()) {
            return
        }

        if (gameState.faceUpCards.size < 4) {
            val newCard = gameState.zenDeck.removeLast()
            gameState.faceUpCards.add(0, newCard)
        }


        onAllRefreshables { refreshAfterChooseCard() }
    }

    /**
     * it initialises GoalTiles for the game
     */
    fun createGoalTiles(goalTilesTypesEntries: MutableList<GoalTileType>, playerSize: Int)
            : MutableList<GoalTile> {

        val goalTiles: MutableList<MutableList<GoalTile>> = mutableListOf()

        val brownGoalTiles = mutableListOf(
            GoalTile(GoalTileType.BROWN, 0, 5),
            GoalTile(GoalTileType.BROWN, 1, 10),
            GoalTile(GoalTileType.BROWN, 2, 15)
        )
        val greenGoalTiles = mutableListOf(
            GoalTile(GoalTileType.GREEN, 0, 6),
            GoalTile(GoalTileType.GREEN, 1, 9),
            GoalTile(GoalTileType.GREEN, 2, 12)
        )
        val pinkGoalTiles = mutableListOf(
            GoalTile(GoalTileType.PINK, 0, 8),
            GoalTile(GoalTileType.PINK, 1, 12),
            GoalTile(GoalTileType.PINK, 2, 16)
        )
        val orangeGoalTiles = mutableListOf(
            GoalTile(GoalTileType.ORANGE, 0, 9),
            GoalTile(GoalTileType.ORANGE, 1, 11),
            GoalTile(GoalTileType.ORANGE, 2, 13)
        )
        val blueGoalTiles = mutableListOf(
            GoalTile(GoalTileType.BLUE, 0, 9),
            GoalTile(GoalTileType.BLUE, 1, 11),
            GoalTile(GoalTileType.BLUE, 2, 13)
        )

        goalTilesTypesEntries.forEach { goalTileType ->
            run {
                when (goalTileType) {
                    GoalTileType.BROWN -> goalTiles.add(brownGoalTiles)
                    GoalTileType.GREEN -> goalTiles.add(greenGoalTiles)
                    GoalTileType.PINK -> goalTiles.add(pinkGoalTiles)
                    GoalTileType.ORANGE -> goalTiles.add(orangeGoalTiles)
                    GoalTileType.BLUE -> goalTiles.add(blueGoalTiles)
                }
            }
        }

        if (playerSize == 2) {
            goalTiles.forEach { goalTile ->
                run {
                    goalTile.removeAt(1)
                }
            }
        }
        return goalTiles.flatten().toMutableList()
    }
}
