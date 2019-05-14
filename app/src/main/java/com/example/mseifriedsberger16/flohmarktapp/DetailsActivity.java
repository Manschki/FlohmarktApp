package com.example.mseifriedsberger16.flohmarktapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
    private static final int RQ_CALL_PHONE = 1;
    //int id, int price, int phone, String name, String username, String email, String password, double lat, double lng
    TextView id;
    TextView price;
    TextView phone;
    TextView name;
    TextView username;
    TextView email;
    TextView lat;
    TextView lng;
    TextView dis;

    Article a;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        int orientation = getResources().getConfiguration().orientation;
        if(orientation != Configuration.ORIENTATION_PORTRAIT){
            return;
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        a = (Article) bundle.get("article");
        float distance = (float) bundle.get("distance");

        id = findViewById(R.id.id);
        price = findViewById(R.id.price);
        phone = findViewById(R.id.phone);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        lat = findViewById(R.id.lat);
        lng = findViewById(R.id.lng);
        dis = findViewById(R.id.distance);

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


/*    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != RQ_CALL_PHONE) return;
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

        } else {
            dial();
        }
    }*/

/*    private void dial() {
        String phone = "tel:" + a.getPhone();
        Uri uri = Uri.parse(phone);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        }
        startActivity(intent);
    }

    public void call(View view) {
        String perm = Manifest.permission.CALL_PHONE;
        if (ActivityCompat.checkSelfPermission(this, perm) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
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
    }*/
}
