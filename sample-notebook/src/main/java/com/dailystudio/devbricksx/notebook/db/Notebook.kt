package com.dailystudio.devbricksx.notebook.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.Record
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.fragment.AbsNotebooksFragment
import com.dailystudio.devbricksx.notebook.ui.NotebookViewHolder

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class],
        extension = NotebookDaoExtension::class,
        database = "notes",
        databaseVersion = 3,
        migrations = [Migration1To2::class, Migration2To3::class]
)
@ViewModel
@Adapter(viewType = ViewType.SingleLine, viewHolder = NotebookViewHolder::class)
@ListFragment(layout = R.layout.fragment_notebooks,
        gridLayout = true,
        superClass = AbsNotebooksFragment::class)
class Notebook(id: Int = 0) : Record(id) {
    @JvmField var name: String? = null

    override fun toString(): String {
        return buildString {
            append("Notebook[$id]: $name")
        }
    }
}

open class Migration1To2: Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        Logger.info("upgrade database from $startVersion to $endVersion")
    }

}

open class Migration2To3: Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        Logger.info("upgrade database from $startVersion to $endVersion")
    }

}