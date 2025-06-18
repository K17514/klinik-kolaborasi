package com.example.klinik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BerandaFragment2 extends Fragment {

    private ImageView iconDoor;
    private ImageButton btnDokter, btnObat, btnJanjiTemu, btnMore;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda_dokter, container, false);

        // Tombol logout
        iconDoor = view.findViewById(R.id.icon_door);
        iconDoor.setOnClickListener(v -> logoutUser());

        // Inisialisasi tombol-tombol menu
        btnDokter = view.findViewById(R.id.btn_dokter);
        btnObat = view.findViewById(R.id.btn_obat);
        btnJanjiTemu = view.findViewById(R.id.btn_janji_temu);
        btnMore = view.findViewById(R.id.btn_more);

        // Aksi klik untuk tombol Dokter
        btnDokter.setOnClickListener(v -> {
            DataDokterFragment fragment = new DataDokterFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Aksi klik untuk tombol Obat
        btnObat.setOnClickListener(v -> {
            DataDokterFragment fragment = new DataDokterFragment(); // Gantilah dengan nama fragment yang sesuai jika berbeda
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Aksi klik untuk tombol Janji Temu
        btnJanjiTemu.setOnClickListener(v -> {
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
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), SplashActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
