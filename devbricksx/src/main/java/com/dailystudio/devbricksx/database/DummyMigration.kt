package com.dailystudio.devbricksx.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * A dummy migration that does nothing.
 *
 * Useful for suppressing migration errors during development when schema changes are non-destructive or handled elsewhere.
 *
 * @param start The start version.
 * @param end The end version.
 */
class DummyMigration(start: Int, end: Int) : Migration(start, end) {
    override fun migrate(database: SupportSQLiteDatabase) {
    }
}