package uk.ac.aston.cs3ip.plannify.repos;

import retrofit2.Call;
import uk.ac.aston.cs3ip.plannify.api.MyQuoteService;
import uk.ac.aston.cs3ip.plannify.api.RetrofitClient;
import uk.ac.aston.cs3ip.plannify.models.network_quote.NetworkQuote;

public class QuoteRepository {

    // This class will store the data retrieved by the API request
    // Reference: CS3MDD Lab 4

    private final MyQuoteService request;

    public QuoteRepository() {
        this.request = RetrofitClient.getQuoteRetrofitInstance().create(MyQuoteService.class);
    }

    /**
     * Returns the health quote of the day.
     *
     * @return the health quote of the day.
     */
    public Call<NetworkQuote> getHealthQuoteOfTheDay() {
        return request.getHealthQuoteOfTheDay();
    }

}
