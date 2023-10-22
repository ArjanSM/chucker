package com.chuckerteam.chucker.internal.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByMethod
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByScheme
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class PreferenceRepositoryTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        RepositoryProvider.close()
    }

    @Test
    fun `fails to apply Filters when uninitialized`() {
        runBlocking {
            assertThrows<IllegalStateException> {
                RepositoryProvider.preferences().applyFilterByScheme(FilterByScheme(false))
            }
        }
    }

    @Test
    fun `retrieves filters with default values on initial call`() {
        runBlocking {
            RepositoryProvider.initializePreferences(context)
            val initialStateOfFilters = RepositoryProvider.preferences().getFilterData()
            assertTrue(initialStateOfFilters.filterByMethod.post)
            assertTrue(initialStateOfFilters.filterByMethod.get)
            assertTrue(initialStateOfFilters.filterByMethod.put)
            assertTrue(initialStateOfFilters.filterByScheme.http)
            assertTrue(initialStateOfFilters.filterByScheme.https)
        }
    }

    @Test
    fun `applies filterByMethod when initialized`() {
        runBlocking {
            RepositoryProvider.initializePreferences(context)
            RepositoryProvider.preferences().applyFilterByMethod(FilterByMethod(get = false))
            val currentStateOfFilters = RepositoryProvider.preferences().getFilterData()
            assertTrue(currentStateOfFilters.filterByMethod.post)
            assertTrue(currentStateOfFilters.filterByMethod.put)
            assertFalse(currentStateOfFilters.filterByMethod.get)
        }
    }

    @Test
    fun `applies filterByScheme when initialized`() {
        runBlocking {
            RepositoryProvider.initializePreferences(context)
            RepositoryProvider.preferences().applyFilterByScheme(FilterByScheme(http = false))
            val currentStateOfFilters = RepositoryProvider.preferences().getFilterData()
            assertTrue(currentStateOfFilters.filterByScheme.https)
            assertFalse(currentStateOfFilters.filterByScheme.http)
        }
    }
}
