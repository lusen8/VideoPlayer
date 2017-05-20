package com.example.myglide.load.resource.transcode;

import com.example.myglide.load.engine.Resource;
import com.example.myglide.load.resource.bytes.BytesResource;
import com.example.myglide.load.resource.gif.GifDrawable;
import com.example.myglide.util.ByteBufferUtil;
import java.nio.ByteBuffer;

/**
 * An {@link com.example.myglide.load.resource.transcode.ResourceTranscoder} that converts {@link
 * com.example.myglide.load.resource.gif.GifDrawable} into bytes by obtaining the original bytes of
 * the GIF from the {@link com.example.myglide.load.resource.gif.GifDrawable}.
 */
public class GifDrawableBytesTranscoder implements ResourceTranscoder<GifDrawable, byte[]> {
  @Override
  public Resource<byte[]> transcode(Resource<GifDrawable> toTranscode) {
    GifDrawable gifData = toTranscode.get();
    ByteBuffer byteBuffer = gifData.getBuffer();
    return new BytesResource(ByteBufferUtil.toBytes(byteBuffer));
  }
}
