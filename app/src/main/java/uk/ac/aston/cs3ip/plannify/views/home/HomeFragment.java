package uk.ac.aston.cs3ip.plannify.views.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.cs3ip.plannify.BuildConfig;
import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.adapters.HomeMealsAdapter;
import uk.ac.aston.cs3ip.plannify.adapters.HomeMealsOnClickInterface;
import uk.ac.aston.cs3ip.plannify.api.QuoteService;
import uk.ac.aston.cs3ip.plannify.databinding.FragmentHomeBinding;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.NetworkRecipe;
import uk.ac.aston.cs3ip.plannify.repos.QuotesRepository;
import uk.ac.aston.cs3ip.plannify.shared_prefs.SharedPreferencesManager;
import uk.ac.aston.cs3ip.plannify.utils.SwipingUtils;
import uk.ac.aston.cs3ip.plannify.viewmodels.HomeViewModel;
import uk.ac.aston.cs3ip.plannify.viewmodels.QuoteViewModel;
import uk.ac.aston.cs3ip.plannify.views.dialogs.DialogLoading;

public class HomeFragment extends Fragment implements HomeMealsOnClickInterface {

    // Reference: CS3MDD Lab 2
    private QuoteViewModel quoteViewModel;
    private HomeViewModel homeViewModel;

    private DialogLoading animatedLoading;
    private HomeMealsAdapter mAdapter;

    private FragmentHomeBinding binding;
    private CompositeDisposable mDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise variables
        animatedLoading = new DialogLoading(requireContext());
        mAdapter = new HomeMealsAdapter(this, new ArrayList<>());
        quoteViewModel = new ViewModelProvider(requireActivity()).get(QuoteViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(HomeViewModel.initializer)).get(HomeViewModel.class);
        mDisposable = new CompositeDisposable();


        requestBreakfastMeals();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        animatedLoading.show();

        setupMotivationalQuoteBasedOnPreferences();
        restoreCheckedChip();

        // setup UI elements
        initChipGroupOnClickListeners();
        initRecyclerView();

        // Observe the quote data requested from the API
        quoteViewModel.getQuote().observe(getViewLifecycleOwner(), quote -> binding.tvMotivationalQuote.setText(quote.getQuote()));

        homeViewModel.getRequestedRecipes().observe(getViewLifecycleOwner(), recipeResponse -> {
            mAdapter.updateData((ArrayList<NetworkRecipe>) recipeResponse.getResults());
            animatedLoading.dismiss();
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwipingUtils.setupSwipeToSave((MainActivity) requireActivity(), homeViewModel, mAdapter, mDisposable, view, binding.rvHomeMeals);
//
//        // Inside your method where you want to delay the swipe action
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                        // Specify the index of the item you want to swipe
//                int index = 2; // For example, swiping the first item
//
//// Specify the distance you want the item to be swiped
//                int distance = 300; // For example, swiping 200 pixels
//
//// Specify the direction of the swipe (ItemTouchHelper.START for left swipe, ItemTouchHelper.END for right swipe)
//                int direction = ItemTouchHelper.END; // For example, swiping to the right
//
//// Specify the duration of the swipe animation
//                long time = 500; // Animation duration in milliseconds
//
//// Call the method to animate the swipe
//                swipeRecyclerViewItem(binding.rvHomeMeals, index, distance, direction, time);
//            }
//        }, 5000); // 5000 milliseconds = 5 seconds

    }

    public static void swipeRecyclerViewItem(
            RecyclerView recyclerView,
            int index,
            int distance,
            int direction,
            long time
    ) {
        View childView = recyclerView.getChildAt(index);
        if (childView == null) return;

        float x = childView.getWidth() / 2F;
        int[] viewLocation = new int[2];
        childView.getLocationInWindow(viewLocation);
        float y = (viewLocation[1] + childView.getHeight()) / 2F;
        long downTime = SystemClock.uptimeMillis();
        recyclerView.dispatchTouchEvent(
                MotionEvent.obtain(
                        downTime,
                        downTime,
                        MotionEvent.ACTION_DOWN,
                        x,
                        y,
                        0
                )
        );

        ValueAnimator animator = ValueAnimator.ofInt(0, distance);
        animator.setDuration(time);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dX = (int) animation.getAnimatedValue();
                float mX;
                if (direction == ItemTouchHelper.END) {
                    mX = x + dX;
                } else if (direction == ItemTouchHelper.START) {
                    mX = x - dX;
                } else {
                    mX = 0F;
                }
                recyclerView.dispatchTouchEvent(
                        MotionEvent.obtain(
                                downTime,
                                SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_MOVE,
                                mX,
                                y,
                                0
                        )
                );
            }
        });
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(() -> {
                    // Reverse animation
                    final ValueAnimator reverseAnimator = ValueAnimator.ofInt(distance, 0);
                    reverseAnimator.setDuration(time);
                    reverseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(@NonNull ValueAnimator animation1) {
                            int dX = (int) animation1.getAnimatedValue();
                            float mX = (direction == ItemTouchHelper.END) ? x + dX : x - dX;
                            recyclerView.dispatchTouchEvent(
                                    MotionEvent.obtain(
                                            downTime,
                                            SystemClock.uptimeMillis(),
                                            MotionEvent.ACTION_MOVE,
                                            mX,
                                            y,
                                            0
                                    )
                            );
                        }
                    });
                    reverseAnimator.start();
                }, 1500);
            }
        });
    }


    /**
     * Displays the motivational quote depending on whether it is enabled or disabled.
     */
    private void setupMotivationalQuoteBasedOnPreferences() {
        // check if motivational quote is enabled
        if (!SharedPreferencesManager.readBoolean(requireContext(), SharedPreferencesManager.IS_MOTIVATIONAL_QUOTE_DISABLED)) {
            // enabled - fetch and display the quote
            requestQuote();
            binding.motivationalQuoteParent.setVisibility(View.VISIBLE);
        } else {
            // disabled - hide the container
            binding.motivationalQuoteParent.setVisibility(View.GONE);
        }
    }

    /**
     * Sets up the recycler view with the appropriate layout manger
     * and sets its adapter.
     */
    private void initRecyclerView() {
        binding.rvHomeMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (mAdapter != null) binding.rvHomeMeals.setAdapter(mAdapter);
    }

    /**
     * Checks which chip the user has previously selected and displays the selection
     * accordingly.
     */
    private void restoreCheckedChip() {

        String checkedChipTag = SharedPreferencesManager.readString(requireContext(), SharedPreferencesManager.HOME_SELECTED_CHIP_TAG);

        if (checkedChipTag != null) {
            for (int i =0 ; i< binding.chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                if (chip!= null) {
                    if (chip.getTag().equals(checkedChipTag)) {
                        chip.setChecked(true);
                        requestMealsBasedOnSelectedChip(chip);
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
            animatedLoading.show();

            // find the selected chip
            Chip selectedChip = null;
            for (Integer id : checkedIds) {
                selectedChip = group.findViewById(id);
            }

            // each selected chip has a unique tag, declared in the xml file
            if (selectedChip != null) {
                if (selectedChip.getTag()!= null) {
                    // update the shared preferences value
                    SharedPreferencesManager.writeString(
                            requireContext(),
                            SharedPreferencesManager.HOME_SELECTED_CHIP_TAG,
                            selectedChip.getTag().toString()
                    );
                }
            }

            // request recipes based on the selected chip
            if (selectedChip != null)
                requestMealsBasedOnSelectedChip(selectedChip);
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

    @Override
    public void onClickMeal(NetworkRecipe networkRecipe) {

        HomeFragmentDirections.ActionHomeFragmentToMealDetailsFragment action =
                HomeFragmentDirections.actionHomeFragmentToMealDetailsFragment(networkRecipe);

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