package edu.cshi5131.forecastapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class SummaryFragment extends Fragment {

    private String city;
    private String title;
    private double latitude;
    private double longitude;

    private TextView temperature_text;
    private TextView summary_text;
    private TextView city_text;
    private ImageView weather_icon;
    private TextView humidity_text;
    private TextView wind_speed_text;
    private TextView visibility_text;
    private TextView pressure_text;
    private TableLayout forecast_table;
    private CardView summary_card_view;
    private FloatingActionButton fab;

    private Context context;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        temperature_text = view.findViewById(R.id.temperature_text);
        summary_text = view.findViewById(R.id.summary_text);
        city_text = view.findViewById(R.id.city_text);
        weather_icon = view.findViewById(R.id.weather_icon);
        humidity_text = view.findViewById(R.id.humidity_text);
        wind_speed_text = view.findViewById(R.id.wind_speed_text);
        visibility_text = view.findViewById(R.id.visibility_text);
        pressure_text = view.findViewById(R.id.pressure_text);
        forecast_table = view.findViewById(R.id.forecast_table);
        summary_card_view = view.findViewById(R.id.summary_card_view);
        fab = view.findViewById(R.id.fab);

        context = getActivity();
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Bundle bundle = getArguments();
        city = bundle.getString("city");
        title = bundle.getString("title");
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");

        if(bundle.getBoolean("currentLocation")) {
            fab.hide();
        }

        city_text.setText(title);

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        String forecastUrl = "http://cshi5131-hw9.us-west-1.elasticbeanstalk.com/currentWeather?longitude="+longitude+"&latitude="+latitude;
        Log.i("currentWeathre",forecastUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, forecastUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try{
                            weather_icon.setImageResource(Icon.getIcon(response.getString("icon")));
                            temperature_text.setText(response.getString("temperature")+"°F");
                            summary_text.setText(response.getString("summary"));
                            humidity_text.setText(response.getString("humidity")+"%");
                            wind_speed_text.setText(response.getString("windSpeed")+"mph");
                            visibility_text.setText(response.getString("visibility")+"km");
                            pressure_text.setText(response.getString("pressure")+"mb");

                            final FavoriteOperation favoriteOperation = (FavoriteOperation) getActivity().getApplication();
                            favoriteOperation.setFlag(0);

                            JSONObject daily = (JSONObject) response.get("daily");
                            JSONArray dailyData = daily.getJSONArray("data");
                            for(int i=0; i<dailyData.length(); i++)
                            {
                                JSONObject data = dailyData.getJSONObject(i);
                                long epoch = Long.parseLong(data.getString("time"));
                                Instant instant = Instant.ofEpochSecond(epoch);
                                ZoneId zoneId = ZoneId.of(response.getString("timezone"));
                                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                                String formattedString = zonedDateTime.format(formatter);

                                TableRow tr = new TableRow(getActivity().getApplicationContext());

                                TextView time = new TextView(getActivity().getApplicationContext());
                                time.setText(formattedString);
                                time.setTextColor(Color.WHITE);
                                time.setTextSize(24);
                                time.setPadding(50,50,100,50);

                                ImageView icon = new ImageView(getActivity().getApplicationContext());
                                icon.setImageResource(Icon.getIcon(data.getString("icon")));
                                icon.setScaleX(1.5f);
                                icon.setScaleY(1.5f);
                                icon.setPadding(0,50,0,50);

                                TextView temperatureLow = new TextView(getActivity().getApplicationContext());
                                temperatureLow.setText(data.getString("temperatureLow"));
                                temperatureLow.setTextColor(Color.WHITE);
                                temperatureLow.setTextSize(28);

                                TextView temperatureHigh = new TextView(getActivity().getApplicationContext());
                                temperatureHigh.setText(data.getString("temperatureHigh"));
                                temperatureHigh.setTextColor(Color.WHITE);
                                temperatureHigh.setTextSize(28);

                                tr.addView(time);
                                tr.addView(icon);
                                tr.addView(temperatureLow);
                                tr.addView(temperatureHigh);

                                forecast_table.addView(tr);

                                if(i!=dailyData.length()-1) {
                                    TableRow divider = new TableRow(getActivity().getApplicationContext());
                                    divider.setMinimumHeight(3);
                                    divider.setBackgroundColor(Color.rgb(56,57,69));

                                    forecast_table.addView(divider);
                                }

                                summary_card_view.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity().getApplicationContext(), DetailedWeatherActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("city", city);
                                        bundle.putString("title", title);
                                        bundle.putString("data",response.toString());
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });

                                if(sharedPref.getString(title, null) != null) {
                                    fab.setImageResource(R.drawable.map_marker_minus);
                                }

                                fab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ViewPager viewPager = getActivity().findViewById(R.id.pager);

                                        if(sharedPref.getString(title, null) == null) {
                                            Toast.makeText(getActivity(), title + " was added to favorites",
                                                    Toast.LENGTH_LONG).show();
                                            fab.setImageResource(R.drawable.map_marker_minus);
                                            JSONObject storage = new JSONObject();
                                            Bundle bundle = new Bundle();
                                            try{
                                                storage.put("city",title.split(",")[0]);
                                                bundle.putString("city",title.split(",")[0]);
                                                storage.put("title",title);
                                                bundle.putString("title",title);
                                                storage.put("longitude",longitude);
                                                bundle.putDouble("longitude",longitude);
                                                storage.put("latitude",latitude);
                                                bundle.putDouble("latitude",latitude);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            editor.putString(title,storage.toString());
                                            editor.commit();
                                            Intent intent = new Intent();
                                            intent.putExtra("city", title.split(",")[0]);
                                            favoriteOperation.setFlag(1);
                                            favoriteOperation.setData(bundle);
                                        }
                                        else {
                                            Toast.makeText(getActivity(), title + " was removed from favorites",
                                                    Toast.LENGTH_LONG).show();
                                            fab.setImageResource(R.drawable.map_marker_plus);

                                            editor.remove(title);
                                            editor.commit();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("title",title);
                                            favoriteOperation.setFlag(2);
                                            favoriteOperation.setData(bundle);

                                            if(viewPager != null)
                                            {
                                                int position = viewPager.getCurrentItem();
                                                Log.i("SummaryFragment", "The position is "+position);
                                                SummaryViewPagerAdapter adapter = (SummaryViewPagerAdapter) viewPager.getAdapter();
                                                adapter.removeTab(position);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });

                                getActivity().findViewById(R.id.progress_layout).setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.i("CurrentWeather", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                        Log.e("Volly Error", error.toString());
                    }
                });

        queue.add(jsonObjectRequest);
        return view;
    }

}
