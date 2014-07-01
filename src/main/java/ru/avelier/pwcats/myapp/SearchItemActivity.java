package ru.avelier.pwcats.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
import ru.avelier.pwcats.db.DbItemsContract.ItemsEntry;
import ru.avelier.pwcats.db.DbItemsHelper;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SearchItemActivity extends Activity {

    private Random rand;
    private DbItemsHelper dbHelper;
    private SharedPreferences prefs;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        rand = new Random();
        dbHelper = new DbItemsHelper(getApplicationContext());

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
        spinner.setSelection(prefs.getInt("server", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putInt("server", position).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // TODO show recent items

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

    // TODO optimise
    // TODO case-insensitive. Maybe create 1 more row with all in lower case. Ибо sqlite буржуйский.
    public void search(String subname) {
        // stop loading icons
        for (AsyncTask<String, Void, Bitmap> task : loadIconTasks) {
            task.cancel(true);
        }
        loadIconTasks.clear();
        // process search
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            // || things because of https://code.google.com/p/android/issues/detail?id=3153
            String SELECT_WHERE = "SELECT _id, name FROM items WHERE name LIKE '%' || ? || '%' OR name LIKE '%' || ? || '%' LIMIT 0, 50";
            String[] binds = new String[]{subname, ("" + subname.charAt(0)).toUpperCase() + subname.substring(1)};
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
                add_item_line(c, insertPoint);
            } while (c.moveToNext());
        } else { // empty
            Log.d("db", "empty select for subname=" + subname);
            insertPoint.removeAllViewsInLayout();
        }


        c.close();
    }

    private final List<AsyncTask<String, Void, Bitmap>> loadIconTasks = new LinkedList<AsyncTask<String, Void, Bitmap>>();

    private void add_item_line(Cursor c, ViewGroup insertPoint) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.search_item_line, null);

        final int id = c.getInt(0);
        final String itemName = c.getString(1);

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


    public void viewItemDetails(int id, String itemName) {
//        setContentView(R.layout.search_item);
        Intent intent = new Intent(this, ItemDetailsActivity.class);

        intent.putExtra("id", id);
        intent.putExtra("itemName", itemName);

        Spinner server_spinner = ((Spinner) findViewById(R.id.spinner_server));
        String server = (String)server_spinner.getItemAtPosition(server_spinner.getSelectedItemPosition());
        Log.d("server", server);
        intent.putExtra("server", server);
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
