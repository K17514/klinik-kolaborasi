package com.example.newgudang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment (Beranda)
        if (savedInstanceState == null) {
            String fragmentToLoad = getIntent().getStringExtra("navigate_to_fragment");
            if ("data_barang".equals(fragmentToLoad)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DataBarangFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BerandaFragment()).commit();
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // Use if-else to determine which fragment to display
                if (item.getItemId() == R.id.menu_beranda) {
                    selectedFragment = new BerandaFragment();
                } else if (item.getItemId() == R.id.menu_data_barang) {
                    selectedFragment = new DataBarangFragment();
                } else if (item.getItemId() == R.id.menu_tambah_data) {
                    selectedFragment = new TambahDataFragment();
                } else if (item.getItemId() == R.id.menu_barang_keluar) {
                    selectedFragment = new BarangKeluarFragment();
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
