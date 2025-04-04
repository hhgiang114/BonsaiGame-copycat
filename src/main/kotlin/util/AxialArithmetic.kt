package util

import entity.Tile


// -----------------------------------------
// -----------------------------------------
// --------------- CONSTANTS ---------------
// -----------------------------------------
// -----------------------------------------
// hex-movement vectors
val VECTOR_TOP_RIGHT = 1 to -1
val VECTOR_RIGHT = 1 to 0
val VECTOR_BOTTOM_RIGHT = 0 to 1
val VECTOR_BOTTOM_LEFT = -1 to 1
val VECTOR_LEFT = -1 to 0
val VECTOR_TOP_LEFT = 0 to -1

// set of possible direction vector
val SIDE_VECTORS = listOf(
    VECTOR_TOP_RIGHT,
    VECTOR_RIGHT,
    VECTOR_BOTTOM_RIGHT,
    VECTOR_BOTTOM_LEFT,
    VECTOR_LEFT,
    VECTOR_TOP_LEFT
)

// root
val ROOT = (0 to 0)

// pot tiles
val POT = setOf(
    -2 to 0, -1 to 0, ROOT, 1 to 0, 2 to 0, 3 to 0,
    -2 to 1, -1 to 1, 0 to 1, 1 to 1, 2 to 1,
    -2 to 2, -1 to 2, 0 to 2, 1 to 2
)

// ------------------------------------------------
// ------------------------------------------------
// --------------- AXIAL ARITHMETIC ---------------
// ------------------------------------------------
// ------------------------------------------------

// --------------- sus feature (rethink later) ---------------

/**
 * for gui, read empty tiles to show in game scene.
 *
 * @return a list of playable positions
 */
fun MutableMap<Pair<Int, Int>, Tile>.getEmptyTiles(): Set<Pair<Int, Int>> {
    val emptyTiles = mutableSetOf<Pair<Int, Int>>()

    for (tileIndex in (this@getEmptyTiles traverseFrom ROOT)) {
        for (neighbourIndex in circleAround(tileIndex)) {
            if (this@getEmptyTiles[neighbourIndex] == null &&
                neighbourIndex !in POT
            ) {
                emptyTiles += neighbourIndex
            }
        }
    }

    return emptyTiles
}

/**
 * BFS. First element in the queue is to be processed. New nodes added in the end. Change queue conditions
 * based on design.
 *
 * @param bfsRoot is where the traversal starts
 *
 * @return tile position as a lazy sequence
 */
infix fun MutableMap<Pair<Int, Int>, Tile>.traverseFrom(bfsRoot: Pair<Int, Int>) :
        Sequence<Pair<Int, Int>> = sequence {
    // args check
    checkNotNull(this@traverseFrom[bfsRoot]) { "Cannot start from non-existing tile." }

    // init
    val visited = mutableSetOf<Pair<Int, Int>>()
    val queue = ArrayDeque<Pair<Int, Int>>()
    queue.add(bfsRoot)
    visited.add(bfsRoot)

    // traverse
    while (queue.isNotEmpty()) {
        // fetch current node
        val currentNode = queue.removeFirst()

        // process current node
        yield(currentNode)

        // add neighbour nodes to queue
        for (neighbour in (circleAround(currentNode))) {
            // add any conditions here to filter nodes
            if (neighbour !in visited &&
                neighbour !in POT &&
                this@traverseFrom[neighbour] != null
            ) {
                visited.add(neighbour)
                queue.add(neighbour)
            }
        }
    }
}

/**
 * Takes an axial coordinate as a parameter and acts like an iterator. It returns a generator that yields a tile around
 * the center one by one.
 *
 * @param center is the coordinate around which we are iterating
 *
 * @return Tile
 * @return null if there is no tile in that spot.
 *
 * @see `Demo` in the same dir for example usage.
 */
fun circleAround(center: Pair<Int, Int>): Sequence<Pair<Int, Int>> = sequence {
    val (q, r) = center

    for ((dq, dr) in SIDE_VECTORS)
        yield((q + dq) to (r + dr))
}
/*
 /**
 * Wrapper for the [circleAround] function to iterate infinitely around a coordinate
 */
private fun foreverCircleAround(center: Pair<Int, Int>): Sequence<Pair<Int, Int>> = sequence {
    while (true)
        for (tile in circleAround(center))
            yield(tile)
}

/**
 * Single-step clockwise rotation around an adjacent center point
 */
private infix fun Pair<Int, Int>.rotateClockwiseAround(center: Pair<Int, Int>): Pair<Int, Int> =
    when (this - center) {
        VECTOR_LEFT -> this + VECTOR_TOP_RIGHT
        VECTOR_TOP_LEFT -> this + VECTOR_RIGHT
        VECTOR_TOP_RIGHT -> this + VECTOR_BOTTOM_RIGHT
        VECTOR_RIGHT -> this + VECTOR_BOTTOM_LEFT
        VECTOR_BOTTOM_RIGHT -> this + VECTOR_LEFT
        VECTOR_BOTTOM_LEFT -> this + VECTOR_TOP_LEFT
        else -> throw IllegalArgumentException("Invalid radius. Implement non-adjacent ones yourself >:)")
    }

/**
 * Single-step counter-clockwise rotation around an adjacent center point
 */
private infix fun Pair<Int, Int>.rotateCounterClockwiseAround(center: Pair<Int, Int>): Pair<Int, Int> =
    when (this - center) {
        VECTOR_LEFT -> this + VECTOR_BOTTOM_RIGHT
        VECTOR_TOP_LEFT -> this + VECTOR_BOTTOM_LEFT
        VECTOR_TOP_RIGHT -> this + VECTOR_LEFT
        VECTOR_RIGHT -> this + VECTOR_TOP_LEFT
        VECTOR_BOTTOM_RIGHT -> this + VECTOR_TOP_RIGHT
        VECTOR_BOTTOM_LEFT -> this + VECTOR_RIGHT
        else -> throw IllegalArgumentException("Invalid radius. Implement non-adjacent ones yourself >:)")
    }
*/
