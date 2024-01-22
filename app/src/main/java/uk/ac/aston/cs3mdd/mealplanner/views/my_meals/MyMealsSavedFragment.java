package uk.ac.aston.cs3mdd.mealplanner.views.my_meals;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.adapters.HomeMealsAdapter;
import uk.ac.aston.cs3mdd.mealplanner.adapters.HomeMealsOnClickInterface;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.LocalRecipe;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentMyMealsSavedBinding;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.CalendarViewModel;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.RecipeViewModel;
import uk.ac.aston.cs3mdd.mealplanner.views.dialogs.DialogLottie;
import uk.ac.aston.cs3mdd.mealplanner.views.meal_details.MealDetailsFragment;


public class MyMealsSavedFragment extends Fragment implements HomeMealsOnClickInterface {

    private FragmentMyMealsSavedBinding binding;
    private CompositeDisposable mDisposable;
    private RecipeViewModel recipeViewModel;
    private HomeMealsAdapter mAdapter;
    private DialogLottie animatedLoading;
    private CalendarViewModel calendarViewModel;
    private LocalDate selectedDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialisation
        mDisposable = new CompositeDisposable();
        recipeViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)).get(RecipeViewModel.class);
        calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
        mAdapter = new HomeMealsAdapter(this, new ArrayList<>());
        animatedLoading = new DialogLottie(requireContext());
        selectedDate = calendarViewModel.getSelectedDate().getValue();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyMealsSavedBinding.inflate(inflater, container, false);
        animatedLoading.show();

        setupRecyclerView();
        setupChipGroupOnClickListeners();

        subscribeToChangesInTheSelectedDate();

        return binding.getRoot();
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
        mDisposable.add(recipeViewModel.getRecipesOfTypeForDate(mealType, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    animatedLoading.dismiss();

                    if (list.isEmpty()) binding.savedMealsStatusText.setVisibility(View.VISIBLE);
                    else binding.savedMealsStatusText.setVisibility(View.GONE);

                    for (LocalRecipe recipe : list) {
                        Log.d(MainActivity.TAG, "local recipe meal type: " + recipe.getMealTypeSavedFor());
                    }

                    // update the adapter
                    if (mAdapter != null)
                        mAdapter.updateData((ArrayList<? extends Recipe>) list);
                }));
    }

    /**
     * Sets up the recycler view and its adapter.
     */
    private void setupRecyclerView() {
        binding.rvSavedRecipes.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (mAdapter != null) binding.rvSavedRecipes.setAdapter(mAdapter);
        setupSwipeToDelete();
    }

    /**
     * Sets up on click listeners for each chip in the chip group and refreshes recipes
     * when the user selects a different chip.
     */
    private void setupChipGroupOnClickListeners() {
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
     * Sets up the "swipe to delete" feature for individual saved recipes.
     */
    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // check for left swipe
                if (direction == ItemTouchHelper.LEFT) {
                    int pos = viewHolder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && mAdapter != null) {
                        LocalRecipe recipeToDelete = (LocalRecipe) mAdapter.getRecipeAt(pos);

                        // delete the recipe
                        mDisposable.add(recipeViewModel.delete(recipeToDelete)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            showSnackBarWithUndo((LocalRecipe) mAdapter.getRecipeAt(pos), pos);
                                            // update adapter
                                            mAdapter.notifyItemChanged(pos);
                                        }
                                        , throwable -> Utilities.showErrorToast(requireContext())));
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // reference: https://github.com/xabaras/RecyclerViewSwipeDecorator
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(requireContext(), R.color.error_red))
                        .addActionIcon(R.drawable.ic_remove)
                        .setActionIconTint(ContextCompat.getColor(requireContext(), R.color.white))
                        .addSwipeLeftLabel("Remove")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(requireContext(), R.color.white))
                        .addCornerRadius(1, 50)
                        .addSwipeLeftPadding(1, 10, 10, 10)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(binding.rvSavedRecipes);
    }

    /**
     * Displays a SnackBar indicating the action completion and allows the user to undo
     * the action - aligns with Nielsen's error prevention principle.
     *
     * @param removedRecipe recipe that was initially un-saved.
     * @param adapterPos    recycler view adapter position which needs to be updated.
     */
    private void showSnackBarWithUndo(LocalRecipe removedRecipe, int adapterPos) {
        if (removedRecipe == null || adapterPos == RecyclerView.NO_POSITION) return;

        // reference: https://m2.material.io/components/snackbars/android#theming-snackbars
        Snackbar.make(requireView(), "Recipe Removed", Snackbar.LENGTH_LONG).setAction("Undo", v -> {
            // undo - delete the saved recipe
            mDisposable.add(recipeViewModel.insert(removedRecipe)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> mAdapter.notifyItemChanged(adapterPos),
                            throwable -> Utilities.showErrorToast(requireContext())));
        }).show();
    }

    @Override
    public void onClickMeal(Recipe recipe) {
        // set arguments
        Bundle args = new Bundle();
        args.putSerializable("Recipe", recipe);
        args.putString("Source", "my_meals_saved_fragment");//flag to manage back stack
        MealDetailsFragment destinationFragment = new MealDetailsFragment();
        destinationFragment.setArguments(args);

        // navigate to the destination fragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.my_meals_fragment_container, destinationFragment, null)
                .setReorderingAllowed(true)
                .addToBackStack("my_meals_saved_fragment")
                .commit();
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