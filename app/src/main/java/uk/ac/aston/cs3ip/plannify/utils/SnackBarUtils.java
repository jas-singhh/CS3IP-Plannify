package uk.ac.aston.cs3ip.plannify.utils;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.adapters.HomeMealsAdapter;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.viewmodels.HomeViewModel;

/**
 *
 * This class provides utility functions to show SnackBar popups based
 * on different scenarios.
 *
 * @author Jaskaran Singh
 */
public class SnackBarUtils {

    /**
     * Shows a SnackBar indicating that the recipe is already saved, along with a
     * dismiss button.
     *
     * @param view view required to show the SnackBar.
     */
    public static void showInformationalSnackBarIfRecipeAlreadySaved(View view) {
        // reference: https://m2.material.io/components/snackbars/android#theming-snackbars
        final Snackbar snackbar = Snackbar.make(view, "Recipe is already saved", Snackbar.LENGTH_LONG);
        snackbar.setAction("Dismiss", v -> snackbar.dismiss());
        snackbar.show();
    }

    /**
     * Shows a SnackBar indicating that the recipe has been saved, along with a
     * dismiss button.
     *
     * @param view view required to show the SnackBar.
     */
    public static void showInformationalSnackBarOnRecipeSaved(View view) {
        // reference: https://m2.material.io/components/snackbars/android#theming-snackbars
        final Snackbar snackbar = Snackbar.make(view, "Recipe saved", Snackbar.LENGTH_LONG);
        snackbar.setAction("Dismiss", v -> snackbar.dismiss());
        snackbar.show();
    }


    /**
     * Displays a SnackBar indicating that the recipe has been saved and allows the user to undo
     * the action - aligns with Nielsen's error prevention principle.
     *
     * @param view            view required to show the SnackBar.
     * @param recipeToUndo    recipe to undo that was originally saved.
     * @param adapterPos      adapter position to update.
     * @param disposable      disposable that will contain the database query.
     * @param recipeViewModel view model containing the operations for saving and deleting the recipe.
     * @param adapter         adapter to notify once the changes take place.
     */
    public static void showSnackBarWithUndoForSavedRecipe(View view, LocalRecipe recipeToUndo, int adapterPos, CompositeDisposable disposable,
                                                           HomeViewModel recipeViewModel, HomeMealsAdapter adapter) {
        // reference: https://m2.material.io/components/snackbars/android#theming-snackbars
        Snackbar.make(view, "Recipe Saved", Snackbar.LENGTH_LONG).setAction("Undo", v -> {
            // undo - delete the saved recipe
            disposable.add(recipeViewModel.delete(recipeToUndo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> adapter.notifyItemChanged(adapterPos),
                            throwable -> Log.e(MainActivity.TAG, "error: " + throwable)));

        }).show();
    }

    /**
     * Displays a SnackBar indicating that the recipe has been deleted and allows the user to undo
     * the action - aligns with Nielsen's error prevention principle.
     *
     * @param view            view required to show the SnackBar.
     * @param recipeToUndo    recipe to undo that was originally saved.
     * @param adapterPos      adapter position to update.
     * @param disposable      disposable that will contain the database query.
     * @param recipeViewModel view model containing the operations for saving and deleting the recipe.
     * @param adapter         adapter to notify once the changes take place.
     */
    public static void showSnackBarWithUndoForDeletedRecipe(View view, LocalRecipe recipeToUndo, int adapterPos, CompositeDisposable disposable,
                                                             HomeViewModel recipeViewModel, HomeMealsAdapter adapter) {
        if (recipeToUndo == null || adapterPos == RecyclerView.NO_POSITION) return;

        // reference: https://m2.material.io/components/snackbars/android#theming-snackbars
        Snackbar.make(view, "Recipe Removed", Snackbar.LENGTH_LONG).setAction("Undo", v -> {
            // undo - delete the saved recipe
            disposable.add(recipeViewModel.insert(recipeToUndo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(primaryId -> adapter.notifyItemChanged(adapterPos),
                            throwable -> Log.e(MainActivity.TAG, "error: " + throwable)));
        }).show();
    }

}
