package uk.ac.aston.cs3mdd.mealplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentHomeBinding;
import uk.ac.aston.cs3mdd.mealplanner.quote_model.Quote;
import uk.ac.aston.cs3mdd.mealplanner.quote_model.QuoteDataSource;
import uk.ac.aston.cs3mdd.mealplanner.quote_model.QuoteViewModel;
import uk.ac.aston.cs3mdd.mealplanner.service.QuoteRequest;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    // Reference: CS3MDD Lab 2
    private QuoteViewModel quoteModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quoteModel = new ViewModelProvider(requireActivity()).get(QuoteViewModel.class);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String API_URL = "https://api.api-ninjas.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuoteRequest request = retrofit.create(QuoteRequest.class);


        quoteModel.requestQuote(new QuoteDataSource(request));
        quoteModel.getQuote().observe(getViewLifecycleOwner(), quote -> binding.tvMotivationalQuote.setText(quote.getQuote()));
    }

    // Helper Methods
}