package com.example.myglide.module;

import android.content.Context;
import com.example.myglide.GlideBuilder;

/**
 * Defines a set of dependencies and options to use when initializing MyGlide within an application.
 *
 * <p>There can be at most one {@link AppGlideModule} in an application. Only Applications
 * can include a {@link AppGlideModule}. Libraries must use {@link LibraryGlideModule}.
 *
 * <p>Classes that extend {@link AppGlideModule} must be annotated with
 * {@link com.example.myglide.annotation.GlideModule} to be processed correctly.
 *
 * <p>Classes that extend {@link AppGlideModule} can optionally be annotated with
 * {@link com.example.myglide.annotation.Excludes} to optionally exclude one or more
 * {@link LibraryGlideModule} and/or {@link GlideModule} classes.
 *
 * <p>Once an application has migrated itself and all libraries it depends on to use MyGlide's
 * annotation processor, {@link AppGlideModule} implementations should override
 * {@link #isManifestParsingEnabled()} and return {@code false}.
 */
public abstract class AppGlideModule extends LibraryGlideModule implements AppliesOptions {
  /**
   * Returns {@code true} if MyGlide should check the AndroidManifest for {@link GlideModule}s.
   *
   * <p>Implementations should return {@code false} after they and their dependencies have migrated
   * to MyGlide's annotation processor.
   *
   * <p>Returns {@code true} by default.
   */
  public boolean isManifestParsingEnabled() {
    return true;
  }

  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    // Default empty impl.
  }
}
