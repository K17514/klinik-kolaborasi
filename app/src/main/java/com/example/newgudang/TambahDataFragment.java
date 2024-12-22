package com.example.newgudang;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;


import androidx.fragment.app.Fragment;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TambahDataFragment extends Fragment {

    EditText etNama, etJenis;
    Button btnSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout untuk fragment
        View view = inflater.inflate(R.layout.fragment_tambahdata, container, false);

        // Inisialisasi Views dengan menggunakan 'view'
        etNama = view.findViewById(R.id.etNama);
        etJenis = view.findViewById(R.id.etJenis);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Aksi ketika tombol Submit ditekan
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil data dari input
                String nama_barang = etNama.getText().toString();
                String jenis_barang = etJenis.getText().toString();

                // Validasi sederhana
                if (nama_barang.isEmpty() || jenis_barang.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Proses input data (misalnya disimpan ke database atau dikirim ke server)
                    new SaveDataTask().execute(nama_barang, jenis_barang);
                }
            }
        });

        return view; // Mengembalikan View yang telah di-inflate
    }

    // AsyncTask untuk mengirim data ke server
    private class SaveDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String nama_barang = params[0];
            String jenis_barang = params[1];

            Log.d("SaveDataTask", "Nama Barang: " + nama_barang + ", Jenis Barang: " + jenis_barang); // Log values for debugging

            try {
                URL url = new URL("http://192.168.100.119/android/insert_data.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Data to send to the server
                String data = "nama_barang=" + URLEncoder.encode(nama_barang, "UTF-8") +
                        "&jenis_barang=" + URLEncoder.encode(jenis_barang, "UTF-8");

                // Write data to output stream
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();

                // Read response from the server
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
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Menampilkan respons dari server
            if (result != null) {
                Toast.makeText(getContext(), "Response from server: " + result, Toast.LENGTH_LONG).show();

                // Jika respons sukses, pindah ke fragment DataBarangFragment
                if (result.contains("Data inserted successfully")) {  // Pastikan pesan ini sesuai dengan yang dikirim dari server PHP
                    // Pindah ke DataBarangFragment setelah data berhasil disimpan
                    Fragment dataBarangFragment = new DataBarangFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, dataBarangFragment) // fragment_container adalah id layout yang menampung fragment
                            .addToBackStack(null) // Menambahkan transaksi ke backstack
                            .commit();
                }
            } else {
                Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_LONG).show();
            }
        }
    }

}