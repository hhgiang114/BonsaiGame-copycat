package entity

import kotlinx.serialization.Serializable

/**
 * Represents a bonsai [Tile], which has shape of a hexagon
 *
 * @param q hexagonal coordinate
 * @param r hexagonal coordinate
 * @param tileType is bonsai [TileType]
 */
@Serializable
data class Tile(var q: Int? = null, var r: Int? = null, val tileType: TileType) {

    override fun toString(): String {
        return "Tile Type: ${this.tileType}"
    }

    /**
     * returns true checked tiles have the same [tileType]
     */
    fun typeEqual(other: Tile): Boolean {
        return this.tileType == other.tileType
    }
}
