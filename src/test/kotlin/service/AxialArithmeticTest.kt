package service

import entity.Tile
import entity.TileType
import kotlin.test.Test
import util.*
import kotlin.math.exp
import kotlin.test.assertEquals

class AxialArithmeticTest {
    /**
     * This test checks if we can get the neighbour positions of a hexagon.
     */
    @Test
    fun circleAroundTest() {
        val tracker = mutableListOf<Pair<Int, Int>>()
        for (neighbour in circleAround((ROOT))) {
            tracker += neighbour
        }

        assertEquals(tracker, SIDE_VECTORS)
    }

    /**
     * Testing what positions are playable überhaupt.
     */
    @Test
    fun getEmptyTilesTest() {
        val bonsaiTree = mutableMapOf(ROOT to Tile(0, 0, TileType.WOOD))
        val tracker = mutableListOf<Pair<Int, Int>>()

        for (emptyPosition in bonsaiTree.getEmptyTiles()) {
            tracker += emptyPosition
        }

        val expectedEmptyPositions = mutableListOf(VECTOR_TOP_RIGHT, VECTOR_TOP_LEFT)

        assertEquals(tracker, expectedEmptyPositions)
    }

    /**
     * Testing a bfs traverse from root
     */
    @Test
    fun traverseFromRootTest() {
        val bonsaiTree = mutableMapOf(
            ROOT to Tile(0, 0, TileType.WOOD),
            VECTOR_TOP_RIGHT to Tile(1, -1, TileType.LEAF)
        )
        val tracker = mutableListOf<Pair<Int, Int>>()

        for (tilePosition in (bonsaiTree traverseFrom ROOT)) {
            tracker += tilePosition
        }

        val expectedTraversedPositions = mutableListOf(ROOT, VECTOR_TOP_RIGHT)

        assertEquals(tracker, expectedTraversedPositions)
    }
}