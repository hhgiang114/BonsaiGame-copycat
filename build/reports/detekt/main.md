# detekt

## Metrics

* 608 number of properties

* 202 number of functions

* 100 number of classes

* 6 number of packages

* 46 number of kt files

## Complexity Report

* 7,843 lines of code (loc)

* 5,281 source lines of code (sloc)

* 4,152 logical lines of code (lloc)

* 1,506 comment lines of code (cloc)

* 875 cyclomatic complexity (mcc)

* 515 cognitive complexity

* 38 number of total code smells

* 28% comment source ratio

* 210 mcc per 1,000 lloc

* 9 code smells per 1,000 lloc

## Findings (38)

### comments, UndocumentedPublicFunction (2)

Public functions require documentation.

[Documentation](https://detekt.dev/docs/rules/comments#undocumentedpublicfunction)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/service/NetworkService.kt:365:9
```
The function receivePlayerJoinedMessage is missing documentation.
```
```kotlin
362         }
363     }
364 
365     fun receivePlayerJoinedMessage(playerName: String) {
!!!         ^ error
366         onAllRefreshables { refreshAfterPlayerJoined(playerName) }
367     }
368 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/util/CSVLoader.kt:15:28
```
The function readCsvFile is missing documentation.
```
```kotlin
12         registerModule(kotlinModule())
13     }
14 
15     inline fun <reified T> readCsvFile(fileName: String): List<T> {
!!                            ^ error
16         val lines = object {}.javaClass.getResourceAsStream(fileName)?.bufferedReader()
17 
18         lines.use { reader ->

```

### complexity, ComplexCondition (1)

Complex conditions should be simplified and extracted into well-named methods if necessary.

[Documentation](https://detekt.dev/docs/rules/complexity#complexcondition)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/service/TreeService.kt:169:21
```
This condition is too complex (5). Defined complexity threshold for conditions is set to '4'
```
```kotlin
166         leafTilesPositions.forEach { position ->
167             val neighbourTiles = getNeighbourTiles(position)
168             if (neighbourTiles.any { it.second == null }) {
169                 if (neighbourTiles.all {
!!!                     ^ error
170                         it.second == null ||
171                                 it.second?.tileType == TileType.WOOD ||
172                                 it.second?.tileType == TileType.LEAF ||

```

### complexity, CyclomaticComplexMethod (5)

Prefer splitting up complex methods into smaller, easier to test methods.

[Documentation](https://detekt.dev/docs/rules/complexity#cyclomaticcomplexmethod)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1455:17
```
The function applyCardPosition appears to be too complex based on Cyclomatic Complexity (complexity: 28). Defined complexity threshold for methods is set to '15'
```
```kotlin
1452         }
1453     }
1454 
1455     private fun applyCardPosition() {
!!!!                 ^ error
1456         val game = rootService.currentGame?.currentBonsaiGameState
1457         checkNotNull(game)
1458         faceUpCards.forEachIndexed { index, card ->

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/ConfigureGameMenuScene.kt:444:17
```
The function addPlayer appears to be too complex based on Cyclomatic Complexity (complexity: 16). Defined complexity threshold for methods is set to '15'
```
```kotlin
441         assignColorButtonFunctionality()
442     }
443 
444     private fun addPlayer() {
!!!                 ^ error
445         val currentIndex = playerInputs.size
446         if (currentIndex >= 4) return
447 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/HostScene.kt:385:17
```
The function addPlayer appears to be too complex based on Cyclomatic Complexity (complexity: 15). Defined complexity threshold for methods is set to '15'
```
```kotlin
382 
383     }
384 
385     private fun addPlayer(playerName: String) {
!!!                 ^ error
386         val currentIndex = playerInputs.size
387         if (currentIndex >= 4) return
388 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/service/PlayerActionService.kt:493:17
```
The function hasReachedGreenGoal appears to be too complex based on Cyclomatic Complexity (complexity: 16). Defined complexity threshold for methods is set to '15'
```
```kotlin
490      *
491      * @return whether the goal tile can be claimed or not
492      */
493     private fun hasReachedGreenGoal(bonsaiTree: MutableMap<Pair<Int, Int>, Tile>, tier: Int): Boolean {
!!!                 ^ error
494         // Directions for hexagonal grid adjacency
495         val directions = listOf(
496             Pair(1, 0), Pair(0, 1), Pair(-1, 1),

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/util/ZenCardLoader.kt:45:17
```
The function readAllMasterCards appears to be too complex based on Cyclomatic Complexity (complexity: 20). Defined complexity threshold for methods is set to '15'
```
```kotlin
42         }
43     }
44 
45     private fun readAllMasterCards(playerAmount: Int): List<MasterCard> {
!!                 ^ error
46         return csvLoader.readCsvFile<CSVMasterCardEntry>("/zenmaster.csv").mapNotNull { cardEntry ->
47 
48             val type1 = when (cardEntry.type1) {

```

### complexity, LongMethod (8)

One method should have one responsibility. Long methods tend to handle many things at once. Prefer smaller methods to make them easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#longmethod)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:422:17
```
The function createRightSidePane is too long (75). The maximum length is 60.
```
```kotlin
419         this.add(saveButton)
420     }
421 
422     private fun createRightSidePane() {
!!!                 ^ error
423         val gameState = rootService.currentGame?.currentBonsaiGameState
424         checkNotNull(gameState) { "Game state is not initialized." }
425 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:639:18
```
The function refreshAfterRedoOrUndo is too long (69). The maximum length is 60.
```
```kotlin
636         )
637     }
638 
639     override fun refreshAfterRedoOrUndo() {
!!!                  ^ error
640         val gameState = rootService.currentGame?.currentBonsaiGameState
641         checkNotNull(gameState) { "Game state is not initialized." }
642 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:747:17
```
The function initPot is too long (60). The maximum length is 60.
```
```kotlin
744 
745     //components for the tree boards
746 
747     private fun initPot(player: Player) {
!!!                 ^ error
748         val playerPane = playerPanes[getOrder(player)]
749         val treeHexagonGrid = HexagonGrid<HexagonView>(
750             posX = 1000,

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:812:17
```
The function initSupply is too long (64). The maximum length is 60.
```
```kotlin
809         createEmptyHex(player)
810     }
811 
812     private fun initSupply(player: Player) {
!!!                 ^ error
813         val game = rootService.currentGame?.currentBonsaiGameState
814         checkNotNull(game)
815         val playerPane = playerPanes[getOrder(player)]

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1455:17
```
The function applyCardPosition is too long (118). The maximum length is 60.
```
```kotlin
1452         }
1453     }
1454 
1455     private fun applyCardPosition() {
!!!!                 ^ error
1456         val game = rootService.currentGame?.currentBonsaiGameState
1457         checkNotNull(game)
1458         faceUpCards.forEachIndexed { index, card ->

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1594:17
```
The function createEmptyHex is too long (70). The maximum length is 60.
```
```kotlin
1591     /**
1592      * really important function for play tile
1593      */
1594     private fun createEmptyHex(player: Player) {
!!!!                 ^ error
1595         val game = rootService.currentGame?.currentBonsaiGameState
1596         checkNotNull(game)
1597         val treeTileMap = treeTileMaps[getOrder(player)]

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/ConfigureGameMenuScene.kt:444:17
```
The function addPlayer is too long (93). The maximum length is 60.
```
```kotlin
441         assignColorButtonFunctionality()
442     }
443 
444     private fun addPlayer() {
!!!                 ^ error
445         val currentIndex = playerInputs.size
446         if (currentIndex >= 4) return
447 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/HostScene.kt:385:17
```
The function addPlayer is too long (88). The maximum length is 60.
```
```kotlin
382 
383     }
384 
385     private fun addPlayer(playerName: String) {
!!!                 ^ error
386         val currentIndex = playerInputs.size
387         if (currentIndex >= 4) return
388 

```

### complexity, NestedBlockDepth (1)

Excessive nesting leads to hidden complexity. Prefer extracting code to make it easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#nestedblockdepth)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1254:18
```
Function refreshAfterPlayTile is nested too deeply.
```
```kotlin
1251         updatePlayableTiles(game.currentPlayer)
1252     }
1253 
1254     override fun refreshAfterPlayTile(goalTileType: GoalTileType?, tier: Int) {
!!!!                  ^ error
1255         val game = rootService.currentGame?.currentBonsaiGameState
1256         checkNotNull(game)
1257         if (goalTileType == null) {

```

### complexity, TooManyFunctions (2)

Too many functions inside a/an file/class/object/interface always indicate a violation of the single responsibility principle. Maybe the file/class/object/interface wants to manage too many things at once. Extract functionality which clearly belongs together.

[Documentation](https://detekt.dev/docs/rules/complexity#toomanyfunctions)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:31:7
```
Class 'BonsaiGameScene' with '11' functions detected. Defined threshold inside classes is set to '11'
```
```kotlin
28  * The [BonsaiGameScene] is a [BoardGameScene] that displays the whole game and
29  * lets the user play the bonsai game
30  */
31 class BonsaiGameScene(private val rootService: RootService) :
!!       ^ error
32     BoardGameScene(1920, 1080, ColorVisual(Color(PRIMARY_COLOUR))), Refreshable {
33 
34     private val treeTileMaps: MutableList<BidirectionalMap<Tile, HexagonView>> = mutableListOf()

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/service/NetworkService.kt:13:7
```
Class 'NetworkService' with '13' functions detected. Defined threshold inside classes is set to '11'
```
```kotlin
10  * Service layer class that realizes the necessary logic for sending and receiving messages
11  * in multiplayer network games. Bridges between the [BonsaiNetworkClient] and the other services.
12  */
13 class NetworkService(private val rootService: RootService) : AbstractRefreshingService() {
!!       ^ error
14 
15     /** Network client. Nullable for offline games. */
16     var client: BonsaiNetworkClient? = null

```

### empty-blocks, EmptyFunctionBlock (3)

Empty block of code detected. As they serve no purpose they should be removed.

[Documentation](https://detekt.dev/docs/rules/empty-blocks#emptyfunctionblock)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1587:73
```
This empty block of code can be removed.
```
```kotlin
1584         }
1585     }
1586 
1587     private fun switchPlayerPane(playerIndex: Int, hasEndTurn: Boolean) {
!!!!                                                                         ^ error
1588 
1589     }
1590 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/service/NetworkService.kt:417:32
```
This empty block of code can be removed.
```
```kotlin
414     /**
415      * Redo the goal tile handling after a goal tile action.
416      */
417     fun redoReceivedGoalTile() {
!!!                                ^ error
418 
419     }
420 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/service/bot/EasyBotService.kt:63:33
```
This empty block of code can be removed.
```
```kotlin
60         }
61     }
62 
63     private fun getTilePlacing(){}
!!                                 ^ error
64 }
65 

```

### style, ClassOrdering (1)

Class contents should be in this order: Property declarations/initializer blocks; secondary constructors; method declarations then companion objects.

[Documentation](https://detekt.dev/docs/rules/style#classordering)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:422:5
```
method `createRightSidePane()` should be declared after property declarations and initializer blocks.
```
```kotlin
419         this.add(saveButton)
420     }
421 
422     private fun createRightSidePane() {
!!!     ^ error
423         val gameState = rootService.currentGame?.currentBonsaiGameState
424         checkNotNull(gameState) { "Game state is not initialized." }
425 

```

### style, UnnecessaryApply (6)

The `apply` usage is unnecessary and can be removed.

[Documentation](https://detekt.dev/docs/rules/style#unnecessaryapply)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1173:47
```
apply expression can be omitted
```
```kotlin
1170                 this.isVisible = false
1171                 rootService.playerActionService.checkSupply()
1172                 game.players[playerIndex].personalSupply.forEach { supplyTile ->
1173                     supplyTileMap[supplyTile].apply {
!!!!                                               ^ error
1174                         isDraggable = false
1175                     }
1176                 }

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1292:25
```
apply expression can be omitted
```
```kotlin
1289             )
1290             goalTilePane.isVisible = true
1291             goalTilePane.isDisabled = false
1292             claimButton.apply {
!!!!                         ^ error
1293                 onMouseClicked = {
1294                     rootService.playerActionService.claimOrRenounceGoal(true, goalTileType, tier)
1295                     goalTilePane.isDisabled = true

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1462:26
```
apply expression can be omitted
```
```kotlin
1459 
1460             when (index) {
1461                 0 -> {
1462                     card.apply {
!!!!                          ^ error
1463 
1464                         onMouseClicked = {
1465                             if (game.currentState == States.START_TURN ||

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1486:26
```
apply expression can be omitted
```
```kotlin
1483                 }
1484 
1485                 1 -> {
1486                     card.apply {
!!!!                          ^ error
1487                         onMouseClicked = {
1488                             if (game.currentState == States.START_TURN ||
1489                                 game.currentState == States.CHOOSE_ACTION ||

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1542:26
```
apply expression can be omitted
```
```kotlin
1539                 }
1540 
1541                 2 -> {
1542                     card.apply {
!!!!                          ^ error
1543                         onMouseClicked = {
1544                             if (game.currentState == States.START_TURN ||
1545                                 game.currentState == States.CHOOSE_ACTION ||

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1564:26
```
apply expression can be omitted
```
```kotlin
1561 
1562 
1563                 else -> {
1564                     card.apply {
!!!!                          ^ error
1565                         onMouseClicked = {
1566                             if (game.currentState == States.START_TURN ||
1567                                 game.currentState == States.CHOOSE_ACTION ||

```

### style, UnusedParameter (4)

Function parameter is unused and should be removed.

[Documentation](https://detekt.dev/docs/rules/style#unusedparameter)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1587:34
```
Function parameter `playerIndex` is unused.
```
```kotlin
1584         }
1585     }
1586 
1587     private fun switchPlayerPane(playerIndex: Int, hasEndTurn: Boolean) {
!!!!                                  ^ error
1588 
1589     }
1590 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1587:52
```
Function parameter `hasEndTurn` is unused.
```
```kotlin
1584         }
1585     }
1586 
1587     private fun switchPlayerPane(playerIndex: Int, hasEndTurn: Boolean) {
!!!!                                                    ^ error
1588 
1589     }
1590 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/service/NetworkService.kt:263:58
```
Function parameter `sender` is unused.
```
```kotlin
260      * @param message The Meditate message received from the opponent.
261      * @param sender The name of the sender (opponent).
262      */
263     fun receiveMeditateMessage(message: MeditateMessage, sender: String) {
!!!                                                          ^ error
264         // --------------- prologue: state check ---------------
265         check(connectionState == ConnectionState.WAITING_FOR_OPPONENT)
266             { "currently not expecting an opponent's turn." }

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/service/NetworkService.kt:314:60
```
Function parameter `sender` is unused.
```
```kotlin
311      * @param message The Cultivate message received from the opponent.
312      * @param sender The name of the sender (opponent).
313      */
314     fun receiveCultivateMessage(message: CultivateMessage, sender: String) {
!!!                                                            ^ error
315         check(connectionState == ConnectionState.WAITING_FOR_OPPONENT)
316         { "currently not expecting an opponent's turn." }
317 

```

### style, UnusedPrivateMember (5)

Private function is unused and should be removed.

[Documentation](https://detekt.dev/docs/rules/style#unusedprivatemember)

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/gui/BonsaiGameScene.kt:1587:17
```
Private function `switchPlayerPane` is unused.
```
```kotlin
1584         }
1585     }
1586 
1587     private fun switchPlayerPane(playerIndex: Int, hasEndTurn: Boolean) {
!!!!                 ^ error
1588 
1589     }
1590 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/service/bot/EasyBotService.kt:63:17
```
Private function `getTilePlacing` is unused.
```
```kotlin
60         }
61     }
62 
63     private fun getTilePlacing(){}
!!                 ^ error
64 }
65 

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/util/AxialArithmetic.kt:130:13
```
Private function `foreverCircleAround` is unused.
```
```kotlin
127 /**
128  * Wrapper for the [circleAround] function to iterate infinitely around a coordinate
129  */
130 private fun foreverCircleAround(center: Pair<Int, Int>): Sequence<Pair<Int, Int>> = sequence {
!!!             ^ error
131     while (true)
132         for (tile in circleAround(center))
133             yield(tile)

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/util/AxialArithmetic.kt:139:34
```
Private function `rotateClockwiseAround` is unused.
```
```kotlin
136 /**
137  * Single-step clockwise rotation around an adjacent center point
138  */
139 private infix fun Pair<Int, Int>.rotateClockwiseAround(center: Pair<Int, Int>): Pair<Int, Int> =
!!!                                  ^ error
140     when (this - center) {
141         VECTOR_LEFT -> this + VECTOR_TOP_RIGHT
142         VECTOR_TOP_LEFT -> this + VECTOR_RIGHT

```

* /Users/gianghoang/Desktop/Projekt2/src/main/kotlin/util/AxialArithmetic.kt:153:34
```
Private function `rotateCounterClockwiseAround` is unused.
```
```kotlin
150 /**
151  * Single-step counter-clockwise rotation around an adjacent center point
152  */
153 private infix fun Pair<Int, Int>.rotateCounterClockwiseAround(center: Pair<Int, Int>): Pair<Int, Int> =
!!!                                  ^ error
154     when (this - center) {
155         VECTOR_LEFT -> this + VECTOR_BOTTOM_RIGHT
156         VECTOR_TOP_LEFT -> this + VECTOR_BOTTOM_LEFT

```

generated with [detekt version 1.23.7](https://detekt.dev/) on 2025-03-19 16:42:18 UTC
