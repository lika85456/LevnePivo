package com.lika85456.levnepivo.components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lika85456.levnepivo.R;
import com.lika85456.levnepivo.lib.BeerAPI;
import com.lika85456.levnepivo.lib.DownloadImageTask;

public class BeerCard extends FrameLayout {
    private final BeerAPI.BeerDiscount discount;

    private final TextView titleTextView;
    private final TextView priceTextView;
    private final ImageView beerImageView;
    private final ImageView bestProviderImageView;

    private final LinearLayout providersLayout;
    private final ImageView heartIcon;

    private boolean isExpanded = false;
    private boolean isFavourite = false;

    public BeerCard(Context context, BeerAPI.BeerDiscount discount, boolean isFavourite, OnClickListener onFavouriteClickListener) {
        super(context);
        this.discount = discount;
        inflate(getContext(), R.layout.beer_view, this);

        this.isFavourite = isFavourite;

        titleTextView = findViewById(R.id.beer_title);
        beerImageView = findViewById(R.id.beer_image);
        priceTextView = findViewById(R.id.beer_price);
        bestProviderImageView = findViewById(R.id.beer_header_discount);

        providersLayout = findViewById(R.id.beer_discounts_list);

        heartIcon = findViewById(R.id.beer_heart_icon);

        fillCard(discount);
        fillDiscounts(discount);

        // on click expand or collapse beer discounts
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded) {
                    providersLayout.setVisibility(GONE);
                    isExpanded = false;
                } else {
                    providersLayout.setVisibility(VISIBLE);
                    isExpanded = true;
                }
            }
        });

        // on heart click
        heartIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavouriteClickListener.onClick(view);
            }
        });
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
        if (isFavourite) {
            heartIcon.setImageResource(R.drawable.heart_filled);
        } else {
            heartIcon.setImageResource(R.drawable.heart_outline);
        }
    }

    /**
     * Fills expandable layout with the rest of the discounts.
     *
     * @param discount
     */
    private void fillDiscounts(BeerAPI.BeerDiscount discount) {
        // if there is only one discount add TextView with "Na tohle pivo momentálně není žádná další sleva"
        if (discount.discounts.size() == 1) {
            TextView noDiscounts = new TextView(getContext());
            noDiscounts.setText("Na tohle pivo momentálně není žádná další sleva");
            noDiscounts.setTextColor(getResources().getColor(R.color.black));
            providersLayout.addView(noDiscounts);
            return;
        }

        // for each except the first one
        for (int i = 1; i < discount.discounts.size(); i++) {
            LinearLayout discountLayout = new LinearLayout(getContext());
            discountLayout.setOrientation(LinearLayout.HORIZONTAL);
            discountLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            // add provider image
            ImageView providerImageView = new ImageView(getContext());
            final float scale = getContext().getResources().getDisplayMetrics().density;
            int width = (int) (96 * scale + 0.5f);
            int height = (int) (40 * scale + 0.5f);
            providerImageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            providerImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            new DownloadImageTask(providerImageView).execute(discount.discounts.get(i).providerImageUrl);

            // add price
            TextView priceTextView = new TextView(getContext());
            priceTextView.setText(discount.discounts.get(i).pricePerVolume.pricePerVolume);
            priceTextView.setTextSize(20);
            priceTextView.setPadding(10, 0, 0, 0);
            priceTextView.setTextColor(getResources().getColor(R.color.black));

            // add to layout
            discountLayout.addView(providerImageView);
            discountLayout.addView(priceTextView);
            providersLayout.addView(discountLayout);
        }
    }

    /**
     * Fills main layout with the discount data.
     * @param discount
     */
    private void fillCard(BeerAPI.BeerDiscount discount) {
        // set title
        // remove "Pivo" from start of the name
        String title = discount.beer.name.substring(5);
        titleTextView.setText(title);

        // set image
        new DownloadImageTask(beerImageView).execute(this.discount.beer.imageUrl);

        // set price
        String priceText = discount.discounts.get(0).pricePerVolume.pricePerVolume;
        priceTextView.setText(priceText);

        // set best discount logo "beer_header_discount"
        new DownloadImageTask(bestProviderImageView).execute(this.discount.discounts.get(0).providerImageUrl);

        // set heart icon depending on isFavourite
        if (isFavourite) {
            heartIcon.setImageResource(R.drawable.heart_filled);
        } else {
            heartIcon.setImageResource(R.drawable.heart_outline);
        }
    }

    public BeerAPI.BeerDiscount getDiscount() {
        return discount;
    }
}
