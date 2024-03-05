package uk.ac.aston.cs3ip.plannify.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.ExtendedIngredient;

@Dao
public interface RecipeDao {

    // RxJava async Dao queries
    // reference: https://developer.android.com/training/data-storage/room/async-queries#java

    // long is the id of the recipe just inserted
    // used for the undo functionality as Room matches the primary id when removing elements
    // from the database, which is assigned upon insertion. Therefore, we need to know the primary id
    // once the data is inserted so we can perform the undo functionality, i.e. delete it if it was saved.
    // reference: https://stackoverflow.com/questions/50413911/android-room-database-delete-is-not-working
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Single<Long> insert(LocalRecipe recipe);

    @Delete
    Completable delete(LocalRecipe recipe);

    @Update
    Completable update(LocalRecipe updatedRecipe);

    @Query("SELECT * FROM recipes")
    Flowable<List<LocalRecipe>> getAll();

    @Query("SELECT * FROM recipes WHERE dateSavedFor = :date")
    Flowable<List<LocalRecipe>> getRecipesForDate(LocalDate date);

    @Query("SELECT * FROM recipes WHERE mealTypeSavedFor = :mealTypeSavedFor AND dateSavedFor = :dateSavedFor")
    Flowable<List<LocalRecipe>> getRecipesOfTypeForDate(EnumMealType mealTypeSavedFor, LocalDate dateSavedFor);


    @Query("SELECT * FROM recipes WHERE dateSavedFor BETWEEN :from AND :to")
    Flowable<List<LocalRecipe>> getRecipesWithinDates(LocalDate from, LocalDate to);

    @Query("SELECT * FROM recipes WHERE primaryId=:primaryId LIMIT 1")
    Single<LocalRecipe> getRecipeByPrimaryId(long primaryId);

    @Query("UPDATE recipes SET extendedIngredients=:extendedIngredients WHERE primaryId=:primaryId")
    Completable updateIngredientsForRecipeWithPrimaryId(List<ExtendedIngredient> extendedIngredients, long primaryId);
}
