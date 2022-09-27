package com.dailystudio.devbricksx.annotations;

import androidx.room.ForeignKey;
import androidx.room.Index;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * RoomCompanion is deprecated by RoomCompanion in package com.dailystudio.devbricksx.annotations.data
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RoomCompanion {
    String[] primaryKeys();
    boolean autoGenerate() default false;

    Class<?>[] converters() default {};
    ForeignKey[] foreignKeys() default {};
    Index[] indices() default {};

    Class<?> extension() default Void.class;

    // database name
    String database() default "";

    // database version
    int databaseVersion() default 1;

    // database migrations
    Class<?>[] migrations() default {};

    // dao settings
    int pageSize() default 10;

    // repository settings
    boolean repository() default true;
}

