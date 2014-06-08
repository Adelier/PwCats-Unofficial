package com.example.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.concurrent.atomic.AtomicInteger;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        GridLayout buttonsLayout = (GridLayout)findViewById(R.id.buttonLayout);
        for (int i = 0; i < 10; i++) {
//            Button digit =

        }


        // final AtomicInteger i = new AtomicInteger();
        // final Button btn1 = (Button)findViewById(R.id.button);

    }
}
