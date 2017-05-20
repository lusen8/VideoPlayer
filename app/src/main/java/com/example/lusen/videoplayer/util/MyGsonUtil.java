package com.example.lusen.videoplayer.util;

import com.example.mygson.JsonArray;
import com.example.mygson.JsonElement;
import com.example.mygson.JsonParser;
import com.example.mygson.MyGson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lusen on 2017/2/14.
 */

public class MyGsonUtil {
    //将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        MyGson gson = new MyGson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }
//    坑爹解析。。。死坑
//    public static <T> List<T> parseJsonArrayWithGson(String jsonData, Class<T> type) {
//        Gson gson = new Gson();
//        List<T> result = gson.fromJson(jsonData, new TypeToken<List<T>>() {
//        }.getType());
//        return result;
//    }

    public static <T> List<T> getObjectList(String jsonString, Class<T> cls){
        List<T> list = new ArrayList<T>();
        try {
            MyGson gson = new MyGson();
            JsonArray arry = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}

