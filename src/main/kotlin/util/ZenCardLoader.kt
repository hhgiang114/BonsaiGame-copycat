package util

import entity.*

/**
 * this class is to create the zenDeck
 * usage sees [readAllZenCards]
 *
 * the private functions read the csv files then map the data class to our zenCard
 * at the end it returns a list of the chosen cards
 */
class ZenCardLoader {
    val csvLoader = CSVLoader()

    private fun readAllGrowthCards(playerAmount: Int): List<GrowthCard> {
        return csvLoader.readCsvFile<CSVGrowthCardEntry>("/zengrowth.csv").mapNotNull {
            val type = when (it.type) {
                "log" -> TileType.WOOD
                "leaf" -> TileType.LEAF
                "blossom" -> TileType.FLOWER
                else -> TileType.FRUIT
            }

            val result = if (playerAmount < it.minPlayerAmount) {
                null
            } else {
                GrowthCard(type, it.id)
            }
            result
        }
    }

    private fun readAllHelperCards(): List<HelperCard> {
        return csvLoader.readCsvFile<CSVHelperCardEntry>("/zenhelper.csv").map {
            val type = when (it.type2) {
                "log" -> TileType.WOOD
                "leaf" -> TileType.LEAF
                "blossom" -> TileType.FLOWER
                else -> TileType.FRUIT
            }
            HelperCard(type, it.id)
        }
    }

    private fun readAllMasterCards(playerAmount: Int): List<MasterCard> {
        return csvLoader.readCsvFile<CSVMasterCardEntry>("/zenmaster.csv").mapNotNull { cardEntry ->
            val tileTypes = mutableListOf(
                getType1(cardEntry), getType2(cardEntry), getType3(cardEntry))

            tileTypes.removeAll {
                it == TileType.EMPTY
            }

            val result = if (playerAmount < cardEntry.minPlayerAmount) {
                null
            } else {
                MasterCard(tileTypes, cardEntry.id)
            }
            result
        }
    }

    private fun readAllParchmentCards(): List<ParchmentCard> {
        return csvLoader.readCsvFile<CSVParchmentCardEntry>("/zenparchment.csv").map {
            val cardType = when (it.targetType) {
                "master" -> CardType.MASTERCARD
                "growth" -> CardType.GROWTHCARD
                "helper" -> CardType.HELPERCARD
                else -> null
            }

            val tileType = when (it.targetType) {
                "blossom" -> TileType.FLOWER
                "fruit" -> TileType.FRUIT
                "leaf" -> TileType.LEAF
                "log" -> TileType.WOOD
                else -> null
            }

            ParchmentCard(tileType, cardType, it.points, it.id)
        }
    }

    private fun readAllToolCards(playerAmount: Int): List<ToolCard> {
        return csvLoader.readCsvFile<CSVToolCardEntry>("/zentool.csv").mapNotNull {
            val result = if (playerAmount < it.minPlayerAmount) {
                null
            } else {
                ToolCard(it.id)
            }
            result
        }
    }

    /**
     * this function returns a [List] of [Card] for a given amount of [playerAmount]
     *
     * @param [playerAmount] of how many players are there in the game
     */
    fun readAllZenCards(playerAmount: Int): List<Card> {
        return readAllGrowthCards(playerAmount) +
                readAllHelperCards() +
                readAllMasterCards(playerAmount) +
                readAllParchmentCards() +
                readAllToolCards(playerAmount)
    }

    private fun getType1(cardEntry: CSVMasterCardEntry): TileType {
        return when (cardEntry.type1) {
            "log" -> TileType.WOOD
            "leaf" -> TileType.LEAF
            else-> TileType.ANY
        }
    }

    private fun getType2(cardEntry: CSVMasterCardEntry): TileType {
        return when (cardEntry.type2) {
            "log" -> TileType.WOOD
            "leaf" -> TileType.LEAF
            "blossom" -> TileType.FLOWER
            "fruit" -> TileType.FRUIT
            else -> TileType.EMPTY
        }
    }

    private fun getType3(cardEntry: CSVMasterCardEntry): TileType {
        return when (cardEntry.type3) {
            "blossom" -> TileType.FLOWER
            "fruit" -> TileType.FRUIT
            else -> TileType.EMPTY
        }
    }
}

/**
 * Represents a CSV entry for a Growth Card.
 */
data class CSVGrowthCardEntry(
    val id: Int, val minPlayerAmount: Int, val type: String
)

/**
 * Represents a CSV entry for a Helper Card.
 */
data class CSVHelperCardEntry(
    val id: Int, val type1: String, val type2: String
)

/**
 * Represents a CSV entry for a Master Card.
 */
data class CSVMasterCardEntry(
    val id: Int, val minPlayerAmount: Int,
    val type1: String, val type2: String, val type3: String
)

/**
 * Represents a CSV entry for a Parchment Card.
 */
data class CSVParchmentCardEntry(
    val id: Int, val points: Int, val targetType: String
)

/**
 * Represents a CSV entry for a Tool Card.
 */
data class CSVToolCardEntry(
    val id: Int, val minPlayerAmount: Int
)
