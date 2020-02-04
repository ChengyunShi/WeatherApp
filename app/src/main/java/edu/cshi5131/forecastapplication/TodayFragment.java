package edu.cshi5131.forecastapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class TodayFragment extends Fragment {

    private static DecimalFormat df = new DecimalFormat("0.00");

    public TodayFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        try {
            JSONObject data = new JSONObject(bundle.getString("data"));

            Log.i("TodayFragment",data.toString());

            TextView wind_speed_text = view.findViewById(R.id.wind_speed_text);
            wind_speed_text.setText(data.getString("windSpeed")+" mph");

            TextView pressure_text = view.findViewById(R.id.pressure_text);
            pressure_text.setText(data.getString("pressure")+" mb");

            TextView precipitation_text = view.findViewById(R.id.precipitation_text);
            precipitation_text.setText(data.getString("precipitation")+" mmph");

            TextView temperature_text = view.findViewById(R.id.temperature_text);
            temperature_text.setText(data.getInt("temperature")+"°F");

            TextView humidity_text = view.findViewById(R.id.humidity_text);
            humidity_text.setText(data.getInt("humidity")+"%");

            TextView visibility_text = view.findViewById(R.id.visibility_text);
            visibility_text.setText(data.getString("visibility")+" km");

            TextView cloud_cover_text = view.findViewById(R.id.cloud_cover_text);
            cloud_cover_text.setText(data.getInt("cloudCover")+"%");

            TextView ozone_text = view.findViewById(R.id.ozone_text);
            ozone_text.setText(data.getString("ozone")+" DU");

            TextView summary_text = view.findViewById(R.id.summary_text);
            summary_text.setText(data.getString("icon").replaceAll("-"," "));

            ImageView summary_icon = view.findViewById(R.id.summary_icon);
            summary_icon.setImageResource(Icon.iconMap.get(data.getString("icon")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}