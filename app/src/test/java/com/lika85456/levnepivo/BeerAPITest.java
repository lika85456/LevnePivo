package com.lika85456.levnepivo;

import org.junit.Test;

import static org.junit.Assert.*;

import com.lika85456.levnepivo.lib.BeerAPI;

import java.io.IOException;
import java.util.ArrayList;

public class BeerAPITest {
    @Test
    public void itFetches() {
        try {
            ArrayList<BeerAPI.BeerDiscount> beers = BeerAPI.fetchDiscounts();

            for(BeerAPI.BeerDiscount beer: beers){
                System.out.println(beer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}