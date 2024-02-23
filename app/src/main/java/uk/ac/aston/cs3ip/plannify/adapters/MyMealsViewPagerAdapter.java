package uk.ac.aston.cs3ip.plannify.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import uk.ac.aston.cs3ip.plannify.views.my_meals.MyMealsGroceryListFragment;
import uk.ac.aston.cs3ip.plannify.views.my_meals.MyMealsSavedFragment;

public class MyMealsViewPagerAdapter extends FragmentStateAdapter {

    // reference: https://developer.android.com/guide/navigation/navigation-swipe-view-2

    public MyMealsViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int).
        switch(position) {
            case 0:
                return new MyMealsSavedFragment();
            case 1:
                return new MyMealsGroceryListFragment();
        }

        return new MyMealsSavedFragment();// default
    }

    @Override
    public int getItemCount() {
        return 2;//there are two tabs in the "my meals" fragment
    }
}
