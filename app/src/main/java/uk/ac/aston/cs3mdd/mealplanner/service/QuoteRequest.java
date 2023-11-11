package uk.ac.aston.cs3mdd.mealplanner.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import uk.ac.aston.cs3mdd.mealplanner.quote_model.Quote;

public interface QuoteRequest {

    String API_KEY = "jBW5VV4Ncyktg+m1B1kNSw==gZk04DY01boWx9Ee";

    // Reference: Mobile Design and Development Lab 4
    @Headers("x-api-key: " + API_KEY)
    @GET("v1/quotes?category=health")
    Call<List<Quote>> requestQuote();

}
