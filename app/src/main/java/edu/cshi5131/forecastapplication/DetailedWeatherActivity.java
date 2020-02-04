package edu.cshi5131.forecastapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailedWeatherActivity extends AppCompatActivity {

    private String title;
    private int temperature;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout progress_layout;

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_weather);

        Intent intent = getIntent();
        bundle = intent.getExtras();

        title = bundle.getString("title");
        try{
            JSONObject data = new JSONObject(bundle.getString("data"));
            temperature = data.getInt("temperature");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        progress_layout = findViewById(R.id.progress_layout);
        progress_layout.setVisibility(View.VISIBLE);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String url = "http://cshi5131-hw9.us-west-1.elasticbeanstalk.com/getPhotos?city=";
        url += bundle.getString("city");
        url = url.replaceAll(" ","%20");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray photos = response.getJSONArray("photos");
                            ArrayList<String> mImages = new ArrayList<>();

                            for(int i=0; i<photos.length(); i++)
                            {
                                mImages.add(photos.getString(i));
                            }

                            bundle.putStringArrayList("photo", mImages);

                            viewPager = findViewById(R.id.viewpager);
                            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), bundle);
                            viewPager.setAdapter(adapter);

                            tabLayout = findViewById(R.id.tabs);
                            tabLayout.setupWithViewPager(viewPager);
                            tabLayout.getTabAt(0).setIcon(R.drawable.calendar_today);
                            tabLayout.getTabAt(1).setIcon(R.drawable.trending_up);
                            tabLayout.getTabAt(2).setIcon(R.drawable.google_photos);

                            tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);;

                            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    tab.getIcon().mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                                }

                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {
                                    tab.getIcon().setColorFilter(Color.parseColor("#A4A4A4"), PorterDuff.Mode.SRC_IN);
                                }

                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {
                                    tab.getIcon().mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volly Error", error.toString());
                    }
                });

        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_twitter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_twitter:
                String url = "https://twitter.com/intent/tweet?text=Check Out "+title+"’s Weather! It is "+temperature+"°F! &hashtags=CSCI571WeatherSearch";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
