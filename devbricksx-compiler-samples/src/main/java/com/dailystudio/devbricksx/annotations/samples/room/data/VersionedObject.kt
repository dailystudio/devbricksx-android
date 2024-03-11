package com.dailystudio.devbricksx.annotations.samples.room.data

import androidx.room.AutoMigration
import androidx.room.RenameTable
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dailystudio.devbricksx.annotations.data.FieldAlias
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.samples.other.DummyViewHolder
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.development.Logger

@Adapter(viewHolder = DummyViewHolder::class)
@ViewModel
@RoomCompanion(
    autoGenerate = true,
    migrations = [
        VersionedObjectMigrationV1toV2::class,
        VersionedObjectMigrationV2toV3::class,
        VersionedObjectMigrationV1toV3::class,
    ],
    databaseVersion = 3,
)
class VersionedObject(val id: Int,
                      @FieldAlias("tagOfObject")
                      val tag: String) {
}

class VersionedObjectMigrationV1toV2: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Logger.debug("migrating from [$startVersion] to [$endVersion]")
    }
}

class VersionedObjectMigrationV2toV3: Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Logger.debug("migrating from [$startVersion] to [$endVersion]")
    }
}


class VersionedObjectMigrationV1toV3: Migration(1, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Logger.debug("migrating from [$startVersion] to [$endVersion]")
    }
}


@Adapter(viewHolder = DummyViewHolder::class)
@ViewModel
@RoomCompanion(
    autoGenerate = true,
    databaseVersion = 1,
//    autoMigrations = [
//        AutoMigration(1, 2),
//        AutoMigration(2, 3),
//        AutoMigration(3, 4, spec = AutoMigrationV3toV4::class),
//    ]
)
class AutoVersionedObject(val id: Int,
                          val prop1: String? = null,
//                          val prop2: String? = null,
//                          val prop3: Int? = null,
) {
}
@RenameTable(fromTableName = "autoversionedobject", toTableName = "avo")
class AutoMigrationV3toV4 : AutoMigrationSpec
