package com.example.denjae.androidprototype;

/**
 * Created by denjae on 26.07.14.
 */
public class APIconnections {
    int foursquare;
    public static final String connection = "https://foursquare.com/oauth2/authenticate?client_id=BXBK3ZES42YG5KDEBCCFCOKZTYKZIP1LYZYXCJCGNO2ORTB5&response_type=code&redirect_uri=https://foursquare.com/oauth2/authenticate";
    String venueURL;
    String city;

    public int getFoursquare(String city){
        this.city=city;
        String venueURL= "https://api.foursquare.com/v2/venues/search?near="+city +"&client_id=BXBK3ZES42YG5KDEBCCFCOKZTYKZIP1LYZYXCJCGNO2ORTB5&client_secret=KE53YHPKFWUS4LJ5JLU1EFOKUPPDBFDFZWZINVBK0QMHIATA&v=20140726";

        return foursquare;
    }

}
