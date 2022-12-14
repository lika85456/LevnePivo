package com.lika85456.levnepivo;

import static com.lika85456.levnepivo.services.NotificationsBroadcastReceiver.CHANNEL_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.lika85456.levnepivo.components.BeerCard;
import com.lika85456.levnepivo.lib.BeerAPI;
import com.lika85456.levnepivo.lib.LovedBeersStorage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LovedBeersStorage lovedBeersStorage;

    private LinearLayout beersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Seznam slev na pivo!");

        lovedBeersStorage = new LovedBeersStorage(this);

        createNotificationChannel();

        loadBeers();
    }

    /**
     * Shows loading screen and starts loading beers
     */
    private void loadBeers() {
        setContentView(R.layout.loading);
        BeerAPI.FetchDiscountsTask(this::onBeerLoaded, this::onBeerLoadFailed).execute();
    }

    /**
     * Notification channel is required for notifications on Android 8+
     */
    private void createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Nove pivni slevy";
                String description = "Notifikacni kanal pro nove pivni slevy";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        } catch (Exception e) {
            Log.d("MainActivity", "Creating notification channel failed. Error: " + e.getMessage());
        }

    }

    private void onBeerLoaded(ArrayList<BeerAPI.BeerDiscount> result) {
        // set proper view
        setContentView(R.layout.activity_main);
        beersLayout = findViewById(R.id.beers);

        // generate cards
        ArrayList<BeerCard> cards = generateCards(result);
        cards = sortWithLovedBeers(cards, lovedBeersStorage.getLovedBeers());

        // render cards
        for (BeerCard card : cards) {
            beersLayout.addView(card);
        }

        // initialize other components
        initializeSearch();
        initializeScrollRefresh();
    }

    /**
     * Loads error layout with the ability to reload beers.
     */
    private void onBeerLoadFailed() {
        setContentView(R.layout.error);

        // set listener to reload
        findViewById(R.id.reloadButton).setOnClickListener(v -> loadBeers());

        // TODO Log error to google analytics?
        Log.e("MainActivity", "Loading beers failed.");
    }


    /**
     * Constructs beer cards with "onHeartClick" callback from discounts.
     *
     * @param discounts
     * @return
     */
    private ArrayList<BeerCard> generateCards(ArrayList<BeerAPI.BeerDiscount> discounts) {
        ArrayList<BeerCard> cards = new ArrayList<>();
        for (BeerAPI.BeerDiscount discount : discounts) {
            BeerCard beerCard = new BeerCard(this, discount, lovedBeersStorage.isLoved(discount.beer.name), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // toggle loved in storage
                    if (lovedBeersStorage.isLoved(discount.beer.name)) {
                        lovedBeersStorage.remove(discount.beer.name);
                    } else {
                        lovedBeersStorage.add(discount.beer.name);
                    }

                    // find beer and update heart icon
                    for (int i = 0; i < beersLayout.getChildCount(); i++) {
                        BeerCard beerCard = (BeerCard) beersLayout.getChildAt(i);
                        if (beerCard.getDiscount().beer.name.equals(discount.beer.name)) {
                            beerCard.setFavourite(lovedBeersStorage.isLoved(discount.beer.name));
                        }
                    }

                    updateBeerOrder();
                }
            });
            cards.add(beerCard);
        }

        return cards;
    }

    /**
     * Sort the beer cards so that the loved beers are on top.
     * It doesn't sort alphabetically.
     *
     * @param beerCards
     * @param lovedBeers
     * @return
     */
    private ArrayList<BeerCard> sortWithLovedBeers(ArrayList<BeerCard> beerCards, ArrayList<String> lovedBeers) {
        ArrayList<BeerCard> sortedCards = new ArrayList<>();

        // loved beers first
        for (String lovedBeer : lovedBeers) {
            for (BeerCard beerCard : beerCards) {
                if (beerCard.getDiscount().beer.name.equals(lovedBeer)) {
                    sortedCards.add(beerCard);
                }
            }
        }

        // other beers
        for (BeerCard beerCard : beerCards) {
            if (!sortedCards.contains(beerCard)) {
                sortedCards.add(beerCard);
            }
        }

        return sortedCards;
    }

    /**
     * Renders cards with new order
     */
    private void updateBeerOrder() {
        ArrayList<BeerCard> sortedCards = sortWithLovedBeers(getCards(), lovedBeersStorage.getLovedBeers());

        beersLayout.removeAllViews();
        for (BeerCard beerCard : sortedCards) {
            beersLayout.addView(beerCard);
        }
    }

    /**
     * Applies on text changed listener to search input. It filters the cards by name in current order.
     */
    private void initializeSearch() {
        TextInputEditText search = findViewById(R.id.search);
        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String search = charSequence.toString().toLowerCase();

                ArrayList<BeerCard> beerCards = getCards();

                // filter beerCards by setting visibility
                for (BeerCard beerCard : beerCards) {
                    if (beerCard.getDiscount().beer.name.toLowerCase().contains(search)) {
                        beerCard.setVisibility(View.VISIBLE);
                    } else {
                        beerCard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * Returns card instances in beer list
     *
     * @return
     */
    private ArrayList<BeerCard> getCards() {
        ArrayList<BeerCard> beerCards = new ArrayList<>();
        for (int j = 0; j < beersLayout.getChildCount(); j++) {
            beerCards.add((BeerCard) beersLayout.getChildAt(j));
        }

        return beerCards;
    }

    private void initializeScrollRefresh() {
        SwipeRefreshLayout scrollView = findViewById(R.id.swipeRefresher);
        scrollView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBeers();
            }
        });
    }
}