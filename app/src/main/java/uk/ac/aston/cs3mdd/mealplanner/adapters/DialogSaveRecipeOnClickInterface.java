package uk.ac.aston.cs3mdd.mealplanner.adapters;

import java.time.LocalDate;

import uk.ac.aston.cs3mdd.mealplanner.enums.EnumMealType;

public interface DialogSaveRecipeOnClickInterface {

    void onClickSave(LocalDate date, EnumMealType mealType);
}
