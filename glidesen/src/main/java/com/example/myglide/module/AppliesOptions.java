package com.example.myglide.module;

import android.content.Context;
import com.example.myglide.GlideBuilder;

/**
 * An internal interface, to be removed when {@link GlideModule}s are removed.
 */
interface AppliesOptions {
  /**
   * Lazily apply options to a {@link com.example.myglide.GlideBuilder} immediately before the MyGlide
   * singleton is created.
   *
   * <p> This method will be called once and only once per implementation. </p>
   *
   * @param context An Application {@link Context}.
   * @param builder The {@link com.example.myglide.GlideBuilder} that will be used to create MyGlide.
   */
  void applyOptions(Context context, GlideBuilder builder);
}
