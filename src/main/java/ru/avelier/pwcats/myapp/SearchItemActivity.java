package ru.avelier.pwcats.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.avelier.pwcats.db.DbItemsHelper;
import ru.avelier.pwcats.db.DbRecentItemsContract.*;
import ru.avelier.pwcats.db.DbRecentItemsHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SearchItemActivity extends Activity {

    private DbItemsHelper items_db;
    private DbRecentItemsHelper recent_items_db;
    private SharedPreferences prefs;

    private List<AsyncTask<String, Void, Bitmap>> loadIconTasks;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        items_db.close();
        recent_items_db.close();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getName(), "onCreate()");

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loadIconTasks = new LinkedList<AsyncTask<String, Void, Bitmap>>();

        items_db = new DbItemsHelper(getApplicationContext());
        recent_items_db = new DbRecentItemsHelper(getApplicationContext());

        formContentView(savedInstanceState);
    }

    private void formContentView(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_item);
        // fill server spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner_server);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.servers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(prefs.getInt(getString(R.string.pref_server), 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putInt(getString(R.string.pref_server), position).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // show recent items
        showRecentItems();

        SearchView searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
    }

    private void showRecentItems() {
        List<Integer> recent_ids = recentItemIds();
        for (int id : recent_ids) {
            add_item_line(id);
        }
    }

    private List<Integer> recentItemIds() {
        int recentCountLimit = prefs.getInt(getString(R.string.pref_recent_count), 30);
        List<Integer> res = new ArrayList<Integer>(recentCountLimit);

        SQLiteDatabase db = recent_items_db.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + RecentItemsEntry.COL_RECENT_ID +
                " FROM " + RecentItemsEntry.TABLE_NAME +
                " ORDER BY " + RecentItemsEntry.COL_RECENT_ID + " DESC" +
                " LIMIT 0, 100", new String[]{});
        if (c.moveToFirst()) do {
            res.add(c.getInt(0));
        } while (c.moveToNext());
        c.close();
        return res;
    }

    // TODO optimise
    // TODO case-insensitive. Maybe create 1 more row with all in lower case. Ибо sqlite буржуйский.
    public void search(String subname) {
        if (subname.equals("")) {
            showRecentItems();
        }

        // stop loading icons
        for (AsyncTask<String, Void, Bitmap> task : loadIconTasks) {
            task.cancel(true);
        }
        loadIconTasks.clear();
        // process search
        SQLiteDatabase db = items_db.getReadableDatabase();
        Cursor c;
        try {
            // || things because of https://code.google.com/p/android/issues/detail?id=3153
            String SELECT_WHERE = "SELECT _id, name FROM items WHERE lower_name LIKE '%' || ? || '%' OR name LIKE '%' || ? || '%' LIMIT 0, 50";
            String[] binds = new String[]{subname.toLowerCase()};
            c = db.rawQuery(SELECT_WHERE, binds);
        } catch (Exception e) {
            Log.wtf("db", subname);
            e.printStackTrace();
            return;
        }

// add proposials to list
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.scrolledLinearView);
        insertPoint.removeAllViewsInLayout();
        if (c.moveToFirst()) {
            do {
                final int id = c.getInt(0);
                final String itemName = c.getString(1);
                add_item_line(id, itemName);
            } while (c.moveToNext());
        } else { // empty
            Log.d("db", "empty select for subname=" + subname);
//            insertPoint.removeAllViewsInLayout();
        }
        c.close();
        db.close();
    }

    private void add_item_line(int id){
        add_item_line(id, getItemNameById(id));
    }

    private void add_item_line(final int id, final String itemName) {
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.scrolledLinearView);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.search_item_line, null);

// load icon
        AsyncTask<String, Void, Bitmap> loadIconTask = new DownloadImageTask((ImageView) v.findViewById(R.id.itemIcon));
        loadIconTasks.add(loadIconTask);
        loadIconTask.execute(String.format("http://www.pwdatabase.com/images/icons/generalm/%d.gif", id));
// fill id (hidden)
        TextView itemId = (TextView) v.findViewById(R.id.textCatName);
        itemId.setText(id + "");
        itemId.setVisibility(View.GONE);
// fill item name
        TextView itemNameView = (TextView) v.findViewById(R.id.textCatTitle);
        itemNameView.setText(itemName);
//
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewItemDetails(id, itemName);
            }
        });

// insert into main view
        insertPoint.addView(v, insertPoint.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    public void viewItemDetails(int id) {
        viewItemDetails(id, getItemNameById(id));
    }

    private String getItemNameById(int id) {
        SQLiteDatabase db = items_db.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM items WHERE _id = ?", new String[]{id+""});
        c.moveToFirst();
        String name = c.getString(0);
        c.close();
        db.close();
        return name;
    }

    public void viewItemDetails(int id, String itemName) {
        // db recent
        Log.d(this.getClass().getName(), "inserting new recent id: " + id);
        SQLiteDatabase db = recent_items_db.getWritableDatabase();
        db.execSQL("INSERT INTO " + RecentItemsEntry.TABLE_NAME +
                " (" + RecentItemsEntry.COL_RECENT_ID + ") VALUES(" + id + ")");
        db.close();

        // intent item details
        Intent intent = new Intent(this, ItemDetailsActivity.class);

        intent.putExtra("id", id);
        intent.putExtra("itemName", itemName);

        Spinner server_spinner = ((Spinner) findViewById(R.id.spinner_server));
        String server = (String)server_spinner.getItemAtPosition(server_spinner.getSelectedItemPosition());
        Log.d(getString(R.string.pref_server), server);
        intent.putExtra(getString(R.string.pref_server), server);
        startActivity(intent);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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
            bmImage.setImageBitmap(result);
            // TODO think of bmImage.setImageURI()
            float scale = (float)bmImage.getHeight() / result.getHeight();
            bmImage.setScaleX( scale );
            bmImage.setScaleY( scale );
        }
    }
}
