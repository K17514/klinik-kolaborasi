package com.example.newgudang;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class DeleteDataActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_data);
        String idBarang = getIntent().getStringExtra("id_barang");
        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(idBarang);
            }
        });
    }

    private void deleteData(final String idBarang) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
// URL ke API PHP untuk delete data
                    URL url = new
                            URL("http://192.168.100.119/android/delete_data.php"); // Ganti dengan URL API Anda
                    HttpURLConnection conn = (HttpURLConnection)
                            url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
// Parameter yang dikirim ke server
                    String params = "id_barang=" + idBarang;
// Kirim data ke server
                    OutputStream os = conn.getOutputStream();
                    os.write(params.getBytes());
                    os.flush();
                    os.close();
// Cek response dari server
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> {
                            Toast.makeText(DeleteDataActivity.this, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DeleteDataActivity.this, MainActivity.class);
                            intent.putExtra("navigate_to_fragment", "data_barang"); // Indicate which fragment to load
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DeleteDataActivity.this,
                                        "Gagal menghapus data!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
