package com.example.myglide.load.resource.file;

import com.example.myglide.load.resource.SimpleResource;
import java.io.File;

/**
 * A simple {@link com.example.myglide.load.engine.Resource} that wraps a {@link File}.
 */
public class FileResource extends SimpleResource<File> {
  public FileResource(File file) {
    super(file);
  }
}
