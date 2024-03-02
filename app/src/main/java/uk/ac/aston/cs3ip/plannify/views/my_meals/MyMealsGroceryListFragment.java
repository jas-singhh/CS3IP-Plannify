package uk.ac.aston.cs3ip.plannify.views.my_meals;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.adapters.MyMealsGroceryListItemsAdapter;
import uk.ac.aston.cs3ip.plannify.adapters.MyMealsGroceryListOnClickInterface;
import uk.ac.aston.cs3ip.plannify.databinding.FragmentMyMealsGroceryListBinding;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.ExtendedIngredient;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.utils.CalendarUtils;
import uk.ac.aston.cs3ip.plannify.viewmodels.CalendarViewModel;
import uk.ac.aston.cs3ip.plannify.viewmodels.HomeViewModel;


public class MyMealsGroceryListFragment extends Fragment implements MyMealsGroceryListOnClickInterface {

    private FragmentMyMealsGroceryListBinding binding;

    private HomeViewModel homeViewModel;
    private CalendarViewModel calendarViewModel;
    private LocalDate selectedDate;
    private CompositeDisposable mDisposable;
    private MyMealsGroceryListItemsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialisation
        homeViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(HomeViewModel.initializer)).get(HomeViewModel.class);
        calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
        selectedDate = calendarViewModel.getSelectedDate().getValue();
        mDisposable = new CompositeDisposable();
        mAdapter = new MyMealsGroceryListItemsAdapter(new ArrayList<>(), this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyMealsGroceryListBinding.inflate(inflater, container, false);

        initRecyclerView();

        calendarViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            selectedDate = date;
            LocalDate[] fromAndTo = getStartAndEndDateFromDate(selectedDate);
            LocalDate from = fromAndTo[0];
            LocalDate to = fromAndTo[1];
            displayIngredientsForRecipesWithinDateRange(from, to);
        });

        return binding.getRoot();
    }

    /**
     * Displays the ingredients for the saved recipes that are within the specified
     * range of dates.
     *
     * @param from date from which to fetch recipes to get the ingredients.
     * @param to   date to which to fetch recipes to get the ingredients.
     */
    private void displayIngredientsForRecipesWithinDateRange(LocalDate from, LocalDate to) {
        mDisposable.add(homeViewModel.getRecipesWithinDates(from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {

                    Log.d(MainActivity.TAG, "recipes for dates: " + CalendarUtils.getFormattedDayMonthYearFromDate(from)
                            + " - " + CalendarUtils.getFormattedDayMonthYearFromDate(to) + "\n");

                    // hide or show status message depending on whether there are saved recipes or not.
                    int statusMessageVisibility = list.isEmpty() ? View.VISIBLE : View.GONE;
                    binding.noGroceryItemsMessageParent.setVisibility(statusMessageVisibility);

                    if (!list.isEmpty()) {
                        Log.d(MainActivity.TAG, "recipe: " + list.get(0).getTitle() + "\nprimary id: " +
                                list.get(0).getPrimaryId());

                        HashMap<Long, ExtendedIngredient> ingredientsMap = new HashMap<>();

                        for (LocalRecipe localRecipe : list) {
                            for (ExtendedIngredient ingredient : localRecipe.getExtendedIngredients()) {

                                // check if the ingredient is already present
                                if (ingredientsMap.isEmpty()) {
                                    ingredientsMap.put(ingredient.getId(), ingredient);
                                } else {
                                    // check if the ingredient is already present
                                    if (ingredientsMap.containsKey(ingredient.getId())) {
                                        // ingredient already present, so update the quantity
                                        ExtendedIngredient currentIngredient = ingredientsMap.get(ingredient.getId());
                                        double originalQuantity = ingredient.getAmount();
                                        if (currentIngredient != null)
                                            currentIngredient.setAmount(currentIngredient.getAmount() + originalQuantity);
                                    } else {
                                        ingredientsMap.put(ingredient.getId(), ingredient);
                                    }
                                }
                            }
                        }

                        if (mAdapter != null) {
                            // map to list
                            ArrayList<ExtendedIngredient> ingredients = new ArrayList<>(ingredientsMap.values());
                            mAdapter.updateData(ingredients);
                        }
                    } else {
                        // clear the adapter if there are no results
                        if (mAdapter != null) {
                            mAdapter.clearDataSet();
                        }
                    }
                }));
    }

    /**
     * Sets up the recycler view with the appropriate layout manger
     * and sets its adapter.
     */
    private void initRecyclerView() {
        binding.rvGroceryList.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (mAdapter != null) binding.rvGroceryList.setAdapter(mAdapter);
    }


    /**
     * Returns the start date and end date of the week based on the specified date.
     *
     * @param selectedDate specified date from which the start and end dates are calculated.
     * @return an array containing the start date and end date.
     */
    private LocalDate[] getStartAndEndDateFromDate(LocalDate selectedDate) {
        if (selectedDate == null) return null;

        LocalDate[] result = new LocalDate[2];

        // get week start date and end date based on selected date
        ArrayList<LocalDate> weekDates = CalendarUtils.getDatesInWeekBasedOnDate(selectedDate);
        if (weekDates != null && !weekDates.isEmpty()) {
            result[0] = weekDates.get(0);
            result[1] = weekDates.get(weekDates.size() - 1);
        }

        return result;
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
        mDisposable.clear();
    }

    @Override
    public void onClickGroceryItem(ExtendedIngredient groceryItem, boolean isChecked) {
        // reference: https://stackoverflow.com/questions/56764884/nested-network-calls-using-rx-android-and-retrofit
        mDisposable.add(homeViewModel.getRecipeByPrimaryId(groceryItem.getParentRecipeId())
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(localRecipe -> {
                    if (localRecipe == null) {
                        return Completable.error(new Exception("No recipe with id " + groceryItem.getParentRecipeId()));
                    }

                    // find the item in the database and update its status
                    for (ExtendedIngredient ingredient : localRecipe.getExtendedIngredients()) {
                        if (groceryItem.getId() == ingredient.getId()) {
                            ingredient.setCheckedInGroceryList(isChecked);
                            break;
                        }
                    }

                    // update the changes in the database to persist them
                    return homeViewModel.updateIngredientsForRecipeWithPrimaryId(localRecipe.getExtendedIngredients(), localRecipe.getPrimaryId());
                })
                .subscribe(() -> {
                    Log.d(MainActivity.TAG, "data updated for " + groceryItem.getName());
                }, throwable -> Log.e(MainActivity.TAG, "MyMealsGroceryListFragment::onClickGroceryItem: " + throwable)));


//        // find the item in the database and update its status
//        mDisposable.add(homeViewModel.getRecipeById(groceryItem.getParentRecipeId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(localRecipe -> {
//                    if (localRecipe != null) {
//                        for (ExtendedIngredient ingredient : localRecipe.getExtendedIngredients()) {
//                            if (groceryItem.getId() == ingredient.getId()) {
//                                ingredient.setCheckedInGroceryList(isChecked);
//                                break;
//                            }
//                        }
//
//                        // update the changes in the database to persist them
//                        mDisposable.add(homeViewModel.updateIngredientsForRecipeWithId(localRecipe.getExtendedIngredients(), localRecipe.getPrimaryId())
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(() -> {
//                                            Log.d(MainActivity.TAG, "data updated for " + groceryItem.getName());
//                                        },
//                                        throwable -> Log.e(MainActivity.TAG, "MyMealsGroceryListFragment::onClickGroceryItem: " + throwable)));
//                    }
//
//                }, throwable -> Log.e(MainActivity.TAG, "MyMealsGroceryListFragment::onClickGroceryItem: " + throwable)));

    }
}