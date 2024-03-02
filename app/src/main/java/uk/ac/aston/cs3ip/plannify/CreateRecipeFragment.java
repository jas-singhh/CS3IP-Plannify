package uk.ac.aston.cs3ip.plannify;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3ip.plannify.adapters.CreateRecipeCustomTextWatcher;
import uk.ac.aston.cs3ip.plannify.adapters.CustomMealIngredientsAdapter;
import uk.ac.aston.cs3ip.plannify.databinding.FragmentCreateRecipeBinding;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.AnalyzedInstruction;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.ExtendedIngredient;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.Measures;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.Metric;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.Nutrient;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.Nutrition;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.utils.CalendarUtils;
import uk.ac.aston.cs3ip.plannify.utils.Utilities;
import uk.ac.aston.cs3ip.plannify.viewmodels.HomeViewModel;
import uk.ac.aston.cs3ip.plannify.views.dialogs.DialogSuccess;

public class CreateRecipeFragment extends Fragment {

    private FragmentCreateRecipeBinding binding;
    private CustomMealIngredientsAdapter mAdapter;
    private DatePickerDialog datePickerDialog;
    private LocalDate selectedDate;
    private HashMap<Integer, String> preparationTimeMap;
    private CompositeDisposable mDisposable;
    private HomeViewModel recipeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new CustomMealIngredientsAdapter(new ArrayList<>(0));
        selectedDate = LocalDate.now();// default selected date is today's
        mDisposable = new CompositeDisposable();
        recipeViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(HomeViewModel.initializer)).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateRecipeBinding.inflate(inflater, container, false);

        setTextWatcherForInputFields();
        initTabSwitching();
        initRecyclerView();
        initPreparationTimeMap();
        initDatePicker();
        initDropdowns();

        onClickDate();
        onClickAddIngredient();
        onClickClear();
        onClickSave();

        return binding.getRoot();
    }

    /**
     * Initialises the custom text watcher for the various input fields which handles
     * the clearing of errors for specified fields and the displaying of the clear button
     * once the user starts typing.
     */
    private void setTextWatcherForInputFields() {
        // check and clear errors for required fields - name, meal type, meal date and steps
        binding.customRecipeName.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customRecipeName, true, binding.customRecipeClearBtn
        ));
        binding.customMealTypeDropdown.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customMealTypeDropdown, true, binding.customRecipeClearBtn
        ));
        binding.customRecipeDate.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customRecipeDate, true, binding.customRecipeClearBtn
        ));
        binding.customRecipeSteps.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customRecipeSteps, true, binding.customRecipeClearBtn
        ));

        // no need to check and clear errors for non-required fields
        binding.customRecipePreparationTimeDropdown.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customRecipePreparationTimeDropdown, false, binding.customRecipeClearBtn
        ));
        binding.customRecipeServingsDropdown.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customRecipeServingsDropdown, false, binding.customRecipeClearBtn
        ));
        binding.customRecipeCalories.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customRecipeCalories, false, binding.customRecipeClearBtn
        ));
        binding.customRecipeIngredientName.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customRecipeIngredientName, false, binding.customRecipeClearBtn
        ));
        binding.customMealIngredientQuantity.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customMealIngredientQuantity, false, binding.customRecipeClearBtn
        ));
        binding.customMealIngredientUnit.addTextChangedListener(new CreateRecipeCustomTextWatcher(
                binding.customMealIngredientUnit, false, binding.customRecipeClearBtn
        ));


    }

    /**
     * Sets up the tab switches.
     */
    private void initTabSwitching() {
        binding.customRecipeTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {
                    // this is the first tab - recipe details tab
                    binding.customRecipeParent.setVisibility(View.VISIBLE);
                    binding.customRecipeIngredientsParent.setVisibility(View.GONE);
                }

                if (tab.getPosition() == 1) {
                    // this is the second tab - ingredients tab
                    binding.customRecipeIngredientsParent.setVisibility(View.VISIBLE);
                    binding.customRecipeParent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Sets up the recycler view and its adapter.
     */
    private void initRecyclerView() {
        LinearLayoutManager rvLayout = new LinearLayoutManager(requireContext());
        rvLayout.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvIngredients.setLayoutManager(rvLayout);
        if (mAdapter != null) binding.rvIngredients.setAdapter(mAdapter);
    }

    /**
     * Initialises the preparation time map for the preparation time dropdown menu.
     */
    private void initPreparationTimeMap() {
        // initialise map with 5h max allowed preparation time
        preparationTimeMap = new HashMap<>(300);//5h max
        for (int i = 0; i < 300; i++) {
            preparationTimeMap.put(i, getTimeArrayInRange(0, 300)[i]);
        }
    }

    /**
     * Populates the dropdowns in the view with the appropriate adapters.
     */
    private void initDropdowns() {
        // reference: https://developer.android.com/reference/android/widget/AutoCompleteTextView

        // meal types dropdown
        ArrayAdapter<EnumMealType> mealTypesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,
                EnumMealType.values());
        binding.customMealTypeDropdown.setAdapter(mealTypesAdapter);

        // preparation time dropdown
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,
                getTimeArrayInRange(1, 300));
        binding.customRecipePreparationTimeDropdown.setAdapter(timeAdapter);

        // servings dropdown
        Integer[] servingsArray = IntStream.range(1, 20)
                .boxed()//this is done as the array adapter does not support primitive types
                .toArray(Integer[]::new);
        ArrayAdapter<Integer> servingsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,
                servingsArray);
        binding.customRecipeServingsDropdown.setAdapter(servingsAdapter);
    }

    /**
     * Sets up the onclick listener for the date button which shows a date picker.
     */
    private void onClickDate() {
        // set default date to today
        binding.customRecipeDate.setOnClickListener(v -> datePickerDialog.show());
    }

    /**
     * Initialises the date picker dialog to show once the user wants to
     * edit the date.
     */
    private void initDatePicker() {
        datePickerDialog = new DatePickerDialog(requireContext());
        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            // default format is yyyy-mm-dd

            // parse to a local date
            month += 1;

            // format date to match with dd/mm/yyyy format
            String date = String.format(Locale.getDefault(), "%d/%02d/%02d", year, month, dayOfMonth);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/dd");
            selectedDate = LocalDate.parse(date, formatter);

            // format to a more readable version - Day Month Year
            binding.customRecipeDate.setText(CalendarUtils.getFormattedDayMonthYearFromDate(selectedDate));
        });
    }

    /**
     * Initialises the onclick listener for the add ingredients button which adds
     * the typed ingredient and amount to the list.
     */
    private void onClickAddIngredient() {
        binding.customRecipeAddIngredientBtnParent.setOnClickListener(v -> {
            String name = binding.customRecipeIngredientName.getText().toString();
            String quantity = binding.customMealIngredientQuantity.getText().toString();
            String unit = binding.customMealIngredientUnit.getText().toString();

            if (areAllIngredientRequiredFieldsProvided()) {
                // make an extended ingredient to add to the list
                ExtendedIngredient ingredient = new ExtendedIngredient();

                // set the name
                ingredient.setName(name);
                ingredient.setNameClean(name);
                ingredient.setOriginalName(name);

                // store the measures for the ingredient - we are using metric
                // as it follows the British conventions.
                Measures measures = new Measures();
                Metric metric = new Metric();

                // set the quantity
                if (quantity.isEmpty()) metric.setAmount(0);
                else metric.setAmount(Float.parseFloat(quantity));

                // set the unit
                metric.setUnitShort(unit);
                metric.setUnitLong(unit);

                measures.setMetric(metric);
                ingredient.setMeasures(measures);

                // update the adapter
                mAdapter.addSingleItem(ingredient);

                // clear the content after adding the ingredient and quantity
                binding.customRecipeIngredientName.setText(null);
                binding.customMealIngredientQuantity.setText(null);
                binding.customMealIngredientUnit.setText(null);
            }

            // hide the keyboard
            // reference: https://stackoverflow.com/questions/1109022/how-can-i-close-hide-the-android-soft-keyboard-programmatically
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
        });
    }

    /**
     * Sets up the clear button to clear all the inputted data
     */
    private void onClickClear() {
        binding.customRecipeClearBtn.setOnClickListener(v -> {
            clearAllFields();
            // disable button
            binding.customRecipeClearBtn.setEnabled(false);
        });
    }

    /**
     * Sets up the onclick listener for the save button which will check if all
     * required fields are provided and save accordingly.
     */
    private void onClickSave() {
        binding.customRecipeSaveButton.setOnClickListener(v -> {
            if (areAllRequiredFieldsProvided()) {
                // order is important - because clearing the fields before saving the recipe
                // will not save the details

                // save the recipe
                LocalRecipe localRecipe = createLocalRecipeFromProvidedInformation();
                mDisposable.add(recipeViewModel.insert(localRecipe)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                                    new DialogSuccess(requireContext(), "Recipe Has Been Saved!");
                                },
                                throwable -> {
                                    Utilities.showErrorToast(requireContext());
                                    Log.d(MainActivity.TAG, throwable.toString());
                                }));


                // clear all the fields
                clearAllFields();
            }
        });

    }

    /**
     * Creates a local recipe from the information provided by the user.
     *
     * @return a local recipe created from the information provided by the user.
     */
    private LocalRecipe createLocalRecipeFromProvidedInformation() {
        LocalRecipe localRecipe = new LocalRecipe();

        // title
        localRecipe.setTitle(binding.customRecipeName.getText().toString());

        // meal type
        EnumMealType enumMealType = EnumMealType.getEnumMealTypeFromValue(binding.customMealTypeDropdown.getText().toString());
        if (enumMealType != null) localRecipe.setMealTypeSavedFor(enumMealType);
        else localRecipe.setMealTypeSavedFor(EnumMealType.BREAKFAST);

        // date saved for
        localRecipe.setDateSavedFor(selectedDate);

        // the following fields are optional, so check if they are provided before adding them

        // preparation time
        int preparationTime = getMinutesFromInputtedTime();
        localRecipe.setReadyInMinutes(preparationTime);

        // servings
        if (binding.customRecipeServingsDropdown.getText().toString().isEmpty())
            localRecipe.setServings(0);
        else
            localRecipe.setServings(Integer.parseInt(binding.customRecipeServingsDropdown.getText().toString()));

        // steps
        AnalyzedInstruction analyzedInstruction = new AnalyzedInstruction();
        analyzedInstruction.setName(binding.customRecipeSteps.getText().toString());
        ArrayList<AnalyzedInstruction> steps = new ArrayList<>();
        steps.add(analyzedInstruction);
        localRecipe.setAnalyzedInstructions(steps);

        // calories - these are the first element in the nutrients
        int calories = 0;
        if (!binding.customRecipeCalories.getText().toString().isEmpty())
            calories = Integer.parseInt(binding.customRecipeCalories.getText().toString());
        Nutrition nutrition = new Nutrition();
        List<Nutrient> nutrientList = new ArrayList<>(1);
        Nutrient nutrient = new Nutrient();
        nutrient.setName("Calories");
        nutrient.setUnit("kcal");
        nutrient.setAmount(calories);
        nutrientList.add(nutrient);
        nutrition.setNutrients(nutrientList);
        localRecipe.setNutrition(nutrition);

        // set the ingredients
        localRecipe.setExtendedIngredients(getIngredientsFromThisRecipe());

        return localRecipe;
    }

    /**
     * Returns the list of ingredients added for this recipe.
     *
     * @return a list of ingredients for this recipe.
     */
    private List<ExtendedIngredient> getIngredientsFromThisRecipe() {
        List<ExtendedIngredient> extendedIngredients = new ArrayList<>();

        if (mAdapter != null && !mAdapter.getIngredients().isEmpty()) {
            // iterate through the added ingredients and add them into the list
            extendedIngredients.addAll(mAdapter.getIngredients());
        }

        return extendedIngredients;
    }


    /**
     * Clears all the fields.
     */
    private void clearAllFields() {
        // clear recipe details
        binding.customRecipeName.setText(null);
        binding.customRecipeName.clearFocus();
        binding.customMealTypeDropdown.setText(null);
        binding.customMealTypeDropdown.clearFocus();
        binding.customRecipeDate.setText(null);
        binding.customRecipeDate.clearFocus();
        binding.customRecipePreparationTimeDropdown.setText(null);
        binding.customRecipePreparationTimeDropdown.clearFocus();
        binding.customRecipeServingsDropdown.setText(null);
        binding.customRecipeServingsDropdown.clearFocus();
        binding.customRecipeCalories.setText(null);
        binding.customRecipeCalories.clearFocus();
        binding.customRecipeSteps.setText(null);
        binding.customRecipeSteps.clearFocus();

        // clear ingredients
        binding.customRecipeIngredientName.setText(null);
        binding.customRecipeIngredientName.clearFocus();
        binding.customMealIngredientQuantity.setText(null);
        binding.customMealIngredientQuantity.clearFocus();
        binding.customMealIngredientUnit.setText(null);
        binding.customMealIngredientUnit.clearFocus();
        if (mAdapter != null) mAdapter.clearDataSet();

        // disable clear button
        binding.customRecipeClearBtn.setEnabled(false);
    }

    /**
     * Returns an array of String containing a list of time ranging from minutes to hours
     * within the specified range.
     *
     * @param from value from which to return the time array.
     * @param to   value to which to return the time array.
     * @return an array of String containing times in format of e.g. 1h 10m.
     */
    private String[] getTimeArrayInRange(int from, int to) {
        // reference: https://stackoverflow.com/questions/50406260/how-to-convert-a-range-to-a-delimited-string-in-java-8
        return IntStream.range(from, to)
                .mapToObj(i -> {
                    if (i < 60) {
                        // minutes
                        return i + "m";
                    } else {
                        if (i % 60 > 0) {
                            // there are minutes over the hour
                            return i / 60 + "h " + i % 60 + "m";
                        }
                        return i / 60 + "h";
                    }
                }).toArray(String[]::new);
    }

    /**
     * Retrieves the minutes from the selected time by the user.
     *
     * @return the preparation time in minutes, translated from the selected value by the user.
     */
    private int getMinutesFromInputtedTime() {
        int result = 0;
        if (!binding.customRecipePreparationTimeDropdown.getText().toString().isEmpty()) {
            String value = binding.customRecipePreparationTimeDropdown.getText().toString();
            if (!binding.customRecipePreparationTimeDropdown.getText().toString().isEmpty()) {
                for (Map.Entry<Integer, String> entry : preparationTimeMap.entrySet()) {
                    if (value.equals(entry.getValue())) {
                        result = entry.getKey();
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks if the required fields, i.e. meal name, date, type, steps,
     * and at least one ingredient are provided.
     *
     * @return true if the required fields are provided, false otherwise.
     */
    private boolean areAllRequiredFieldsProvided() {
        boolean result = true;
        // required fields are meal name, meal date, meal type, steps, and at least
        // one ingredient
        // these are in reverse order so that the error message is displayed
        // in the order in which text boxes are placed.



        // steps
        if (binding.customRecipeSteps.getText().toString().trim().isEmpty()) {
            binding.customRecipeSteps.setError("Please enter the steps");
            binding.customRecipeSteps.requestFocus();
            result = false;
        }

        // date
        if (binding.customRecipeDate.getText().toString().trim().isEmpty()) {
            binding.customRecipeDate.setError("Please enter the date");
            binding.customRecipeDate.requestFocus();
            result = false;
        }

        // meal type
        if (binding.customMealTypeDropdown.getText().toString().trim().isEmpty()) {
            binding.customMealTypeDropdown.setError("Please select a meal type");
            binding.customMealTypeDropdown.requestFocus();
            result = false;
        }

        // name
        if (binding.customRecipeName.getText().toString().trim().isEmpty()) {
            binding.customRecipeName.setError("Please enter a name");
            binding.customRecipeName.requestFocus();
            result = false;
        }


        // check for at least one ingredient
        if (result) {// all other required fields are provided
            if (mAdapter!= null && mAdapter.getIngredients().isEmpty()) {
                // no ingredients added
                binding.customRecipeIngredientName.setError("Please enter at least one ingredient");
                binding.customRecipeIngredientName.requestFocus();
                // update the tab layout accordingly
                if (binding.customRecipeTabLayout.getSelectedTabPosition() != 1) binding.customRecipeTabLayout.selectTab(binding.customRecipeTabLayout.getTabAt(1));
                result = false;
            }
        } else {
            // check if we need to update the tab
            if (binding.customRecipeTabLayout.getSelectedTabPosition() == 1)
                binding.customRecipeTabLayout.selectTab(binding.customRecipeTabLayout.getTabAt(0));
        }

        return result;
    }

    private boolean areAllIngredientRequiredFieldsProvided() {
        boolean result = true;

        // check if ingredient quantity is provided
        if (binding.customMealIngredientQuantity.getText().toString().isEmpty()) {
            binding.customMealIngredientQuantity.setError("Quantity is required");
            binding.customMealIngredientQuantity.requestFocus();
            result = false;
        }

        // check if ingredient name is provided
        if (binding.customRecipeIngredientName.getText().toString().isEmpty()) {
            binding.customRecipeIngredientName.setError("Name is required");
            binding.customRecipeIngredientName.requestFocus();
            result = false;
        }

        return result;
    }
}