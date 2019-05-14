package com.example.mseifriedsberger16.flohmarktapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity implements OnSelectionChangedListener{

    private static final int RQ_ACCESS_FINE_LOCATION = 1;
    //List<Article> articles = new LinkedList<>();
    //private ListView listView;
    private ArrayAdapter<Article> adapter;
    private SharedPreferences prefs;
    private boolean isGPSAllowed = false;

    private Context ctx = this;
    private RightFragment rightFragment;
    private boolean showRight = false;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
        rightFragment = (RightFragment) getSupportFragmentManager().findFragmentById(R.id.fragRight);
        showRight = rightFragment != null && rightFragment.isInLayout();

        //listView = findViewById(R.id.listView);

        //listView.setAdapter(adapter);

        //registerForContextMenu(listView);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //new MyAsyncTask(this).execute("?operation=get", "&username=admin");


/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Article a = adapter.getItem(pos);
                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                Location l = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                Location loc = new Location(locationManager.GPS_PROVIDER);
                loc.setLatitude(a.getLat());
                loc.setLongitude(a.getLng());

                float distance = l.distanceTo(loc);
                distance = distance/1000;

                Intent i = new Intent(ctx, DetailsActivity.class);
                i.putExtra("article", a);
                i.putExtra("distance", distance);
                startActivity(i);
            }
        });*/
    }


    @Override
    public void onSelectionChanged(int pos, Article item) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        Location l = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        Location loc = new Location(locationManager.GPS_PROVIDER);
        loc.setLatitude(item.getLat());
        loc.setLongitude(item.getLng());

        float distance = l.distanceTo(loc);
        distance = distance/1000;

        if(showRight){
            rightFragment.show(pos, item, distance);
        }
    }
}








