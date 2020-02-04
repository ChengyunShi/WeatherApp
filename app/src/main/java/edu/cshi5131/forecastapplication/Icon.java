package edu.cshi5131.forecastapplication;

import java.util.HashMap;
import java.util.Map;

public class Icon {
    public static Map<String, Integer> iconMap = new HashMap<String, Integer>() {{
        put("clear-day", R.drawable.weather_sunny);
        put("clear-night", R.drawable.weather_night);
        put("rain", R.drawable.weather_rainy);
        put("sleet", R.drawable.weather_snowy_rainy);
        put("snow", R.drawable.weather_snowy);
        put("wind", R.drawable.weather_windy_variant);
        put("fog", R.drawable.weather_fog);
        put("cloudy", R.drawable.weather_cloudy);
        put("partly-cloudy-night", R.drawable.weather_night_partly_cloudy);
        put("partly-cloudy-day", R.drawable.weather_partly_cloudy);
    }};

    public static Integer getIcon(String key) {
        if(iconMap.containsKey(key))
            return iconMap.get(key);
        else
            return R.drawable.weather_sunny;
    }

}
