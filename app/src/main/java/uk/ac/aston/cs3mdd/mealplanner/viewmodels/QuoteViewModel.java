package uk.ac.aston.cs3mdd.mealplanner.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.aston.cs3mdd.mealplanner.data.quote.Quote;
import uk.ac.aston.cs3mdd.mealplanner.repos.QuotesRepository;

public class QuoteViewModel extends ViewModel {

    // Reference: Mobile Design and Development lab 2
    // Mutable data = data that can be changed
    // Live data = read-only data
    private MutableLiveData<Quote> quote;

    public QuoteViewModel() {
        super();
        quote = new MutableLiveData<>();
    }

    public MutableLiveData<Quote> getQuote() {
        return quote;
    }

    public void requestQuote(QuotesRepository quoteDataSource) {
        Call<List<Quote>> request = quoteDataSource.getQuote();
        request.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(@NonNull Call<List<Quote>> call, @NonNull Response<List<Quote>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    storeResponse(response.body().get(0));//store the first received response
                } else {
                    assert response.errorBody() != null;
                    Log.i("err", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Quote>> call, @NonNull Throwable t) {
                Log.i("err", "Error in requestQuote: " + t);
            }
        });
    }

    private void storeResponse(Quote response) {quote.setValue(response);}
}
