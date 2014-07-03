package ru.avelier.pwcats.myapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.adelier.pw.PwItemCat;
import ru.adelier.pw.PwItemCatDetailed;
import ru.adelier.pw.PwcatsRequester;
import ru.avelier.pwcats.db.DbItemsHelper;

import java.util.LinkedList;
import java.util.List;

public class ItemStarDetailsFragment extends Fragment {
    private ViewGroup rootView;
    
    Activity parentActivity;

    private PwcatsRequester.Server server;
    private Integer stars;
    private List<DownloadImageTask> downloadTasks;

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDownloading();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = getActivity();
        downloadTasks = new LinkedList<DownloadImageTask>();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopDownloading();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateArguments();
    }

    private void updateTitle(int stars) {
        switch (stars) {
            case 1: parentActivity.setTitle(getString(R.string.nav_1star)); break;
            case 2: parentActivity.setTitle(getString(R.string.nav_2star)); break;
            case 3: parentActivity.setTitle(getString(R.string.nav_3star)); break;
        }
    }
    public void updateArguments() {
        stopDownloading();
        stars = getArguments().getInt(getString(R.string.pref_stars), -1);
        server = PwcatsRequester.Server.valueOf(getArguments().getString(getString(R.string.pref_server),
                getResources().getStringArray(R.array.servers)[0]));
        updateTitle(stars);
        asynkFillViewWithAllRequestedNodes();
    }
    private void stopDownloading() {
        for (DownloadImageTask task : downloadTasks)
            task.cancel(true);
        downloadTasks.clear();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load params from intent extras
        server = PwcatsRequester.Server.valueOf(getArguments().getString("server"));
        if (server == null) {
            Log.wtf(this.toString(), "server not passed :(");
            return rootView;
        }
        stars = getArguments().getInt("stars", -1);
        if (stars == -1) {
            Log.wtf(this.toString(), "stars count not passed :(");
            return rootView;
        }
        updateTitle(stars);


//        setContentView(R.layout.item_details_cat);
        rootView = (ViewGroup) inflater.inflate(R.layout.item_details_stars, container, false);

        asynkFillViewWithAllRequestedNodes();
        return rootView;
    }

    private void asynkFillViewWithAllRequestedNodes(){
        AsyncTask<Object, Void, List<PwItemCatDetailed>> asyncTask = new RetrievePwItemCatTask().execute(new Object[]{stars, server});
    }
    LayoutInflater vi;
    private void fillViewWithNodes(List<PwItemCatDetailed> infos) {
        // remove old
        ViewGroup insertPoint = (ViewGroup)rootView.findViewById(R.id.scrolledLinearView);
        // maybe there is nothing to fill with?
        if (infos == null || infos.isEmpty()) {
            LayoutInflater vi = (LayoutInflater) parentActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.message, null);
            int message_id;
            if (infos == null)
                message_id = R.string.network_error;
            else
                message_id = R.string.nothing_found;
            ((TextView)v.findViewById(R.id.messageText)).setText(message_id);
// insert into main view
            insertPoint.addView(v, insertPoint.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return;
        }

        // so there is. add them now
        try {
            vi = (LayoutInflater) parentActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (NullPointerException e) {
            Log.d(this.toString(), "can't get LayoutInflater");
            return;
        }
        for (PwItemCatDetailed info : infos) {
            add_item_node_star(info);
        }
    }

    class RetrievePwItemCatTask extends AsyncTask<Object, Void, List<PwItemCatDetailed>> {
        List<PwItemCatDetailed> infos = null;
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            // remove old
            ViewGroup insertPoint = (ViewGroup)rootView.findViewById(R.id.scrolledLinearView);
            insertPoint.removeAllViewsInLayout();
        }

        protected List<PwItemCatDetailed> doInBackground(Object... stars_server) {
            try {
                String ci_session = PreferenceManager.getDefaultSharedPreferences(parentActivity.getApplicationContext())
                        .getString(getString(R.string.ci_session), null);
                infos = PwcatsRequester.itemsStars((PwcatsRequester.Server) (stars_server[1]), (Integer) stars_server[0],
                        ci_session);
                return infos;
            } catch (Exception e) {
                exception = e;
                Log.e(this.getClass().toString(), "error while requesting pwcats.info", e);
                return null;
            }
        }

        protected void onPostExecute(List<PwItemCatDetailed> feed) {
            fillViewWithNodes(infos);
            rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }

    public void add_item_node_star(PwItemCatDetailed itemInfo) {
        View v = vi.inflate(R.layout.item_details_node_stars, null);

// fill in any details dynamically here
        ImageView itemIcon = (ImageView) v.findViewById(R.id.itemIcon);
        DownloadImageTask downloadImageTask = new DownloadImageTask(itemIcon);
        downloadImageTask.execute(DownloadImageTask.getIconUrl(itemInfo.getId()));

        TextView textCatTitle = (TextView) v.findViewById(R.id.textCatTitle);
        textCatTitle.setText(itemInfo.getCatTitle());
        TextView textCatName = (TextView) v.findViewById(R.id.textCatNickname);
        textCatName.setText(itemInfo.getNickname());

        // TODO http://www.pwmap.ru/
        TextView textLocationName = (TextView) v.findViewById(R.id.textLocationName);
        textLocationName.setText(itemInfo.getLocation().toString());
        TextView textLocationCoordinates = (TextView) v.findViewById(R.id.textLocationCoordinates);
        textLocationCoordinates.setText(String.format("%d %d", itemInfo.getCoord()[0], itemInfo.getCoord()[1]));

        TextView textItemCostHi = (TextView) v.findViewById(R.id.textItemCostHi);
        textItemCostHi.setText( String.format("%,d", itemInfo.getPriceHi()) );

        final TextView textHtmlDesc = (TextView) v.findViewById(R.id.textHtmlDesc);
        String realName = MainActivity.getItemNameById(itemInfo.getId());
        String desc = itemInfo.getDesc();
        if (realName != null)
            desc = desc.replace(itemInfo.getItemName(), realName);
        textHtmlDesc.setText(Html.fromHtml(desc));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // switch visibility
                if (textHtmlDesc.getVisibility() == View.GONE)
                    textHtmlDesc.setVisibility(View.VISIBLE);
                else
                    textHtmlDesc.setVisibility(View.GONE);
            }
        });

// sizes
        DisplayMetrics metrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
//        View itemIcon = v.findViewById(R.id.itemIcon);
        itemIcon.setMinimumWidth((int) (width * 0.13f));
        View catNameLayout = v.findViewById(R.id.catNameLayout);
        catNameLayout.setMinimumWidth((int) (width * 0.47f));
        View locationLayout = v.findViewById(R.id.locationLayout);
        locationLayout.setMinimumWidth((int) (width * 0.20f));
        View priceAndExpandLayout = v.findViewById(R.id.priceAndExpandLayout);
        priceAndExpandLayout.setMinimumWidth((int) (width * 0.23f));

// insert into main view
        ViewGroup insertPoint = (ViewGroup) rootView.findViewById(R.id.scrolledLinearView);
        insertPoint.addView(v, insertPoint.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }
}

