package com.example.klinik;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JanjiTemuDokterFragment extends Fragment {

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
            // Pindah ke TambahJanjiTemuFragment
            Fragment tambahFragment = new TambahJanjiTemuFragment();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, tambahFragment) // Ganti dengan ID container fragment kamu
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    private void setupData() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String idUser = preferences.getString("id_user", ""); // Ambil id_user dari SharedPreferences

        String baseUrl = getResources().getString(R.string.base_url);
        String url = baseUrl + "get_janjitemudokter.php?id_user=" + idUser;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    janjiTemuList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            Map<String, String> item = new HashMap<>();
                            item.put("tanggal", obj.getString("tanggal_berobat"));
                            item.put("dokter", obj.getString("nama_d"));
                            item.put("keluhan", obj.getString("keluhan_pasien"));
                            item.put("status", obj.getString("status"));

                            janjiTemuList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Gagal parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Gagal ambil data dari server", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
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
