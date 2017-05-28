package com.jatin.mapsdemo;


import com.jatin.mapsdemo.request.DirectionOriginRequest;


public class GoogleDirection {
    public static DirectionOriginRequest withServerKey(String apiKey) {
        return new DirectionOriginRequest(apiKey);
    }
}
