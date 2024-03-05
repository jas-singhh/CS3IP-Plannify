package uk.ac.aston.cs3ip.plannify.views.my_meals;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.adapters.HomeMealsAdapter;
import uk.ac.aston.cs3ip.plannify.adapters.HomeMealsOnClickInterface;
import uk.ac.aston.cs3ip.plannify.databinding.FragmentMyMealsSavedBinding;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.NetworkRecipe;
import uk.ac.aston.cs3ip.plannify.shared_prefs.SharedPreferencesManager;
import uk.ac.aston.cs3ip.plannify.utils.SwipingUtils;
import uk.ac.aston.cs3ip.plannify.viewmodels.CalendarViewModel;
import uk.ac.aston.cs3ip.plannify.viewmodels.HomeViewModel;
import uk.ac.aston.cs3ip.plannify.views.dialogs.DialogLoading;


public class MyMealsSavedFragment extends Fragment implements HomeMealsOnClickInterface {

    private FragmentMyMealsSavedBinding binding;
    private CompositeDisposable mDisposable;
    private HomeViewModel homeViewModel;
    private HomeMealsAdapter mAdapter;
    private DialogLoading animatedLoading;
    private CalendarViewModel calendarViewModel;
    private LocalDate selectedDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialisation
        mDisposable = new CompositeDisposable();
        homeViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(HomeViewModel.initializer)).get(HomeViewModel.class);
        calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
        mAdapter = new HomeMealsAdapter(this, new ArrayList<>());
        animatedLoading = new DialogLoading(requireContext());
        selectedDate = calendarViewModel.getSelectedDate().getValue();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyMealsSavedBinding.inflate(inflater, container, false);

        animatedLoading.show();

        initRecyclerView();
        initChipGroupOnClickListeners();

        subscribeToChangesInTheSelectedDate();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipingUtils.setupSwipeToDelete((MainActivity) requireActivity(), homeViewModel, mAdapter,
                mDisposable, view, binding.rvSavedRecipes);
    }

    /**
     * Subscribes to the changes in the selected date, which occur each time the user
     * selects a date in the horizontal calendar view, and fetches the local recipes
     * for that specific date.
     */
    private void subscribeToChangesInTheSelectedDate() {
        calendarViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            selectedDate = date;
            loadRecipesForGivenMealTypeAndDate(getSelectedMealType(), selectedDate);
        });
    }

    /**
     * Loads local recipes for the given meal type and the specified date.
     *
     * @param mealType meal type for which to load local recipes.
     * @param date     date for which to load local recipes.
     */
    private void loadRecipesForGivenMealTypeAndDate(EnumMealType mealType, LocalDate date) {
        if (mealType == null || date == null) return;

        // request the meals for the selected meal type and date
        mDisposable.add(homeViewModel.getRecipesOfTypeForDate(mealType, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (animatedLoading!= null) animatedLoading.dismiss();

                    // hide or show the status message depending on whether there are any
                    // saved meals or not.
                    int visibility = list.isEmpty() ? View.VISIBLE : View.GONE;
                    binding.noSavedMealsMessageParent.setVisibility(visibility);

                    // update the adapter
                    if (mAdapter != null)
                        mAdapter.updateData((ArrayList<? extends NetworkRecipe>) list);

                    // check if we need to display the intro to swipe to delete
                    if (!list.isEmpty())
                        binding.rvSavedRecipes.post(this::showIntroIfNotAlreadyShown);
                }));
    }

    /**
     * Sets up the recycler view and its adapter.
     */
    private void initRecyclerView() {
        binding.rvSavedRecipes.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (mAdapter != null) binding.rvSavedRecipes.setAdapter(mAdapter);
    }

    /**
     * Sets up on click listeners for each chip in the chip group and refreshes recipes
     * when the user selects a different chip.
     */
    private void initChipGroupOnClickListeners() {
        binding.chipGroup.setOnCheckedStateChangeListener(((group, checkedIds) -> {
            // find the selected chip
            Chip selectedChip = null;
            for (Integer id : checkedIds) {
                selectedChip = group.findViewById(id);
            }

            assert selectedChip != null;
            // get Enum value from the selected option
            String value = selectedChip.getText().toString().toUpperCase();
            value = value.replace("-", "_");
            EnumMealType mealType = EnumMealType.valueOf(value);
            loadRecipesForGivenMealTypeAndDate(mealType, selectedDate);
        }));
    }

    /**
     * Gets and returns the currently selected chip's value and translates it
     * into the meal type enum.
     *
     * @return the meal type selected in the chip group.
     */
    private EnumMealType getSelectedMealType() {
        Chip selectedChip = null;

        List<Integer> checkedChipIds = binding.chipGroup.getCheckedChipIds();
        for (Integer id : checkedChipIds) {
            selectedChip = binding.chipGroup.findViewById(id);
        }

        // get Enum value from the selected option
        assert selectedChip != null;
        String value = selectedChip.getText().toString().toUpperCase();
        value = value.replace("-", "_");

        return EnumMealType.valueOf(value);
    }

    /**
     * Shows the intro if not already shown after a 1 second delay
     */
    private void showIntroIfNotAlreadyShown() {

        // check if the swipe to save intro has already been shown
        if (!SharedPreferencesManager.readBoolean(requireContext(), SharedPreferencesManager.SWIPE_TO_DELETE_INTRO_SHOWN)) {
            // not yet shown - show it
            new Handler().postDelayed(this::displaySwipeToDeleteIntro, MainActivity.INTRO_DELAY_MS);
            // and update the shared preferences
            SharedPreferencesManager.writeBoolean(requireContext(), SharedPreferencesManager.SWIPE_TO_DELETE_INTRO_SHOWN, true);
        }
    }

    /**
     * Displays the swipe to save introduction by drawing a circle around the first item
     * in the recycler view, if there is any, otherwise the first item in the chip group and
     * informs the user about the option.
     */
    private void displaySwipeToDeleteIntro() {
        // reference: https://github.com/KeepSafe/TapTargetView

        RecyclerView.LayoutManager layoutManager = binding.rvSavedRecipes.getLayoutManager();
        View rvChild = binding.chipGroup.getChildAt(0);// use chip group if the recycler view child is null
        if (layoutManager != null)
            rvChild = layoutManager.getChildAt(0);

        if (rvChild != null) {
            TapTargetView.showFor(requireActivity(),// `this` is an Activity
                    TapTarget.forView(rvChild, "Swipe Left to Delete!", "Tap to Continue")
                            // All options below are optional
                            .outerCircleColor(R.color._secondary)      // Specify a color for the outer circle
                            .outerCircleAlpha(.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color._complementary)   // Specify a color for the target circle
                            .titleTextSize(25)                  // Specify the size (in sp) of the title text
                            .descriptionTextColor(R.color.text_black)
                            .descriptionTextSize(20)
                            .titleTextColor(R.color.text_black)      // Specify the color of the title text
                            .textTypeface(ResourcesCompat.getFont(requireContext(), R.font.alata))  // Specify a typeface for the text
                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(false)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(60),                  // Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                        }
                    });
        }
    }


    @Override
    public void onClickMeal(NetworkRecipe networkRecipe) {

        MyMealsFragmentDirections.ActionMyMealsFragmentToMealDetailsFragment action =
                MyMealsFragmentDirections.actionMyMealsFragmentToMealDetailsFragment(networkRecipe);

        // reference: https://developer.android.com/guide/navigation/navcontroller
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }


}