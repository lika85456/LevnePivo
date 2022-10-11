package com.lika85456.levnepivo.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.lika85456.levnepivo.R;
import com.lika85456.levnepivo.lib.BeerAPI;

public class BeerCard extends CardView {
    private BeerAPI.BeerDiscount discount;

    public BeerCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public BeerCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BeerCard(Context context, BeerAPI.BeerDiscount discount) {
        super(context);
        this.discount = discount;
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.beer_view, this);

        // get text
        TextView t = findViewById(R.id.beerText);
        t.setText(this.discount.beer.name);
    }
}
