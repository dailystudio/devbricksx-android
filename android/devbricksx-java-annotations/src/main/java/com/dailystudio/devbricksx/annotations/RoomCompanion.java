package com.dailystudio.devbricksx.annotations;

import androidx.room.ForeignKey;
import androidx.room.Index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface RoomCompanion {
    String primaryKey();
    Class<?> extension() default Void.class;
    Class<?>[] converters() default {};
    ForeignKey[] foreignKeys() default {};
    Index[] indices() default {};

    // database settings
    String database() default "";

    // dao settings
    int pageSize() default 10;

    // repository settings
    boolean repository() default true;
    String repositoryPackage() default "";
}

