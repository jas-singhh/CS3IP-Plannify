package uk.ac.aston.cs3ip.plannify.adapters;

import java.time.LocalDate;

import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;

public interface DialogSaveRecipeOnClickInterface {

    void onClickSave(LocalDate date, EnumMealType mealType);
}
