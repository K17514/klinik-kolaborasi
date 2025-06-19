package com.example.klinik;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BerandaFragment extends Fragment {

    private ImageView iconDoor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda_admin, container, false);

        // Use the door icon as logout button
        iconDoor = view.findViewById(R.id.icon_door);
        iconDoor.setOnClickListener(v -> logoutUser());

        TextView tvGreeting = view.findViewById(R.id.tv_greeting);

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String nama = prefs.getString("nama", "Pengguna");

        tvGreeting.setText("Hi, " + nama + "!");

        return view;
    }

    private void logoutUser() {
        // Clear session
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to Splash or Login
        Intent intent = new Intent(getActivity(), SplashActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
