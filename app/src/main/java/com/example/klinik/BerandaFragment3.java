package com.example.klinik;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BerandaFragment3 extends Fragment {

    private ImageView iconDoor;
    private ImageButton btnDokter, btnJanjiTemu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda_pasien, container, false);

        // Tombol logout
        iconDoor = view.findViewById(R.id.icon_door);
        iconDoor.setOnClickListener(v -> logoutUser());

        // Tombol dokter dan janji temu
        btnDokter = view.findViewById(R.id.btn_dokter);
        btnJanjiTemu = view.findViewById(R.id.btn_janji_temu);

        btnDokter.setOnClickListener(v -> {
            // Pindah ke DataDokterFragment
            DataDokterFragment fragment = new DataDokterFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)  // ganti fragment_container dengan id container fragment di layout activity-mu
                    .addToBackStack(null)  // optional, supaya bisa kembali ke fragment sebelumnya dengan back button
                    .commit();
        });

        TextView tvGreeting = view.findViewById(R.id.tv_greeting);

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String nama = prefs.getString("nama", "Pengguna");

        tvGreeting.setText("Hi, " + nama + "!");


        btnJanjiTemu.setOnClickListener(v -> {
            // Pindah ke JanjiTemuFragment
            JanjiTemuFragment fragment = new JanjiTemuFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), SplashActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
