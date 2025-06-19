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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JanjiTemuAdapter extends BaseAdapter {

    private final Context context;
    private final List<Map<String, String>> dataList;
    private final LayoutInflater inflater;
    private final String baseUrl;

    public JanjiTemuAdapter(Context context, List<Map<String, String>> dataList, String baseUrl) {
        this.context = context;
        this.dataList = dataList;
        this.baseUrl = baseUrl;
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
        View view = inflater.inflate(R.layout.itemlistjanjitemu, parent, false);

        TextView txtTanggal = view.findViewById(R.id.txtTanggal);
        TextView txtDokter = view.findViewById(R.id.txtDokter);
        TextView txtKeluhan = view.findViewById(R.id.txtKeluhan);
        TextView txtStatus = view.findViewById(R.id.txtStatus);
        Button btnAksi = view.findViewById(R.id.btnAksi);

        Map<String, String> item = dataList.get(position);

        txtTanggal.setText(item.get("tanggal"));
        txtDokter.setText(item.get("dokter"));
        txtKeluhan.setText(item.get("keluhan"));
        txtStatus.setText(item.get("status"));

        String statusText = item.get("status");
        txtStatus.setText(statusText);

// Sembunyikan tombol jika status sudah "Dibatalkan"
        if ("Dibatalkan".equalsIgnoreCase(statusText)) {
            btnAksi.setVisibility(View.GONE);
        } else {
            btnAksi.setVisibility(View.VISIBLE);
            if ("Selesai".equalsIgnoreCase(statusText)) {
                btnAksi.setText("Detail");
                btnAksi.setEnabled(true);
                btnAksi.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.darker_gray));
            } else {
                btnAksi.setText("Batalkan");
            }
        }



        btnAksi.setOnClickListener(v -> {
            String status = item.get("status");

            if ("Selesai".equalsIgnoreCase(status)) {
                // Buka halaman detail
                Intent intent = new Intent(context, EditJanjiTemuActivity.class);
                intent.putExtra("tanggal", item.get("tanggal"));
                intent.putExtra("dokter", item.get("dokter"));
                intent.putExtra("keluhan", item.get("keluhan"));
                intent.putExtra("status", item.get("status"));
                intent.putExtra("id_rm", item.get("id_rm"));
                intent.putExtra("hasil_diagnosa", item.get("hasil_diagnosa"));
                intent.putExtra("tindakan", item.get("tindakan"));
                intent.putExtra("id_obat", item.get("id_obat"));
                intent.putExtra("mode_readonly", true); // Semua field disable
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            } else {
                // Jalankan pembatalan janji
                String idRm = item.get("id_rm");
                String url = baseUrl + "update_status.php";

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            Toast.makeText(context, "Status berhasil dibatalkan", Toast.LENGTH_SHORT).show();
                            item.put("status", "Dibatalkan");
                            notifyDataSetChanged();
                        },
                        error -> {
                            error.printStackTrace();
                            Toast.makeText(context, "Gagal membatalkan janji temu", Toast.LENGTH_SHORT).show();
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("id_rm", idRm);
                        params.put("status", "Dibatalkan");
                        return params;
                    }
                };

                Volley.newRequestQueue(context).add(postRequest);
            }
        });


        return view;
    }
}
