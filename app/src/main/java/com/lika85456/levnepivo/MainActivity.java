package com.lika85456.levnepivo;

import static com.lika85456.levnepivo.NotificationsBroadcastReceiver.CHANNEL_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.lika85456.levnepivo.lib.BeerDiscountsStorage;
import com.lika85456.levnepivo.lib.LovedBeersStorage;
import com.lika85456.levnepivo.services.NewBeerDiscountsService;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private LovedBeersStorage lovedBeersStorage;

    private LinearLayout beersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            createNotificationChannel();
        }
        catch(Exception e){
            Log.d("MainActivity", "Error: " + e.getMessage());
        }
        lovedBeersStorage = new LovedBeersStorage(this);

        // set title
        setTitle("Seznam slev na pivo!");

        loadBeers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nove pivni slevy";
            String description = "Notifikacni kanal pro nove pivni slevy";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private ArrayList<BeerCard> generateCards(ArrayList<BeerAPI.BeerDiscount> discounts){
        ArrayList<BeerCard> cards = new ArrayList<>();
        for(BeerAPI.BeerDiscount discount : discounts){
            BeerCard beerCard = new BeerCard(this, discount, lovedBeersStorage.isLoved(discount.beer.name), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(lovedBeersStorage.isLoved(discount.beer.name)){
                        lovedBeersStorage.remove(discount.beer.name);
                    }else{
                        lovedBeersStorage.add(discount.beer.name);
                    }

                    // find beer and update heart icon
                    for(int i = 0; i < beersLayout.getChildCount(); i++){
                        BeerCard beerCard = (BeerCard) beersLayout.getChildAt(i);
                        if(beerCard.getDiscount().beer.name.equals(discount.beer.name)){
                            beerCard.setFavourite(lovedBeersStorage.isLoved(discount.beer.name));
                        }
                    }

                    changeOrder();
                }
            });
            cards.add(beerCard);
        }

        return cards;
    }

    private ArrayList<BeerCard> sortWithLovedBeers(ArrayList<BeerCard> beerCards, ArrayList<String> lovedBeers){
        ArrayList<BeerCard> sortedCards = new ArrayList<>();

        // loved beers first
        for(String lovedBeer : lovedBeers){
            for(BeerCard beerCard : beerCards){
                if(beerCard.getDiscount().beer.name.equals(lovedBeer)){
                    sortedCards.add(beerCard);
                }
            }
        }

        // other beers
        for(BeerCard beerCard : beerCards){
            if(!sortedCards.contains(beerCard)){
                sortedCards.add(beerCard);
            }
        }

        return sortedCards;
    }

    private void changeOrder(){
        ArrayList<BeerCard> sortedCards = sortWithLovedBeers(getCards(), lovedBeersStorage.getLovedBeers());

        beersLayout.removeAllViews();
        for(BeerCard beerCard : sortedCards){
            beersLayout.addView(beerCard);
        }
    }

    private void initializeSearch(){
        // on search filter beer names and show only matching
        TextInputEditText search = findViewById(R.id.search);
        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String search = charSequence.toString().toLowerCase();

                ArrayList<BeerCard> beerCards = getCards();

                // filter beerCards
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
    }

    private ArrayList<BeerCard> getCards(){
        ArrayList<BeerCard> beerCards = new ArrayList<>();
        for(int j = 0; j < beersLayout.getChildCount(); j++){
            beerCards.add((BeerCard) beersLayout.getChildAt(j));
        }

        return beerCards;
    }

    private void initializeScrollRefresh(){
        // on scroll up refresh
        SwipeRefreshLayout scrollView = findViewById(R.id.swipeRefresher);
        scrollView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBeers();
            }
        });
    }

    private void onBeerLoaded(ArrayList<BeerAPI.BeerDiscount> result) {
        setContentView(R.layout.activity_main);
        beersLayout = findViewById(R.id.beers);

        ArrayList<BeerCard> cards = generateCards(result);
        cards = sortWithLovedBeers(cards, lovedBeersStorage.getLovedBeers());

        // render cards
        for(BeerCard card : cards){
            beersLayout.addView(card);
        }

        initializeSearch();
        initializeScrollRefresh();

        BeerDiscountsStorage beerDiscountsStorage = new BeerDiscountsStorage(this);

    }

    private void onBeerLoadFailed(Exception exception){
        setContentView(R.layout.error);

        // set listener to reload
        findViewById(R.id.reloadButton).setOnClickListener(v -> loadBeers());

        // TODO Log error to google analytics?
    }

    private void loadBeers(){
        setContentView(R.layout.loading);
        BeerAPI.FetchDiscountsTask(this::onBeerLoaded, this::onBeerLoadFailed).execute();
    }
}