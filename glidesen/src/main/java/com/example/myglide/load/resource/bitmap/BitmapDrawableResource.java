package com.example.myglide.load.resource.bitmap;

import android.graphics.drawable.BitmapDrawable;
import com.example.myglide.load.engine.Initializable;
import com.example.myglide.load.engine.bitmap_recycle.BitmapPool;
import com.example.myglide.load.resource.drawable.DrawableResource;
import com.example.myglide.util.Util;

/**
 * A {@link com.example.myglide.load.engine.Resource} that wraps an
 * {@link BitmapDrawable}
 *
 * <p> This class ensures that every call to {@link #get()}} always returns a new
 * {@link BitmapDrawable} to avoid rendering issues if used in multiple
 * views and is also responsible for returning the underlying {@link android.graphics.Bitmap} to the
 * given {@link com.example.myglide.load.engine.bitmap_recycle.BitmapPool} when the resource is
 * recycled. </p>
 */
public class BitmapDrawableResource extends DrawableResource<BitmapDrawable>
    implements Initializable {
  private final BitmapPool bitmapPool;

  public BitmapDrawableResource(BitmapDrawable drawable, BitmapPool bitmapPool) {
    super(drawable);
    this.bitmapPool = bitmapPool;
  }

  @Override
  public Class<BitmapDrawable> getResourceClass() {
    return BitmapDrawable.class;
  }

  @Override
  public int getSize() {
    return Util.getBitmapByteSize(drawable.getBitmap());
  }

  @Override
  public void recycle() {
    bitmapPool.put(drawable.getBitmap());
  }

  @Override
  public void initialize() {
    drawable.getBitmap().prepareToDraw();
  }
}
