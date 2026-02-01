package com.dailystudio.devbricksx.database

import androidx.room.Ignore
import com.dailystudio.devbricksx.annotations.data.IgnoreField
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ui.SelectableListItem
import java.util.*

/**
 * Base class for a database record with an Integer ID.
 *
 * It provides basic fields like `id`, `created`, and `lastModified`.
 * It is annotated with [RoomCompanion] to generate Room database entities and DAOs.
 *
 * @property id The primary key of the record.
 */
@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class])
open class Record(open val id: Int = 0) {
    /**
     * The creation timestamp of the record.
     */
    var created: Date? = null

    /**
     * The last modification timestamp of the record.
     */
    var lastModified: Date? = null

    override fun toString(): String {
        return buildString {
            append("[${javaClass.simpleName}: $id]")
        }
    }
}

/**
 * A selectable version of [Record].
 *
 * It implements [SelectableListItem] to support selection in UI lists.
 * The selection state is ignored by the database.
 */
@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class]
)
open class SelectableRecord(id: Int = 0) : Record(id), SelectableListItem {

    /**
     * The selection state of the record. Not persisted in the database.
     */
    @IgnoreField
    @Ignore
    var selected: Boolean = false

    override fun isItemSelected(): Boolean {
        return selected
    }

    override fun setItemSelected(selected: Boolean) {
        this.selected = selected
    }

}

/**
 * Base class for a database record with a String ID.
 *
 * Similar to [Record] but uses a String as the primary key.
 *
 * @property id The primary key of the record.
 */
@RoomCompanion(primaryKeys = ["id"],
    converters = [DateConverter::class])
open class StringIdRecord(open val id: String) {
    /**
     * The creation timestamp of the record.
     */
    var created: Date? = null

    /**
     * The last modification timestamp of the record.
     */
    var lastModified: Date? = null

    override fun toString(): String {
        return buildString {
            append("[${this@StringIdRecord.javaClass.simpleName}: $id]")
        }
    }
}

/**
 * A selectable version of [StringIdRecord].
 *
 * It implements [SelectableListItem] to support selection in UI lists.
 * The selection state is ignored by the database.
 */
@RoomCompanion(primaryKeys = ["id"],
    converters = [DateConverter::class]
)
open class SelectableStringIdRecord(id: String) : StringIdRecord(id), SelectableListItem {

    /**
     * The selection state of the record. Not persisted in the database.
     */
    @IgnoreField
    @Ignore
    var selected: Boolean = false

    override fun isItemSelected(): Boolean {
        return selected
    }

    override fun setItemSelected(selected: Boolean) {
        this.selected = selected
    }

}
