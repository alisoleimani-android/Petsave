package com.raywenderlich.android.petsave.common.data.cache.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.raywenderlich.android.petsave.common.data.cache.model.cachedanimal.CachedAnimalAggregate
import io.reactivex.Flowable

@Dao
abstract class AnimalsDao {

    @Transaction
    @Query("SELECT * FROM animals")
    abstract fun getAllAnimals(): Flowable<List<CachedAnimalAggregate>>
}