package com.lika85456.levnepivo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.lika85456.levnepivo.components.BeerCard;
import com.lika85456.levnepivo.lib.BeerAPI;
import com.lika85456.levnepivo.lib.BeerAsyncTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        BeerAsyncTask asyncTask = (BeerAsyncTask) new BeerAsyncTask(new BeerAsyncTask.AsyncResponse(){
            @Override
            public void processFinish(BeerAsyncTask.FetchResult output) {
                onBeerLoaded(output);
            }
        }).execute();
    }

    private void onBeerLoaded(BeerAsyncTask.FetchResult result){
        setContentView(R.layout.activity_main);
        LinearLayout beers = findViewById(R.id.beers);
        for(BeerAPI.BeerDiscount beerDiscount:result.result){
            BeerCard beerView = new BeerCard(this.getBaseContext(), beerDiscount);
            beers.addView(beerView);
        }
    }
}