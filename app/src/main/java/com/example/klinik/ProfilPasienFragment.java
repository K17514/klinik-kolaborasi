package com.example.klinik;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfilPasienFragment extends Fragment {

    EditText edtNama, edtEmail, edtPassword, edtTanggalLahir;
    ImageView backButton, profileImage;
    Button btnChooseImage, btnSimpan;

    public ProfilPasienFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_pasien, container, false);

        // Inisialisasi komponen UI
        edtNama = view.findViewById(R.id.editTextNama);
        edtEmail = view.findViewById(R.id.editTextEmail);
        edtPassword = view.findViewById(R.id.editTextPassword);
        edtTanggalLahir = view.findViewById(R.id.editTextTanggal);

        backButton = view.findViewById(R.id.back_button);
        profileImage = view.findViewById(R.id.profile_image);

        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        btnSimpan = view.findViewById(R.id.btnTambah);

        // Dummy data sementara
        edtNama.setText("Obama Jonatan");
        edtEmail.setText("Obama@gmail.com");
        edtPassword.setText("******");
        edtTanggalLahir.setText("12/12/2008");

        // Tombol kembali
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Tombol pilih gambar
        btnChooseImage.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Pilih gambar belum diimplementasikan", Toast.LENGTH_SHORT).show();
        });

        // Tombol simpan
        btnSimpan.setOnClickListener(v -> {
            String nama = edtNama.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String tanggalLahir = edtTanggalLahir.getText().toString().trim();

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || tanggalLahir.isEmpty()) {
                Toast.makeText(getContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Profil disimpan!", Toast.LENGTH_SHORT).show();
                // TODO: Kirim ke server atau simpan lokal
            }
        });

        return view;
    }
}
