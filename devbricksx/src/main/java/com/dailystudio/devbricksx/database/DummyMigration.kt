package com.dailystudio.devbricksx.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class DummyMigration(start: Int, end: Int) : Migration(start, end) {
    override fun migrate(database: SupportSQLiteDatabase) {
    }
}