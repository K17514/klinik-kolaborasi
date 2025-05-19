package com.example.klinik;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BukuAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> bukuList;
    private ArrayList<String> idList;
    private ArrayList<String> imageList;

    // Untuk filter
    private ArrayList<String> originalBukuList;
    private ArrayList<String> originalIdList;
    private ArrayList<String> originalImageList;

    public BukuAdapter(Context context, ArrayList<String> bukuList, ArrayList<String> idList, ArrayList<String> imageList) {
        super(context, R.layout.itemlistbuku, bukuList);
        this.context = context;
        this.bukuList = new ArrayList<>(bukuList);
        this.idList = new ArrayList<>(idList);
        this.imageList = new ArrayList<>(imageList);
        this.originalBukuList = new ArrayList<>(bukuList);
        this.originalIdList = new ArrayList<>(idList);
        this.originalImageList = new ArrayList<>(imageList);
    }

    @Override
    public int getCount() {
        return bukuList.size();
    }

    @Override
    public String getItem(int position) {
        return bukuList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Inflate layout item
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.itemlistbuku, parent, false);
        }

        String buku = bukuList.get(position);
        String id_buku = idList.get(position);
        String imageFileName = imageList.get(position);

        // Set judul buku
        TextView txtBuku = convertView.findViewById(R.id.txtBuku);
        txtBuku.setText(buku);

        // Load image
        ImageView imageView = convertView.findViewById(R.id.imageView3);
        String fullImageUrl = context.getString(R.string.base_url) + "uploads/" + imageFileName;

        Glide.with(context)
                .load(fullImageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_error_image)
                .into(imageView);

        // Edit
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditBukuActivity.class);
            intent.putExtra("bukuData", buku);
            intent.putExtra("id_buku", id_buku);
            intent.putExtra("imageUrl", imageFileName);
            context.startActivity(intent);
        });

        // Delete
        Button btnDelete = convertView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeleteBukuActivity.class);
            intent.putExtra("id_buku", id_buku);
            context.startActivity(intent);
        });

        return convertView;
    }

    // Update list (dipakai saat ambil ulang data)
    public void updateData(ArrayList<String> newBukuList, ArrayList<String> newIdList, ArrayList<String> newImageList) {
        this.bukuList.clear();
        this.bukuList.addAll(newBukuList);
        this.idList.clear();
        this.idList.addAll(newIdList);
        this.imageList.clear();
        this.imageList.addAll(newImageList);

        this.originalBukuList = new ArrayList<>(newBukuList);
        this.originalIdList = new ArrayList<>(newIdList);
        this.originalImageList = new ArrayList<>(newImageList);

        notifyDataSetChanged();
    }

    // Untuk SearchView filter
    @Override
    public Filter getFilter() {
        return new Filter() {

            ArrayList<String> matchedIdList;
            ArrayList<String> matchedImageList;

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<String> matchedTitleList = new ArrayList<>();
                matchedIdList = new ArrayList<>();
                matchedImageList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    matchedTitleList.addAll(originalBukuList);
                    matchedIdList.addAll(originalIdList);
                    matchedImageList.addAll(originalImageList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (int i = 0; i < originalBukuList.size(); i++) {
                        String title = originalBukuList.get(i);
                        if (title.toLowerCase().contains(filterPattern)) {
                            matchedTitleList.add(title);
                            matchedIdList.add(originalIdList.get(i));
                            matchedImageList.add(originalImageList.get(i));
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = matchedTitleList;
                results.count = matchedTitleList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                bukuList.clear();
                idList.clear();
                imageList.clear();

                bukuList.addAll((ArrayList<String>) results.values);
                idList.addAll(matchedIdList);
                imageList.addAll(matchedImageList);

                notifyDataSetChanged();
            }
        };
    }
}
