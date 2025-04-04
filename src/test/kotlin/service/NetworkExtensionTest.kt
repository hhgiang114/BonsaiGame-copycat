package service

import edu.udo.cs.sopra.ntf.CardTypeMessage
import edu.udo.cs.sopra.ntf.ColorTypeMessage
import edu.udo.cs.sopra.ntf.GoalTileTypeMessage
import edu.udo.cs.sopra.ntf.TileTypeMessage
import entity.*
import kotlin.test.*

class NetworkExtensionTest {

    /**
     * Test for [ColorTypeMessage.toColor]
     */
    @Test
    fun testColorTypeMessageToColor(){
        assertEquals(ColorType.BLUE, ColorTypeMessage.BLUE.toColor())
        assertEquals(ColorType.PURPLE, ColorTypeMessage.PURPLE.toColor())
        assertEquals(ColorType.BLACK, ColorTypeMessage.BLACK.toColor())
        assertEquals(ColorType.RED, ColorTypeMessage.RED.toColor())

    }

    /**
     * Test for [ColorType.toColorMessage]
     */
    @Test
    fun testColorToColorTypeMessage(){
        assertEquals(ColorTypeMessage.BLUE, ColorType.BLUE.toColorMessage())
        assertEquals(ColorTypeMessage.PURPLE, ColorType.PURPLE.toColorMessage())
        assertEquals(ColorTypeMessage.BLACK, ColorType.BLACK.toColorMessage())
        assertEquals(ColorTypeMessage.RED, ColorType.RED.toColorMessage())
    }

    /**
     * Test for [TileTypeMessage.toTileType]
     */
    @Test
    fun testTileTypeMessageToTile(){
        assertEquals(TileTypeMessage.WOOD, TileType.WOOD.toTileTypeMessage())
        assertEquals(TileTypeMessage.LEAF, TileType.LEAF.toTileTypeMessage())
        assertEquals(TileTypeMessage.FLOWER, TileType.FLOWER.toTileTypeMessage())
        assertEquals(TileTypeMessage.FRUIT, TileType.FRUIT.toTileTypeMessage())
    }

    /**
     * Test for [TileType.toTileTypeMessage]
     */
    @Test
    fun testTileToTileTypeMessage(){
        assertEquals(TileType.WOOD, TileTypeMessage.WOOD.toTileType())
        assertEquals(TileType.LEAF, TileTypeMessage.LEAF.toTileType())
        assertEquals(TileType.FLOWER, TileTypeMessage.FLOWER.toTileType())
        assertEquals(TileType.FRUIT, TileTypeMessage.FRUIT.toTileType())
    }

    /**
     * Test for [CardTypeMessage.toCardType]
     */
    @Test
    fun testCardTypeMessageToCard(){
        assertEquals(CardTypeMessage.TOOL, CardType.TOOLCARD.toCardTypeMessage())
        assertEquals(CardTypeMessage.GROWTH, CardType.GROWTHCARD.toCardTypeMessage())
        assertEquals(CardTypeMessage.PARCHMENT, CardType.PARCHMENTCARD.toCardTypeMessage())
        assertEquals(CardTypeMessage.HELPER, CardType.HELPERCARD.toCardTypeMessage())
        assertEquals(CardTypeMessage.MASTER, CardType.MASTERCARD.toCardTypeMessage())
    }

    /**
     * Test for [CardType.toCardTypeMessage]
     */
    @Test
    fun testCardToCardTypeMessage(){
        assertEquals(CardType.TOOLCARD, CardTypeMessage.TOOL.toCardType())
        assertEquals(CardType.GROWTHCARD, CardTypeMessage.GROWTH.toCardType())
        assertEquals(CardType.PARCHMENTCARD, CardTypeMessage.PARCHMENT.toCardType())
        assertEquals(CardType.HELPERCARD, CardTypeMessage.HELPER.toCardType())
        assertEquals(CardType.MASTERCARD, CardTypeMessage.MASTER.toCardType())
    }

    /**
     * Test for [GoalTileTypeMessage.toGoalTileType]
     */
    @Test
    fun testGoalTileTypeMessageToGoal(){
        assertEquals(GoalTileTypeMessage.BLUE, GoalTileType.BLUE.toGoalTileTypeMessage())
        assertEquals(GoalTileTypeMessage.GREEN, GoalTileType.GREEN.toGoalTileTypeMessage())
        assertEquals(GoalTileTypeMessage.BROWN, GoalTileType.BROWN.toGoalTileTypeMessage())
        assertEquals(GoalTileTypeMessage.PINK, GoalTileType.PINK.toGoalTileTypeMessage())
        assertEquals(GoalTileTypeMessage.ORANGE, GoalTileType.ORANGE.toGoalTileTypeMessage())
    }

    /**
     * Test for [GoalTileType.toGoalTileTypeMessage]
     */
    @Test
    fun testGoalToGoalTileTypeMessage(){
        assertEquals(GoalTileType.BLUE, GoalTileTypeMessage.BLUE.toGoalTileType())
        assertEquals(GoalTileType.GREEN, GoalTileTypeMessage.GREEN.toGoalTileType())
        assertEquals(GoalTileType.BROWN, GoalTileTypeMessage.BROWN.toGoalTileType())
        assertEquals(GoalTileType.PINK, GoalTileTypeMessage.PINK.toGoalTileType())
        assertEquals(GoalTileType.ORANGE, GoalTileTypeMessage.ORANGE.toGoalTileType())
    }
}
