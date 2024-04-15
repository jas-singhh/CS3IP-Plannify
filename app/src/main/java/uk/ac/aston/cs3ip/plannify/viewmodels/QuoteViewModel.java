package uk.ac.aston.cs3ip.plannify.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.aston.cs3ip.plannify.models.network_quote.NetworkQuote;
import uk.ac.aston.cs3ip.plannify.repos.QuoteRepository;

public class QuoteViewModel extends ViewModel {

    // Reference: Mobile Design and Development lab 2
    // Mutable data = data that can be changed
    // Live data = read-only data

    private final MutableLiveData<NetworkQuote> healthQuoteOfTheDay;

    public QuoteViewModel() {
        super();
        healthQuoteOfTheDay = new MutableLiveData<>();
    }

    /**
     * Returns the quote of the day live data.
     *
     * @return quote of the day.
     */
    public MutableLiveData<NetworkQuote> getHealthQuoteOfTheDay() {
        return healthQuoteOfTheDay;
    }

    /**
     * Requests the quote through an API call and stores the response in the live data on success,
     * or outputs an error message on failure.
     *
     * @param quoteRepository quote repository for creating the API request.
     */
    public void requestHealthQuoteOfTheDay(QuoteRepository quoteRepository) {
        Call<NetworkQuote> request = quoteRepository.getHealthQuoteOfTheDay();
        request.enqueue(new Callback<NetworkQuote>() {
            @Override
            public void onResponse(@NonNull Call<NetworkQuote> call, @NonNull Response<NetworkQuote> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        storeResponse(response.body());//store the first received response
                } else {
                    if (response.errorBody() != null)
                        Log.i("err", "quote response not successful: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NetworkQuote> call, @NonNull Throwable t) {
                Log.i("err", "Error in requestQuote: " + t);
            }
        });
    }

    /**
     * Stores the response into the live data.
     *
     * @param response response to store into the live data.
     */
    private void storeResponse(NetworkQuote response) {
        healthQuoteOfTheDay.setValue(response);
    }
}
