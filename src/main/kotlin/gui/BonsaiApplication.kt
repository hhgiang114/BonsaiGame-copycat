package gui

import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.util.Font


/**
 * The [BonsaiApplication] is a [BoardGameApplication] that is the main class of the application
 */
class BonsaiApplication : BoardGameApplication("Bonsai", 1920, 1080), Refreshable {

    /**
     * Central services for all scenes and holds current game as well
     */
    private val rootService = RootService()

    /**
     * Initialization of all scene
     */
    private val mainMenuScene = MainMenuScene(rootService, this)
    private val gameScene = BonsaiGameScene(rootService, this)
    private val configureGameMenuScene = ConfigureGameMenuScene(this, rootService)
    private val joinScene = JoinScene(this, rootService)
    private val hostScene = HostScene(this, rootService)
    private val waitingScene = WaitingScene(this)
    private val startSessionScene = StartSessionScene(this, rootService)
    private val showResultScene = ResultScene(this, rootService)

    // loads to begin the used Font
    // shows gameScene and shows the mainMenuScene
    init {
        rootService.addRefreshables(
            this,
            gameScene,
            mainMenuScene,
            waitingScene,
            configureGameMenuScene,
            joinScene,
            hostScene,
            startSessionScene,
            showResultScene
        )

        loadFont("Font/Osake-x3oqj.otf", "Osake Regular", Font.FontWeight.NORMAL)
        loadFont("Font/Okashi-Regular.otf", "Okashi Regular", Font.FontWeight.NORMAL)
        loadFont("Font/Okashi-italic.otf", "Okashi italic", Font.FontWeight.NORMAL)

        showMainMenuScene()
    }

    /**
     * Function [showMainMenuScene] shows the mainMenuScene
     */
    fun showMainMenuScene() = this.showMenuScene(mainMenuScene)

    /**
     * Funktion [showConfigScene] shows the configScene
     */
    fun showConfigScene() = this.showMenuScene(configureGameMenuScene)

    /**
     * Funktion [showJoinScene] shows the joinScene
     */
    fun showJoinScene() = this.showMenuScene(joinScene)

    /**
     * Funktion [showHostScene] shows the hostScene
     */
    fun showHostScene() {
        //hostScene.playerInput.prompt = rootService.networkService.myName
        this.showMenuScene(hostScene)
    }

    /**
     * Funktion [showStartSessionScene] shows the startSessionScene
     */
    fun showStartSessionScene() = this.showMenuScene(startSessionScene)

    /**
     * Funktion [showWaitingScene] shows the waitingScene
     */
    fun showWaitingScene() = this.showMenuScene(waitingScene)

    /**
     * Function [showGameScene] shows the GameScene
     */
    fun showGameScene() = this.showGameScene(gameScene)

    override fun refreshAfterShowWinner(players: List<Player>) {
        this.showMenuScene(showResultScene)
    }
}
