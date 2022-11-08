package com.lika85456.levnepivo.components;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.lika85456.levnepivo.R;
import com.lika85456.levnepivo.lib.BeerAPI;
import com.lika85456.levnepivo.lib.DownloadImageTask;

public class BeerCard extends FrameLayout {
    private BeerAPI.BeerDiscount discount;

    public BeerCard(Context context, BeerAPI.BeerDiscount discount) {
        super(context);
        this.discount = discount;
        inflate(getContext(), R.layout.beer_view, this);

        // set title
        TextView titleView = findViewById(R.id.beer_title);
        // remove "Pivo" from start of the name
        String title = discount.beer.name.substring(5);
        titleView.setText(title);

        // set image
        ImageView imageView = findViewById(R.id.beer_image);
        new DownloadImageTask(imageView).execute(this.discount.beer.imageUrl);

        // set price
        TextView priceView = findViewById(R.id.beer_price);
        String priceText = discount.discounts.get(0).pricePerVolume;
        // remove "cena" at the beginning of the price
        priceText = priceText.substring(5);
        priceView.setText(priceText);

        // set best discount logo "beer_header_discount"
        ImageView discountView = findViewById(R.id.beer_header_discount);
        new DownloadImageTask(discountView).execute(this.discount.discounts.get(0).providerImageUrl);

        Log.i("BeerCard", "BeerCard: " + this.discount.beer.imageUrl);

    }

    public BeerAPI.BeerDiscount getDiscount() {
        return discount;
    }
}
