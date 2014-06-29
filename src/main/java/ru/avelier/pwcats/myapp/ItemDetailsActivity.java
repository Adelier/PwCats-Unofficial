package ru.avelier.pwcats.myapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

public class ItemDetailsActivity extends Activity {

    private Random rand;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        rand = new Random();
    }


    public void button_AddPwItem(View view){
//        TextView textRes = (TextView)findViewById(R.id.textRes);
//        SearchView searchView = (SearchView)findViewById(R.id.searchView);

//        textRes.setText(searchView.getQuery());

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.item_node_cat, null);

// fill in any details dynamically here
        TextView textCatName = (TextView) v.findViewById(R.id.textCatName);
        textCatName.setText("Name" + rand.nextInt(10));

        TextView textCatTitle = (TextView) v.findViewById(R.id.textCatTitle);
        textCatTitle.setText("Продаю по " + rand.nextInt(10000));

// insert into main view
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.scrolledLinearView);
        insertPoint.addView(v, insertPoint.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }
}
