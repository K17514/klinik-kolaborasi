package com.example.klinik;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.SimpleAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.util.ArrayList;


public class JanjiTemuFragment extends Fragment {

    private ListView listView;
    private SearchView searchView;
    private Button btnTambah;

    private List<Map<String, String>> janjiTemuList = new ArrayList<>();
    private SimpleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_datajanjitemu, container, false);

        listView = view.findViewById(R.id.listView);
        searchView = view.findViewById(R.id.searchView);
        btnTambah = view.findViewById(R.id.btnTambah);

        setupData();
        setupListView();
        setupSearch();

        btnTambah.setOnClickListener(v -> {
            // Handle tambah janji temu
            Toast.makeText(getContext(), "Aksi Input Janji Temu", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void setupData() {
        // Data dummy, bisa diganti ambil dari database atau API
        Map<String, String> item1 = new HashMap<>();
        item1.put("tanggal", "Mei 24 2025");
        item1.put("dokter", "Dr. Ryukusune");
        item1.put("keluhan", "Erm, saya sekarat");
        item1.put("status", "Menunggu diagnosis");

        Map<String, String> item2 = new HashMap<>();
        item2.put("tanggal", "Mei 25 2025");
        item2.put("dokter", "Dr. Haruto");
        item2.put("keluhan", "Pusing dan lemas");
        item2.put("status", "Dikonfirmasi");

        janjiTemuList.add(item1);
        janjiTemuList.add(item2);
    }

    private void setupListView() {
        adapter = new SimpleAdapter(
                getContext(),
                janjiTemuList,
                R.layout.itemlistjanjitemu, // layout list item kamu
                new String[]{"tanggal", "dokter", "keluhan", "status"},
                new int[]{R.id.txtTanggal, R.id.txtDokter, R.id.txtKeluhan, R.id.txtStatus}
        );
        listView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            }
        });
    }

    private void filterData(String keyword) {
        List<Map<String, String>> filtered = new ArrayList<>();
        for (Map<String, String> item : janjiTemuList) {
            if (item.get("dokter").toLowerCase().contains(keyword.toLowerCase()) ||
                    item.get("keluhan").toLowerCase().contains(keyword.toLowerCase()) ||
                    item.get("status").toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(item);
            }
        }

        adapter = new SimpleAdapter(
                getContext(),
                filtered,
                R.layout.itemlistjanjitemu,
                new String[]{"tanggal", "dokter", "keluhan", "status"},
                new int[]{R.id.txtTanggal, R.id.txtDokter, R.id.txtKeluhan, R.id.txtStatus}
        );
        listView.setAdapter(adapter);
    }
}
