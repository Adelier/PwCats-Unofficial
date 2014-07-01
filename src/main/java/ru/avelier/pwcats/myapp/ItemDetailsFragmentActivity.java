package ru.avelier.pwcats.myapp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import ru.adelier.pw.PwcatsRequester;

import java.io.InputStream;

/**
 * Created by Adelier on 02.07.2014.
 */
public class ItemDetailsFragmentActivity extends FragmentActivity {

    private static final int NUM_PAGES = 2;
    private SharedPreferences prefs;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.item_details_base);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.item_details_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
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

        PwcatsRequester.Server server = PwcatsRequester.Server.valueOf(getIntent().getStringExtra("server"));
        if (server == null) {
            Log.wtf(this.toString(), "server not passed :(");
            return;
        }
        String itemName = getIntent().getStringExtra("itemName");
        if (itemName == null) {
            Log.wtf(this.toString(), "itemName not passed :(");
            return;
        }
        Integer id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            Log.wtf(this.toString(), "id not passed :(");
            return;
        }
//        title
        setTitle(String.format("%s (%s)", itemName, server.toString()));
//        icon
        new DownloadActionBarIconTask().execute(SearchItemActivity.getIconUrl(id));
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

            getActionBar().setIcon(new BitmapDrawable(result));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            switch (position) {
                case 0: return new ItemCatDetailsFragment();
                case 1: return new ItemAucDetailsFragment();
                default: return null;
            }
//            return new ScreenSlidePageFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
