package com.dailystudio.devbricksx.annotations;

import androidx.room.ForeignKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface RoomCompanion {
    String primaryKey();
    String database() default "";
    Class<?> extension() default Void.class;
    Class<?>[] converters() default {};
    ForeignKey[] foreignKeys() default {};
}

