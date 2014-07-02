package ru.avelier.pwcats.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Adelier on 02.07.2014.
 */
public class MainActivity extends FragmentActivity {

    private String[] navigationItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private Fragment activeFragment;
    private SearchItemFragment fragmentSearch;
    private ItemStarDetailsFragment itemStarDetailsFragment;
//    ...

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        navigationItems = getResources().getStringArray(R.array.navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, navigationItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                0,//R.string.drawer_open,  /* "open drawer" description */
                0 //R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                getActionBar().setTitle(mDrawerTitle);
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        showSearchFragment(false);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Log.i("drawer", position + ": " + navigationItems[position]);
            selectItem(position);
        }
    }
    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        if (position == 0) // search
            showSearchFragment(true);
        if (position == 1) // Заточка
            ;//showSearchFragment(true);
        if (position == 2) // Вещи ☆
            showStarItemDetails(1);
        if (position == 3) // Вещи ☆☆
            showStarItemDetails(2);
        if (position == 4) // Вещи ☆☆☆
            showStarItemDetails(3);
        if (position == 5) // pwcats.info
            intentPwcatsInfo();


        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void showStarItemDetails(int stars) {
        if (itemStarDetailsFragment == null)
            itemStarDetailsFragment = new ItemStarDetailsFragment();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String[] servers = getResources().getStringArray(R.array.servers);
        String server = servers[prefs.getInt(getString(R.string.pref_server), 0)];
        if (itemStarDetailsFragment.getArguments() == null) {
            Bundle args = new Bundle();
            args.putString(getString(R.string.pref_server), server);
            args.putInt(getString(R.string.pref_stars), stars);
            itemStarDetailsFragment.setArguments(args);
        } else {
            itemStarDetailsFragment.getArguments().putString(getString(R.string.pref_server), server);
            itemStarDetailsFragment.getArguments().putInt(getString(R.string.pref_stars), stars);
        }

        if (activeFragment == itemStarDetailsFragment) {
            itemStarDetailsFragment.updateArguments();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction t = fragmentManager.beginTransaction()
                .replace(R.id.content_frame, itemStarDetailsFragment);
        if (activeFragment == fragmentSearch)
            t = t.addToBackStack(null);
        t.commit();
        activeFragment = itemStarDetailsFragment;
    }

    public void setActiveFragment(Fragment activeFragment) {
        this.activeFragment = activeFragment;
    }

    private void showSearchFragment(boolean clear) {
        if (fragmentSearch == null)
            fragmentSearch = new SearchItemFragment();

        if (clear)
            fragmentSearch.clearQuery();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragmentSearch)
                .commit();
        activeFragment = fragmentSearch;
    }

    private void intentPwcatsInfo() {
        String url = "http://pwcats.info";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
