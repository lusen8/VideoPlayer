package com.example.myglide;

/**
 * Created by lusen on 2017/5/3.
 */

import android.annotation.TargetApi;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import com.example.myglide.load.engine.Engine;
import com.example.myglide.request.RequestOptions;
import com.example.myglide.request.target.ImageViewTargetFactory;
import com.example.myglide.request.target.Target;

/**
 * Global context for all loads in MyGlide containing and exposing the various registries and classes
 * required to load resources.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class GlideContext extends ContextWrapper implements ComponentCallbacks2 {
    private final Handler mainHandler;
    private final Registry registry;
    private final ImageViewTargetFactory imageViewTargetFactory;
    private final RequestOptions defaultRequestOptions;
    private final Engine engine;
    private final ComponentCallbacks2 componentCallbacks;
    private final int logLevel;

    public GlideContext(Context context, Registry registry,
                        ImageViewTargetFactory imageViewTargetFactory, RequestOptions defaultRequestOptions,
                        Engine engine, ComponentCallbacks2 componentCallbacks, int logLevel) {
        super(context.getApplicationContext());
        this.registry = registry;
        this.imageViewTargetFactory = imageViewTargetFactory;
        this.defaultRequestOptions = defaultRequestOptions;
        this.engine = engine;
        this.componentCallbacks = componentCallbacks;
        this.logLevel = logLevel;

        mainHandler = new Handler(Looper.getMainLooper());
    }

    public RequestOptions getDefaultRequestOptions() {
        return defaultRequestOptions;
    }

    public <X> Target<X> buildImageViewTarget(ImageView imageView, Class<X> transcodeClass) {
        return imageViewTargetFactory.buildTarget(imageView, transcodeClass);
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    public Engine getEngine() {
        return engine;
    }

    public Registry getRegistry() {
        return registry;
    }

    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void onTrimMemory(int level) {
        componentCallbacks.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        componentCallbacks.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        componentCallbacks.onLowMemory();
    }
}

