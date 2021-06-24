package com.dailystudio.devbricksx.database

import androidx.room.Ignore
import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.ui.SelectableListItem
import java.util.*

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class])
open class Record(@JvmField val id: Int = 0) {
    @JvmField var created: Date? = null
    @JvmField var lastModified: Date? = null

    override fun toString(): String {
        return buildString {
            append("[${javaClass.simpleName}: $id]")
        }
    }
}

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class]
)
open class SelectableRecord(id: Int = 0) : Record(id), SelectableListItem {

    @Ignore
    var selected: Boolean = false

    override fun isItemSelected(): Boolean {
        return selected
    }

    override fun setItemSelected(selected: Boolean) {
        this.selected = selected
    }

}

@RoomCompanion(primaryKeys = ["id"],
    converters = [DateConverter::class])
open class StringIdRecord(@JvmField val id: String) {
    @JvmField var created: Date? = null
    @JvmField var lastModified: Date? = null

    override fun toString(): String {
        return buildString {
            append("[${javaClass.simpleName}: $id]")
        }
    }
}

@RoomCompanion(primaryKeys = ["id"],
    converters = [DateConverter::class]
)
open class SelectableStringIdRecord(id: String) : StringIdRecord(id), SelectableListItem {

    @Ignore
    var selected: Boolean = false

    override fun isItemSelected(): Boolean {
        return selected
    }

    override fun setItemSelected(selected: Boolean) {
        this.selected = selected
    }

}
