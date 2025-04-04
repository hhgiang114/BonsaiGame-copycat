package service

import entity.*
import util.POT


/**
 * The service layer class which contains all actions related to the bonsai tree
 */
class TreeService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Place a bonsai tile to players bonsai tree.
     *
     * preconditions:
     * - Player has chosen action cultivate, or has a helper card.
     * - Player can place a wood tile to his bonsai tree.
     *
     * post conditions:
     * - bonsai tile is added to bonsai tree.
     *
     * @param tile The bonsai tile to be placed.
     * @param tilePosition The position of the bonsai tile to be placed.
     * @throws IllegalArgumentException if the bonsai tile is not playable.
     * @throws IllegalArgumentException if the tile position is invalid.
     */
    fun playTile(tile: Tile, tilePosition: Pair<Int, Int>) {

        require(canPlayTile(tile, tilePosition)) { "Tile can not be played" }

        val currentPlayer = getCurrentPlayer()
        currentPlayer.bonsaiTree[tilePosition] = tile
        currentPlayer.personalSupply.remove(tile)

        if (tile.tileType in currentPlayer.playableTilesCopy) {
            currentPlayer.playableTilesCopy.remove(tile.tileType)
        } else if (TileType.ANY in currentPlayer.playableTilesCopy) {
            currentPlayer.playableTilesCopy.remove(TileType.ANY)
        }

        // update message
        val net = rootService.networkService
        if (net.connectionState != ConnectionState.DISCONNECTED &&
            currentPlayer.isLocal
        )
            if (net.hasCultivated) {
                net.toBeSentCultivateMessage.playedTiles.add(
                    (tile.tileType to (tilePosition))
                )
            } else if (net.hasMeditated) {
                net.toBeSentMeditateMessage.playedTiles.add(
                    (tile.tileType to (tilePosition))
                )
            }

        val game = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(game)
        val goalTiles: MutableList<Pair<GoalTileType, Int>> = mutableListOf()
        var reached = false
        game.goalTiles.forEach {
            if (rootService.playerActionService.canClaimOrRenounceGoal(it.goalTileType, it.tier) &&
                    !currentPlayer.renouncedGoals.contains(it)) {
                goalTiles += (it.goalTileType to it.tier)
                reached = true
                onAllRefreshables { refreshAfterPlayTile(it.goalTileType, it.tier, tilePosition) }
                }
        }

        if (!reached) {
            // do other stuff in gui
            onAllRefreshables { refreshAfterPlayTile(null, 0, tilePosition) }
        }
    }

    /**
     * Removes a tile from the current player's bonsai tree and updates the game state accordingly.
     *
     * This function ensures that the game is in a valid state before removing the tile. It verifies that:
     * - A game is currently active.
     * - The game state is properly initialized.
     * - The tile to be removed is a **LEAF** tile.
     * - The removal is **minimal and correct** according to game rules.
     * - If the removed tile is adjacent to any **FRUIT** tiles, those adjacent **FRUIT** tiles are also removed.
     *
     * After removing the tile(s), the function refreshes the game UI and, if applicable, updates the network message
     * for multiplayer synchronization.
     *
     * @param tilePosition The axial coordinates of the tile to be removed.
     * @throws IllegalStateException if no game is active.
     * @throws IllegalArgumentException if the move is not valid.
     */
    fun removeFromTree(tilePosition: Pair<Int, Int>) {

        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }


        val currentPlayer = getCurrentPlayer()

        require(!canPlayWood())


        require(gameState.currentPlayer.bonsaiTree[tilePosition]?.tileType == TileType.LEAF
                    || gameState.currentPlayer.bonsaiTree[tilePosition]?.tileType == TileType.FLOWER)
            { "not a valid move" }



                if((gameState.currentPlayer.bonsaiTree[tilePosition]?.tileType == TileType.LEAF)
                    && isMinimalAndCorrect(tilePosition)){
                    if (getNeighbourTiles(tilePosition).any { it.second?.tileType == TileType.FRUIT }) {
                        getNeighbourTiles(tilePosition)
                            .filter { it.second?.tileType == TileType.FRUIT }
                            .forEach { currentPlayer.bonsaiTree.remove(it.first) }
                        currentPlayer.bonsaiTree.remove(tilePosition)
                    } else {
                        currentPlayer.bonsaiTree.remove(tilePosition)
                    }
                }
                else{
                    currentPlayer.bonsaiTree.remove(tilePosition)
                }

                gameState.currentState = States.CHOOSE_ACTION
                //currentPlayer.hasPlayed = true
                // Refresh GUI to reflect the updated tree
                onAllRefreshables { refreshAfterRemoveFromTree(tilePosition) }

                // update message
                val net = rootService.networkService
                if (net.connectionState != ConnectionState.DISCONNECTED &&
                    currentPlayer.isLocal
                ) {
                    net.toBeSentCultivateMessage.removedTilesAxialCoordinates.add(tilePosition)
                    net.toBeSentMeditateMessage.removedTilesAxialCoordinates.add(tilePosition)
                }
    }

    /**
     * Determines whether a given **LEAF** tile can be **minimally and correctly** removed from the bonsai tree.
     *
     * A **LEAF** tile is considered removable if:
     * - It has at least one empty neighboring tile.
     * - It does not violate game constraints regarding WOOD, LEAF, and FLOWER tiles.
     * - If multiple LEAF tiles are removable, priority is given to the ones in `removableLeafs1`,
     *   which are the most minimal and correct removals.
     *
     * The function first classifies all removable **LEAF** tiles into two groups:
     * 1. **`removableLeafs1`** - The highest-priority LEAF tiles that can be removed.
     * 2. **`removableLeafs2`** - Other LEAF tiles that are removable but the system should also remove connected tiles.
     *
     * The function returns `true` if the given `tilePosition` exists in either of these lists,
     * with priority given to `removableLeafs1`.
     *
     * @param tilePosition The axial coordinates of the tile to check for removability.
     * @return `true` if the tile is a **valid minimal removal**, otherwise `false`.
     * @throws IllegalStateException if no game or game state is active.
     */
    fun isMinimalAndCorrect(tilePosition: Pair<Int, Int>): Boolean {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }
        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        val tree = gameState.currentPlayer.bonsaiTree
        val flowerTilesPosition = tree
            .filter { it.value.tileType == TileType.FLOWER }.keys.toMutableList()
        val removableFlowers: MutableList<Pair<Int, Int>> = mutableListOf()
        flowerTilesPosition.forEach { position ->
            val neighbourTiles = getNeighbourTiles(position)

            val hasNullNeighbor = neighbourTiles.any { it.second == null }
            val hasWoodNeighbor = neighbourTiles.any { it.second?.tileType == TileType.WOOD }

            if (hasNullNeighbor && hasWoodNeighbor) {
                if (neighbourTiles.all { checkNullWoodLeaf(it) }) {
                    removableFlowers.add(position)
                }
            }
        }

        val leafTilesPositions = tree
            .filter { it.value.tileType == TileType.LEAF }.keys.toMutableList()
        val removableLeafs1: MutableList<Pair<Int, Int>> = mutableListOf()
        val removableLeafs2: MutableList<Pair<Int, Int>> = mutableListOf()
        leafTilesPositions.forEach { position ->
            val neighbourTiles = getNeighbourTiles(position)
            if (neighbourTiles.any { it.second == null }) {
                if (neighbourTiles.all {
                        checkNullWoodLeaf(it) || (it.second?.tileType == TileType.FLOWER &&
                                getNeighbourTiles(it.first)
                                    .filter { neighbor -> neighbor.first != position }
                                    .any { neighbor -> neighbor.second?.tileType == TileType.LEAF })
                    }) {
                    removableLeafs1.add(position)
                } else {
                    removableLeafs2.add(position)
                }
            }
        }
        if (gameState.currentPlayer.bonsaiTree[tilePosition]?.tileType == TileType.FLOWER){
            return removableFlowers.contains(tilePosition)
        }
        else{
            if (removableLeafs1.isNotEmpty()) return removableLeafs1.contains(tilePosition)
                    return removableLeafs2.contains(tilePosition)
        }

    }

    private fun checkNullWoodLeaf(neighbourTile: Pair<Pair<Int, Int>, Tile?>): Boolean{
        return  neighbourTile.second == null ||
                neighbourTile.second?.tileType == TileType.WOOD ||
                neighbourTile.second?.tileType == TileType.LEAF
    }

    /**
     * Retrieves the six neighboring tiles of a given position in a hexagonal grid.
     *
     * Each hexagonal tile has six neighbors, determined by axial coordinates `(q, r)`.
     * This function returns a list of all neighboring positions along with their corresponding tiles.
     * If a neighboring tile does not exist in the bonsai tree, `null` is returned for that position.
     *
     * @param position The axial coordinates `(q, r)` of the tile whose neighbors should be retrieved.
     * @return a list of pairs where each pair consists of:
     *         - The neighbor's coordinates as a `Pair<Int, Int>`.
     *         - The corresponding `Tile?` (or `null` if there is no tile at that position).
     * @throws IllegalStateException if no game or game state is active.
     */
    private fun getNeighbourTiles(position: Pair<Int, Int>): List<Pair<Pair<Int, Int>, Tile?>> {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        val tree = gameState.currentPlayer.bonsaiTree
        val q = position.first
        val r = position.second
        return listOf(
            Pair(Pair(q + 1, r), tree.getOrDefault(Pair(q + 1, r), null)),
            Pair(Pair(q, r + 1), tree.getOrDefault(Pair(q, r + 1), null)),
            Pair(Pair(q - 1, r + 1), tree.getOrDefault(Pair(q - 1, r + 1), null)),
            Pair(Pair(q - 1, r), tree.getOrDefault(Pair(q - 1, r), null)),
            Pair(Pair(q, r - 1), tree.getOrDefault(Pair(q, r - 1), null)),
            Pair(Pair(q + 1, r - 1), tree.getOrDefault(Pair(q + 1, r - 1), null))
        )
    }

    /**
     * Checks if a bonsai tile can be played based on the symbols shown on
     * Seishi tile or growth cards.
     *
     * preconditions:
     * - player must have bonsai tiles to place with.
     * - player must have bonsai tile shown on Seishi tile or growth cards.
     *
     * @param tile The bonsai tile to be placed.
     * @return true if bonsai tile can be placed.
     * @throws IllegalArgumentException if player has no bonsai tiles to place.
     */
    fun canPlayTile(tile: Tile): Boolean {
        val currentPlayer = getCurrentPlayer()
        require(currentPlayer.personalSupply.any{
            it.typeEqual(tile)
        }
        )
        { "Player does not have this bonsai tile in hand" }

        return currentPlayer.playableTilesCopy.contains(tile.tileType)
                || currentPlayer.playableTilesCopy.contains(TileType.ANY)
    }

    /**
     * Checks if a WOOD tile can be placed in the bonsai tree.
     *
     * The function iterates through all existing WOOD tiles in the tree and determines
     * whether any of them have an available adjacent position
     * A position is considered available if:
     * - It is one of the six hexagonal neighbors of a WOOD tile.
     * - It is **not** part of the predefined `POT` (restricted area).
     * - It is currently unoccupied
     *
     * If at least one WOOD tile has an open space next to it,
     * the function returns `true`; otherwise, it returns `false`.
     *
     * @return `true` if a WOOD tile can be placed, otherwise `false`.
     */
    fun canPlayWood(): Boolean {
        val game = rootService.currentGame
        checkNotNull(game) { "No game was started." }

        val gameState = game.currentBonsaiGameState
        checkNotNull(gameState) { "No active game state." }
        val tree = getCurrentPlayer().bonsaiTree
        tree.filter { it.value.tileType == TileType.WOOD }
            .forEach { (position, tile) ->
                val neighbourTiles = getNeighbourTiles(position).filter { it.first !in POT }

                val nullPositions = neighbourTiles.filter { it.second == null }.map { it.first }

                if (nullPositions.isNotEmpty()) {
                    gameState.currentState = States.CHOOSE_ACTION
                    return true
                }
            }
        gameState.currentState = States.REMOVE_TILES
        return false
    }


    /**
     * Checks if a bonsai tile can be played based on the game rules.
     *
     * Rules:
     * - A wood tile must be placed on another wood tile.
     * - A leaf tile must be placed on a wood tile.
     * - A flower tile must be placed on a leaf tile.
     * - A fruit tile must be placed in between two leave tiles.
     *
     * @param tile The bonsai tile to be placed.
     * @param tilePosition The position of the bonsai tile to be placed.
     * @return true if bonsai tile can be placed.
     * @throws IllegalArgumentException if player has no bonsai tiles to place.
     * @throws IllegalArgumentException if bonsai tile is played in invalid position.
     */
    fun canPlayTile(tile: Tile, tilePosition: Pair<Int, Int>): Boolean {
        if (!canPlayTile(tile)) return false
        val currentPlayer = getCurrentPlayer()

        // Position of pot
        val forbiddenPositions = POT

        require(
            !currentPlayer.bonsaiTree.containsKey(tilePosition)
                    && tilePosition !in forbiddenPositions
        ) { "Position is already occupied or is pot" }

        val tree = currentPlayer.bonsaiTree
        val q = tilePosition.first
        val r = tilePosition.second
        val neighbourTiles = mutableListOf(
            tree.getOrDefault(Pair(q + 1, r), null)?.tileType,
            tree.getOrDefault(Pair(q, r + 1), null)?.tileType,
            tree.getOrDefault(Pair(q - 1, r + 1), null)?.tileType,
            tree.getOrDefault(Pair(q - 1, r), null)?.tileType,
            tree.getOrDefault(Pair(q, r - 1), null)?.tileType,
            tree.getOrDefault(Pair(q + 1, r - 1), null)?.tileType,
        ).filterNotNull()

        require(neighbourTiles.isNotEmpty()) { "There are no adjacent cards" }

        return when (tile.tileType) {
            TileType.WOOD -> neighbourTiles.contains(TileType.WOOD)
            TileType.LEAF -> neighbourTiles.contains(TileType.WOOD)
            TileType.FLOWER -> neighbourTiles.contains(TileType.LEAF)
            TileType.FRUIT -> {
                val neighbours = listOf(
                    Pair(q + 1, r), Pair(q, r + 1), Pair(q - 1, r + 1),
                    Pair(q - 1, r), Pair(q, r - 1), Pair(q + 1, r - 1)
                )
                val leafPositions = neighbours.filter { tree[it]?.tileType == TileType.LEAF }

                if (leafPositions.size < 2) return false

                for (i in 0 until leafPositions.size - 1) {
                    val firstLeaf = leafPositions[i]
                    val secondLeaf = leafPositions[i + 1]
                    val firstIndex = neighbours.indexOf(firstLeaf)
                    val secondIndex = neighbours.indexOf(secondLeaf)
                    if ((firstIndex + 1) % 6 == secondIndex || (secondIndex + 1) % 6 == firstIndex) {
                        return !neighbourTiles.contains(TileType.FRUIT)
                    }
                }
                false

            }

            else -> {
                return false
            }
        }
    }

    /**
     * Retrieves the current player from the active bonsai game state.
     *
     * This function ensures that a game is active before attempting to access the current player.
     * If no game or game state is found, an exception is thrown.
     *
     * @return The `Player` object representing the current player.
     * @throws IllegalStateException if no active game or game state exists.
     */
    private fun getCurrentPlayer(): Player {
        val currentGameState = checkNotNull(rootService.currentGame?.currentBonsaiGameState)
        return currentGameState.currentPlayer
    }

}
