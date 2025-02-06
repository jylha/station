package dev.jylha.station.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DefaultSettingsRepositoryTest {

    @get:Rule
    val temporaryFolder: TemporaryFolder = TemporaryFolder.builder()
        .assureDeletion()
        .build()

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())
    private val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { temporaryFolder.newFile("test_store.preferences_pb") }
    )
    private val repository: DefaultSettingsRepository = DefaultSettingsRepository(testDataStore)


    @Test
    fun `Set station with empty recent stations`() = runTest(testDispatcher) {
        // GIVEN the recent stations are empty

        // WHEN setting a new station
        repository.setStation(123)

        // THEN it is the first and only recent station
        val result = repository.recentStations().first()
        assertThat(result).containsExactly(123)
    }

    @Test
    fun `Set station with multiple stations in recent list`() = runTest(testDispatcher) {
        // GIVEN multiple different stations are set
        repository.setStation(123)
        repository.setStation(456)
        repository.setStation(789)

        // WHEN retrieving recent stations
        val result = repository.recentStations().first()

        // THEN the order should be correct, from most to least recent.
        assertThat(result).containsExactly(789, 456, 123)
    }

    @Test
    fun `Set station when it is already the current station`() = runTest(testDispatcher) {
        // GIVEN a station is the current station
        repository.setStation(123)

        // WHEN setting the current station to the same station
        repository.setStation(123)

        // THEN the recent station list should not be modified.
        val result = repository.recentStations().first()
        assertThat(result).containsExactly(123)
    }

    @Test
    fun `Set station when it is already in recent stations`() = runTest(testDispatcher) {
        // GIVEN a station already in recent stations
        repository.setStation(1)
        repository.setStation(2)
        repository.setStation(3)
        assertThat(repository.recentStations().first()).containsExactly(3, 2, 1)

        // WHEN the station is set again
        repository.setStation(2)

        // THEN it is moved to the front and the rest maintain order
        assertThat(repository.recentStations().first()).containsExactly(2, 3, 1)
    }

    @Test
    fun `Set station with recent stations at max capacity`() = runTest(testDispatcher) {
        // GIVEN recent stations are at max capacity
        repository.setStation(1)
        repository.setStation(2)
        repository.setStation(3)
        assertThat(repository.recentStations().first()).containsExactly(3, 2, 1)

        // WHEN a new station is added, and then another one
        repository.setStation(4)
        repository.setStation(5)

        // THEN the least recently used station should be removed, keeping max capacity
        assertThat(repository.recentStations().first()).containsExactly(5, 4, 3)
    }
}
