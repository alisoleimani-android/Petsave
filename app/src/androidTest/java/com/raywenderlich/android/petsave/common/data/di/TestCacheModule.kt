package com.raywenderlich.android.petsave.common.data.di

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.raywenderlich.android.petsave.common.data.cache.PetSaveDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TestCacheModule {

    @Provides
    fun provideRoomDatabase(): PetSaveDatabase {
        return Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            PetSaveDatabase::class.java
        ).allowMainThreadQueries().build()
    }

}