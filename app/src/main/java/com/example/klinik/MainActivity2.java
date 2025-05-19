package com.example.klinik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment (Beranda)
        if (savedInstanceState == null) {
            String fragmentToLoad = getIntent().getStringExtra("navigate_to_fragment");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BerandaFragment2()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // Use if-else to determine which fragment to display
                if (item.getItemId() == R.id.menu_beranda) {
                    selectedFragment = new BerandaFragment2();
                } else if (item.getItemId() == R.id.menu_data_buku) {
                    selectedFragment = new DataBukuFragment();
                }

                // Display the selected fragment
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });
    }
}
