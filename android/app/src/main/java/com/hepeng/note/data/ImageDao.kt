package com.hepeng.note.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM image")
    fun getAll(): Flow<List<Image>>

    @Query("SELECT * FROM image WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): Flow<List<Image>>

    @Insert
    fun insertAll(vararg image: Image)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: Image)

    @Delete
    fun delete(image: Image)

    @Query("SELECT * FROM image WHERE id = :id")
    fun loadUserById(id: Int): Flow<Image>
}