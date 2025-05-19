package com.example.klinik;


import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;


public class TambahBukuFragment extends Fragment {

    EditText etJudul, etPengarang, etPenerbit, etKategori ,etDeskripsi, etStok;
    ImageView imageView;
    Button btnSubmit , btnChooseImage;
    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout untuk fragment
        View view = inflater.inflate(R.layout.fragment_tambahbuku, container, false);

        // Inisialisasi Views dengan menggunakan 'view'
        etJudul = view.findViewById(R.id.etJudul);
        etPengarang = view.findViewById(R.id.etPengarang);
        etPenerbit = view.findViewById(R.id.etPenerbit);
        etKategori = view.findViewById(R.id.etKategori);
        etDeskripsi = view.findViewById(R.id.etDeskripsi);
        etStok = view.findViewById(R.id.etStok);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        imageView = view.findViewById(R.id.imageView);

        // Aksi ketika tombol Submit ditekan
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil data dari input
                String judul = etJudul.getText().toString();
                String pengarang = etPengarang.getText().toString();
                String penerbit = etPenerbit.getText().toString();
                String kategori = etKategori.getText().toString();
                String deskripsi = etDeskripsi.getText().toString();
                String stok = etStok.getText().toString();

                // Validasi sederhana
                if (judul.isEmpty() || pengarang.isEmpty() || penerbit.isEmpty() || kategori.isEmpty() || deskripsi.isEmpty() || stok.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Proses input data (misalnya disimpan ke database atau dikirim ke server)
                    new SaveDataTask(judul, pengarang, penerbit, kategori, deskripsi, stok, imageUri).execute();

                }
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Memilih gambar dari galeri
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);  // Request code 1
            }
        });

        return view; // Mengembalikan View yang telah di-inflate
    }

    // Menangani hasil pemilihan gambar
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            imageUri = data.getData();
            // Menampilkan gambar di ImageView
            imageView.setImageURI(imageUri);
        }
    }


    // AsyncTask untuk mengirim data ke server
    private class SaveDataTask extends AsyncTask<Void, Void, String> {
        private String judul, pengarang, penerbit, kategori, deskripsi, stok;
        private Uri imageUri;

        public SaveDataTask(String judul, String pengarang, String penerbit, String kategori,
                            String deskripsi, String stok, Uri imageUri) {
            this.judul = judul;
            this.pengarang = pengarang;
            this.penerbit = penerbit;
            this.kategori = kategori;
            this.deskripsi = deskripsi;
            this.stok = stok;
            this.imageUri = imageUri;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String boundary = "===" + System.currentTimeMillis() + "===";
            String lineEnd = "\r\n";
            String twoHyphens = "--";

            try {
                String baseUrl = getResources().getString(R.string.base_url);
                URL url = new URL(baseUrl + "insert_buku.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                // Helper function buat tulis form field
                writeFormField(dos, boundary, "judul", judul);
                writeFormField(dos, boundary, "pengarang", pengarang);
                writeFormField(dos, boundary, "penerbit", penerbit);
                writeFormField(dos, boundary, "kategori", kategori);
                writeFormField(dos, boundary, "deskripsi", deskripsi);
                writeFormField(dos, boundary, "stok", stok);

                // Upload file gambar
                if (imageUri != null) {
                    String filePath = getRealPathFromURI(imageUri);
                    if (filePath != null) {
                        File imageFile = new File(filePath);
                        FileInputStream fis = new FileInputStream(imageFile);

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"cover\"; filename=\"" + imageFile.getName() + "\"" + lineEnd);
                        dos.writeBytes("Content-Type: image/jpeg" + lineEnd);
                        dos.writeBytes(lineEnd);

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, bytesRead);
                        }

                        dos.writeBytes(lineEnd);
                        fis.close();
                    }
                }

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

                // Baca respon dari server
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
            super.onPostExecute(result);
            Toast.makeText(getContext(), "Response: " + result, Toast.LENGTH_LONG).show();
        }

        // Helper untuk field text
        private void writeFormField(DataOutputStream dos, String boundary, String fieldName, String fieldValue) throws IOException {
            String lineEnd = "\r\n";
            String twoHyphens = "--";

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(fieldValue + lineEnd);
        }

        // Dapatkan path asli dari URI
        private String getRealPathFromURI(Uri uri) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
            if (cursor == null) return null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
    }


}