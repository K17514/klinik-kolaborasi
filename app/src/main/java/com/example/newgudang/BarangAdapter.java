package com.example.newgudang;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class BarangAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> barangList;
    private final ArrayList<String> idList; // Daftar ID barang untuk referensi

    public BarangAdapter (Context context, ArrayList<String> barangList, ArrayList<String> idList) {
        super(context, R.layout.itemlist, barangList);
        this.context = context;
        this.barangList = barangList;
        this.idList = idList; // Menyimpan daftar ID barang
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
// Inflate custom Layout untuk setiap item
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.itemlist, parent, false);
        }

        // Ambil item data barang saat ini
        String barang = barangList.get(position);
        String idBarang = idList.get(position); // Ambil ID barang yang sesuai

        // Set text dari barang
        TextView txtBarang = convertView.findViewById(R.id.txtBarang);
        txtBarang.setText(barang);

        // Tombol Ubah
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka EditActivity dan kirimkan data barang yang akan diubah
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("barangData", barang); // Mengirim data ke EditActivity
                context.startActivity(intent);
            }
        });

            // Tombol Hapus
            Button btnDelete = convertView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    // Panggil DeleteDataActivity dan kirim id barang
                    Intent intent = new Intent(context, DeleteDataActivity.class);
                    intent.putExtra("id_barang", idBarang); // Mengirimkan ID barang ke DeleteDataActivity
                    context.startActivity(intent);
            }
            });
                return convertView;
        }
    }