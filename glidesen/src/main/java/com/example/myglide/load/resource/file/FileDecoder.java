package com.example.myglide.load.resource.file;

import com.example.myglide.load.Options;
import com.example.myglide.load.ResourceDecoder;
import com.example.myglide.load.engine.Resource;
import java.io.File;

/**
 * A simple {@link com.example.myglide.load.ResourceDecoder} that creates resource for a given {@link
 * File}.
 */
public class FileDecoder implements ResourceDecoder<File, File> {

  @Override
  public boolean handles(File source, Options options) {
    return true;
  }

  @Override
  public Resource<File> decode(File source, int width, int height, Options options) {
    return  new FileResource(source);
  }
}
