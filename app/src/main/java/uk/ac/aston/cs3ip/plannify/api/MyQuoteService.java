package uk.ac.aston.cs3ip.plannify.api;

import retrofit2.Call;
import retrofit2.http.GET;
import uk.ac.aston.cs3ip.plannify.models.network_quote.NetworkQuote;

public interface MyQuoteService {

    // Reference: Mobile Design and Development Lab 4
    @GET("/apiv1/qod/?tags=health")
    Call<NetworkQuote> getHealthQuoteOfTheDay();
}
