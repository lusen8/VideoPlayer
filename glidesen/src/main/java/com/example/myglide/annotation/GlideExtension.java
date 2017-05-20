package com.example.myglide.annotation;

/**
 * Created by lusen on 2017/5/3.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate a class that extends MyGlide's public API.
 *
 * @see GlideOption
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GlideExtension { }
