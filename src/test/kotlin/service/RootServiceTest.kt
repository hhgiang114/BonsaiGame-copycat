package service

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class RootServiceTest {

    /** Test adding a single Refreshable to the RootService. */
    @Test
    fun testAddRefreshable() {
        val rootService = RootService()
        val testRefreshable = RefreshableTest(rootService)

        // Test: The Refreshable is added without an error
        assertDoesNotThrow { rootService.addRefreshable(testRefreshable) }
    }

    /** Test adding multiple Refreshables to the RootService. */
    @Test
    fun testAddMultipleRefreshables() {
        val rootService = RootService()
        val testRefreshable1 = RefreshableTest(rootService)
        val testRefreshable2 = RefreshableTest(rootService)

        // Test: The Refreshables are added without an error
        assertDoesNotThrow { rootService.addRefreshables(testRefreshable1, testRefreshable2) }
    }
}