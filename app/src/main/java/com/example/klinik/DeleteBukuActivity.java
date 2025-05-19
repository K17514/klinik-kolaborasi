package com.example.klinik;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteBukuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_buku);
        String id_buku = getIntent().getStringExtra("id_buku");
        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(id_buku);
            }
        });
    }

    private void deleteData(final String id_buku) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseUrl = getResources().getString(R.string.base_url);
                    URL url = new URL(baseUrl + "delete_buku.php");// Ganti dengan URL API Anda
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Explicitly set header

                    // Send data
                    String params = "id_buku=" + id_buku;
                    OutputStream os = conn.getOutputStream();
                    os.write(params.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    // Read response for debugging
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Log response
                    Log.d("DeleteBukuActivity", "Server response: " + response.toString());

                    // Check if deletion was successful
                    if (response.toString().contains("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(DeleteBukuActivity.this, "Data successfully deleted!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DeleteBukuActivity.this, MainActivity.class);
                            intent.putExtra("navigate_to_fragment", "data_siswa");
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(DeleteBukuActivity.this, "Failed to delete data: " + response.toString(), Toast.LENGTH_LONG).show();
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("DeleteBukuActivity", "Error: " + e.getMessage());
                }
            }
        }).start();
    }

}
