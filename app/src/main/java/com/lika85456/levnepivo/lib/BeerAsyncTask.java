package com.lika85456.levnepivo.lib;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Neat way to fetch data
 * @see <a href="https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a">...</a>
 */
public class BeerAsyncTask extends AsyncTask<Void, Void, BeerAsyncTask.FetchResult> {

    public class FetchResult {
        public ArrayList<BeerAPI.BeerDiscount> result;
        public Exception exception;

        FetchResult(ArrayList<BeerAPI.BeerDiscount> result, Exception exception) {
            this.result = result;
            this.exception = exception;
        }
    }

    public interface AsyncResponse {
        void processFinish(FetchResult output);
    }

    public AsyncResponse delegate = null;

    public BeerAsyncTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected FetchResult doInBackground(Void... voids) {
        try {
            return new FetchResult(BeerAPI.fetchDiscounts(), null);
        } catch (IOException e) {
            e.printStackTrace();
            return new FetchResult(null, e);
        }
    }

    @Override
    protected void onPostExecute(FetchResult result) {
        delegate.processFinish(result);
    }
}
