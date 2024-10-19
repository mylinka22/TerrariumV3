package com.sample.terrarium.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sample.terrarium.R;
import com.sample.terrarium.network.NetworkCallback;
import com.sample.terrarium.network.NetworkUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;

public class FragmentFeeder extends Fragment {

    private String savedIP;
    private Vibrator vibrator;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feeder, container, false);

        Button buttonR1 = rootView.findViewById(R.id.buttonR1);
        Button buttonR2 = rootView.findViewById(R.id.buttonR2);
        Button buttonR3 = rootView.findViewById(R.id.buttonR3);
        Button buttonR4 = rootView.findViewById(R.id.buttonR4);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        savedIP = sharedPreferences.getString("savedIP", "");

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        setupButtonClickListener(buttonR1, "servo0");
        setupButtonClickListener(buttonR2, "servo2500");
        setupButtonClickListener(buttonR3, "servo4700");
        setupButtonClickListener(buttonR4, "servo7500");

        return rootView;
    }


    private void setupButtonClickListener(Button button, String endpoint) {
        button.setOnClickListener(v -> {
            vibrate();
            NetworkUtils networkUtils = new NetworkUtils(new OkHttpClient(), savedIP);
            respons(networkUtils, endpoint);
        });
    }


    private void respons(NetworkUtils networkUtils, String post){

        networkUtils.post(post, new NetworkCallback() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> System.out.println(response));
                }
            }

            @Override
            public void onFailure(IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });

    }


    private void vibrate() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}