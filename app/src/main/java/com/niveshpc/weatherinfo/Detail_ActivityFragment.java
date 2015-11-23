package com.niveshpc.weatherinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class Detail_ActivityFragment extends Fragment {

    private static final String LOG_TAG = Detail_ActivityFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastStr;


    public Detail_ActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT))
        {
             mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView)rootView.findViewById(R.id.detail_forecast_textview)).setText(mForecastStr);
        }

        return rootView;
    }


    private  Intent createShareForecastIntent ()
    {
        Intent  shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
       String hello =  mForecastStr;
       shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.putExtra(Intent.EXTRA_TEXT,mForecastStr+FORECAST_SHARE_HASHTAG);
        return shareIntent;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment,menu);
        //Retrieve the sharemenu item
        MenuItem menuitem = menu.findItem(R.id.action_share);
        //Get the Provider and hold onto it to set/change the share intent
        android.support.v7.widget.ShareActionProvider mShareActionProvider =
                (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuitem);


        // Attach an intent to this ShareActionProvider.  You can update this at any time,
                   // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }
}
