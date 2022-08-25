package com.dailystudio.devbricksx.annotations.samples.room

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingSource
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dailystudio.devbricksx.annotations.plus.DaoExtension
import com.dailystudio.devbricksx.annotations.plus.RoomCompanion
import kotlinx.coroutines.flow.Flow

@RoomCompanion(
    primaryKeys = ["id"]
)
class AllTypesInOne {
    var id: Int = 0
    var intType: Int = 0
    var shortType: Short = 0
    var longType: Long = 0
    var floatType: Float = 0f
    var doubleType: Double = 0.0
    var stringType: String = ""
}

@DaoExtension(entity = AllTypesInOne::class)
abstract class AllTypesInOneDaoExtension {

    @Query("SELECT * FROM alltypesinone WHERE int_type = :arrayOfInt")
    abstract fun selectOneByValOfIntType(arrayOfInt: Array<Int>): AllTypesInOne

    @Query("SELECT * FROM alltypesinone WHERE int_type = :arrayOfInt")
    abstract fun selectAllByValOfIntType(arrayOfInt: Array<Int>): List<AllTypesInOne>

    @Query("SELECT * FROM alltypesinone WHERE int_type = :arrayOfInt")
    abstract fun selectOneByValOfIntTypeLive(arrayOfInt: Array<Int>): LiveData<AllTypesInOne>

    @Query("SELECT * FROM alltypesinone WHERE int_type = :arrayOfInt")
    abstract fun selectAllByValOfIntTypeLive(arrayOfInt: Array<Int>): LiveData<List<AllTypesInOne>>

    @Query("SELECT * FROM alltypesinone WHERE int_type = :arrayOfInt")
    abstract fun selectAllByValOfIntTypeFlow(arrayOfInt: Array<Int>): Flow<List<AllTypesInOne>>

    @Query("SELECT * FROM alltypesinone WHERE int_type = :arrayOfInt")
    abstract fun selectAllByValOfIntTypePagedList(arrayOfInt: Array<Int>): PagedList<List<AllTypesInOne>>

    @Query("SELECT * FROM alltypesinone WHERE int_type = :arrayOfInt")
    abstract fun selectAllByValOfIntTypePagingSource(arrayOfInt: Array<Int>): PagingSource<Int, List<AllTypesInOne>>

    @Query("SELECT * FROM alltypesinone WHERE id IN (:ids)")
    abstract fun loadAllByIds(ids: IntArray): List<AllTypesInOne>

    @Insert
    abstract fun insertTwo(o1: AllTypesInOne, o2: AllTypesInOne, dummy: Int)

    @Insert
    abstract fun insertAll(ones: List<AllTypesInOne>) : List<Long>

    @Insert
    abstract fun insertMany(vararg ones: AllTypesInOne) : List<Long>

    @Update
    abstract fun updateFriends(friends: List<AllTypesInOne>)

    @Delete
    abstract fun deleteFriends(friends: List<AllTypesInOne>)

}