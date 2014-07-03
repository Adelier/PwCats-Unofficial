package ru.avelier.pwcats.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ru.adelier.pw.PwcatsRequester;
import ru.avelier.pwcats.db.DbItemsHelper;
import ru.avelier.pwcats.db.DbRecentItemsHelper;

/**
 * Created by Adelier on 02.07.2014.
 */
public class MainActivity extends FragmentActivity {

    private String[] navigationItems;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLeftLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private Fragment activeFragment;
    private SearchItemFragment fragmentSearch;
    private ItemStarDetailsFragment itemStarDetailsFragment;

    public static DbItemsHelper items_db;
    public static DbRecentItemsHelper recent_items_db;
    private SharedPreferences prefs;

    private boolean isLogined;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        items_db.close();
        recent_items_db.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        items_db = new DbItemsHelper(getApplicationContext());
        recent_items_db = new DbRecentItemsHelper(getApplicationContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLeftLayout = (LinearLayout) findViewById(R.id.left_drawer);

        // AUTHORISE
        tryLogin();

        // NAVIGATION
        TextView mNavSearch = (TextView) findViewById(R.id.nav_search);
        TextView mNavRefine = (TextView) findViewById(R.id.nav_refine);
        TextView mNavProfit = (TextView) findViewById(R.id.nav_profit);
        TextView mNav1star = (TextView) findViewById(R.id.nav_1star);
        TextView mNav2star = (TextView) findViewById(R.id.nav_2star);
        TextView mNav3star = (TextView) findViewById(R.id.nav_3star);
        TextView mNavAuthLogin = (TextView) findViewById(R.id.nav_auth_login);
        TextView mNavAuthLogout = (TextView) findViewById(R.id.nav_auth_logout);
        TextView mNavPwcats = (TextView) findViewById(R.id.nav_pwcats);
        // ...

        // NAVIGATION onClick()
        mNavSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(getString(R.string.nav_search));
                showSearchFragment(true);
                afterAnyNavClick();
            }
        });
        mNavRefine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(getString(R.string.nav_refine));
//                showSearchFragment(true); TODO
                afterAnyNavClick();
            }
        });
        mNavProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(getString(R.string.nav_profit));
//                showSearchFragment(true); TODO
                afterAnyNavClick();
            }
        });
        mNav1star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(getString(R.string.nav_1star));
                showStarItemDetails(1);
                afterAnyNavClick();
            }
        });
        mNav2star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(getString(R.string.nav_2star));
                showStarItemDetails(2);
                afterAnyNavClick();
            }
        });
        mNav3star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(getString(R.string.nav_3star));
                showStarItemDetails(3);
                afterAnyNavClick();
            }
        });
        mNavAuthLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authLogin();
                afterAnyNavClick();
            }
        });
        mNavAuthLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authLogout();
//                afterAnyNavClick();
            }
        });
        mNavPwcats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(getString(R.string.nav_pwcats));
                intentPwcatsInfo();
                afterAnyNavClick();
            }
        });

        // drawer
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

    // AUTHORISATION

    /** returns true if login succseed */
    private void tryLogin() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(String... ci_session) {
                boolean isValid = PwcatsRequester.isValidCiSession(ci_session[0]);
                return isValid;
            }
            @Override
            protected void onPostExecute(Boolean isValid) {
                isLogined = isValid;
                showOrHideLogInOut(isLogined);
                if (!isValid)
                    authLogout();
                TextView mNavAuthLogin = (TextView) findViewById(R.id.nav_auth_login);
                TextView mNavAuthLogout = (TextView) findViewById(R.id.nav_auth_logout);
            }
        };
        String ci_session = prefs.getString(getString(R.string.ci_session), null);
        task.execute(ci_session);
    }
    private void authLogout() {
        prefs.edit().remove(getString(R.string.ci_session)).apply();
        isLogined = false;
        showOrHideLogInOut(isLogined);
    }
    private void authLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }
    private void showOrHideLogInOut(boolean isLogined) {
        TextView mNavAuthLogin = (TextView) findViewById(R.id.nav_auth_login);
        TextView mNavAuthLogout = (TextView) findViewById(R.id.nav_auth_logout);
        if (isLogined) {
            mNavAuthLogin.setVisibility(View.GONE);
            mNavAuthLogout.setVisibility(View.VISIBLE);
        } else {
            mNavAuthLogin.setVisibility(View.VISIBLE);
            mNavAuthLogout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.wtf("onActivityResult", data.getStringExtra(getString(R.string.ci_session)));
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String ci_session = data.getStringExtra(getString(R.string.ci_session));
                if (ci_session == null)
                    prefs.edit().remove(getString(R.string.ci_session)).apply();
                else
                    prefs.edit().putString(getString(R.string.ci_session), ci_session).apply();
                isLogined = (ci_session != null);
                showOrHideLogInOut(isLogined);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerLeftLayout);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
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


//        mDrawerLeftLayout.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerLeftLayout);
    }
    private void afterAnyNavClick() {
        mDrawerLayout.closeDrawer(mDrawerLeftLayout);
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
        setTitle(getString(R.string.nav_search));
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

    public static String getItemNameById(int id) {
        SQLiteDatabase db = MainActivity.items_db.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM items WHERE _id = ?", new String[]{id+""});
        String name;
        if (c.moveToFirst())
            name = c.getString(0);
        else
            name = null;
        c.close();
        db.close();
        if (name == null)
            Log.w("null", "MainActivity.getItemNameById id = " + id + ", returns " + name);
        return name;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
