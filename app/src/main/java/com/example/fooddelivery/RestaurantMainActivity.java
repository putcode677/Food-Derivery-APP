package com.example.fooddelivery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fooddelivery.fragments.CategoriesFragment;
import com.example.fooddelivery.fragments.DashboardFragment;
import com.example.fooddelivery.fragments.FoodsFragment;
import com.example.fooddelivery.fragments.OrdersFragment;
import com.example.fooddelivery.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RestaurantMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        bottomNav = findViewById(R.id.bottomNav);

        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                loadFragment(new DashboardFragment());
                return true;
            } else if (id == R.id.nav_orders) {
                loadFragment(new OrdersFragment());
                return true;
            } else if (id == R.id.nav_foods) {
                loadFragment(new FoodsFragment());
                return true;
            } else if (id == R.id.nav_categories) {
                loadFragment(new CategoriesFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
