package com.example.myglide;

/**
 * Created by lusen on 2017/5/3.
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.myglide.load.DecodeFormat;
import com.example.myglide.load.engine.Engine;
import com.example.myglide.load.engine.bitmap_recycle.ArrayPool;
import com.example.myglide.load.engine.bitmap_recycle.BitmapPool;
import com.example.myglide.load.engine.bitmap_recycle.LruArrayPool;
import com.example.myglide.load.engine.bitmap_recycle.LruBitmapPool;
import com.example.myglide.load.engine.cache.DiskCache;
import com.example.myglide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.example.myglide.load.engine.cache.LruResourceCache;
import com.example.myglide.load.engine.cache.MemoryCache;
import com.example.myglide.load.engine.cache.MemorySizeCalculator;
import com.example.myglide.load.engine.executor.GlideExecutor;
import com.example.myglide.manager.ConnectivityMonitorFactory;
import com.example.myglide.manager.DefaultConnectivityMonitorFactory;
import com.example.myglide.manager.RequestManagerRetriever;
import com.example.myglide.manager.RequestManagerRetriever.RequestManagerFactory;
import com.example.myglide.request.RequestOptions;

/**
 * A builder class for setting default structural classes for MyGlide to use.
 */
public final class GlideBuilder {
    private Engine engine;
    private BitmapPool bitmapPool;
    private ArrayPool arrayPool;
    private MemoryCache memoryCache;
    private GlideExecutor sourceExecutor;
    private GlideExecutor diskCacheExecutor;
    private DiskCache.Factory diskCacheFactory;
    private MemorySizeCalculator memorySizeCalculator;
    private ConnectivityMonitorFactory connectivityMonitorFactory;
    private int logLevel = Log.INFO;
    private RequestOptions defaultRequestOptions = new RequestOptions();
    @Nullable
    private RequestManagerFactory requestManagerFactory;

    /**
     * Sets the {@link com.example.myglide.load.engine.bitmap_recycle.BitmapPool} implementation to use
     * to store and retrieve reused {@link android.graphics.Bitmap}s.
     *
     * @param bitmapPool The pool to use.
     * @return This builder.
     */
    public GlideBuilder setBitmapPool(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
        return this;
    }

    /**
     * Sets the {@link ArrayPool} implementation to allow variable sized arrays to be stored
     * and retrieved as needed.
     *
     * @param arrayPool The pool to use.
     * @return This builder.
     */
    public GlideBuilder setArrayPool(ArrayPool arrayPool) {
        this.arrayPool = arrayPool;
        return this;
    }

    /**
     * Sets the {@link com.example.myglide.load.engine.cache.MemoryCache} implementation to store
     * {@link com.example.myglide.load.engine.Resource}s that are not currently in use.
     *
     * @param memoryCache The cache to use.
     * @return This builder.
     */
    public GlideBuilder setMemoryCache(MemoryCache memoryCache) {
        this.memoryCache = memoryCache;
        return this;
    }

    /**
     * Sets the {@link com.example.myglide.load.engine.cache.DiskCache} implementation to use to store
     * {@link com.example.myglide.load.engine.Resource} data and thumbnails.
     *
     * @param diskCache The disk cache to use.
     * @return This builder.
     * @deprecated Creating a disk cache directory on the main thread causes strict mode violations,
     * use {@link #setDiskCache(com.example.myglide.load.engine.cache.DiskCache.Factory)} instead.
     * Scheduled to be removed in MyGlide 4.0.
     */
    @Deprecated
    public GlideBuilder setDiskCache(final DiskCache diskCache) {
        return setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                return diskCache;
            }
        });
    }

    /**
     * Sets the {@link com.example.myglide.load.engine.cache.DiskCache.Factory} implementation to use
     * to construct the {@link com.example.myglide.load.engine.cache.DiskCache} to use to store {@link
     * com.example.myglide.load.engine.Resource} data on disk.
     *
     * @param diskCacheFactory The disk cache factory to use.
     * @return This builder.
     */
    public GlideBuilder setDiskCache(DiskCache.Factory diskCacheFactory) {
        this.diskCacheFactory = diskCacheFactory;
        return this;
    }

    /**
     * Sets the {@link java.util.concurrent.ExecutorService} implementation to use when retrieving
     * {@link com.example.myglide.load.engine.Resource}s that are not already in the cache.
     *
     * <p> Any implementation must order requests based on their {@link com.example.myglide.Priority}
     * for thumbnail requests to work properly.
     *
     * @param service The ExecutorService to use.
     * @return This builder.
     * @see #setDiskCacheExecutor(GlideExecutor)
     * @see GlideExecutor
     */
    public GlideBuilder setResizeExecutor(GlideExecutor service) {
        this.sourceExecutor = service;
        return this;
    }

    /**
     * Sets the {@link java.util.concurrent.ExecutorService} implementation to use when retrieving
     * {@link com.example.myglide.load.engine.Resource}s that are currently in cache.
     *
     * <p> Any implementation must order requests based on their {@link com.example.myglide.Priority}
     * for thumbnail requests to work properly.
     *
     * @param service The ExecutorService to use.
     * @return This builder.
     * @see #setResizeExecutor(GlideExecutor)
     * @see GlideExecutor
     */
    public GlideBuilder setDiskCacheExecutor(GlideExecutor service) {
        this.diskCacheExecutor = service;
        return this;
    }

    /**
     * Sets the default {@link RequestOptions} to use for all loads across the app.
     *
     * <p>Applying additional options with {@link
     * RequestBuilder#apply(com.example.myglide.request.BaseRequestOptions)} will override defaults
     * set here.
     *
     * @param requestOptions The options to use by default.
     * @return This builder.
     */
    public GlideBuilder setDefaultRequestOptions(RequestOptions requestOptions) {
        this.defaultRequestOptions = requestOptions;
        return this;
    }

    /**
     * Sets the {@link com.example.myglide.load.DecodeFormat} that will be the default format for all
     * the default decoders that can change the {@link android.graphics.Bitmap.Config} of the {@link
     * android.graphics.Bitmap}s they decode.
     *
     * <p> Decode format is always a suggestion, not a requirement. See {@link
     * com.example.myglide.load.DecodeFormat} for more details. </p>
     *
     * @param decodeFormat The format to use.
     * @return This builder.
     *
     * @deprecated Use {@link #setDefaultRequestOptions(RequestOptions)} instead.
     */
    @Deprecated
    public GlideBuilder setDecodeFormat(DecodeFormat decodeFormat) {
        defaultRequestOptions.apply(new RequestOptions().format(decodeFormat));
        return this;
    }

    /**
     * Sets the {@link MemorySizeCalculator} to use to calculate maximum sizes for default
     * {@link MemoryCache MemoryCaches} and/or default {@link BitmapPool BitmapPools}.
     *
     * @see #setMemorySizeCalculator(MemorySizeCalculator)
     *
     * @param builder The builder to use (will not be modified).
     * @return This builder.
     */
    public GlideBuilder setMemorySizeCalculator(MemorySizeCalculator.Builder builder) {
        return setMemorySizeCalculator(builder.build());
    }

    /**
     * Sets the {@link MemorySizeCalculator} to use to calculate maximum sizes for default
     * {@link MemoryCache MemoryCaches} and/or default {@link BitmapPool BitmapPools}.
     *
     * <p>The given {@link MemorySizeCalculator} will not affect custom pools or caches provided
     * via {@link #setBitmapPool(BitmapPool)} or {@link #setMemoryCache(MemoryCache)}.
     *
     * @param calculator The calculator to use.
     * @return This builder.
     */
    public GlideBuilder setMemorySizeCalculator(MemorySizeCalculator calculator) {
        this.memorySizeCalculator = calculator;
        return this;
    }

    /**
     * Sets the {@link com.example.myglide.manager.ConnectivityMonitorFactory}
     * to use to notify {@link com.example.myglide.RequestManager} of connectivity events.
     * If not set {@link com.example.myglide.manager.DefaultConnectivityMonitorFactory} would be used.
     *
     * @param factory The factory to use
     * @return This builder.
     */
    public GlideBuilder setConnectivityMonitorFactory(ConnectivityMonitorFactory factory) {
        this.connectivityMonitorFactory = factory;
        return this;
    }

    /**
     * Sets a log level constant from those in {@link Log} to indicate the desired log verbosity.
     *
     * <p>The level must be one of {@link Log#VERBOSE}, {@link Log#DEBUG}, {@link Log#INFO},
     * {@link Log#WARN}, or {@link Log#ERROR}.
     *
     * <p>{@link Log#VERBOSE} means one or more lines will be logged per request, including
     * timing logs and failures. {@link Log#DEBUG} means at most one line will be logged
     * per successful request, including timing logs, although many lines may be logged for
     * failures including multiple complete stack traces. {@link Log#INFO} means
     * failed loads will be logged including multiple complete stack traces, but successful loads
     * will not be logged at all. {@link Log#WARN} means only summaries of failed loads will be
     * logged. {@link Log#ERROR} means only exceptional cases will be logged.
     *
     * <p>All logs will be logged using the 'MyGlide' tag.
     *
     * <p>Many other debugging logs are available in individual classes. The log level supplied here
     * only controls a small set of informative and well formatted logs. Users wishing to debug
     * certain aspects of the library can look for individual <code>TAG</code> variables at the tops
     * of classes and use <code>adb shell setprop log.tag.TAG</code> to enable or disable any relevant
     * tags.
     *
     * @param logLevel The log level to use from {@link Log}.
     * @return This builder.
     */
    public GlideBuilder setLogLevel(int logLevel) {
        if (logLevel < Log.VERBOSE || logLevel > Log.ERROR) {
            throw new IllegalArgumentException("Log level must be one of Log.VERBOSE, Log.DEBUG,"
                    + " Log.INFO, Log.WARN, or Log.ERROR");
        }
        this.logLevel = logLevel;
        return this;
    }

    GlideBuilder setRequestManagerFactory(
            @Nullable RequestManagerRetriever.RequestManagerFactory factory) {
        this.requestManagerFactory = factory;
        return this;
    }

    // For testing.
    GlideBuilder setEngine(Engine engine) {
        this.engine = engine;
        return this;
    }

    public MyGlide build(Context context) {
        if (sourceExecutor == null) {
            sourceExecutor = GlideExecutor.newSourceExecutor();
        }

        if (diskCacheExecutor == null) {
            diskCacheExecutor = GlideExecutor.newDiskCacheExecutor();
        }

        if (memorySizeCalculator == null) {
            memorySizeCalculator = new MemorySizeCalculator.Builder(context).build();
        }

        if (connectivityMonitorFactory == null) {
            connectivityMonitorFactory = new DefaultConnectivityMonitorFactory();
        }

        if (bitmapPool == null) {
            int size = memorySizeCalculator.getBitmapPoolSize();
            bitmapPool = new LruBitmapPool(size);
        }

        if (arrayPool == null) {
            arrayPool = new LruArrayPool(memorySizeCalculator.getArrayPoolSizeInBytes());
        }

        if (memoryCache == null) {
            memoryCache = new LruResourceCache(memorySizeCalculator.getMemoryCacheSize());
        }

        if (diskCacheFactory == null) {
            diskCacheFactory = new InternalCacheDiskCacheFactory(context);
        }

        if (engine == null) {
            engine = new Engine(memoryCache, diskCacheFactory, diskCacheExecutor, sourceExecutor,
                    GlideExecutor.newUnlimitedSourceExecutor());
        }

        RequestManagerRetriever requestManagerRetriever = new RequestManagerRetriever(
                requestManagerFactory);

        return new MyGlide(
                context,
                engine,
                memoryCache,
                bitmapPool,
                arrayPool,
                requestManagerRetriever,
                connectivityMonitorFactory,
                logLevel,
                defaultRequestOptions.lock());
    }
}