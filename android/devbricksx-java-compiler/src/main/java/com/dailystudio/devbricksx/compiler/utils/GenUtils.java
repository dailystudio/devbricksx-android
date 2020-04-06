package com.dailystudio.devbricksx.compiler.utils;

public class GenUtils {

    private final static String ROOM_COMPANION = "RoomCompanion";

    public static String getRoomCompanionName(String className) {
        return className + ROOM_COMPANION;
    }
    public static String getRoomCompanionDatabaseName(String className) {
        return getRoomCompanionName(className) + "Database";
    }

}
