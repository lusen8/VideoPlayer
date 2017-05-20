package com.example.myglide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.example.myglide.load.EncodeStrategy;
import com.example.myglide.load.Options;
import com.example.myglide.load.ResourceEncoder;
import com.example.myglide.load.engine.Resource;
import com.example.myglide.load.engine.bitmap_recycle.BitmapPool;
import java.io.File;

/**
 * Encodes {@link BitmapDrawable}s.
 */
public class BitmapDrawableEncoder implements ResourceEncoder<BitmapDrawable> {

  private final BitmapPool bitmapPool;
  private final ResourceEncoder<Bitmap> encoder;

  public BitmapDrawableEncoder(BitmapPool bitmapPool, ResourceEncoder<Bitmap> encoder) {
    this.bitmapPool = bitmapPool;
    this.encoder = encoder;
  }

  @Override
  public boolean encode(Resource<BitmapDrawable> data, File file, Options options) {
    return encoder.encode(new BitmapResource(data.get().getBitmap(), bitmapPool), file, options);
  }

  @Override
  public EncodeStrategy getEncodeStrategy(Options options) {
    return encoder.getEncodeStrategy(options);
  }
}
