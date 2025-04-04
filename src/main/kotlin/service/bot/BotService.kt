package service.bot

import entity.*
import service.RootService


/**
 * The [BotService] class is used to implement all the actions of the bots
 *
 */
class BotService(val rootService: RootService) {

    /**
     * Function that makes a move for the bot
     *
     * @param //move is the move that will be used
     */
    /*
    fun makeMove(move: Move) {

        val currentGameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(currentGameState)

        //
        when {
            (move.actionType == MEDITATE) && (move.takenCard == 0) -> {
                rootService.playerActionService.meditate(move.takenCard, null)
                if (currentGameState.faceUpCards[move.takenCard].cardType == CardType.HELPERCARD) {
                    botPlayTiles(move)
                }
                if (currentGameState.faceUpCards[move.takenCard].cardType == CardType.MASTERCARD) {
                    val anyTile = move.chosenAnyTile
                    if (anyTile != null) {
                        rootService.playerActionService.chooseTile(move.chosenAnyTile)
                    }
                }
                rootService.playerActionService.endTurn()
            }

            (move.actionType == MEDITATE) && (move.takenCard == 1) -> {
                rootService.playerActionService.meditate(move.takenCard, move.chosenWoodOrLeafTile)
                if (currentGameState.faceUpCards[move.takenCard].cardType == CardType.HELPERCARD) {
                    botPlayTiles(move)
                }
                if (currentGameState.faceUpCards[move.takenCard].cardType == CardType.MASTERCARD) {
                    val anyTile = move.chosenAnyTile
                    if (anyTile != null) {
                        rootService.playerActionService.chooseTile(move.chosenAnyTile)
                    }
                }
                rootService.playerActionService.endTurn()
            }

            (move.actionType == MEDITATE) && (move.takenCard == 2) -> {
                rootService.playerActionService.meditate(move.takenCard, null)
                if (currentGameState.faceUpCards[move.takenCard].cardType == CardType.HELPERCARD) {
                    botPlayTiles(move)
                }
                if (currentGameState.faceUpCards[move.takenCard].cardType == CardType.MASTERCARD) {
                    val anyTile = move.chosenAnyTile
                    if (anyTile != null) {
                        rootService.playerActionService.chooseTile(move.chosenAnyTile)
                    }
                }
                rootService.playerActionService.endTurn()
            }

            (move.actionType == MEDITATE) && (move.takenCard == 3) -> {
                rootService.playerActionService.meditate(move.takenCard, null)
                if (currentGameState.faceUpCards[move.takenCard].cardType == CardType.HELPERCARD) {
                    botPlayTiles(move)
                }
                if (currentGameState.faceUpCards[move.takenCard].cardType == CardType.MASTERCARD) {
                    val anyTile = move.chosenAnyTile
                    if (anyTile != null) {
                        rootService.playerActionService.chooseTile(move.chosenAnyTile)
                    }
                }
                rootService.playerActionService.endTurn()
            }

            move.actionType == CULTIVATE -> {
                rootService.playerActionService.cultivate()
                botPlayTiles(move)
                rootService.playerActionService.endTurn()
            }
        }
    }
    */

    /**
     * an action an easy bot would make
     */


    fun makeRandomMove() {
        val currentGameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(currentGameState)

        //val randomAction = MEDITATE //(0..1).random()

        val hasTiles = mutableListOf<TileType>()

        for (tile in currentGameState.currentPlayer.personalSupply) {
            hasTiles.add(tile.tileType)
        }
        /*

        val possibleTilePlacing = allPossiblePlayedTiles(
            tree = currentGameState.currentPlayer.bonsaiTree,
            alreadyPlayedTiles = mutableListOf(),
            playableTiles = currentGameState.currentPlayer.playableTiles,
            hasTiles = hasTiles,
            returnList = mutableListOf()
        )

        when (randomAction) {

            CULTIVATE -> {
                rootService.playerActionService.cultivate()
                val placingTiles = possibleTilePlacing.random()
                for (tile in placingTiles) {
                    rootService.treeService.playTile(Tile(null, null, tile.first), tile.second)
                    currentGameState.goalTiles.forEach {
                        if (rootService.playerActionService.canClaimOrRenounceGoal(
                                it.goalTileType, it.tier
                            ) && !currentGameState.currentPlayer.renouncedGoals.contains(it)
                        ) {
                            rootService.playerActionService.claimOrRenounceGoal(
                                randomClaimOrRenounce(),
                                it.goalTileType,
                                it.tier,
                            )
                        }
                    }
                }
                rootService.playerActionService.endTurn()
            }
            */


            //MEDITATE ->
        onMeditateRandomAction()
    }

    private fun onMeditateRandomAction(){
        val currentGameState = rootService.currentGame?.currentBonsaiGameState
        checkNotNull(currentGameState)

        val chosenCardSlot = (0 until currentGameState.faceUpCards.size).random()
        val chosenRandomTile = randomWoodOrLeafTile()
        val chosenRandomAnyTile = randomTile()
//        val hasTiles = mutableListOf<TileType>()


        when (chosenCardSlot) {
            1 -> {
                rootService.playerActionService.meditate(1, chosenRandomTile)
            }

            else -> {
                rootService.playerActionService.meditate(chosenCardSlot, null)
            }
        }
//        if (currentGameState.faceUpCards[chosenCardSlot].cardType == CardType.HELPERCARD) {
//            val chosenCard = currentGameState.faceUpCards[chosenCardSlot] as HelperCard
            /*
            val helperCardPlayedTiles = allPossiblePlayedTiles(
                tree = currentGameState.currentPlayer.bonsaiTree,
                alreadyPlayedTiles = mutableListOf(),
                playableTiles = chosenCard.tileTypes,
                hasTiles = hasTiles,
                returnList = mutableListOf()
            ).random()for (tiles in helperCardPlayedTiles) {
                        rootService.treeService.playTile(Tile(null,null,tiles.first),tiles.second)
                        currentGameState.goalTiles.forEach {
                            if (rootService.playerActionService.canClaimOrRenounceGoal(it.goalTileType, it.tier) &&
                                !currentGameState.currentPlayer.renouncedGoals.contains(it)
                            ) {
                                rootService.playerActionService.claimOrRenounceGoal(
                                    randomClaimOrRenounce(),
                                    it.goalTileType,
                                    it.tier,
                                )
                            }
                        }
                    }
                    */
//        }
        if (currentGameState.faceUpCards[chosenCardSlot].cardType == CardType.MASTERCARD) {
            currentGameState.currentState = States.USING_MASTER
            rootService.playerActionService.chooseTile(chosenRandomAnyTile)
        }
        val personalSupply = currentGameState.currentPlayer.personalSupply
        var differenceSupplyAndTiles = currentGameState.currentPlayer.tileCapacity - personalSupply.size
        while (differenceSupplyAndTiles < 0) {
            personalSupply.removeAt((0 until personalSupply.size).random())
            differenceSupplyAndTiles = currentGameState.currentPlayer.tileCapacity - personalSupply.size
        }


        Thread.sleep(1000)
        rootService.playerActionService.endTurn()
    }

    /*
    private fun botPlayTiles(move: Move) {
        for (tile in move.playedTiles) {
            rootService.treeService.playTile(Tile(null, null, tile.first), tile.second)
        }
        for (goalTaken in move.takenGoalTile) {
            rootService.playerActionService.claimOrRenounceGoal(
                true, goalTaken.goalTileType, goalTaken.tier
            )
        }
        for (goalRenounced in move.renouncedGoalTile) {
            rootService.playerActionService.claimOrRenounceGoal(
                true, goalRenounced.goalTileType, goalRenounced.tier
            )
        }
    }
    */

    /*
    private fun randomClaimOrRenounce(): Boolean {
        return listOf(true, false).random()
    }
     */


//    // gives back the card position
//    private fun chooseCard(): Int {
//        val gameState = rootService.currentGame?.currentBonsaiGameState
//        checkNotNull(gameState)
//
//        val openCards = gameState.faceUpCards.size
//
//        return (0 until openCards).random()
//    }

    private fun randomWoodOrLeafTile(): TileType {
        return when ((0..1).random()) {
            0 -> TileType.WOOD
            1 -> TileType.LEAF
            else -> TileType.WOOD
        }
    }

    private fun randomTile(): TileType {
        return when ((0..3).random()) {
            0 -> TileType.WOOD
            1 -> TileType.LEAF
            2 -> TileType.FLOWER
            3 -> TileType.FRUIT
            else -> TileType.WOOD
        }
    }

/*
    private fun allPossiblePlayedTiles(
        tree: MutableMap<Pair<Int, Int>, Tile>,
        alreadyPlayedTiles: MutableList<Pair<TileType, Pair<Int, Int>>>,
        playableTiles: MutableList<TileType>,
        hasTiles: MutableList<TileType>,
        returnList: MutableList<MutableList<Pair<TileType, Pair<Int, Int>>>>
    ): MutableList<MutableList<Pair<TileType, Pair<Int, Int>>>> {

        val playedTiles = alreadyPlayedTiles
        val newReturnList: MutableList<MutableList<Pair<TileType, Pair<Int, Int>>>> = mutableListOf()
        returnList.add(playedTiles)

        if (playableTiles.isEmpty() || hasTiles.isEmpty()) return returnList

        for (emptyPosition in tree.getEmptyTiles()) {
            val neighbors = intArrayOf(0, 0, 0, 0, 0)
            for (tilePosition in circleAround(emptyPosition)) {
                val currentTile = tree[tilePosition]
                if (currentTile != null) {
                    when (currentTile.tileType) {
                        TileType.WOOD -> neighbors[0] += 1

                        TileType.LEAF -> neighbors[1] += 1

                        TileType.FLOWER -> neighbors[2] += 1

                        TileType.FRUIT -> neighbors[3] += 1

                        else -> neighbors[4] += 1
                    }
                }
            }

            var isRelevantType = (playableTiles.contains(TileType.WOOD) || playableTiles.contains(TileType.ANY))
            if (neighbors[0] >= 1 && isRelevantType && hasTiles.contains(
                    TileType.WOOD
                )
            ) {
                val newTreeWood1 = tree
                newTreeWood1[emptyPosition] = Tile(emptyPosition.first, emptyPosition.second, TileType.WOOD)
                val newPlayedTiles1 = playedTiles
                newPlayedTiles1.add(Pair(TileType.WOOD, emptyPosition))
                val newPlayableTiles1 = playableTiles
                if (!newPlayableTiles1.remove(TileType.WOOD)) {
                    newPlayableTiles1.remove(TileType.ANY)
                }
                val newHasTile1 = hasTiles
                newHasTile1.remove(TileType.WOOD)

                returnList += allPossiblePlayedTiles(
                    newTreeWood1, newPlayedTiles1, newPlayableTiles1, newHasTile1, newReturnList
                )
            }

            isRelevantType = (playableTiles.contains(TileType.LEAF) || playableTiles.contains(TileType.ANY))
            if (neighbors[0] >= 1 && isRelevantType && hasTiles.contains(
                    TileType.LEAF
                )
            ) {
                val newTreeWood2 = tree
                newTreeWood2[emptyPosition] = Tile(emptyPosition.first, emptyPosition.second, TileType.LEAF)
                val newPlayedTiles2 = playedTiles
                newPlayedTiles2.add(Pair(TileType.LEAF, emptyPosition))
                val newPlayableTiles2 = playableTiles
                if (!newPlayableTiles2.remove(TileType.LEAF)) {
                    newPlayableTiles2.remove(TileType.ANY)
                }
                val newHasTile2 = hasTiles
                newHasTile2.remove(TileType.LEAF)

                returnList += allPossiblePlayedTiles(
                    newTreeWood2, newPlayedTiles2, newPlayableTiles2, newHasTile2, newReturnList
                )
            }

            isRelevantType = (playableTiles.contains(TileType.FLOWER) || playableTiles.contains(TileType.ANY))
            if (neighbors[1] >= 1 && isRelevantType && hasTiles.contains(
                    TileType.FLOWER
                )
            ) {
                val newTreeWood3 = tree
                newTreeWood3[emptyPosition] = Tile(emptyPosition.first, emptyPosition.second, TileType.FLOWER)
                val newPlayedTiles3 = playedTiles
                newPlayedTiles3.add(Pair(TileType.FLOWER, emptyPosition))
                val newPlayableTiles3 = playableTiles
                if (!newPlayableTiles3.remove(TileType.FLOWER)) {
                    newPlayableTiles3.remove(TileType.ANY)
                }
                val newHasTile3 = hasTiles
                newHasTile3.remove(TileType.FLOWER)

                returnList += allPossiblePlayedTiles(
                    newTreeWood3, newPlayedTiles3, newPlayableTiles3, newHasTile3, newReturnList
                )
            }

            isRelevantType = (playableTiles.contains(TileType.FRUIT) || playableTiles.contains(TileType.ANY))

            if (neighbors[1] >= 2 && neighbors[4] == 0 && isRelevantType && hasTiles.contains(TileType.FRUIT)
            ) {
                val newTreeWood4 = tree
                newTreeWood4[emptyPosition] = Tile(emptyPosition.first, emptyPosition.second, TileType.FRUIT)
                val newPlayedTiles4 = playedTiles
                newPlayedTiles4.add(Pair(TileType.FRUIT, emptyPosition))
                val newPlayableTiles4 = playableTiles
                newPlayableTiles4.remove(TileType.FRUIT)
                val newHasTile4 = hasTiles
                newHasTile4.remove(TileType.FRUIT)

                returnList += allPossiblePlayedTiles(
                    newTreeWood4, newPlayedTiles4, newPlayableTiles4, newHasTile4, newReturnList
                )
            }
        }

        return returnList

    }

 */


//    /**
//     * function that generates a list of all possible Moves
//     */
//    private fun getAllPossibleMoves(state: BonsaiGameState): MutableList<Move> {
//        val returnList = mutableListOf<Move>()
//        val hasTiles = mutableListOf<TileType>()
//
//        // gets the tiles that a player has
//        for (tiles in state.currentPlayer.personalSupply) {
//            hasTiles.add(tiles.tileType)
//        }
//
//        // generates all cultivate moves
//        for (listOfPlacedTiles in allPossiblePlayedTiles(
//            tree = state.currentPlayer.bonsaiTree,
//            alreadyPlayedTiles = mutableListOf(),
//            playableTiles = state.currentPlayer.playableTiles,
//            hasTiles = hasTiles,
//            returnList = mutableListOf()
//        )) {
//            returnList += Move(
//                currentState = state,
//                removedTiles = mutableListOf(),
//                actionType = CULTIVATE,
//                takenCard = null,
//                chosenWoodOrLeafTile = null,
//                allTilesReceived = mutableListOf(),
//                playedTiles = listOfPlacedTiles,
//                chosenRemoveTiles = mutableListOf(),
//            )
//        }
//
//        // generates all meditate moves
//        // generates the
//
//
//        for (position in 0 until state.faceUpCards.size) {
//            val newMove = Move(
//                currentState = state,
//                removedTiles = mutableListOf(),
//                actionType = MEDITATE,
//                takenCard = position,
//                chosenWoodOrLeafTile = null,
//                allTilesReceived = mutableListOf(),
//                playedTiles = mutableListOf(),
//                chosenRemoveTiles = mutableListOf()
//            )
//
//
//
//            when (state.faceUpCards[position].cardType) {
//                CardType.HELPERCARD -> TODO()
//                CardType.MASTERCARD -> {
//                    val card = state.faceUpCards[position] as MasterCard
//                    if (card.tileTypes.contains(TileType.ANY)) {
//                        val newMove3 = newMove
//                        val newMove4 = newMove
//                        val newMove5 = newMove
//                        val newMove6 = newMove
//
//                        newMove3.allTilesReceived.add(TileType.WOOD)
//                        newMove4.allTilesReceived.add(TileType.LEAF)
//                        newMove5.allTilesReceived.add(TileType.FLOWER)
//                        newMove6.allTilesReceived.add(TileType.FRUIT)
//
//                    } else {
//                        for (tile in card.tileTypes) {
//                            newMove.allTilesReceived.add(tile)
//                        }
//                    }
//                }
//
//                else -> TODO()
//            }
//
//
//
//            return returnList
//        }
//
//        return returnList
//    }
}

