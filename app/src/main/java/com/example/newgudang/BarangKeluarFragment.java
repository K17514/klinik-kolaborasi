package com.example.newgudang;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BarangKeluarFragment extends Fragment {
    ListView listView;
    ArrayList<String> itemList = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Refresh data when broadcast is received
            new GetDataTask().execute();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register the broadcast receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(refreshReceiver,
                new IntentFilter("ACTION_REFRESH_DATA"));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(refreshReceiver);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                // URL ke API PHP
                URL url = new URL("http://192.168.100.119/android/ketersediaan.php"); // Ganti dengan URL API Anda
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
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    // Ambil atribut id, name, email, dan phone
                    String nama_barang = obj.getString("nama_barang");
                    String jenis_barang = obj.getString("jenis_barang");
                    String status = obj.getString("status");
                    String ketersediaan = obj.getString("ketersediaan");

                    // Tambahkan data ke dalam ArrayList (menampilkan id dan nama misalnya)
                    itemList.add("Nama: " + nama_barang + ", Jenis Barang: " + jenis_barang + ", Status Barang: " + status + ", Ketersediaan Barang: " + ketersediaan);
                    idList.add(nama_barang);
                }
                adapter.notifyDataSetChanged();  // Update ListView setelah data ditambahkan
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barangkeluar, container, false);

        listView = view.findViewById(R.id.listView); // Use 'view' to find listView
        adapter = new BarangAdapter(getContext(), itemList, idList); // Use 'getContext()' as parameter
        listView.setAdapter(adapter);

        // Find the button and set an OnClickListener
        Button btn_barangrusak = view.findViewById(R.id.btn_barangrusak);
        btn_barangrusak.setOnClickListener(v -> {
            // Transition to FragmentBarangRusak
            Fragment fragment = new BarangRusakFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment) // Replace with the container ID where the fragment is displayed
                    .addToBackStack(null) // Add to back stack for navigation
                    .commit();
        });

        // Call AsyncTask to fetch data
        new GetDataTask().execute();
        return view; // Return the correct view
    }
}
