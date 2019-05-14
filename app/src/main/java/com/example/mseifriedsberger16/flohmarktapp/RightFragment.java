package com.example.mseifriedsberger16.flohmarktapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RightFragment extends Fragment {
    public final static String TAG = RightFragment.class.getSimpleName();
    private static final int RQ_CALL_PHONE = 100;
    TextView id;
    TextView price;
    TextView phone;
    TextView name;
    TextView username;
    TextView email;
    TextView lat;
    TextView lng;
    TextView dis;

    private SharedPreferences prefs;
    private Article a;
    private Context ctx;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.ctx = getActivity();
        Log.d(TAG, "onCreateView: entered");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_right, container, false);
        intializeViews(view);
        return view;
    }
    private void intializeViews(View view) {
        Log.d(TAG, "intializeViews: entered");
        this.view = view;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        Button call = view.findViewById(R.id.call);
        call.setOnClickListener(v -> call(v));

        Button mail = view.findViewById(R.id.sendEmail);
        mail.setOnClickListener(v -> sendMail(v));

        Button map = view.findViewById(R.id.map);
        map.setOnClickListener(v -> map(v));

    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: entered");
    }

    public void show(int pos, Article item, float distance) {
        Log.d(TAG, "show: entered");


        a = item;

        id = view.findViewById(R.id.id);
        price = view.findViewById(R.id.price);
        phone = view.findViewById(R.id.phone);
        name = view.findViewById(R.id.name);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        lat = view.findViewById(R.id.lat);
        lng = view.findViewById(R.id.lng);
        dis = view.findViewById(R.id.distance);

        id.setText(String.valueOf(a.getId()));
        price.setText(String.valueOf(a.getPrice()));
        phone.setText(String.valueOf(a.getPhone()));
        name.setText(a.getName());
        username.setText(a.getUsername());
        email.setText(a.getEmail());
        lat.setText(String.valueOf(a.getLat()));
        lng.setText(String.valueOf(a.getLng()));
        dis.setText(String.valueOf(distance) + "km");
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != RQ_CALL_PHONE) return;
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

        } else {
            dial();
        }
    }

    private void dial() {
        String phone = "tel:" + a.getPhone();
        Uri uri = Uri.parse(phone);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        }
        startActivity(intent);
    }

    public void call(View view) {
        String perm = Manifest.permission.CALL_PHONE;
        if (ActivityCompat.checkSelfPermission(ctx, perm) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{perm}, RQ_CALL_PHONE);
        } else {
            dial();
        }
    }

    public void sendMail(View view) {
        String username = prefs.getString("username", "");

        String mail = "mailto:" + a.getEmail();
        Uri uri = Uri.parse(mail);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, username + " hat Interesse an: " + a.getName());
        intent.putExtra(Intent.EXTRA_TEXT, username + " hat Interesse an: " + a.getName());

        startActivity(intent);
    }

    public void map(View view) {
        String pos = "geo:" + a.getLat() + "," + a.getLng() + "?q=(" + a.getName()+ ")@" + a.getLat()+ "," + a.getLng() + ",?z=12";
        Uri uri = Uri.parse(pos);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);


        startActivity(intent);
    }
}