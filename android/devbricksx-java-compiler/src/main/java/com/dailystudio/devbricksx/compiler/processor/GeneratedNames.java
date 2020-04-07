package com.dailystudio.devbricksx.compiler.processor;

public class GeneratedNames {

    private final static String ROOM_COMPANION = "_Companion";
    private final static String ROOM_COMPANION_DB_SUFFIX = "Database";
    private final static String ROOM_COMPANION_DAO_SUFFIX = "Dao";

    public static String getRoomCompanionName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.append(ROOM_COMPANION);

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

}
