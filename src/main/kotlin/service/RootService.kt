package service

import entity.*
import gui.Refreshable
import service.bot.BotService

/**
 * Main class of the service layer for the Bonsai game. Provides access
 * to all other service classes and holds the [currentGame] state for these
 * services to access.
 */
class RootService {

    val playerActionService = PlayerActionService(this)
    var gameService = GameService(this)
    val networkService = NetworkService(this)
    val historyService = HistoryService(this)
    val treeService = TreeService(this)
    val botService = BotService(this)

    /** current active Game, that could be Null if the game hasn't started yet*/
    var currentGame: BonsaiGame? = null


    /**
     * Adds the provided [newRefreshable] to all services connected
     * to this root service
     */
    fun addRefreshable(newRefreshable: Refreshable) {
        gameService.addRefreshable(newRefreshable)
        playerActionService.addRefreshable(newRefreshable)
        historyService.addRefreshable(newRefreshable)
        treeService.addRefreshable(newRefreshable)
        networkService.addRefreshable(newRefreshable)
    }

    /**
     * Adds each of the provided [newRefreshables] to all services
     * connected to this root service
     */
    fun addRefreshables(vararg newRefreshables: Refreshable) {
        newRefreshables.forEach { addRefreshable(it) }
    }
}
