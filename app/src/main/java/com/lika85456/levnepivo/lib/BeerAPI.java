package com.lika85456.levnepivo.lib;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

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
            discount.pricePerVolume = discountElement.getElementsByClass("discounts_price").first().text();
            // keep just price per volume without the discounted %
            discount.pricePerVolume = discount.pricePerVolume.split("l")[0] + "l";
            toRet.discounts.add(discount);
        }

        return toRet;
    }

    /**
     * Contains beer definition and it's discounts from different providers
     */
    public static class BeerDiscount {
        // beer
        public Beer beer;
        public ArrayList<BeerProviderDiscount> discounts;

        public BeerDiscount(){
            beer = new Beer();
            discounts = new ArrayList<>();
        }
    }

    public static class BeerProviderDiscount {
        public String providerName;
        public String providerImageUrl;
        public String pricePerVolume;
        public String validTo;
    }
    public static class Beer {
        public String name;
        public String imageUrl;
    }


}
