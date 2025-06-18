package com.example.klinik;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JanjiTemuAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> data;
    private LayoutInflater inflater;

    public JanjiTemuAdapter(Context context, List<Map<String, String>> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.itemlistjanjitemu, parent, false);
        }

        TextView txtTanggal = view.findViewById(R.id.txtTanggal);
        TextView txtDokter = view.findViewById(R.id.txtDokter);
        TextView txtKeluhan = view.findViewById(R.id.txtKeluhan);
        TextView txtStatus = view.findViewById(R.id.txtStatus);
        Button btnBatalkan = view.findViewById(R.id.btnAksi);

        Map<String, String> item = data.get(i);

        txtTanggal.setText(item.get("tanggal"));
        txtDokter.setText(item.get("dokter"));
        txtKeluhan.setText(item.get("keluhan"));
        txtStatus.setText(item.get("status"));

        String status = item.get("status");

        if ("Dibatalkan".equalsIgnoreCase(status)) {
            btnBatalkan.setVisibility(View.GONE);
        } else {
            btnBatalkan.setVisibility(View.VISIBLE);
        }

        btnBatalkan.setOnClickListener(v -> {
            Log.d("JanjiTemuAdapter", "Tombol Batalkan diklik");

            String id = item.get("id");
            Log.d("JanjiTemuAdapter", "ID Janji: " + id);

            if (id == null || id.isEmpty()) {
                Toast.makeText(context, "ID kosong!", Toast.LENGTH_SHORT).show();
                Log.e("JanjiTemuAdapter", "ID kosong, tidak bisa kirim ke server");
                return;
            }

            String baseUrl = context.getResources().getString(R.string.base_url);
            String updateUrl = baseUrl + "update_status.php";
            Log.d("JanjiTemuAdapter", "Mengirim POST ke: " + updateUrl);

            StringRequest request = new StringRequest(Request.Method.POST, updateUrl,
                    response -> {
                        Log.d("JanjiTemuAdapter", "Response dari server: " + response);
                        item.put("status", "Dibatalkan");
                        notifyDataSetChanged();
                        Toast.makeText(context, "Janji temu berhasil dibatalkan", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Log.e("JanjiTemuAdapter", "Gagal membatalkan", error);
                        Toast.makeText(context, "Gagal membatalkan janji temu", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", id);
                    params.put("status", "Dibatalkan");
                    Log.d("JanjiTemuAdapter", "Params: " + params.toString());
                    return params;
                }
            };

            Volley.newRequestQueue(context).add(request);
        });

        return view;
    }
}
