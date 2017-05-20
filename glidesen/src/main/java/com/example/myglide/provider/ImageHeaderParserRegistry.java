package com.example.myglide.provider;

import com.example.myglide.load.ImageHeaderParser;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains an unordered list of {@link ImageHeaderParser}s capable of parsing image headers.
 */
public final class ImageHeaderParserRegistry {
  private final List<ImageHeaderParser> parsers = new ArrayList<>();

  public synchronized List<ImageHeaderParser> getParsers() {
    return parsers;
  }

  public synchronized void add(ImageHeaderParser parser) {
    parsers.add(parser);
  }
}
