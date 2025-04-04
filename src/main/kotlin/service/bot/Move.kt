package service.bot

import entity.BonsaiGameState
import entity.GoalTile
import entity.TileType

/**
 * describes the different actions a bot can do
 */

class Move(
    // current state of the game at the start of the move
    val currentState : BonsaiGameState,
    // the tiles and their position that are removed from the tree at the start of the turn
//    val removedTiles : MutableList<Pair<Int,Int>> = mutableListOf(),
    // the action type is cultivate = 0 and meditate = 1
    val actionType : Int,
    // gives the position of the chosen card in meditate
    // if the action is cultivate is null
    val takenCard : Int? = null,
    // the chosen TileType in meditate at card position 2
    val chosenWoodOrLeafTile : TileType? = null,
    // the chosen tile in a mastercard any
    val chosenAnyTile : TileType? = null,
    // list of all received Tiles in a move
    val allTilesReceived : MutableList<TileType>,
    // list of all taken goal tiles in a move
//    val takenGoalTile: MutableList<GoalTile> = mutableListOf(),
    // list of all renounced goal tiles in a move
//    val renouncedGoalTile: MutableList<GoalTile> = mutableListOf(),
    // list of all tile types and their position where they have been placed
//    val playedTiles : MutableList<Pair<TileType,Pair<Int,Int>>>,
    // list of all tiles that are removed of the personal supply of a player at the end of a move
//    val chosenRemoveTiles : MutableList<Pair<TileType,Pair<Int,Int>>>,
) {

    val currentPlayer
        get() = currentState.currentPlayer

}
