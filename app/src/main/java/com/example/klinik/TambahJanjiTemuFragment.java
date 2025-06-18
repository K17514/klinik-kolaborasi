package com.example.klinik;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TambahJanjiTemuFragment extends Fragment {

    private EditText etKeluhan;
    private Button btnTambah;
    private Spinner spinnerDokter;
    private ArrayList<Dokter> listDokter = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tambahjanjitemu, container, false);

        spinnerDokter = view.findViewById(R.id.spinnerInput1);
        etKeluhan = view.findViewById(R.id.etKeluhan);
        btnTambah = view.findViewById(R.id.btnTambah);

        loadDokterData(); // Load data spinner

        btnTambah.setOnClickListener(v -> simpanJanjiTemu());

        return view;
    }

    private void simpanJanjiTemu() {
        Dokter selectedDokter = (Dokter) spinnerDokter.getSelectedItem();
        if (selectedDokter == null) {
            Toast.makeText(getContext(), "Pilih dokter terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String idDokter = selectedDokter.getId_dokter();
        String keluhan = etKeluhan.getText().toString().trim();

        if (keluhan.isEmpty()) {
            Toast.makeText(getContext(), "Keluhan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String status = "Menunggu";

        SharedPreferences prefs = getActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        String idUser = prefs.getString("id_user", null);
        if (idUser == null) {
            Toast.makeText(getContext(), "Silakan login ulang", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String baseUrl = getResources().getString(R.string.base_url);
                    URL url = new URL(baseUrl + "insert_janjitemu.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // URL encode parameters
                    String data = "id_user=" + URLEncoder.encode(idUser, "UTF-8") +
                            "&id_dokter=" + URLEncoder.encode(idDokter, "UTF-8") +
                            "&keluhan=" + URLEncoder.encode(keluhan, "UTF-8") +
                            "&tanggal=" + URLEncoder.encode(tanggal, "UTF-8") +
                            "&status=" + URLEncoder.encode(status, "UTF-8");

                    conn.getOutputStream().write(data.getBytes("UTF-8"));

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    return sb.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(getContext(), "Respon: " + result, Toast.LENGTH_LONG).show();

                // Setelah input berhasil, ganti fragment ke DataJanjiTemuFragment
                Fragment fragment = new JanjiTemuFragment();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment) // Pastikan ID sesuai container kamu
                        .addToBackStack(null)
                        .commit();
            }

        }.execute();
    }


    private void loadDokterData() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String baseUrl = getResources().getString(R.string.base_url);
                    URL url = new URL(baseUrl + "spinnerdokter.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    return result.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    Toast.makeText(getContext(), "Gagal mengambil data dokter", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject json = new JSONObject(result);
                    JSONArray dokterArray = json.getJSONArray("dokter");
                    listDokter.clear();

                    for (int i = 0; i < dokterArray.length(); i++) {
                        JSONObject obj = dokterArray.getJSONObject(i);
                        Dokter dokter = new Dokter(
                                obj.getString("id_user"),
                                obj.getString("nama_d"),
                                obj.getString("spesialis")
                        );

                        listDokter.add(dokter);
                    }

                    ArrayAdapter<Dokter> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            listDokter
                    );
                    spinnerDokter.setAdapter(adapter);


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
