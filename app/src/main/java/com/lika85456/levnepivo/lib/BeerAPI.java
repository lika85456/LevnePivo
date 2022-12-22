package com.lika85456.levnepivo.lib;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * BeerAPI is an API implementation for getting beer discounts from kupi.cz
 */
public class BeerAPI {
    private static final String apiUrl = "https://www.kupi.cz/slevy/pivo#sgc=pivo";

    public static ArrayList<BeerDiscount> fetchDiscounts() throws IOException, NullPointerException{
        Elements fetchedElements = fetchDiscountElements();

        // parse HTML elements to custom discount structure
        ArrayList<BeerDiscount> beerElements = new ArrayList<>();
        for(Element fetchedElement: fetchedElements){
            beerElements.add(parseBeerElement(fetchedElement));
        }

        return beerElements;
    }

    public static AsyncTask<Void, Void, ArrayList<BeerDiscount>> FetchDiscountsTask(@NonNull OnBeerLoaded onBeerLoaded, @NonNull OnBeerLoadFailed onBeerLoadFailed){
        return new AsyncTask<Void, Void, ArrayList<BeerDiscount>>() {
            @Override
            protected ArrayList<BeerDiscount> doInBackground(Void... voids) {
                try {
                    return fetchDiscounts();
                } catch (IOException e) {
                    onBeerLoadFailed.onBeerLoadFailed(e);
                } catch (NullPointerException e){
                    onBeerLoadFailed.onBeerLoadFailed(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<BeerDiscount> beerDiscounts) {
                super.onPostExecute(beerDiscounts);
                onBeerLoaded.onBeerLoaded(beerDiscounts);
            }
        };
    }

    public interface OnBeerLoaded{
        void onBeerLoaded(ArrayList<BeerDiscount> result);
    }

    public interface OnBeerLoadFailed{
        void onBeerLoadFailed(Exception exception);
    }

    /**
     * @return DOM elements containing discount for each beer with its provider discounts
     * @throws IOException if internet is not available
     */
    private static Elements fetchDiscountElements() throws IOException{
        Document document = Jsoup.connect(BeerAPI.apiUrl).get();
        return document.getElementsByClass("group_discounts active");
    }

    /**
     * Parses beer discount from its DOM element to custom structure
     * @implNote Provider beer discounts are sorted in ascending order
     * @param element DOM element of the discount
     * @return parsed beer discount
     */
    private static BeerDiscount parseBeerElement(@NonNull Element element) throws NullPointerException{
        BeerDiscount toRet = new BeerDiscount();

        // parse beer
        toRet.beer.name = element.getElementsByClass("product_link_history").first().getElementsByTag("strong").first().text();
        toRet.beer.imageUrl = element.getElementsByClass("product_image").first().getElementsByTag("img").first().attr("data-src");

        // parse provider discounts
        Elements discounts = element.getElementsByClass("discount_row");
        toRet.discounts = new ArrayList<>();
        for(Element discountElement : discounts){
            BeerProviderDiscount discount = new BeerProviderDiscount();
            discount.providerName = discountElement.getElementsByTag("span").first().text();
            discount.providerImageUrl = discountElement.getElementsByTag("img").first().attr("src");
            discount.validTo = discountElement.getElementsByClass("discounts_validity").first().text();

            // get price per volume
            String pricePerVolume = discountElement.getElementsByClass("discounts_price").first().text();
            // remove discount percentage
            pricePerVolume = pricePerVolume.split("l")[0] + "l";
            // remove "cena " prefix
            pricePerVolume = pricePerVolume.substring(5);
            discount.pricePerVolume = new PricePerVolume(pricePerVolume);

            toRet.discounts.add(discount);
        }

        // sort discounts by price per volume ascending
        Arrays.sort(toRet.discounts.toArray(), (o1, o2) -> {
            BeerProviderDiscount discount1 = (BeerProviderDiscount) o1;
            BeerProviderDiscount discount2 = (BeerProviderDiscount) o2;
            return Math.round((discount1.pricePerVolume.getPricePerVolume() - discount2.pricePerVolume.getPricePerVolume())*100000);
        });

        return toRet;
    }

    /**
     * Contains beer definition and it's discounts from different providers
     */
    public static class BeerDiscount {
        // beer
        public Beer beer;
        public ArrayList<BeerProviderDiscount> discounts;

        public BeerDiscount() {
            beer = new Beer();
            discounts = new ArrayList<>();
        }

        public static BeerDiscount fromString(String json){
            try {
                JSONObject jsonObject = new JSONObject(json);
                BeerDiscount toRet = new BeerDiscount();
                toRet.beer = Beer.fromString(jsonObject.getString("beer"));
                toRet.discounts = new ArrayList<>();
                for(int i = 0; i < jsonObject.getJSONArray("discounts").length(); i++){
                    toRet.discounts.add(BeerProviderDiscount.fromString(jsonObject.getJSONArray("discounts").getString(i)));
                }
                return toRet;
            } catch (Exception e){
                Log.e("BeerDiscount", "Error parsing JSON", e);
            }
            return null;
        }

        public String toJSON() {
            return JSONObject.wrap(this).toString();
        }

    }

    public static class BeerProviderDiscount {
        public String providerName;
        public String providerImageUrl;
        public PricePerVolume pricePerVolume;
        public String validTo;

        public static BeerProviderDiscount fromString(String json){
            try {
                JSONObject jsonObject = new JSONObject(json);
                BeerProviderDiscount toRet = new BeerProviderDiscount();
                toRet.providerName = jsonObject.getString("providerName");
                toRet.providerImageUrl = jsonObject.getString("providerImageUrl");
                toRet.pricePerVolume = PricePerVolume.fromString(jsonObject.getString("pricePerVolume"));
                toRet.validTo = jsonObject.getString("validTo");
                return toRet;
            } catch (Exception e){
                Log.e("BeerProviderDiscount", "Error parsing JSON", e);
            }
            return null;
        }

        public String toJSON() {
            return JSONObject.wrap(this).toString();
        }
    }

    public static class Beer {
        public String name;
        public String imageUrl;

        public static Beer fromString(String json){
            try {
                JSONObject jsonObject = new JSONObject(json);
                Beer toRet = new Beer();
                toRet.name = jsonObject.getString("name");
                toRet.imageUrl = jsonObject.getString("imageUrl");
                return toRet;
            } catch (Exception e){
                Log.e("Beer", "Error parsing JSON", e);
            }
            return null;
        }

        public String toJSON() {
            return JSONObject.wrap(this).toString();
        }
    }

    public static class PricePerVolume {
        public float price; // in czk
        public float volume; // in liters
        public float getPricePerVolume(){
            return price/volume;
        }
        public String pricePerVolume;

        public PricePerVolume(String pricePerVolume){
            this.pricePerVolume = pricePerVolume;
            // the format of pricePerVolume is the following:
            // 1,50 Kč / 0.5 l
            // 124,90 Kč / 6x 2 l
            // 69,69 Kč / 4x 0.5 l

            // split by "/" to separate price and volume
            String[] priceAndVolume = pricePerVolume.split("/");

            // parse price
            String priceString = priceAndVolume[0].split("Kč")[0].trim();
            price = Float.parseFloat(priceString.replace(",", "."));

            // parse volume and check for multiplier
            if(priceAndVolume[1].contains("x")){
                String[] volumeAndMultiplier = priceAndVolume[1].replace(" l", "").split("x");
                volume = Float.parseFloat(volumeAndMultiplier[1].trim().replace(",", ".")) * Float.parseFloat(volumeAndMultiplier[0].trim());
            } else {
                // parse volume
                String volumeString = priceAndVolume[1].split("l")[0].trim();
                volume = Float.parseFloat(volumeString.replace(",", "."));
            }
        }

        public static PricePerVolume fromString(String json){
            try {
                JSONObject jsonObject = new JSONObject(json);
                PricePerVolume toRet = new PricePerVolume(jsonObject.getString("pricePerVolume"));
                return toRet;
            } catch (Exception e){
                Log.e("PricePerVolume", "Error parsing JSON", e);
            }
            return null;
        }

        public String toJSON() {
            return JSONObject.wrap(this).toString();
        }
    }
}
