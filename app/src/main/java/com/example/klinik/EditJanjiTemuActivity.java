package com.example.klinik;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditJanjiTemuActivity extends AppCompatActivity {

    EditText editInput1, editInput2, editInput3, editInput4;
    Spinner spinnerObat;
    ImageView btnBack;
    Button btnTambah;

    ArrayList<String> namaObatList = new ArrayList<>();
    ArrayList<String> idObatList = new ArrayList<>();
    ArrayAdapter<String> obatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editjanjitemu);

        editInput1 = findViewById(R.id.editInput1);
        editInput2 = findViewById(R.id.editInput2);
        editInput3 = findViewById(R.id.editInput3);
        editInput4 = findViewById(R.id.editInput4);
        spinnerObat = findViewById(R.id.spinnerObat);
        btnBack = findViewById(R.id.btnBack);
        btnTambah = findViewById(R.id.btnTambah);

        // Isi data pasien & keluhan dari Intent
        editInput1.setText(getIntent().getStringExtra("pasien"));
        editInput2.setText(getIntent().getStringExtra("keluhan"));
        editInput3.setText(getIntent().getStringExtra("hasil_diagnosa"));
        editInput4.setText(getIntent().getStringExtra("tindakan"));


        loadObatFromAPI();

        boolean isReadOnly = getIntent().getBooleanExtra("mode_readonly", false);
        if (isReadOnly) {
            editInput3.setEnabled(false); // hasil diagnosa
            editInput4.setEnabled(false); // tindakan
            editInput3.setFocusable(false);
            editInput3.setBackgroundColor(Color.TRANSPARENT);
            spinnerObat.setEnabled(false);
            btnTambah.setVisibility(View.GONE); // sembunyikan tombol submit
        }


        btnBack.setOnClickListener(v -> finish());

        btnTambah.setOnClickListener(v -> {
            String hasil = editInput3.getText().toString().trim();
            String tindakan = editInput4.getText().toString().trim();
            String idRm = getIntent().getStringExtra("id_rm");
            Log.d("CekIDJanji", "ID Janji yang diterima: " + idRm);

            int selectedIndex = spinnerObat.getSelectedItemPosition();

            if (hasil.isEmpty() || tindakan.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi hasil dan tindakan", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedIndex < 0 || selectedIndex >= idObatList.size()) {
                Toast.makeText(this, "Pilih obat terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            String idObat = idObatList.get(selectedIndex);

            String url = getString(R.string.base_url) + "update_janjitemu.php";

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Toast.makeText(this, "Janji temu diperbarui!", Toast.LENGTH_SHORT).show();
                        finish(); // kembali ke halaman sebelumnya
                    },
                    error -> {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            String errorBody = new String(error.networkResponse.data);
                            Log.e("VolleyError", "Response: " + errorBody);
                        } else {
                            Log.e("VolleyError", "No network response, might be timeout or unreachable server");
                        }
                        Toast.makeText(this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_rm", idRm);
                    params.put("hasil_diagnosa", hasil);
                    params.put("tindakan", tindakan);
                    params.put("id_obat", idObat);
                    return params;
                }
            };

            queue.add(request);
        });
    }

    private void loadObatFromAPI() {
        String url = getString(R.string.base_url) + "get_obat.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    namaObatList.clear();
                    idObatList.clear();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            namaObatList.add(obj.getString("nama_obat"));
                            idObatList.add(obj.getString("id_obat"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    obatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, namaObatList);
                    obatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerObat.setAdapter(obatAdapter);

                    String idObatIntent = getIntent().getStringExtra("id_obat");
                    if (idObatIntent != null) {
                        int index = idObatList.indexOf(idObatIntent);
                        if (index >= 0) {
                            spinnerObat.setSelection(index);
                        }
                    }

                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Gagal ambil data obat", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
}
