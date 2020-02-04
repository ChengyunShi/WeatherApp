package edu.cshi5131.forecastapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalStorage {

    private static LocalStorage localStorage = new LocalStorage();
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private LocalStorage() {}

    public static LocalStorage getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("data", Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return localStorage;
    }

    public void save(String key, String value) {

        editor.putString(key,value);
        editor.apply();
    }

    public String get(String key) {

        return sharedPreferences.getString(key,null);
    }

    public SharedPreferences getSharedPreferences() {return sharedPreferences;}

    public void remove(String key) {

        editor.remove(key);
        editor.commit();

    }

    public List<Bundle> getAll() {
        Map<String,String> localStorage = (Map<String,String>) sharedPreferences.getAll();
        List<Bundle> bundleList = new ArrayList<>();
        for(String key: localStorage.keySet())
        {
            try{
                JSONObject jsonObject = new JSONObject(localStorage.get(key));
                Bundle bundle = new Bundle();
                try{
                    bundle.putString("city",jsonObject.getString("city"));
                    bundle.putString("title",jsonObject.getString("title"));
                    bundle.putBoolean("currentLocation",false);
                    bundle.putDouble("latitude",jsonObject.getDouble("latitude"));
                    bundle.putDouble("longitude",jsonObject.getDouble("longitude"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bundleList.add(bundle);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return bundleList;
    }

}
