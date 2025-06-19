package com.example.klinik;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Map;

public class JanjiTemuDokterAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> dataList;
    private LayoutInflater inflater;

    public JanjiTemuDokterAdapter(Context context, List<Map<String, String>> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.itemlistjanjitemu, parent, false);
            holder = new ViewHolder();
            holder.txtTanggal = convertView.findViewById(R.id.txtTanggal);
            holder.txtJDokter = convertView.findViewById(R.id.txtJDokter); // 🔥 tambahkan ini
            holder.txtDokter = convertView.findViewById(R.id.txtDokter);
            holder.txtKeluhan = convertView.findViewById(R.id.txtKeluhan);
            holder.txtStatus = convertView.findViewById(R.id.txtStatus);
            holder.btnAksi = convertView.findViewById(R.id.btnAksi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, String> item = dataList.get(position);

        holder.txtTanggal.setText(item.get("tanggal"));
        holder.txtDokter.setText(item.get("pasien")); // dari data pasien
        holder.txtKeluhan.setText(item.get("keluhan"));
        holder.txtStatus.setText(item.get("status"));

        // Ganti label "Dokter :" menjadi "Pasien :"
        holder.txtJDokter.setText("Pasien  :");


        if (item.get("status").equalsIgnoreCase("Selesai")) {
            holder.btnAksi.setText("Detail");
            holder.btnAksi.setEnabled(true);
            holder.btnAksi.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.darker_gray));
        } else {
            holder.btnAksi.setText("Diagnosa");
            holder.btnAksi.setEnabled(true);
            holder.btnAksi.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.biru));
        }



        // Aksi tombol
        holder.btnAksi.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditJanjiTemuActivity.class);
            intent.putExtra("tanggal", item.get("tanggal"));
            intent.putExtra("pasien", item.get("pasien"));
            intent.putExtra("keluhan", item.get("keluhan"));
            intent.putExtra("status", item.get("status"));
            intent.putExtra("id_rm", item.get("id_rm"));
            intent.putExtra("hasil_diagnosa", item.get("hasil_diagnosa"));
            intent.putExtra("tindakan", item.get("tindakan"));
            intent.putExtra("id_obat", item.get("id_obat")); // untuk pre-select spinner
            intent.putExtra("mode_readonly", item.get("status").equalsIgnoreCase("Selesai")); // true kalau selesai
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        return convertView;
    }

    static class ViewHolder {
        TextView txtJDokter, txtTanggal, txtDokter, txtKeluhan, txtStatus;
        Button btnAksi;
    }
}
