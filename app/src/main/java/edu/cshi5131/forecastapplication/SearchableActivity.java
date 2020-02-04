package edu.cshi5131.forecastapplication;

import android.app.SearchManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class SearchableActivity extends AppCompatActivity {

    private double latitude;
    private double longitude;

    private Toolbar toolbar;
    private LinearLayout progress_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        toolbar = findViewById(R.id.toolbar);
        progress_layout = findViewById(R.id.progress_layout);
        progress_layout.setVisibility(View.VISIBLE);

        final Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);

            toolbar.setTitle(query);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://cshi5131-hw9.us-west-1.elasticbeanstalk.com/getLocation?address=";
            url += query;
            url = url.replaceAll(" ", "%20");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("GoolgeApi", response.toString());
                            try {
                                latitude = response.getDouble("latitude");
                                longitude = response.getDouble("longitude");

                                SummaryFragment fragment = new SummaryFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("city",query.split(",")[0]);
                                bundle.putString("title",query);
                                bundle.putDouble("latitude",latitude);
                                bundle.putDouble("longitude",longitude);
                                fragment.setArguments(bundle);

                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.summary_fragment, fragment);
                                ft.commit();

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
