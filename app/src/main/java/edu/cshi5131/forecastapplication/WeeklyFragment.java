package edu.cshi5131.forecastapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeeklyFragment extends Fragment {

    public WeeklyFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_weekly, container, false);
        ImageView weekly_icon = view.findViewById(R.id.weekly_icon);
        TextView weekly_text = view.findViewById(R.id.weekly_text);
        LineChart chart = view.findViewById(R.id.chart);
        Bundle bundle = getArguments();

        try {
            JSONObject data = new JSONObject(bundle.getString("data"));
            JSONObject daily = data.getJSONObject("daily");
            JSONArray dailyData = daily.getJSONArray("data");
            weekly_icon.setImageResource(Icon.iconMap.get(daily.getString("icon")));
            weekly_text.setText(daily.getString("summary"));

            List<Entry> entriesLow = new ArrayList<>();
            List<Entry> entriesHigh = new ArrayList<>();
            for (int i=0; i<dailyData.length(); i++) {
                entriesLow.add(new Entry(i, Math.round(dailyData.getJSONObject(i).getDouble("temperatureLow"))));
                entriesHigh.add(new Entry(i, Math.round(dailyData.getJSONObject(i).getDouble("temperatureHigh"))));
            }
            LineDataSet setCompLow = new LineDataSet(entriesLow, "Minimum Temperature");
            setCompLow.setColor(Color.rgb(187,134,252));
            setCompLow.setCircleColor(R.color.colorText);

            LineDataSet setComHigh = new LineDataSet(entriesHigh,"Maximum Teperature");
            setComHigh.setColor(Color.rgb(250,171,26));
            setComHigh.setCircleColor(R.color.colorText);

            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(setCompLow);
            dataSets.add(setComHigh);

            Legend legend = chart.getLegend();
            legend.setFormSize(15f);
            legend.setForm(Legend.LegendForm.SQUARE);
            legend.setTextColor(Color.WHITE);
            legend.setTextSize(15f);
            legend.setEnabled(true);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            xAxis.setTextSize(12f);
            xAxis.setTextColor(Color.WHITE);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawGridLines(false);

            YAxis left = chart.getAxisLeft();
            left.setTextSize(12f);
            left.setTextColor(Color.WHITE);
            left.setDrawAxisLine(true);
            left.setDrawGridLines(true);

            YAxis right = chart.getAxisRight();
            right.setTextSize(12f);
            right.setTextColor(Color.WHITE);
            right.setDrawAxisLine(true);
            right.setDrawGridLines(true);

            LineData lineData = new LineData(dataSets);
            chart.setData(lineData);
            chart.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}
