package gui

import entity.GoalTileType
import entity.Player
import entity.Tile
import entity.TileType
import service.AbstractRefreshingService
import service.ConnectionState

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * that certain changes have been made to the entity layer, so that the user
 * can be updated accordingly
 *
 * @see AbstractRefreshingService
 */
interface Refreshable {

    /**
     * perform refreshes that are necessary after the game has started
     */
    fun refreshAfterGameStart(){}

    /**
     * perform refreshes that are necessary after the game has ended
     */
    fun refreshAfterGameEnd(){}

    /**
     * perform refreshes that are necessary after a player has played a tile
     */
    fun refreshAfterPlayTile(goalTileType: GoalTileType?, tier: Int, tilePosition: Pair<Int, Int>) {}

    /**
     * perform refreshes that are necessary after a player has meditated
     */
    //fun refreshAfterMeditate(position: Int?){}

    /**
     * perform refreshes that are necessary after a player has started the cultivate action
     */
    fun refreshAfterCultivateStart(){}

    /**
     * perform refreshes that are necessary after a player has chosen a card
     */
    fun refreshAfterChooseCard(){}

    /**
     * perform refreshes that are necessary after a player has chosen a MasterCard
     */
    fun refreshAfterDrawingMasterCardAny(){}

    /**
     * perform refreshes that are necessary after a player has chosen a HelperCard
     */
    fun refreshAfterDrawingHelperCard(playableTilesCopy: MutableList<TileType>){}
    /**
     * perform refreshes that are necessary after a player has received tiles
     */
    fun refreshAfterReceivedTile(discard : Boolean){}

    /**
     *  perform refreshes that are necessary after apply card effects
     */
    fun refreshAfterApplyCardEffects(position: Int?){}
    /**
     * perform refreshes that are necessary after a player has received tiles
     */
    fun refreshAfterChooseTileToPlay(tile: Tile) {}

    /**
     * perform refreshes that are necessary after a player has ended his turn
     */
    fun refreshAfterEndTurn(){}

    /**
     * perform refreshes that are necessary after a player has discarded tile
     */
    fun refreshAfterDiscardTile(){}

    /**
     * perform refreshes that are necessary after a player has removed tiles from his tree
     */
    fun refreshAfterRemoveFromTree(tilePosition: Pair<Int, Int>){}

    /**
     * perform refreshes that are necessary a player has claimed a goal tile
     */
    fun refreshAfterClaimGoal(goalTileType: GoalTileType, tier: Int){}

    /**
     * perform refreshes that are necessary after a player has redone or undone his turn
     */
    fun refreshAfterRedoOrUndo(){}

    /**
     * perform refreshes that are necessary after the [ConnectionState] was changed
     */
    fun refreshConnectionState(newState: ConnectionState) {}

    /**
     * perform refreshes that are necessary to show the result scene
     */
    fun refreshAfterShowWinner(players: List<Player>){}

    /**
     * perform refreshes that are necessary after a player joins
     */
    fun refreshAfterPlayerJoined(playerName: String) {}
}
