package uk.ac.aston.cs3mdd.mealplanner;

import android.os.Bundle;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3mdd.mealplanner.databinding.ActivityMainBinding;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.RecipeViewModel;
import uk.ac.aston.cs3mdd.mealplanner.views.dialogs.DialogCustomMeal;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static final String TAG = "WMK";

    private RecipeViewModel recipeViewModel;
    private CompositeDisposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initNavigation();
        initFab();

        recipeViewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)).get(RecipeViewModel.class);
        mDisposable = new CompositeDisposable();
    }

    /**
     * Sets up the navigation bar.
     */
    private void initNavigation() {
        // Reference: https://stackoverflow.com/questions/65170700/activity-does-not-have-a-navcontroller-set-on
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
//        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        PopupMenu menu = new PopupMenu(this, null);
        menu.inflate(R.menu.bottom_navigation);
        binding.smoothNavigation.setupWithNavController(menu.getMenu(), navController);
    }

    /**
     * Sets up the on click listener for the floating action button to open a dialog
     * box to add a custom meal.
     */
    private void initFab() {
        findViewById(R.id.fab).setOnClickListener(v -> new DialogCustomMeal(MainActivity.this, localRecipe -> {
            // save recipe once the user clicks on save
            mDisposable.add(recipeViewModel.insert(localRecipe)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> Toast.makeText(this, "Recipe Saved", Toast.LENGTH_LONG).show()
                            , throwable -> Utilities.showErrorToast(this)));
        }));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }
}