package service

import entity.*
import gui.Refreshable

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 *
 * @param rootService The root service to which this service belongs
 */

class RefreshableTest(val rootService: RootService) : Refreshable {

    var refreshAfterGameStartCalled: Boolean = false
        private set

    var refreshAfterGameEndCalled: Boolean = false
        private set

    var refreshAfterPlayTileCalled: Boolean = false
        private set

    var refreshAfterMeditateCalled: Boolean = false
        private set

    var refreshAfterCultivateStartCalled: Boolean = false
        private set

    var refreshAfterChooseCardCalled: Boolean = false
        private set

    var refreshAfterDrawingMasterCardAnyCalled: Boolean = false
        private set

    var refreshAfterDrawingHelperCardCalled: Boolean = false
        private set

    var refreshAfterReceivedTileCalled: Boolean = false
        private set

    var refreshAfterApplyCardEffectsCalled: Boolean = false
        private set

    var refreshAfterChooseTileToPlayCalled: Boolean = false
        private set

    var refreshAfterEndTurnCalled: Boolean = false
        private set

    var refreshAfterDiscardTileCalled: Boolean = false
        private set

    var refreshAfterRemoveFromTreeCalled: Boolean = false
        private set

    var refreshAfterClaimGoalCalled: Boolean = false
        private set

    var refreshAfterRedoOrUndoCalled: Boolean = false
        private set

    var refreshConnectionStateCalled: Boolean = false
        private set

    var refreshAfterShowWinnerCalled: Boolean = false
        private set

    /**
     * Reset all *Called properties
     */
    fun reset() {
        refreshAfterGameStartCalled = true
        refreshAfterGameEndCalled = true
        refreshAfterPlayTileCalled = true
        refreshAfterMeditateCalled = true
        refreshAfterCultivateStartCalled = true
        refreshAfterChooseCardCalled = true
        refreshAfterDrawingMasterCardAnyCalled = true
        refreshAfterDrawingHelperCardCalled = true
        refreshAfterReceivedTileCalled = true
        refreshAfterApplyCardEffectsCalled = true
        refreshAfterChooseTileToPlayCalled = true
        refreshAfterEndTurnCalled = true
        refreshAfterDiscardTileCalled = true
        refreshAfterRemoveFromTreeCalled = true
        refreshAfterClaimGoalCalled = true
        refreshAfterRedoOrUndoCalled = true
        refreshConnectionStateCalled = true
        refreshAfterShowWinnerCalled = true
    }

    override fun refreshAfterGameStart() {
        refreshAfterGameStartCalled = true
    }

    override fun refreshAfterGameEnd() {
        refreshAfterGameEndCalled = true
    }

    override fun refreshAfterPlayTile(goalTileType: GoalTileType?, tier: Int, tilePosition: Pair<Int, Int>) {
        refreshAfterPlayTileCalled = false
    }

//    override fun refreshAfterMeditate() {
//        refreshAfterMeditateCalled = false
//
//    }

    override fun refreshAfterCultivateStart() {
        refreshAfterCultivateStartCalled = true
    }

    override fun refreshAfterChooseCard() {
        refreshAfterChooseCardCalled = true
    }

    override fun refreshAfterDrawingMasterCardAny() {
        refreshAfterDrawingMasterCardAnyCalled = true
    }

//    override fun refreshAfterDrawingHelperCard(secondTileTypeToPlace: TileType) {
//        refreshAfterDrawingHelperCardCalled = false
//    }

    override fun refreshAfterReceivedTile(discard: Boolean) {
        refreshAfterReceivedTileCalled = false
    }


    override fun refreshAfterApplyCardEffects(position: Int?) {
        refreshAfterApplyCardEffectsCalled = false
    }

    override fun refreshAfterChooseTileToPlay(tile: Tile) {
        refreshAfterChooseTileToPlayCalled = false
    }

    override fun refreshAfterEndTurn() {
        refreshAfterEndTurnCalled = false
    }

    override fun refreshAfterDiscardTile() {
        refreshAfterDiscardTileCalled = false
    }

    override fun refreshAfterRemoveFromTree(tilePosition: Pair<Int, Int>) {
        refreshAfterRemoveFromTreeCalled = false
    }

    override fun refreshAfterClaimGoal(goalTileType: GoalTileType, tier: Int) {
        refreshAfterClaimGoalCalled = false
    }

    override fun refreshAfterRedoOrUndo() {
        refreshAfterRedoOrUndoCalled = false
    }

    override fun refreshConnectionState(newState: ConnectionState) {
        refreshConnectionStateCalled = false
    }

    override fun refreshAfterShowWinner(players: List<Player>) {
        refreshAfterShowWinnerCalled = false
    }
}
