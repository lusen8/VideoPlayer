package com.example.mygson.internal.bind;

/**
 * Created by lusen on 2017/4/17.
 */

import com.example.mygson.JsonSyntaxException;
import com.example.mygson.MyGson;
import com.example.mygson.TypeAdapter;
import com.example.mygson.TypeAdapterFactory;
import com.example.mygson.reflect.TypeToken;
import com.example.mygson.stream.JsonReader;
import com.example.mygson.stream.JsonToken;
import com.example.mygson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Adapter for Time. Although this class appears stateless, it is not.
 * DateFormat captures its time zone and locale when it is created, which gives
 * this class state. DateFormat isn't thread safe either, so this class has
 * to synchronize its read and write methods.
 */
public final class TimeTypeAdapter extends TypeAdapter<Time> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
        @Override public <T> TypeAdapter<T> create(MyGson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == Time.class ? (TypeAdapter<T>) new TimeTypeAdapter() : null;
        }
    };

    private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");

    @Override public synchronized Time read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        try {
            Date date = format.parse(in.nextString());
            return new Time(date.getTime());
        } catch (ParseException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override public synchronized void write(JsonWriter out, Time value) throws IOException {
        out.value(value == null ? null : format.format(value));
    }
}
