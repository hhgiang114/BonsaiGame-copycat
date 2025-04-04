package entity

import kotlinx.serialization.Serializable

/**
 * A goal tile in the game, representing fought over objectives for [Player]s
 * No [Player] can have the same [GoalTile] as another [Player]
 *
 * @property goalTileType [GoalTileType], one of five options
 * @property tier Tier of the [GoalTile] can be 1,2 or 3
 * @property score The number of points awarded at the end of the game,
 * when owning this [GoalTile]. Must be a positive int
 *
 * @throws IllegalArgumentException If [tier] is not 1,2 or 3
 * @throws IllegalArgumentException If [score] is not a non-negative int
 */
@Serializable
data class GoalTile(
    val goalTileType: GoalTileType,
    val tier: Int,
    val score: Int
)
