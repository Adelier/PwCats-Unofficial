package ru.avelier.pwcats.myapp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.adelier.pw.PwcatsRequester;

import java.io.InputStream;

/**
 * Created by Adelier on 02.07.2014.
 */
public class ItemDetailsPagesFragment extends Fragment {

    private ViewGroup rootView;

    private static final int NUM_PAGES = 2;
    private SharedPreferences prefs;

    private int id;
    private PwcatsRequester.Server server;
    private String itemName;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.item_details_base, container, false);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) rootView.findViewById(R.id.item_details_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(prefs.getInt("item_details_selected_slide", 0));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                prefs.edit().putInt("item_details_selected_slide", position).apply();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        String sServer = getArguments().getString("server");
        if (sServer == null) {
            Log.wtf(this.toString(), "server not passed :(");
            return rootView;
        } else {
            server = PwcatsRequester.Server.valueOf(sServer);
        }
        itemName = getArguments().getString("itemName");
        if (itemName == null) {
            Log.wtf(this.toString(), "itemName not passed :(");
            return rootView;
        }
        id = getArguments().getInt("id", -1);
        if (id == -1) {
            Log.wtf(this.toString(), "id not passed :(");
            return rootView;
        }
        setActionBarInfoAsItemInfo(server, itemName, id);
        return rootView;
    }

    private void setActionBarInfoAsItemInfo(PwcatsRequester.Server server, String itemName, Integer id) {
//        title
        getActivity().setTitle(String.format("%s (%s)", itemName, server.toString()));
//        icon
        new DownloadActionBarIconTask().execute(DownloadImageTask.getIconUrl(id));
    }

    private class DownloadActionBarIconTask extends AsyncTask<String, Void, Bitmap> {
        public DownloadActionBarIconTask() {
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            final float scale = getResources().getDisplayMetrics().density;
            int size = (int)(64 * scale + 0.5f);
            result.setDensity(4);

            getActivity().getActionBar().setIcon(new BitmapDrawable(result));
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            switch (position) {
                case 0:
                    f = new ItemCatDetailsFragment();
                    break;
                case 1:
                    f = new ItemAucDetailsFragment();
                    break;
                default: return null;
            }
            f.setArguments(getArguments());
            return f;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getActionBar().setIcon(R.drawable.ic_launcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBarInfoAsItemInfo(server, itemName, id);
    }
}
