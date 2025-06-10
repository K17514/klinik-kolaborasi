package com.example.klinik;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class BerandaFragment extends Fragment {

    ImageView btnLogout;
    ImageButton btnDokter, btnJanjiTemu, btnMore;
    SearchView searchView;
    ListView listAktivitas;
    ArrayAdapter<String> adapter;
    ArrayList<String> aktivitasList;

    public BerandaFragment() {
        // Konstruktor kosong wajib
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_beranda_pasien, container, false);

        // Inisialisasi view
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDokter = view.findViewById(R.id.btn_dokter);
        btnJanjiTemu = view.findViewById(R.id.btn_janji_temu);
        btnMore = view.findViewById(R.id.btn_more);
        searchView = view.findViewById(R.id.search_view);
        listAktivitas = view.findViewById(R.id.list_aktivitas);

        // Data dummy aktivitas
        aktivitasList = new ArrayList<>();
        aktivitasList.add("Konsultasi dengan Dr. Andi");
        aktivitasList.add("Cek tekanan darah");
        aktivitasList.add("Ambil resep obat");
        aktivitasList.add("Tes laboratorium");

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, aktivitasList);
        listAktivitas.setAdapter(adapter);

        // Aksi tombol Dokter
        btnDokter.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, new DataDokterFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Aksi tombol Janji Temu
        btnJanjiTemu.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, new JanjiTemuFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Aksi tombol More
        btnMore.setOnClickListener(v -> showToast("Fitur lainnya belum tersedia"));

        // Aksi Logout
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();
            // Intent ke LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Hapus back stack
            startActivity(intent);
        });

        // Aksi SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        // Aksi klik list item
        listAktivitas.setOnItemClickListener((parent, view1, position, id) -> {
            String selected = adapter.getItem(position);
            showToast("Dipilih: " + selected);
        });

        return view;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
