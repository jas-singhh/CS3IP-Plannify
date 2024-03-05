package uk.ac.aston.cs3ip.plannify.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.cs3ip.plannify.BuildConfig;

public class RetrofitClient {
    /*
    *  Reference: https://blog.devgenius.io/thread-safe-network-retrofit-singleton-7cf5b143459c
    */

    private static Retrofit RECIPE_RETROFIT = null;
    private static Retrofit QUOTE_RETROFIT = null;

    private RetrofitClient(){}

    // thread safe
    public static Retrofit getRecipeRetrofitInstance() {
        if (RECIPE_RETROFIT == null) {
            synchronized (RetrofitClient.class) {
                RECIPE_RETROFIT = new Retrofit.Builder()
                        .baseUrl(BuildConfig.SPOONACULAR_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }

        return RECIPE_RETROFIT;
    }

    public static Retrofit getQuoteRetrofitInstance() {
        if (QUOTE_RETROFIT == null) {
            synchronized (RetrofitClient.class) {
                QUOTE_RETROFIT = new Retrofit.Builder()
                        .baseUrl(BuildConfig.PAPER_QUOTES_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }

        return QUOTE_RETROFIT;
    }

}
