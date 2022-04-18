package com.raywenderlich.android.petsave.common.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.raywenderlich.android.petsave.common.data.api.PetFinderApi
import com.raywenderlich.android.petsave.common.data.api.model.mappers.ApiAnimalMapper
import com.raywenderlich.android.petsave.common.data.api.model.mappers.ApiPaginationMapper
import com.raywenderlich.android.petsave.common.data.api.utils.FakeServer
import com.raywenderlich.android.petsave.common.data.cache.Cache
import com.raywenderlich.android.petsave.common.data.cache.PetSaveDatabase
import com.raywenderlich.android.petsave.common.data.cache.RoomCache
import com.raywenderlich.android.petsave.common.data.di.CacheModule
import com.raywenderlich.android.petsave.common.data.di.PreferencesModule
import com.raywenderlich.android.petsave.common.data.preferences.FakePreferences
import com.raywenderlich.android.petsave.common.data.preferences.Preferences
import com.raywenderlich.android.petsave.common.domain.repositories.AnimalRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.Instant
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(PreferencesModule::class, CacheModule::class)
class PetFinderAnimalRepositoryTest {

    private val fakeServer = FakeServer()
    private lateinit var api: PetFinderApi
    private lateinit var repository: AnimalRepository

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var cache: Cache

    @Inject
    lateinit var database: PetSaveDatabase

    @Inject
    lateinit var retrofitBuilder: Retrofit.Builder

    @Inject
    lateinit var apiAnimalMapper: ApiAnimalMapper

    @Inject
    lateinit var apiPaginationMapper: ApiPaginationMapper

    @BindValue
    @JvmField
    val preferences: Preferences = FakePreferences()

    @Before
    fun setup() {
        fakeServer.start()

        preferences.deleteTokenInfo()
        preferences.putToken("validToken")
        preferences.putTokenExpirationTime(
            Instant.now().plusSeconds(3600).epochSecond
        )
        preferences.putTokenType("Bearer")

        hiltRule.inject()

        api = retrofitBuilder
            .baseUrl(fakeServer.baseEndpoint)
            .build()
            .create(PetFinderApi::class.java)

        cache = RoomCache(database.organizationsDao(), database.animalsDao())

        repository = PetFinderAnimalRepository(api, cache, apiAnimalMapper, apiPaginationMapper)
    }

    @Test
    fun requestMoreAnimals_success() = runBlocking {
        // Given
        val expectedAnimalId = 124L
        fakeServer.setHappyPathDispatcher()

        // When
        val paginatedAnimals = repository.requestMoreAnimals(1, 100)

        // Then
        val animal = paginatedAnimals.animals.first()
        assertThat(animal.id).isEqualTo(expectedAnimalId)
    }

    @Test
    fun insertAnimals_success() {
        // Given
        val expectedAnimalId = 124L
        fakeServer.setHappyPathDispatcher()

        // When
        runBlocking {
            val paginatedAnimals = repository.requestMoreAnimals(1, 100)
            val animal = paginatedAnimals.animals.first()
            repository.storeAnimals(listOf(animal))
        }

        // Then
        repository.getAnimals().test().run {
            assertNoErrors()
            assertNotComplete()
            assertValue { it.first().id == expectedAnimalId }
        }
    }

    @After
    fun teardown() {
        fakeServer.shutdown()
    }
}