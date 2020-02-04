package edu.cshi5131.forecastapplication;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.SearchView;

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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String city;
    String state;
    double latitude;
    double longitude;

    private LinearLayout progress_layout;

    private ViewPager mViewPager;
    private SummaryViewPagerAdapter mSummaryViewPagerAdapter;
    private TabLayout mTabLayout;

    private List<Bundle> mBundleList;

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 100;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;

    private Context context;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBundleList = new ArrayList<>();

        progress_layout = findViewById(R.id.progress_layout);
        progress_layout.setVisibility(View.VISIBLE);

        context = getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://ip-api.com/json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            city = response.getString("city");
                            state = response.getString("region");
                            latitude = response.getDouble("lat");
                            longitude = response.getDouble("lon");

                            Bundle bundle = new Bundle();
                            bundle.putString("city",city);
                            bundle.putString("title",city+" ,"+state+", USA");
                            bundle.putBoolean("currentLocation",true);
                            bundle.putDouble("latitude",latitude);
                            bundle.putDouble("longitude",longitude);
                            mBundleList.add(bundle);

//                            mBundleList.addAll(new ArrayList(LocalStorage.getInstance(getApplicationContext()).getAll()));

                            Map<String, String> map = (Map<String, String>)sharedPref.getAll();
                            List<Bundle> bundleList = new ArrayList<>();
                            for(String key: map.keySet())
                            {
                                try{
                                    JSONObject jsonObject = new JSONObject(map.get(key));
                                    Bundle b = new Bundle();
                                    try{
                                        b.putString("city",jsonObject.getString("city"));
                                        b.putString("title",jsonObject.getString("title"));
                                        b.putBoolean("currentLocation",false);
                                        b.putDouble("latitude",jsonObject.getDouble("latitude"));
                                        b.putDouble("longitude",jsonObject.getDouble("longitude"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    bundleList.add(b);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            mBundleList.addAll(bundleList);
                            mViewPager = findViewById(R.id.pager);
                            mViewPager.setOffscreenPageLimit(10);
                            mSummaryViewPagerAdapter = new SummaryViewPagerAdapter(getSupportFragmentManager(), mBundleList, getApplicationContext());
                            mViewPager.setAdapter(mSummaryViewPagerAdapter);
                            mTabLayout = findViewById(R.id.tabDots);
                            mTabLayout.setupWithViewPager(mViewPager, true);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("Volly Error", error.toString());
                    }
                });

        queue.add(jsonObjectRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageDrawable(null);

        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setBackgroundColor(Color.rgb(30,30,30));
        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setDropDownBackgroundResource(R.color.colorText);
        searchAutoComplete.setLinkTextColor(Color.BLACK);

        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setAdapter(autoSuggestAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
            }
        });

        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                        String url = "http://cshi5131-hw9.us-west-1.elasticbeanstalk.com/autocompleteCities?input=";
                        url += searchAutoComplete.getText().toString();
                        url = url.replaceAll(" ","%20");

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONArray cities = response.getJSONArray("description");
                                            List<String> stringList = new ArrayList<>();
                                            for(int i=0; i<cities.length(); i++)
                                            {
                                                stringList.add(cities.getString(i));
                                            }
                                            autoSuggestAdapter.setData(stringList);
                                            autoSuggestAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error
                                        Log.e("Volley Error", error.toString());
                                    }
                                });
                        queue.add(jsonObjectRequest);
                    }
                }
                return false;
            }
        });

        ComponentName componentName = new ComponentName(MainActivity.this, SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FavoriteOperation favoriteOperation = (FavoriteOperation) MainActivity.this.getApplication();
        if(favoriteOperation.getFlag()==1)
        {
            mTabLayout.addTab(mTabLayout.newTab());
            mSummaryViewPagerAdapter.addTab(favoriteOperation.getData());
        }
        else if(favoriteOperation.getFlag()==2)
        {
            int position = 0;
            Bundle bundle = favoriteOperation.getData();
            String title = bundle.getString("title");
            for(int i=0; i<mBundleList.size(); i++)
            {
                if(mBundleList.get(i).getString("title").equals(title)) {
                    position = i;
                    mTabLayout.removeTabAt(position);
                    mSummaryViewPagerAdapter.removeTab(position);
                    break;
                }
            }
        }
    }
}
