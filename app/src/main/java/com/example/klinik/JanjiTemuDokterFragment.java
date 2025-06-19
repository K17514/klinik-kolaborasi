package com.example.klinik;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
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
    private JanjiTemuDokterAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datajanjitemu, container, false);

        listView = view.findViewById(R.id.listView);
        searchView = view.findViewById(R.id.searchView);
        btnTambah = view.findViewById(R.id.btnTambah);
        btnTambah.setVisibility(View.GONE);

        setupListView();
        setupData();
        setupSearch();

        return view;
    }

    private void setupData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String idUser = prefs.getString("id_user", "");
        Log.d("JANJI_TEMU", "id_user saat ini: " + idUser);

        String baseUrl = getResources().getString(R.string.base_url);
        String url = baseUrl + "get_janjitemudokter.php?id_user=" + idUser;
        Log.d("JANJI_TEMU", "URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    janjiTemuList.clear();
                    Log.d("JANJI_TEMU", "RESP: " + response.toString());
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            Map<String, String> item = new HashMap<>();
                            item.put("tanggal", obj.getString("tanggal_berobat"));
                            item.put("pasien", obj.getString("nama_p"));  // nama pasien
                            item.put("keluhan", obj.getString("keluhan_pasien"));
                            item.put("status", obj.getString("status"));
                            item.put("id_rm", obj.getString("id_rm")); // ✅ Tambahkan ini
                            item.put("hasil_diagnosa", obj.optString("hasil_diagnosa", ""));
                            item.put("tindakan", obj.optString("tindakan", ""));
                            item.put("id_obat", obj.optString("id_obat", ""));




                            janjiTemuList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("JANJI_TEMU", "JSON ERROR: " + e.getMessage());
                        Toast.makeText(getContext(), "Gagal parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("JANJI_TEMU", "Volley ERROR: " + error.toString());
                    Toast.makeText(getContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }

    private void setupListView() {
        adapter = new JanjiTemuDokterAdapter(getContext(), janjiTemuList);
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
            if (item.get("pasien").toLowerCase().contains(keyword.toLowerCase()) ||
                    item.get("keluhan").toLowerCase().contains(keyword.toLowerCase()) ||
                    item.get("status").toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(item);
            }
        }

        adapter = new JanjiTemuDokterAdapter(getContext(), filtered);
        listView.setAdapter(adapter);
    }
}
