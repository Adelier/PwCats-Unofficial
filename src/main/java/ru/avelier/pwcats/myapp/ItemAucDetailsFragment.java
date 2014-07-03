package ru.avelier.pwcats.myapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.adelier.pw.PwItemAuc;
import ru.adelier.pw.PwcatsRequester;

import java.io.InputStream;
import java.util.List;

public class ItemAucDetailsFragment extends Fragment {

    private Activity parentActivity;
    private ViewGroup rootView;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = getActivity();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = (ViewGroup) inflater.inflate(R.layout.item_details_auc, container, false);

        // load params from intent extras
        PwcatsRequester.Server server = PwcatsRequester.Server.valueOf(getArguments().getString("server"));
        if (server == null) {
            Log.wtf(this.toString(), "server not passed :(");
            return rootView;
        }
        Integer id = getArguments().getInt("id", -1);
        if (id == -1) {
            Log.wtf(this.toString(), "id not passed :(");
            return rootView;
        }

        // asynk ask pwcats.info and fill view with nodes
        AsyncTask<Object, Void, List<PwItemAuc>> asyncTask = new RetrievePwItemAucTask().execute(new Object[]{id, server});
        return rootView;
    }

    private void fillViewWithNodes(List<PwItemAuc> infos) {
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
            ViewGroup insertPoint = (ViewGroup) rootView.findViewById(R.id.scrolledLinearView);
            insertPoint.addView(v, insertPoint.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            return;
        }
        for (PwItemAuc info : infos) {
            add_item_node_cat(info);
            Log.d(this.toString(), "item info added " + info.toString());
        }
        rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    class RetrievePwItemAucTask extends AsyncTask<Object, Void, List<PwItemAuc>> {
        List<PwItemAuc> infos = null;
        Exception exception = null;
        protected List<PwItemAuc> doInBackground(Object... id_server) {
            try {
                String ci_session = PreferenceManager.getDefaultSharedPreferences(parentActivity.getApplicationContext())
                                            .getString(getString(R.string.ci_session), null);
                infos = PwcatsRequester.itemsAuc((PwcatsRequester.Server)(id_server[1]), (Integer)id_server[0], ci_session);
                return infos;
            } catch (Exception e) {
                exception = e;
                Log.e(this.getClass().toString(), "error while requesting pwcats.info", e);
                return null;
            }
        }

        protected void onPostExecute(List<PwItemAuc> feed) {
            if (exception == null)
                fillViewWithNodes(infos);
            else {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        }
    }

    public void add_item_node_cat(PwItemAuc itemInfo) {
        LayoutInflater vi = (LayoutInflater) parentActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.item_details_node_auc, null);

// lot id
        TextView textAucId = (TextView) v.findViewById(R.id.auc_id);
        textAucId.setText("#" + itemInfo.getLot_id());
// price prepare
        int count = itemInfo.getCount();
        Integer priceBidAll = itemInfo.getPriceLo();
        Integer priceBuyoutAll = itemInfo.getPriceHi();
        Integer priceBidX1 = null;
        if (priceBidAll != null)
            priceBidX1 = priceBidAll / count;
        Integer priceBuyoutX1 = null;
        if (priceBuyoutAll != null)
            priceBuyoutX1 = priceBuyoutAll / count;
        boolean bidBuyoutDiffers = true;//((int)priceBidAll != (int)priceBuyoutAll);
// price x1
        TextView textPriceBidX1 = (TextView) v.findViewById(R.id.price_bid_x1);
        if(bidBuyoutDiffers) {
            textPriceBidX1.setVisibility(View.VISIBLE);
            textPriceBidX1.setText(String.format("1 x %,d", priceBidX1));
        }  else {
            textPriceBidX1.setVisibility(View.GONE);
        }
        TextView textPriceBuyoutX1 = (TextView) v.findViewById(R.id.price_buyout_x1);
        textPriceBuyoutX1.setText( String.format("1 x %,d", priceBuyoutX1) );
// price all
        if (count != 1) {
            TextView textPriceBidAll = (TextView) v.findViewById(R.id.price_bid_all);
            if(bidBuyoutDiffers) {
                textPriceBidAll.setVisibility(View.VISIBLE);
                textPriceBidAll.setText(String.format("%d x %,d", count, priceBidAll));
            } else {
                textPriceBidAll.setVisibility(View.GONE);
            }
            TextView textPriceBuyoutAll = (TextView) v.findViewById(R.id.price_buyout_all);
            textPriceBidAll.setVisibility(View.VISIBLE);
            textPriceBuyoutAll.setText(String.format("%d x %,d", count, priceBuyoutAll));
        } else {
            TextView textPriceBidAll = (TextView) v.findViewById(R.id.price_bid_all);
            textPriceBidAll.setVisibility(View.GONE);
            TextView textPriceBuyoutAll = (TextView) v.findViewById(R.id.price_buyout_all);
            textPriceBuyoutAll.setVisibility(View.GONE);
        }
// count
//        TextView textItemCount = (TextView) v.findViewById(R.id.textItemCount);
//        textItemCount.setText("x" + count);
// up to time
        // TODO colour
        TextView textUpToTime = (TextView) v.findViewById(R.id.textUpToTime);
        textUpToTime.setText(itemInfo.getUpToTime());

// sizes
        DisplayMetrics metrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        textAucId.setMinimumWidth((int) (width * 0.20f));
        View priceX1Layout = v.findViewById(R.id.priceX1Layout);
        priceX1Layout.setMinimumWidth((int) (width * 0.25f));
        View priceAllLayout = v.findViewById(R.id.priceAllLayout);
        priceAllLayout.setMinimumWidth((int) (width * 0.25f));
//        textItemCount.setMinimumWidth((int) (width * 0.10f));
        textUpToTime.setMinimumWidth((int) (width * 0.10f));

// insert into main view
        ViewGroup insertPoint = (ViewGroup) rootView.findViewById(R.id.scrolledLinearView);
        insertPoint.addView(v, insertPoint.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}

