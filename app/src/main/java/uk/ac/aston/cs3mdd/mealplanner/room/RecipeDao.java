package uk.ac.aston.cs3mdd.mealplanner.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;

@Dao
public interface RecipeDao {

    // RxJava async Dao queries
    // reference: https://developer.android.com/training/data-storage/room/async-queries#java

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Recipe recipe);

    @Delete
    Completable delete(Recipe recipe);

    @Update
    Completable update(Recipe updatedRecipe);

    @Query("SELECT * FROM Recipe")
    Flowable<List<Recipe>> getAll();

    @Query("DELETE FROM Recipe")
    Completable deleteAll();
}
