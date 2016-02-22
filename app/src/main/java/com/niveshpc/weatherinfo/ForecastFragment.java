package com.niveshpc.weatherinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

   private  ArrayAdapter<String> mForecastAdapter;

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Log.d("***************", "***********************");
            //updateWeather sets the location key (pincode)
            updateWeather();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {

        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
        //Getting the value from the Location Shared Preference


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //here if there is no value stored for the location key,
        //we get the default value as seen from the second argument.
        String location =  sharedPreferences.getString (
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default)
        );

        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
        fetchWeatherTask.execute(location);
    }



    @Override
    public void onStart() {
        super.onStart();
        try {
            updateWeather();
        }catch(NullPointerException e) {
            Toast.makeText(getActivity(),"Check Wifi Connection",Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflating the fragmentLayout into a View
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //converting the raw data into list

        //finding the  view of the list
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);

        //initializing the adapter
         mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                         R.layout.list_view_item_forecast,
                         R.id.text_view_forecast,
                         new ArrayList<String>());

        //binding the listview to the adpater
        listView.setAdapter(mForecastAdapter);

        //Click Event on List
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String forecast = mForecastAdapter.getItem(position);
                Intent forecastIntent = new Intent(getActivity(), Detail_Activity.class).putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(forecastIntent);

            }
        });



        return rootView;
    }




}

