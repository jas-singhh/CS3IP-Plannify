package uk.ac.aston.cs3ip.plannify.room;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;

import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.test_utils.TestUtils;

@RunWith(AndroidJUnit4.class)
public class EntityReadWriteTest {
    // reference 1: https://developer.android.com/training/data-storage/room/testing-db
    // reference 2: https://medium.com/androiddevelopers/room-rxjava-acb0cd4f3757
    private RecipeDao recipeDao;
    private AppDatabase mDatabase;

    // required to make sure that Room executes all database operations instantly
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Sets up the components required to perform the tests
     */
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        recipeDao = mDatabase.recipeDao();
    }

    /**
     * Tests the insertion query by checking whether  the data is correctly
     * inserted in the database.
     */
    @Test
    public void insertRow() {
        // create sample local recipe
        LocalRecipe sampleRecipe = TestUtils.createSampleLocalRecipe();

        // insert the data
        recipeDao.insert(sampleRecipe).subscribeOn(Schedulers.io()).blockingSubscribe();

        // test if the data was inserted
        recipeDao.getAll().test()
                .assertNoErrors()
                // check if there is exactly 1 row in the database and compare the id
                .assertValue(list -> list.size() == 1 && list.get(0).getId() == sampleRecipe.getId());
    }

    /**
     * Tests the deletion query by checking whether the data is deleted
     * correctly from the database.
     */
    @Test
    public void deleteRow() {
        // create sample local recipe
        LocalRecipe sampleRecipe = TestUtils.createSampleLocalRecipe();
        // create the same recipe so we can test the deletion
        LocalRecipe sameRecipe = TestUtils.createSampleLocalRecipe();

        // insert the data
        // set the primary id of the sample recipe to be the same as the one inserted because
        // the database compares the primary id to delete the row
        recipeDao.insert(sampleRecipe).subscribeOn(Schedulers.io()).blockingSubscribe(sameRecipe::setPrimaryId);

        // test if the data was inserted
        recipeDao.getAll().test()
                .assertNoErrors()
                .assertValue(list -> list.size() == 1 && list.get(0).getId() == sampleRecipe.getId());

        // delete the inserted recipe
        recipeDao.delete(sameRecipe).blockingAwait();

        // test if the recipe was deleted
        recipeDao.getAll().test()
                .assertNoErrors()
                .assertValue(List::isEmpty);// check if list is empty after deletion
    }

    /**
     * Tests the get row by date query by checking whether the operation
     * returns the correct data for a specific date.
     */
    @Test
    public void getRowByDate() {
        // create and insert a sample recipe with date set for tomorrow
        LocalRecipe sampleRecipe = TestUtils.createSampleLocalRecipe();
        LocalDate date = LocalDate.now();
        date.plusDays(1);
        sampleRecipe.setDateSavedFor(date);

        recipeDao.insert(sampleRecipe).blockingSubscribe();

        // test if the database returns the row correctly for the specified date
        recipeDao.getRecipesForDate(date).test()
                .assertNoErrors()
                .assertValue(localRecipes -> localRecipes.get(0).getId() == sampleRecipe.getId()// check if the recipe was saved by comparing the id
                        && localRecipes.get(0).getDateSavedFor().isEqual(date));// check if inserted row has the correct specified date
    }

    /**
     * Tests the get rows of type for date query by checking whether the operation
     * returns the correct data for the specified meal type and date.
     */
    @Test
    public void getRowsOfTypeForDate() {
        // create and insert a recipe for tomorrow with the meal type as lunch
        LocalRecipe sampleRecipe = TestUtils.createSampleLocalRecipe();
        sampleRecipe.setMealTypeSavedFor(EnumMealType.LUNCH);
        LocalDate date = LocalDate.now();
        date.plusDays(1);
        sampleRecipe.setDateSavedFor(date);

        recipeDao.insert(sampleRecipe).blockingSubscribe();

        // test if the database returns the row correctly for the specified meal type and date
        recipeDao.getRecipesOfTypeForDate(EnumMealType.LUNCH, date).test()
                .assertNoErrors()
                .assertValue(localRecipes -> localRecipes.get(0).getId() == sampleRecipe.getId()// check if the inserted row has the same id
                        && localRecipes.get(0).getMealTypeSavedFor().equals(EnumMealType.LUNCH)// check if the inserted row has the same meal type
                        && localRecipes.get(0).getDateSavedFor().isEqual(date));// check if inserted row has the correct specified date
    }

    /**
     * Tests the get rows within dates query by checking whether the operation
     * returns the correct data for range of dates.
     */
    @Test
    public void getRowsWithinDates() {
        LocalDate localDate = LocalDate.now();
        LocalDate tomorrow = localDate.plusDays(1);
        LocalDate nextWeek = localDate.plusWeeks(1);

        // create and insert two recipe for different dates
        LocalRecipe sampleRecipeForTomorrow = TestUtils.createSampleLocalRecipe();
        sampleRecipeForTomorrow.setDateSavedFor(tomorrow);

        // sample recipe for tomorrow
        LocalRecipe sampleRecipeForNextWeek = TestUtils.createSampleLocalRecipe();
        sampleRecipeForNextWeek.setDateSavedFor(nextWeek);

        recipeDao.insert(sampleRecipeForTomorrow).blockingSubscribe();
        recipeDao.insert(sampleRecipeForNextWeek).blockingSubscribe();

        // test if the database returns the row correctly for the specified range of dates
        recipeDao.getRecipesWithinDates(tomorrow, nextWeek).test()
                .assertNoErrors()
                .assertValue(localRecipes -> localRecipes.size() == 2 // check if the returned rows have size of 2 as we inserted two sample recipes
                        && localRecipes.get(0).getDateSavedFor().isAfter(LocalDate.now()) && localRecipes.get(0).getDateSavedFor().isBefore(LocalDate.now().plusWeeks(1).plusDays(1))// check if the inserted row is within the dates range
                        && localRecipes.get(1).getDateSavedFor().isAfter(LocalDate.now()) && localRecipes.get(1).getDateSavedFor().isBefore(LocalDate.now().plusWeeks(1).plusDays(1)));// check if inserted row is within the dates range
    }

    /**
     * Tests the get row by primary id query by checking whether it returns the
     * correct data for the specific primary id.
     */
    @Test
    public void getRowByPrimaryId() {
        // create a sample recipe
        LocalRecipe sampleRecipe = TestUtils.createSampleLocalRecipe();

        // get the id of the inserted recipe
        Long insertedId = recipeDao.insert(sampleRecipe).blockingGet();

        // test if the database returns the correct row for the specified primary id
        recipeDao.getRecipeByPrimaryId(insertedId).test()
                .assertNoErrors()
                .assertValue(localRecipe -> localRecipe.getPrimaryId() == insertedId// check if the recipe was saved by comparing the id
                        && localRecipe.getTitle().equals(sampleRecipe.getTitle()) && localRecipe.getMealTypeSavedFor().equals(sampleRecipe.getMealTypeSavedFor())
                        && localRecipe.getDateSavedFor().isEqual(sampleRecipe.getDateSavedFor()));// check if inserted row has the correct specified date
    }


    /**
     * Closes the database at the end of the test.
     */
    @After
    public void closeDb() {
        mDatabase.close();
    }
}
