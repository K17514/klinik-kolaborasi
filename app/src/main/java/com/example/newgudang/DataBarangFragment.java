package com.example.newgudang;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
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

public class DataBarangFragment extends Fragment {
    ListView listView;
    ArrayList<String> itemList = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_databarang, container, false);

        listView = view.findViewById(R.id.listView);
        adapter = new BarangAdapter(getContext(), itemList, idList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = itemList.get(position);
            Intent intent = new Intent(getContext(), EditActivity.class);
            intent.putExtra("barangData", selectedItem);  // Kirim data ke EditActivity
            startActivity(intent);
        });

        new GetDataTask().execute();
        return view;
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                URL url = new URL("http://192.168.100.119/android/get_data.php");
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

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String id_barang = obj.optString("id_barang", "Tidak Diketahui");
                    String nama_barang = obj.optString("nama_barang", "Tidak Diketahui");
                    String jenis_barang = obj.optString("jenis_barang", "Tidak Diketahui");
                    String status = obj.optString("status", "Tidak Diketahui");
                    String ketersediaan = obj.optString("ketersediaan", "Tidak Diketahui");

                    itemList.add("ID: " + id_barang + ", Nama: " + nama_barang + ", Jenis: " + jenis_barang + ", Status: " + status + ", Ketersediaan: " + ketersediaan);
                    idList.add(id_barang);
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

