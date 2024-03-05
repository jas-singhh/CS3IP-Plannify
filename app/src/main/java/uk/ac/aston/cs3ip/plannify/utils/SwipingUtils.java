package uk.ac.aston.cs3ip.plannify.utils;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.adapters.HomeMealsAdapter;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.NetworkRecipe;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.viewmodels.HomeViewModel;
import uk.ac.aston.cs3ip.plannify.views.dialogs.DialogSaveRecipe;

/**
 * This class provides utilities for different views to set up the swiping functionality,
 * such as the swipe to save and swipe to remove operations.
 *
 * @author Jaskaran Singh
 */
public class SwipingUtils {

    /**
     * Sets up the swipe to save functionality and displays a snack bar to provide the
     * undo functionality.
     *
     * @param activity     activity required for the dialog and the snack bar.
     * @param adapter      adapter to update when performing the swiping gesture.
     * @param disposable   disposable that will contain the queries to save or remove the recipe.
     * @param view         view required for the snack bar.
     * @param recyclerView recycler view, to which to attach the swiping functionality.
     */
    public static void setupSwipeToSave(MainActivity activity, HomeViewModel homeViewModel, HomeMealsAdapter adapter,
                                        CompositeDisposable disposable, @NonNull View view,
                                        RecyclerView recyclerView) {

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // only allow right swipe
                if (direction == ItemTouchHelper.RIGHT) {
                    int pos = viewHolder.getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {

                        // display popup to request date and meal type for the recipe
                        new DialogSaveRecipe(activity, adapter.getRecipeAt(pos), (date, mealType) -> {
                            NetworkRecipe selectedNetworkRecipe = adapter.getRecipeAt(pos);
                            LocalRecipe localRecipe = new LocalRecipe(selectedNetworkRecipe, date, mealType);
                            localRecipe.setDateSavedFor(date);
                            localRecipe.setMealTypeSavedFor(mealType);

                            // reference: https://developer.android.com/training/data-storage/room/async-queries
                            disposable.add(homeViewModel.insert(localRecipe)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(primaryId -> {
                                                // set the primary id Room Database assigned to the recipe so
                                                // we can undo it
                                                // if the id returns -1, it means that the recipe was not
                                                // inserted as it already exists - so inform the user.
                                                if (primaryId == -1) {
                                                    SnackBarUtils.showInformationalSnackBarIfRecipeAlreadySaved(view);
                                                } else {
                                                    localRecipe.setPrimaryId(primaryId);
                                                    SnackBarUtils.showSnackBarWithUndoForSavedRecipe(view, localRecipe, pos, disposable, homeViewModel, adapter);
                                                }
                                            },
                                            throwable -> Utilities.showErrorToast(activity)));
                        });

                        adapter.notifyItemChanged(pos);
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // reference: https://github.com/xabaras/RecyclerViewSwipeDecorator
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(activity, R.color.green))
                        .addActionIcon(R.drawable.ic_add)
                        .setActionIconTint(ContextCompat.getColor(activity, R.color.white))
                        .addSwipeRightLabel("Save")
                        .setSwipeRightLabelColor(ContextCompat.getColor(activity, R.color.white))
                        .addCornerRadius(1, 50)
                        .addSwipeRightPadding(1, 10, 10, 10)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }

    /**
     * Sets up the swipe to delete functionality and displays a snack bar to provide the
     * undo functionality.
     *
     * @param activity     activity required for the dialog and the snack bar.
     * @param adapter      adapter to update when performing the swiping gesture.
     * @param disposable   disposable that will contain the queries to save or remove the recipe.
     * @param view         view required for the snack bar.
     * @param recyclerView recycler view, to which to attach the swiping functionality.
     */
    public static void setupSwipeToDelete(MainActivity activity, HomeViewModel homeViewModel, HomeMealsAdapter adapter,
                                          CompositeDisposable disposable, @NonNull View view,
                                          RecyclerView recyclerView) {

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // check for left swipe
                if (direction == ItemTouchHelper.LEFT) {
                    int pos = viewHolder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && adapter != null) {
                        LocalRecipe recipeToDelete = (LocalRecipe) adapter.getRecipeAt(pos);

                        // delete the recipe
                        disposable.add(homeViewModel.delete(recipeToDelete)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            SnackBarUtils.showSnackBarWithUndoForDeletedRecipe(
                                                    view,
                                                    (LocalRecipe) adapter.getRecipeAt(pos),
                                                    pos,
                                                    disposable,
                                                    homeViewModel,
                                                    adapter);
                                            // update adapter
                                            adapter.notifyItemChanged(pos);
                                        }
                                        , throwable -> Utilities.showErrorToast(activity)));
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // reference: https://github.com/xabaras/RecyclerViewSwipeDecorator
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(activity, R.color.error_red))
                        .addActionIcon(R.drawable.ic_remove)
                        .setActionIconTint(ContextCompat.getColor(activity, R.color.white))
                        .addSwipeLeftLabel("Remove")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(activity, R.color.white))
                        .addCornerRadius(1, 50)
                        .addSwipeLeftPadding(1, 10, 10, 10)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }



}
