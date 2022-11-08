package com.lika85456.levnepivo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.android.material.textfield.TextInputEditText;
import com.lika85456.levnepivo.components.BeerCard;
import com.lika85456.levnepivo.lib.BeerAPI;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<BeerCard> beerCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set title
        setTitle("Seznam slev na pivo!");

        loadBeers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void onBeerLoaded(ArrayList<BeerAPI.BeerDiscount> result) {
        setContentView(R.layout.activity_main);
        LinearLayout beers = findViewById(R.id.beers);
        // generate beerCards
        beerCards = new ArrayList<>();
        for(BeerAPI.BeerDiscount beerDiscount: result){
            beerCards.add(new BeerCard(this, beerDiscount));
        }

        // add beerCards to layout
        for(BeerCard beerCard: beerCards){
            beers.addView(beerCard);
        }

        // on search filter beer names and show only matching
        TextInputEditText search = findViewById(R.id.search);
        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String search = charSequence.toString().toLowerCase();
                for(BeerCard beerCard: beerCards){
                    if(beerCard.getDiscount().beer.name.toLowerCase().contains(search)){
                        beerCard.setVisibility(View.VISIBLE);
                    }else{
                        beerCard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // on scroll up refresh
        SwipeRefreshLayout scrollView = findViewById(R.id.swipeRefresher);
        scrollView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBeers();
            }
        });
    }

    private void onBeerLoadFailed(Exception exception){
        setContentView(R.layout.error);

        // set listener to reload
        findViewById(R.id.reloadButton).setOnClickListener(v -> loadBeers());

        // TODO Log error to google analytics
    }

    private void loadBeers(){
        setContentView(R.layout.loading);
        BeerAPI.FetchDiscountsTask(this::onBeerLoaded, this::onBeerLoadFailed).execute();
    }
}