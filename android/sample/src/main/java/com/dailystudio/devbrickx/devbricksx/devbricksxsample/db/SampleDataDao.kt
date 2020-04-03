package com.dailystudio.devbrickx.devbricksx.devbricksxsample.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SampleDataDao {
    @Query("SELECT * FROM sampledata")
    fun getAll(): List<SampleData>

    @Query("SELECT * FROM sampledata WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<SampleData>

    @Insert
    fun insertAll(vararg data: SampleData)

    @Delete
    fun delete(data: SampleData)
}