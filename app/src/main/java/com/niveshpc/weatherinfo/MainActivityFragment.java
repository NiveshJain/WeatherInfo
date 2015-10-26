package com.niveshpc.weatherinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String [] forecastArray =  {"Mon 6/23-Sunny-31/17",
                "Tue 6/24-Foggy-21/8",
                "Wed 6/25-Cloudy-22/17",
                "Thurs 6/26-Rainy-18/11",
        "Fri 6/27-Foggy-21/10",
        "Sat 6/28-Trapped In WeatherStation-23/18",
        "Sun 6/29-Sunny-20/7"};

        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));

        ListView listView =   (ListView) rootView.findViewById(R.id.list_view_forecast);

        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_view_item_forecast,R.id.text_view_forecast,weekForecast);


       listView.setAdapter(mForecastAdapter);

        return rootView ;
    }


}
