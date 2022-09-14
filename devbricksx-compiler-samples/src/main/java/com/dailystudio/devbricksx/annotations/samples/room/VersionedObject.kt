package com.dailystudio.devbricksx.annotations.samples.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.development.Logger

@Adapter(viewHolder = Unit::class)
@ViewModel
@RoomCompanion(
    autoGenerate = true,
    migrations = [
        VersionedObjectMigrationV1toV2::class,
        VersionedObjectMigrationV2toV3::class,
        VersionedObjectMigrationV1toV3::class,
    ],
    databaseVersion = 3
)
class VersionedObject(val id: Int) {
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