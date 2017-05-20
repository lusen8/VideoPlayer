package com.example.myglide;

/**
 * Created by lusen on 2017/5/3.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.myglide.manager.RequestManagerRetriever;
import com.example.myglide.module.AppGlideModule;
import java.util.Set;

/**
 * A temporary interface to allow {@link AppGlideModule}s to exclude
 * {@link com.example.myglide.annotation.GlideModule}s to ease the migration from
 * {@link com.example.myglide.annotation.GlideModule}s to MyGlide's annotation processing system.
 */
@Deprecated
abstract class GeneratedAppGlideModule extends AppGlideModule {
    /**
     * This method can be removed when manifest parsing is no longer supported.
     */
    @Deprecated
    @NonNull
    abstract Set<Class<?>> getExcludedModuleClasses();

    @Nullable
    RequestManagerRetriever.RequestManagerFactory getRequestManagerFactory() {
        return null;
    }
}
