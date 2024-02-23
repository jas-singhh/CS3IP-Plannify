package uk.ac.aston.cs3ip.plannify.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    /*
    *  Reference: https://blog.devgenius.io/thread-safe-network-retrofit-singleton-7cf5b143459c
    */

    private static Retrofit INSTANCE = null;

    private RetrofitClient(){}

    public static synchronized Retrofit getInstance(String baseUrl) {
        if (INSTANCE == null) {
            INSTANCE = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return INSTANCE;
    }

}
