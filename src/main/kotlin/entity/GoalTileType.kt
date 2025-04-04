package entity

/**
 * The different types of [GoalTile]s in the game.
 *
 * Each [GoalTile] type represents an objective that players can achieve
 * one player can only achieve one [GoalTile] per type
 */

enum class GoalTileType {
    // The brown goal tile, associated with having 8/10/12 wood [Tile]s in the players bonsaiTree
    BROWN,

    // The green goal tile, associated with having 5/7/9 leaf [Tile]s adjacent to one another
    GREEN,

    // The pink goal tile, associated with having 3/4/5 flower [Tile]s protruding from the same
    // side of the Pot
    PINK,

    // The orange goal tile, associated with having 3/4/5 fruit [Tile]s in the players bonsaiTree
    ORANGE,

    // The blue goal tile, associated with having tiles protruding from sides of the players pot
    BLUE
}
