package com.example.myglide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.example.myglide.MyGlide;
import com.example.myglide.load.Transformation;
import com.example.myglide.load.engine.Resource;
import com.example.myglide.load.engine.bitmap_recycle.BitmapPool;
import com.example.myglide.util.Preconditions;
import java.security.MessageDigest;

/**
 * Transforms {@link BitmapDrawable}s.
 */
public class BitmapDrawableTransformation implements Transformation<BitmapDrawable> {

  private final Transformation<Bitmap> wrapped;

  public BitmapDrawableTransformation(Transformation<Bitmap> wrapped) {
    this.wrapped = Preconditions.checkNotNull(wrapped);
  }

  /**
   * @deprecated use {@link #BitmapDrawableTransformation(Transformation)}}
   */
  @Deprecated
  public BitmapDrawableTransformation(
      @SuppressWarnings("unused") Context context, Transformation<Bitmap> wrapped) {
    this(wrapped);
  }

  /**
   * @deprecated use {@link #BitmapDrawableTransformation(Transformation)}}
   */
  @Deprecated
  public BitmapDrawableTransformation(
      @SuppressWarnings("unused") Context context,
      @SuppressWarnings("unused") BitmapPool bitmapPool,
      Transformation<Bitmap> wrapped) {
    this(wrapped);
  }

  @Override
  public Resource<BitmapDrawable> transform(
      Context context, Resource<BitmapDrawable> drawableResourceToTransform, int outWidth,
      int outHeight) {
    BitmapDrawable drawableToTransform = drawableResourceToTransform.get();
    Bitmap bitmapToTransform = drawableToTransform.getBitmap();

    BitmapPool bitmapPool = MyGlide.get(context).getBitmapPool();
    BitmapResource bitmapResourceToTransform = BitmapResource.obtain(bitmapToTransform, bitmapPool);
    Resource<Bitmap> transformedBitmapResource =
        wrapped.transform(context, bitmapResourceToTransform, outWidth, outHeight);

    if (transformedBitmapResource.equals(bitmapResourceToTransform)) {
      return drawableResourceToTransform;
    } else {
      return LazyBitmapDrawableResource.obtain(context, transformedBitmapResource.get());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BitmapDrawableTransformation) {
      BitmapDrawableTransformation other = (BitmapDrawableTransformation) o;
      return wrapped.equals(other.wrapped);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return wrapped.hashCode();
  }

  @Override
  public void updateDiskCacheKey(MessageDigest messageDigest) {
    wrapped.updateDiskCacheKey(messageDigest);
  }
}
