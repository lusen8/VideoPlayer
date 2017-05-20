package com.example.myglide.module;

import android.content.Context;
import com.example.myglide.Registry;

/**
 * Registers a set of components to use when initializing MyGlide within an app when
 * MyGlide's annotation processor is used.
 *
 * <p>Any number of LibraryGlideModules can be contained within any library or application.
 *
 * <p>LibraryGlideModules are called in no defined order. If LibraryGlideModules within an
 * application conflict, {@link AppGlideModule}s can use the
 * {@link com.example.myglide.annotation.Excludes} annotation to selectively remove one or more of
 * the conflicting modules.
 */
public abstract class LibraryGlideModule implements RegistersComponents {
  @Override
  public void registerComponents(Context context, Registry registry) {
    // Default empty impl.
  }
}
