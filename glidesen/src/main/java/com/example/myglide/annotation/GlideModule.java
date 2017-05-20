package com.example.myglide.annotation;

/**
 * Created by lusen on 2017/5/3.
 */

import com.example.myglide.MyGlide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies AppGlideModules and ChildeGlideModules for MyGlide's annotation processor to merge at
 * compile time.
 *
 * <p>Replaces <meta-data /> tags in AndroidManifest.xml.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GlideModule {
    /**
     * Returns the name of the class that will be used as a replacement for
     * {@link MyGlide} in Applications that depend on MyGlide's generated code.
     */
    String glideName() default "GlideApp";
}
