package com.example.myapplication.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Assurez-vous que cette URL correspond à votre serveur backend
    private static final String BASE_URL = "http://10.0.2.2:8080/";  // pour l'émulateur Android
    // OU
    // private static final String BASE_URL = "http://192.168.x.x:8080/";  // pour un appareil physique

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}