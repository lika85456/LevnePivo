package com.lika85456.levnepivo.lib;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BeerAPI {
    private static String apiUrl = "https://www.kupi.cz/slevy/pivo#sgc=pivo";

    public static ArrayList<BeerDiscount> fetchDiscounts() throws IOException {
        //Connect to website
        Document document = Jsoup.connect(BeerAPI.apiUrl).get();
        Elements discountBlocks = document.getElementsByClass("group_discounts active");

        ArrayList<BeerDiscount> allDiscounts = new ArrayList();
        HashMap<String, BeerProvider> providers = new HashMap();
        HashMap<String, Beer> beers = new HashMap();

        for(Element discountElement: discountBlocks){
            allDiscounts.addAll(parseDiscount(discountElement, providers, beers));
        }

        System.out.println(discountBlocks.get(0).getElementsByTag("strong").get(0).text());
        //get(0).getElementsByClass("strong").get(0).text()

        return allDiscounts;
    }

    private static Beer parseBeer(Element beerElement){
        Beer beer = new Beer();

        beer.name = beerElement.getElementsByTag("strong").get(0).text();
        beer.imageUrl = beerElement.getElementsByClass("product_image").get(0).getElementsByTag("img").attr("src");

        return beer;
    }

    private static ArrayList<BeerDiscount> parseDiscount(Element discountElement, HashMap<String, BeerProvider> providers, HashMap<String, Beer> beers) {
        ArrayList<BeerDiscount> discounts = new ArrayList();

        // check beer existance
        String beerName = discountElement.getElementsByTag("strong").get(0).text();
        Beer beer = beers.get(beerName);
        if(beer==null){
            beer = parseBeer(discountElement);
            beers.put(beerName, beer);
        }

        BeerDiscount b = new BeerDiscount();
        b.beer = beer;
        discounts.add(b);
        return discounts;
    }

    public class BeerProvider {
        String name;
        String imageUrl;

        @Override
        public String toString() {
            return "BeerProvider{" +
                    "name='" + name + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    '}';
        }
    }

    public static class Beer {
        String name;
        String imageUrl;

        @Override
        public String toString() {
            return "Beer{" +
                    "name='" + name + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    '}';
        }
    }

    public static class BeerDiscount {
        Beer beer;
        BeerProvider provider;
        String pricePerVolume; // why parse

        @Override
        public String toString() {
            return "BeerDiscount{" +
                    "beer=" + beer +
                    ", provider=" + provider +
                    ", pricePerVolume='" + pricePerVolume + '\'' +
                    '}';
        }
    }
}
