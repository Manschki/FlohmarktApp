package com.example.mseifriedsberger16.flohmarktapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
    //int id, int price, int phone, String name, String username, String email, String password, double lat, double lng
    TextView id;
    TextView price;
    TextView phone;
    TextView name;
    TextView username;
    TextView email;
    TextView lat;
    TextView lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        Article a = (Article) bundle.get("article");

        id = findViewById(R.id.id);
        price = findViewById(R.id.price);
        phone = findViewById(R.id.phone);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        lat = findViewById(R.id.lat);
        lng = findViewById(R.id.lng);

        id.setText(a.getId());
        price.setText(a.getPrice());
        phone.setText(a.getPhone());
        name.setText(a.getName());
        username.setText(a.getUsername());
        email.setText(a.getEmail());
        lat.setText(String.valueOf(a.getLat()));
        lng.setText(String.valueOf(a.getLng()));


    }
}
