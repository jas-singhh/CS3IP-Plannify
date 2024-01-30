package uk.ac.aston.cs3mdd.mealplanner.room;

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
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.models.local_recipe.LocalRecipe;

@Dao
public interface RecipeDao {

    // RxJava async Dao queries
    // reference: https://developer.android.com/training/data-storage/room/async-queries#java

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    Completable insert(LocalRecipe recipe);

    @Delete
    Completable delete(LocalRecipe recipe);

    @Update
    Completable update(LocalRecipe updatedRecipe);

    @Query("SELECT * FROM recipes")
    Flowable<List<LocalRecipe>> getAll();

    @Query("DELETE FROM recipes")
    Completable deleteAll();

    @Query("SELECT EXISTS (SELECT 1 FROM recipes WHERE primaryId = :id)")
    Single<Integer> existsById(int id);

    @Query("SELECT * FROM recipes WHERE dateSavedFor = :date")
    Flowable<List<LocalRecipe>> getRecipesForDate(LocalDate date);

    @Query("SELECT * FROM recipes WHERE mealTypeSavedFor = :mealTypeSavedFor AND dateSavedFor = :dateSavedFor")
    Flowable<List<LocalRecipe>> getRecipesOfTypeForDate(EnumMealType mealTypeSavedFor, LocalDate dateSavedFor);


    @Query("SELECT * FROM recipes WHERE dateSavedFor BETWEEN :from AND :to")
    Flowable<List<LocalRecipe>> getRecipesWithinDates(LocalDate from, LocalDate to);
}
