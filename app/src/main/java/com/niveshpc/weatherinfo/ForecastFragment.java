package com.niveshpc.weatherinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

        //Getting the value from the Location Shared Preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location =  sharedPreferences.getString //here if there is no value stored for the location key,
                //we get the default value as seen from the second argument.
                (getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
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



    class FetchWeatherTask extends AsyncTask<String, Void, String[]> {


        @Override
        protected void onPostExecute(String[] weekForecast) {
            if(weekForecast != null)
            {
                mForecastAdapter.clear();
                for(String dayForecast : weekForecast)
                mForecastAdapter.add(dayForecast);
            }

        }

        @Override
        protected String[] doInBackground(String... params) {


            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String apiKey = "5e419dd169645528f464030b0ba4c461";
            String format = "json";
            String units = "metric";
            int numDays = 7;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast


                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APIKEY = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APIKEY, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.e(LOG_TAG, "**Built URI\t" + builtUri.toString());


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                Log.i(LOG_TAG + "*****DATA********", "Forecast JSON String" + forecastJsonStr);

            } catch (IOException e) {

                Log.e("*********", "Error ", e);

                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } catch (RuntimeException re) {
                String errorMessage = re.getCause().getMessage();
                Log.e("******", errorMessage);

            }

            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }



        /* The date/time conversion code is going to be moved outside the asynctask later,
             * so for convenience we're breaking it out into its own method now.
             */
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            long millitime = 1000 * time;
            Date date = new Date(millitime);

            //*****Date Conversion Not Working as expected
            Log.d("GettingDate", "GMTDate" + date.toString());
            //Setting the Format for Date and Time
            SimpleDateFormat pattern = new SimpleDateFormat("MM dd yyyy: HH mm ss", new Locale("en","IND"));
            pattern.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")) ;
            String istDate = pattern.format(date);

            Log.d("GettingDate", "IST :" + istDate);
            return (istDate);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low,String unitType) {

            if(unitType.equals(getString(R.string.pref_units_label_imperial)))
            {
                high = (high * 1.8 )+32;
                low = (low * 1.8 )+32;

            }
            else if (!unitType.equals(getString(R.string.pref_units_label_metric))){
            Log.d (LOG_TAG, "Unit type not found: " + unitType);

            }
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_TIME = "dt";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.


            String[] resultStrs = new String[numDays];


            // Data is fetched in Celsius by default.
            // If user prefers to see in Fahrenheit, convert the values here.
            // We do this rather than fetching in Fahrenheit so that the user can
            // change this option without us having to re-fetch the data once
            // we start storing the values in a database.
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));

            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;
                String humidity;
                long time;


                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // Get date & time from dayforecast

                time = Integer.parseInt(dayForecast.getString(OWM_TIME));

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".

                // Cheating to convert this to UTC time, which is what we want anyhow
                day = getReadableDateString(time);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low,unitType);


                //Extracting the Humidity from the dayforecast

                humidity = dayForecast.getString(OWM_HUMIDITY);


                resultStrs[i] = day + " - " + description + " - " + highAndLow + "-" + humidity;

            }

            for (String s : resultStrs) {
                Log.e(LOG_TAG, "***Forecast entry:*** " + s);
            }
            return resultStrs;

        }


    }
}

