package com.example.newgudang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditActivity extends AppCompatActivity {
    EditText editId, editNama, editJenis;
    Spinner spinnerStatus, spinnerKetersediaan;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Inisialisasi view
        editId = findViewById(R.id.editId);
        editNama = findViewById(R.id.editNama);
        editJenis = findViewById(R.id.editJenis);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerKetersediaan = findViewById(R.id.spinnerKetersediaan);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Mendapatkan data yang dikirim dari Intent
        String barangData = getIntent().getStringExtra("barangData");
        if (barangData == null || barangData.isEmpty()) {
            Toast.makeText(this, "Data barang tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String[] data = barangData.split(", ");
        if (data.length < 5) {
            Toast.makeText(this, "Format data barang tidak valid!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String id = data[0].replace("ID: ", "");
        String nama = data[1].replace("Nama: ", "");
        String jenis = data[2].replace("Jenis: ", "");
        String status = data[3].replace("Status: ", "");
        String ketersediaan = data[4].replace("Ketersediaan: ", "");

        // Set nilai pada EditText
        editId.setText(id);
        editNama.setText(nama);
        editJenis.setText(jenis);

        // Konfigurasi Spinner untuk Status
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Baik", "Rusak"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Set nilai awal pada Spinner Status
        if (status.equalsIgnoreCase("Baik")) {
            spinnerStatus.setSelection(0);
        } else if (status.equalsIgnoreCase("Rusak")) {
            spinnerStatus.setSelection(1);
        }

        // Konfigurasi Spinner untuk Ketersediaan
        ArrayAdapter<String> ketersediaanAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Tersedia", "Tidak Tersedia"}
        );
        ketersediaanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKetersediaan.setAdapter(ketersediaanAdapter);

        // Set nilai awal pada Spinner Ketersediaan
        spinnerKetersediaan.setSelection(ketersediaan.equalsIgnoreCase("Tersedia") ? 0 : 1);

        // Klik tombol Update
        btnUpdate.setOnClickListener(v -> {
            String selectedStatus = spinnerStatus.getSelectedItem().toString();
            String selectedKetersediaan = spinnerKetersediaan.getSelectedItem().toString();
            updateData(
                    editId.getText().toString(),
                    editNama.getText().toString(),
                    editJenis.getText().toString(),
                    selectedStatus,
                    selectedKetersediaan
            );
        });
    }

    // Fungsi untuk update data barang
    private void updateData(final String id, final String nama, final String jenis, final String status, final String ketersediaan) {
        new Thread(() -> {
            try {
                // URL ke API PHP untuk update data
                URL url = new URL("http://192.168.100.119/android/update_data.php"); // Ganti dengan URL API Anda
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Parameter yang akan dikirim ke server
                String params = "id_barang=" + URLEncoder.encode(id, "UTF-8") +
                        "&nama_barang=" + URLEncoder.encode(nama, "UTF-8") +
                        "&jenis_barang=" + URLEncoder.encode(jenis, "UTF-8") +
                        "&status=" + URLEncoder.encode(status, "UTF-8") +
                        "&ketersediaan=" + URLEncoder.encode(ketersediaan, "UTF-8");

                // Kirim data ke server
                OutputStream os = conn.getOutputStream();
                os.write(params.getBytes());
                os.flush();
                os.close();

                // Tampilkan pesan berhasil atau gagal
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(EditActivity.this, "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        intent.putExtra("navigate_to_fragment", "data_barang"); // Indikasikan fragment tujuan
                        startActivity(intent);
                        finish(); // Tutup activity saat ini
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(EditActivity.this, "Gagal mengubah data!", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
