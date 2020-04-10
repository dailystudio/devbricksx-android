package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import androidx.room.Database;

import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;

public class GeneratedNames {

    private final static String ROOM_COMPANION_PREFIX = "_";
    private final static String ROOM_COMPANION_DB_SUFFIX = "Database";
    private final static String ROOM_COMPANION_DAO_SUFFIX = "Dao";

    public static String getRoomCompanionName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.insert(0, ROOM_COMPANION_PREFIX);

        return builder.toString();
    }

    public static String getRoomCompanionDaoName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.append(ROOM_COMPANION_DAO_SUFFIX);

        return builder.toString();
    }

    public static String getRoomCompanionDatabaseName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.append(ROOM_COMPANION_DB_SUFFIX);

        return builder.toString();
    }

    public static String databaseToClassName(String database) {
        if (TextUtils.isEmpty(database)) {
            return ROOM_COMPANION_DB_SUFFIX;
        }

        StringBuilder builder = new StringBuilder(NameUtils.capitalizeName(database));

        if (!database.endsWith(ROOM_COMPANION_DB_SUFFIX)) {
            builder.append(ROOM_COMPANION_DB_SUFFIX);
        }

        return builder.toString();
    }

}
