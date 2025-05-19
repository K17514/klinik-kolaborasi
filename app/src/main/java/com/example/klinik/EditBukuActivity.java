package com.example.klinik;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditBukuActivity extends AppCompatActivity {
    EditText editId, editJudul, editPengarang, editPenerbit, editKategori, editDeskripsi, editStok;
    Button btnUpdate;
    ImageView imageView;
    Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbuku);

        // Inisialisasi view
        editId = findViewById(R.id.editId);
        editJudul = findViewById(R.id.editJudul);
        editPengarang = findViewById(R.id.editPengarang);
        editPenerbit = findViewById(R.id.editPenerbit);
        editKategori = findViewById(R.id.editKategori);
        editDeskripsi = findViewById(R.id.editDeskripsi);
        editStok = findViewById(R.id.editStok);
        btnUpdate = findViewById(R.id.btnUpdate);
        imageView = findViewById(R.id.imageView);
        Button btnChooseImage = findViewById(R.id.btnChooseImage);

        // Ambil nama file gambar dari intent
        String imageFileName = getIntent().getStringExtra("imageUrl");
        if (imageFileName != null && !imageFileName.isEmpty()) {
            String fullImageUrl = getString(R.string.base_url) + "uploads/" + imageFileName;

            // Tampilkan gambar lama pakai Glide
            Glide.with(this)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // optional placeholder
                    .error(R.drawable.ic_error_image) // optional error image
                    .into(imageView);
        }


        // Mendapatkan data yang dikirim dari Intent
        String bukuData = getIntent().getStringExtra("bukuData");
        if (bukuData == null || bukuData.isEmpty()) {
            Toast.makeText(this, "Data Buku tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Misalkan data dikirim dengan format ID: ... , Judul: ..., Pengarang: ...
        String[] data = bukuData.split(", ");
        if (data.length < 5) {
            Toast.makeText(this, "Format data buku tidak valid!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Menampilkan data ke EditText sesuai data yang diterima
        String id = data[0].replace("ID: ", "");
        String judul = data[1].replace("Judul: ", "");
        String pengarang = data[2].replace("Pengarang: ", "");
        String penerbit = data[3].replace("Penerbit: ", "");
        String kategori = data[4].replace("Kategori: ", "");
        String deskripsi = data[5].replace("Deskripsi: ", "");
        String stok = data[6].replace("Stok: ", "");

        // Menetapkan data yang diterima ke field EditText
        editId.setText(id);
        editJudul.setText(judul);
        editPengarang.setText(pengarang);
        editPenerbit.setText(penerbit);
        editKategori.setText(kategori);
        editDeskripsi.setText(deskripsi);
        editStok.setText(stok);

        // Pilih gambar
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);  // Request code 1 untuk memilih gambar
        });

        // Tombol Update
        btnUpdate.setOnClickListener(v -> {
            String encodedImage = "";

            // Jika user memilih gambar baru, encode ke Base64
            if (selectedImageBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }

            // Tetap panggil fungsi update meskipun gambar kosong
            updateData(
                    editId.getText().toString(),
                    editJudul.getText().toString(),
                    editPengarang.getText().toString(),
                    editPenerbit.getText().toString(),
                    editKategori.getText().toString(),
                    editDeskripsi.getText().toString(),
                    editStok.getText().toString(),
                    encodedImage
            );
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();  // Mendapatkan URI gambar yang dipilih
            imageView.setImageURI(selectedImage);  // Menampilkan gambar di ImageView
            selectedImageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap(); // Mengambil bitmap
        }
    }

    // Fungsi untuk update data buku
    private void updateData(final String id, final String judul, final String pengarang, final String penerbit, final String kategori, final String deskripsi, final String stok, final String encodedImage) {
        new Thread(() -> {
            try {
                String baseUrl = getResources().getString(R.string.base_url);
                URL url = new URL(baseUrl + "update_buku.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String params = "id_buku=" + URLEncoder.encode(id, "UTF-8") +
                        "&judul=" + URLEncoder.encode(judul, "UTF-8") +
                        "&pengarang=" + URLEncoder.encode(pengarang, "UTF-8") +
                        "&penerbit=" + URLEncoder.encode(penerbit, "UTF-8") +
                        "&kategori=" + URLEncoder.encode(kategori, "UTF-8") +
                        "&deskripsi=" + URLEncoder.encode(deskripsi, "UTF-8") +
                        "&stok=" + URLEncoder.encode(stok, "UTF-8") +
                        "&cover=" + URLEncoder.encode(encodedImage, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(params.getBytes());
                os.flush();
                os.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(EditBukuActivity.this, "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditBukuActivity.this, MainActivity.class);
                        intent.putExtra("navigate_to_fragment", "data_buku");
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(EditBukuActivity.this, "Gagal mengubah data!", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
