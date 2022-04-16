package com.raywenderlich.android.petsave.common.data

import com.raywenderlich.android.petsave.common.data.api.PetFinderApi
import com.raywenderlich.android.petsave.common.data.api.model.mappers.ApiAnimalMapper
import com.raywenderlich.android.petsave.common.data.api.model.mappers.ApiPaginationMapper
import com.raywenderlich.android.petsave.common.data.api.utils.FakeServer
import com.raywenderlich.android.petsave.common.data.cache.Cache
import com.raywenderlich.android.petsave.common.data.di.PreferencesModule
import com.raywenderlich.android.petsave.common.data.preferences.FakePreferences
import com.raywenderlich.android.petsave.common.data.preferences.Preferences
import com.raywenderlich.android.petsave.common.domain.repositories.AnimalRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(PreferencesModule::class)
class PetFinderAnimalRepositoryTest {

    private val fakeServer = FakeServer()
    private lateinit var api: PetFinderApi
    private lateinit var repository: AnimalRepository

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject // 3
    lateinit var cache: Cache

    @Inject
    lateinit var retrofitBuilder: Retrofit.Builder

    @Inject
    lateinit var apiAnimalMapper: ApiAnimalMapper

    @Inject
    lateinit var apiPaginationMapper: ApiPaginationMapper

    @BindValue
    @JvmField
    val preferences: Preferences = FakePreferences()
}