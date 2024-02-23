package uk.ac.aston.cs3ip.plannify.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import uk.ac.aston.cs3ip.plannify.BuildConfig;
import uk.ac.aston.cs3ip.plannify.models.quote.Quote;

public interface QuoteService {

    // Reference: Mobile Design and Development Lab 4
    @Headers("x-api-key: " + BuildConfig.NINJA_API_KEY)
    @GET("v1/quotes?category=health")
    Call<List<Quote>> getQuotes();

}
