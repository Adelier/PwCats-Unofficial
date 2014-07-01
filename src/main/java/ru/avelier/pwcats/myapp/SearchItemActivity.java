package ru.avelier.pwcats.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.avelier.pwcats.db.DbItemsContract.ItemsEntry;
import ru.avelier.pwcats.db.DbItemsHelper;

import java.util.Random;

public class SearchItemActivity extends Activity {

    private Random rand;
    private DbItemsHelper dbHelper;


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
        formContentView(savedInstanceState);

        rand = new Random();

        dbHelper = new DbItemsHelper(getApplicationContext());
    }

    private void formContentView(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_item);
        // fill server spinner
        // TODO remember selection
        Spinner spinner = (Spinner) findViewById(R.id.spinner_server);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.servers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            // || things because of https://code.google.com/p/android/issues/detail?id=3153
            String SELECT_WHERE = "SELECT _id, name FROM items WHERE name LIKE '%' || ? || '%' OR name LIKE '%' || ? || '%' LIMIT 0, 100";
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
                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.search_item_line, null);

                // TODO icon
                TextView itemId = (TextView) v.findViewById(R.id.textCatName);
                final int id = c.getInt(0);
                itemId.setText(id + "");
                final String itemName = c.getString(1);
                TextView itemNameView = (TextView) v.findViewById(R.id.textCatTitle);
                itemNameView.setText(itemName);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewItemDetails(id, itemName);

                    }
                });
// insert into main view
                insertPoint.addView(v, insertPoint.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } while (c.moveToNext());
        } else { // empty
            Log.d("db", "empty select for subname=" + subname);
            insertPoint.removeAllViewsInLayout();
        }


        c.close();
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

}
