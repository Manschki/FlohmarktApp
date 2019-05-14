package com.example.mseifriedsberger16.flohmarktapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;


public class LeftFragment extends Fragment {
    private static final int RQ_PREFERENCES = 10;
    private static final int RQ_ACCESS_FINE_LOCATION = 1;

    private static final String TAG = LeftFragment.class.getSimpleName();
    private ListView listView;
    private Context ctx;
    private ArrayAdapter<Article> adapter;
    Article selected;
    private OnSelectionChangedListener listener;
    private SharedPreferences Fprefs;
    private View view;
    private LocationManager locationManager;
    private boolean isGPSAllowed = false;
    private SharedPreferences prefs;


    @Override
    public void onAttach(Context context) {

        Log.d(TAG, "onAttach: entered");
        super.onAttach(context);
        ctx = context;

        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        getData();
        if (ctx instanceof OnSelectionChangedListener) {
            listener = (OnSelectionChangedListener) ctx;
        } else {
            Log.d(TAG, "onAttach: Activity does not implement OnSelectionChangedListener");
        }

        if (ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RQ_ACCESS_FINE_LOCATION);
        } else {
            gpsGranted();
        }
        registerSystemService();


    }


    private void registerSystemService() {
        locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
        // from Api 23 and above you can call getSystemService this way:
        // locationManager = (LocationManager) getSystemService(LocationManager.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: entered");
        // Inflate the layout for ctx fragment
        View view = inflater.inflate(R.layout.fragment_left, container, false);
        setHasOptionsMenu(true);
        initializeViews(view);
        adapter = new ArrayAdapter<>(ctx,android.R.layout.simple_list_item_1);
        return view;
    }

    private void initializeViews(View view) {
        Log.d(TAG, "initializeViews: entered");
        this.view = view;
        listView = view.findViewById(R.id.listView);
        registerForContextMenu(listView);
        listView.setOnItemClickListener
                ((parent, view1, position, id) -> itemSelected(position));
    }

    private void itemSelected(int position) {
        selected = adapter.getItem(position);
        listener.onSelectionChanged(position, selected);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: entered");
        super.onStart();


        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);

        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("TAG", "onOptionsItemSelected: " + id);
        switch (id) {
            case R.id.menu_get:
                getData();
                break;
            case R.id.menu_add:
                LinearLayout linearLayout = new LinearLayout(ctx);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                EditText name = new EditText(ctx);
                name.setHint("Name");
                linearLayout.addView(name);
                EditText price = new EditText(ctx);
                price.setHint("Preis");
                price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                linearLayout.addView(price);
                EditText email = new EditText(ctx);
                email.setHint("Email");
                email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                linearLayout.addView(email);
                EditText phone = new EditText(ctx);
                phone.setHint("Telefon");
                phone.setInputType(InputType.TYPE_CLASS_PHONE);
                linearLayout.addView(phone);
                EditText lat = new EditText(ctx);
                lat.setHint("Latitude");
                lat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                linearLayout.addView(lat);
                EditText lng = new EditText(ctx);
                lng.setHint("Longitude");
                lng.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                linearLayout.addView(lng);

                new AlertDialog.Builder(ctx)
                        .setTitle("Artikel als " + prefs.getString("username", "") + " hinzufügen")
                        .setView(linearLayout)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            String sEmail = email.getText().toString();
                            String sPhone = phone.getText().toString();
                            String sName = name.getText().toString();
                            String sPrice = price.getText().toString();
                            String sLat = lat.getText().toString();
                            String sLng = lng.getText().toString();

                            if (!sEmail.isEmpty() && !sPhone.isEmpty() && !sName.isEmpty() && !sPrice.isEmpty()) {
                                new MyAsyncTask(ctx, this).execute("?operation=add",
                                        sName,
                                        sPrice,
                                        sEmail,
                                        sPhone,
                                        sLat,
                                        sLng
                                );


                            } else {
                                new AlertDialog.Builder(ctx).setTitle("Fehler").setMessage("Mindestens ein Feld ist leer!").show();
                            }
                        })
                        .setNeutralButton("Koordinaten übernehmen", ((dialog, which) -> {
                            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            }
                            Location location = locationManager.getLastKnownLocation(
                                    LocationManager.GPS_PROVIDER);

                            String sEmail = email.getText().toString();
                            String sPhone = phone.getText().toString();
                            String sName = name.getText().toString();
                            String sPrice = price.getText().toString();
                            String sLat = String.valueOf(location.getLatitude());
                            String sLng = String.valueOf(location.getLongitude());

                            if (!sEmail.isEmpty() && !sPhone.isEmpty() && !sName.isEmpty() && !sPrice.isEmpty()) {
                                new MyAsyncTask(ctx, this).execute("?operation=add",
                                        sName,
                                        sPrice,
                                        sEmail,
                                        sPhone,
                                        sLat,
                                        sLng
                                );
                            } else {
                                new AlertDialog.Builder(ctx).setTitle("Fehler").setMessage("Mindestens ein Feld ist leer!").show();
                            }
                        }))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();


                break;

            case R.id.menu_pref:
                Intent intent = new Intent(ctx, MySettingsActivity.class);
                startActivityForResult(intent, RQ_PREFERENCES);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == RQ_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //user does not allow
            } else {
                gpsGranted();
            }
        }
    }
    private void gpsGranted() {
        Log.d("TAG", "gps permission granted!");
        isGPSAllowed = true;
        //showAvailableProviders();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Article a;
            if (info != null) {
                int pos = info.position;
                a = adapter.getItem(pos);
                String id = String.valueOf(a.getId());
                new MyAsyncTask(ctx, this).execute("?operation=delete", id);


            }

            return true;
        } else if (item.getItemId() == R.id.menu_distance) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Article a;
            if (info != null) {
                int pos = info.position;
                a = adapter.getItem(pos);
                Location l = new Location(locationManager.GPS_PROVIDER);
                l.setLatitude(a.getLat());
                l.setLongitude(a.getLng());

                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                Location location = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);


                float distance = location.distanceTo(l);
                distance = distance/1000;

                TextView t = new TextView(ctx);
                t.setText("Distanz zum Artikel: " + distance + " km");
                new AlertDialog.Builder(ctx)
                        .setMessage("Distanz zum Artikel")
                        .setView(t)
                        .setPositiveButton("OK", null)
                        .show();

            }
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }



    public void getData(){
        String user = prefs.getString("username", "");
        String username = "&username=" + user;
        new MyAsyncTask(ctx, this).execute("?operation=get", username);

    }

    public void setData(List<Article> showArticles) {
        adapter.clear();
        adapter.addAll(showArticles);
    }
}
