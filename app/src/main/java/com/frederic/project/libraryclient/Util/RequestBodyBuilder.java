package com.frederic.project.libraryclient.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fk101 on 2017/09/13.
 */

public class RequestBodyBuilder {

    private JSONObject object = new JSONObject();

    public void addValue(String key,int value) throws JSONException {
        object.put(key,value);
    }

    public void addValue(String key,double value) throws JSONException {
        object.put(key, value);
    }

    public void addValue(String key,String value) throws JSONException {
        object.put(key, value);
    }

    public void addValue(String key, long value) throws JSONException {
        object.put(key, value);
    }

    public String build(){
        return object.toString();
    }
}
