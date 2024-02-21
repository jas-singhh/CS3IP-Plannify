package uk.ac.aston.cs3mdd.mealplanner.views.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.cs3mdd.mealplanner.BuildConfig;
import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.adapters.HomeMealsAdapter;
import uk.ac.aston.cs3mdd.mealplanner.adapters.HomeMealsOnClickInterface;
import uk.ac.aston.cs3mdd.mealplanner.api.QuoteService;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentHomeBinding;
import uk.ac.aston.cs3mdd.mealplanner.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3mdd.mealplanner.repos.QuotesRepository;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.HomeViewModel;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.QuoteViewModel;
import uk.ac.aston.cs3mdd.mealplanner.views.dialogs.DialogLottie;
import uk.ac.aston.cs3mdd.mealplanner.views.dialogs.DialogSaveRecipe;

public class HomeFragment extends Fragment implements HomeMealsOnClickInterface {

    // Reference: CS3MDD Lab 2
    private QuoteViewModel quoteViewModel;
    private HomeViewModel homeViewModel;

    private DialogLottie animatedLoading;
    private HomeMealsAdapter mAdapter;

    private FragmentHomeBinding binding;
    private CompositeDisposable mDisposable;
    private SharedPreferences mSharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise variables
        animatedLoading = new DialogLottie(requireContext());
        mAdapter = new HomeMealsAdapter(this, new ArrayList<>());
        quoteViewModel = new ViewModelProvider(requireActivity()).get(QuoteViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(HomeViewModel.initializer)).get(HomeViewModel.class);
        mDisposable = new CompositeDisposable();
        mSharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE);

        // Perform Retrofit call to request the quote from the API
//        requestQuote();
//        requestBreakfastMeals();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

//        animatedLoading.show();

        restoreCheckedChip();

        // setup UI elements
        initChipGroupOnClickListeners();
        initRecyclerView();
        initOnClickForExpandMinimiseMotivationalQuote();

        // Observe the quote data requested from the API
        quoteViewModel.getQuote().observe(getViewLifecycleOwner(), quote -> binding.tvMotivationalQuote.setText(quote.getQuote()));

        homeViewModel.getRequestedRecipes().observe(getViewLifecycleOwner(), recipeResponse -> {
            mAdapter.updateData((ArrayList<Recipe>) recipeResponse.getResults());
//            animatedLoading.dismiss();
        });

        return binding.getRoot();
    }

    /**
     * Sets up the on click listener for the expand/minimise button in the
     * motivational quote container.
     */
    private void initOnClickForExpandMinimiseMotivationalQuote() {
//        binding.motivationalQuoteExpandMinimise.setOnClickListener(click -> {
//            expandOrMinimiseMotivationalQuote();
//        });
    }

    /**
     * Sets up the recycler view with the appropriate layout manger
     * and sets its adapter.
     */
    private void initRecyclerView() {
        binding.rvHomeMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (mAdapter != null) binding.rvHomeMeals.setAdapter(mAdapter);
        initSwipeToSave();
    }

    /**
     * Checks which chip the user has previously selected and displays the selection
     * accordingly.
     */
    private void restoreCheckedChip() {

        String checkedChipTag = mSharedPrefs.getString("checkedChipTag", null);
        Log.d(MainActivity.TAG, "sharedPrefs Id: " + checkedChipTag);
        if (checkedChipTag != null) {
            for (int i =0 ; i< binding.chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                if (chip!= null) {
                    if (chip.getTag().equals(checkedChipTag)) {
                        chip.setChecked(true);
                    }
                }
            }
        } else {
            // if the tag is null - it means it has not been initialised
            // so we do not need to do anything as the breakfast chip is set by default
        }
    }

    /**
     * Sets up on click listeners for each chip in the chip group.
     */
    private void initChipGroupOnClickListeners() {
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // show loading screen
//            animatedLoading.show();

            // find the selected chip
            Chip selectedChip = null;
            for (Integer id : checkedIds) {
                selectedChip = group.findViewById(id);
            }

            // each selected chip has a unique tag, declared in the xml file
            if (selectedChip != null) {
                if (selectedChip.getTag()!= null) {
                    SharedPreferences.Editor editor = mSharedPrefs.edit();
                    editor.putString("checkedChipTag", selectedChip.getTag().toString());
                    editor.apply();
                }
            }

            // request recipes based on the selected chip
//            if (selectedChip != null)
//                requestMealsBasedOnSelectedChip(selectedChip);
        });
    }

    /**
     * Requests meals based on the selected chip.
     *
     * @param selectedChip selected chip for which meals are fetched.
     */
    private void requestMealsBasedOnSelectedChip(Chip selectedChip) {
        try {
            assert selectedChip != null;
            // get Enum value from the selected option
            String value = selectedChip.getText().toString().toUpperCase();
            value = value.replace("-", "_");
            EnumMealType mealType = EnumMealType.valueOf(value);

            // request the meals for the selected meal type
            homeViewModel.requestRecipesByMealType(mealType);

        } catch (IllegalArgumentException e) {
            Log.e(MainActivity.TAG, e.toString());
        }
    }

    /**
     * Requests the motivational quote to display at launch.
     */
    private void requestQuote() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.NINJA_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        QuoteService request = retrofit.create(QuoteService.class);
        quoteViewModel.requestQuote(new QuotesRepository(request));
    }

    /**
     * Request breakfast meals to show at launch as breakfast
     * is the default choice in the chip group.
     */
    private void requestBreakfastMeals() {
        homeViewModel.requestRecipesByMealType(EnumMealType.BREAKFAST);
    }


    /**
     * Expands or minimises the motivational quote depending on whether
     * it is already visible or not.
     */
    private void expandOrMinimiseMotivationalQuote() {
//        if (binding.tvMotivationalQuote.getVisibility() == View.VISIBLE) {
//            TransitionManager.beginDelayedTransition(binding.cardContentContainer, new AutoTransition());
//            binding.tvMotivationalQuote.setVisibility(View.GONE);
//            binding.motivationalQuoteExpandMinimise.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_more));
//        } else {
//            TransitionManager.beginDelayedTransition(binding.motivationalQuoteContainer, new AutoTransition());
//            binding.tvMotivationalQuote.setVisibility(View.VISIBLE);
//            binding.motivationalQuoteExpandMinimise.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_less));
//        }
    }

    /**
     * Sets up the swipe to save gesture for efficiently saving the recipe
     * by swiping right.
     */
    private void initSwipeToSave() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // only allow right swipe
                if (direction == ItemTouchHelper.RIGHT) {
                    int pos = viewHolder.getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {

                        // display popup to request date and meal type for the recipe
                        new DialogSaveRecipe(requireContext(), (date, mealType) -> {
                            Recipe selectedRecipe = mAdapter.getRecipeAt(pos);
                            LocalRecipe recipeToSave = new LocalRecipe(selectedRecipe, date, mealType);
                            recipeToSave.setDateSavedFor(date);
                            recipeToSave.setMealTypeSavedFor(mealType);

                            // reference: https://developer.android.com/training/data-storage/room/async-queries
                            mDisposable.add(homeViewModel.insert(recipeToSave)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        // update adapter
                                        if (mAdapter != null) {
                                            showSnackBarWithUndo(recipeToSave, pos);
                                        }
                                    }));
                        });

                        if (mAdapter != null)
                            mAdapter.notifyItemChanged(pos);
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // reference: https://github.com/xabaras/RecyclerViewSwipeDecorator
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                        .addActionIcon(R.drawable.ic_add)
                        .setActionIconTint(ContextCompat.getColor(requireContext(), R.color.white))
                        .addSwipeRightLabel("Save")
                        .setSwipeRightLabelColor(ContextCompat.getColor(requireContext(), R.color.white))
                        .addCornerRadius(1, 50)
                        .addSwipeRightPadding(1, 10, 10, 10)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(binding.rvHomeMeals);
    }

    /**
     * Displays a SnackBar indicating the action completion and allows the user to undo
     * the action - aligns with Nielsen's error prevention principle.
     *
     * @param recipeToUndo recipe that was initially saved.
     * @param adapterPos   recycler view adapter position which needs to be updated.
     */
    private void showSnackBarWithUndo(LocalRecipe recipeToUndo, int adapterPos) {
        // reference: https://m2.material.io/components/snackbars/android#theming-snackbars
        Snackbar.make(requireView(), "Recipe Saved", Snackbar.LENGTH_LONG).setAction("Undo", v -> {
            // undo - delete the saved recipe
            mDisposable.add(homeViewModel.delete(recipeToUndo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> mAdapter.notifyItemChanged(adapterPos),
                            throwable -> Utilities.showErrorToast(requireContext())));

        }).show();
    }

    @Override
    public void onClickMeal(uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.Recipe recipe) {

        HomeFragmentDirections.ActionHomeFragmentToMealDetailsFragment action =
                HomeFragmentDirections.actionHomeFragmentToMealDetailsFragment(recipe);

        // reference: https://developer.android.com/guide/navigation/navcontroller
        NavHostFragment.findNavController(this).navigate(action);

    }

    @Override
    public void onStop() {
        super.onStop();

        /*
         * Reference1: https://developer.android.com/training/data-storage/room/async-queries
         * Reference2: https://github.com/android/architecture-components-samples/blob/main/BasicRxJavaSample/app/src/main/java/com/example/android/observability/persistence/LocalUserDataSource.java
         */
        mDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /*
         * Reference1: https://developer.android.com/training/data-storage/room/async-queries
         * Reference2: https://github.com/android/architecture-components-samples/blob/main/BasicRxJavaSample/app/src/main/java/com/example/android/observability/persistence/LocalUserDataSource.java
         */
        mDisposable.clear();
    }
}