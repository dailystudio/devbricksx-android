package com.dailystudio.devbricksx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * DaoExtension is deprecated by DaoExtension in package com.dailystudio.devbricksx.annotations.data
 */
@Deprecated
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DaoExtension {
    Class<?> entity();
}

