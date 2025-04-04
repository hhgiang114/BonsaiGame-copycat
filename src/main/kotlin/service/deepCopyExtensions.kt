package service

import entity.*

/**
 * create recursive deep copies of the game state
 */


/**
 * create deepcopy of BonsaiGameState
 */
fun BonsaiGameState.deepCopy(): BonsaiGameState {
    return BonsaiGameState(
        currentPlayer = currentPlayer.deepCopy(),
        players = players.map { it.deepCopy() }.toMutableList(),
        botSpeed = botSpeed,
        currentState = currentState
    ).also { copy ->
        copy.endGameCounter = this.endGameCounter
        copy.zenDeck = this.zenDeck.map { it.deepCopy() }.toMutableList()
        copy.faceUpCards = this.faceUpCards.map { it.deepCopy() }.toMutableList()
        copy.goalTiles = this.goalTiles.map { it.deepCopy() }.toMutableList()
    }

}

/**
 * create deepcopy of Player
 */
fun Player.deepCopy(): Player {

    val copiedPersonalSupply = this.personalSupply.map {
        it.deepCopy()
    }.toMutableList()


    val copiedPlayer = Player(
        name = this.name,
        playerType = this.playerType,
        isLocal = this.isLocal,
        color = this.color
    ).also { copy ->
        copy.bonsaiTree = this.bonsaiTree.mapValues { it.value.deepCopy() }.toMutableMap()
        copy.personalSupply = copiedPersonalSupply
        copy.collectedCards = this.collectedCards.map { it.deepCopy() }.toMutableList()
        copy.claimedGoals = this.claimedGoals.map { it.deepCopy() }.toMutableList()
        copy.renouncedGoals = this.renouncedGoals.map { it.deepCopy() }.toMutableList()

        copy.playableTiles = this.playableTiles.toMutableList()
        copy.playableTilesCopy = this.playableTilesCopy.toMutableList()

        copy.tileCapacity = this.tileCapacity
        copy.score = this.score
        copy.hasPlayed = this.hasPlayed
    }

    return copiedPlayer
}

/**
 * create deepcopy of Tile
 */

fun Tile.deepCopy(): Tile {
    return Tile(this.q, this.r, this.tileType)
}

/**
 * create deepcopy of GoalTile
 */
fun GoalTile.deepCopy(): GoalTile {
    return GoalTile(this.goalTileType, this.tier, this.score)
}
/**
 * create deepcopy of Card
 */
fun Card.deepCopy(): Card {
    return when (this) {
        is GrowthCard -> GrowthCard(this.tileType, this.id)
        is HelperCard -> this.deepCopy()
        is MasterCard -> this.deepCopy()
        is ParchmentCard -> ParchmentCard(this.parchmentTileType, this.parchmentCardType, this.basePoints, this.id)
        is ToolCard -> ToolCard(this.id)
        else -> throw IllegalArgumentException("cardtype does not exist: ${this::class.simpleName}")
    }
}

/**
 * create deepcopy of HelperCard
 */
fun HelperCard.deepCopy(): HelperCard {
    return HelperCard(this.tileTypes[1], this.id).also { copy ->
        copy.tileTypes.clear()
        copy.tileTypes.addAll(this.tileTypes)
    }
}

/**
 * create deepcopy of MasterCard
 */
fun MasterCard.deepCopy(): MasterCard {
    return MasterCard(this.tileTypes.toMutableList(), this.id)
}


