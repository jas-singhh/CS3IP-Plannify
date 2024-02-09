package uk.ac.aston.cs3mdd.mealplanner.repos;

import java.util.List;

import retrofit2.Call;
import uk.ac.aston.cs3mdd.mealplanner.api.QuoteService;
import uk.ac.aston.cs3mdd.mealplanner.models.quote.Quote;

public class QuotesRepository {

    // This class will store the data retrieved by the API request
    // Reference: CS3MDD Lab 4

    private final QuoteService request;

    public QuotesRepository(QuoteService request) {this.request = request;}

    public Call<List<Quote>> getQuote() {
        return request.getQuotes();
    }

}
