package uk.ac.aston.cs3mdd.mealplanner.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import uk.ac.aston.cs3mdd.mealplanner.models.local_recipe.LocalRecipe;


@Database(entities = LocalRecipe.class, version = 1)
@TypeConverters({RoomDataTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    // reference: https://developer.android.com/codelabs/android-room-with-a-view#7

    public abstract RecipeDao recipeDao();

    private static volatile AppDatabase INSTANCE;

    public static synchronized AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
