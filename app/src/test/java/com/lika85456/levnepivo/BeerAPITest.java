package com.lika85456.levnepivo;

import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.*;

import com.lika85456.levnepivo.lib.BeerAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class BeerAPITest {

    // image regex
    // @see http://www.java2s.com/example/java/java.util.regex/is-string-an-image-url.html
    private final static Pattern IMG_URL = Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)");

    /**
     * This test may fail, if Kupi changes it's DOM structure, or if internet is not available.
     */
    @Test
    public void itFetchesReasonableData() throws IOException, NullPointerException {
            ArrayList<BeerAPI.BeerDiscount> discounts = BeerAPI.fetchDiscounts();
            assert(discounts.size() > 0);
            for(BeerAPI.BeerDiscount discount : discounts){
                // check all names contain pivo
                assert(discount.beer.name.contains("Pivo"));

                // check all beers have image url
                assert(discount.beer.imageUrl.startsWith("https://"));
                assert(IMG_URL.matcher(discount.beer.imageUrl).matches());

                // check all beers have discounts
                assert(discount.discounts.size()>0);

                // check all discounts are reasonable
                for(BeerAPI.BeerProviderDiscount providerDiscount : discount.discounts){
                    // provider name
                    assert(providerDiscount.providerName.length()>0);

                    // provider url
                    assert(IMG_URL.matcher(providerDiscount.providerImageUrl).matches());

                    // price per volume
                    assert(providerDiscount.pricePerVolume.price > 0 && providerDiscount.pricePerVolume.price < 2000);
                    assert(providerDiscount.pricePerVolume.volume > 0 && providerDiscount.pricePerVolume.volume < 20);
                    assert(providerDiscount.pricePerVolume.getPricePerVolume() > 0 && providerDiscount.pricePerVolume.getPricePerVolume() < 100);

                    // valid to
                    assert(providerDiscount.validTo.length()>0);
                }
            }
    }
}