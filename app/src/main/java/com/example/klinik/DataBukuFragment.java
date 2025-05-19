package com.example.klinik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DataBukuFragment extends Fragment {
    ListView listView;
    ArrayList<String> itemList = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();
    ArrayList<String> imageList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new GetDataTask().execute(); // Refresh data
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(refreshReceiver,
                new IntentFilter("ACTION_REFRESH_DATA"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(refreshReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_databuku, container, false);

        listView = view.findViewById(R.id.listView);
        adapter = new BukuAdapter(getContext(), itemList, idList, imageList);
        listView.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.searchView);
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





        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = itemList.get(position);
            Intent intent = new Intent(getContext(), EditBukuActivity.class);
            intent.putExtra("barangData", selectedItem);  // Kirim data ke EditActivity
            startActivity(intent);
        });

        Button btnTambah = view.findViewById(R.id.btnTambah);
        btnTambah.setOnClickListener(v -> {
            // Transition to FragmentBarangRusak
            Fragment fragment = new TambahBukuFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment) // Replace with the container ID where the fragment is displayed
                    .addToBackStack(null) // Add to back stack for navigation
                    .commit();
        });

        new GetDataTask().execute();
        return view;
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                String baseUrl = getResources().getString(R.string.base_url);
                URL url = new URL(baseUrl + "get_buku.php");// Ganti dengan URL API Anda
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                result = sb.toString();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                itemList.clear();
                idList.clear();
                imageList.clear();

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String id_buku = obj.optString("id_buku", "Tidak Diketahui");
                    String judul = obj.optString("judul", "Tidak Diketahui");
                    String pengarang = obj.optString("pengarang", "Tidak Diketahui");
                    String penerbit = obj.optString("penerbit", "Tidak Diketahui");
                    String kategori = obj.optString("kategori", "Tidak Diketahui");
                    String deskripsi = obj.optString("deskripsi", "Tidak Diketahui");
                    String stok = obj.optString("stok", "Tidak Diketahui");
                    String cover = obj.optString("cover", ""); // Ganti 'gambar' menjadi 'cover'

                    // Menambahkan informasi buku ke dalam list
                    itemList.add("ID: " + id_buku + ", Judul: " + judul + ", Pengarang: " + pengarang + ", Penerbit: " + penerbit + ", Kategori: " + kategori + ", Deskripsi: " + deskripsi + ", Stok: " + stok);
                    idList.add(id_buku);
                    imageList.add(cover); // Menyimpan nama file cover (gambar) untuk dipakai di adapter
                }
                ((BukuAdapter) adapter).updateData(itemList, idList, imageList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

