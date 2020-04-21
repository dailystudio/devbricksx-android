package com.dailystudio.devbricksx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Page {

    final static int DEFAULT_PAGE_SIZE = 10;

    int pageSize() default DEFAULT_PAGE_SIZE;

}

