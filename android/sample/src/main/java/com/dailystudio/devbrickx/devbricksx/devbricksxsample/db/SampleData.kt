package com.dailystudio.devbrickx.devbricksx.devbricksxsample.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SampleData (
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name") val name: String?,
        @ColumnInfo(name = "int_value") val intValue: Int?
)
