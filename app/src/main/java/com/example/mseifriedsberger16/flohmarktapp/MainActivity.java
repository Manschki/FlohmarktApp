package com.example.mseifriedsberger16.flohmarktapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RQ_PREFERENCES = 10;
    private static final int RQ_ACCESS_FINE_LOCATION = 1;
    //List<Article> articles = new LinkedList<>();
    private ListView listView;
    private ArrayAdapter<Article> adapter;
    private SharedPreferences prefs;
    private boolean isGPSAllowed = false;
    private LocationManager locationManager;
    private Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //new MyAsyncTask(this).execute("?operation=get", "&username=admin");

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RQ_ACCESS_FINE_LOCATION);
        } else {
            gpsGranted();
        }
        registerSystemService();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });
    }

    private void registerSystemService() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // from Api 23 and above you can call getSystemService this way:
        // locationManager = (LocationManager) getSystemService(LocationManager.class);
    }

    private void gpsGranted() {
        Log.d("TAG", "gps permission granted!");
        isGPSAllowed = true;
        //showAvailableProviders();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int viewId = v.getId();
        if (viewId == R.id.listView) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
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
                new MyAsyncTask(this).execute("?operation=delete", id);


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

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                Location location = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);


                float distance = location.distanceTo(l);
                distance = distance/1000;

                TextView t = new TextView(this);
                t.setText("Distanz zum Artikel: " + distance + " km");
                new AlertDialog.Builder(this)
                        .setMessage("Distanz zum Artikel")
                        .setView(t)
                        .setPositiveButton("OK", null)
                        .show();

            }
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("TAG", "onOptionsItemSelected: " + id);
        switch (id) {
            case R.id.menu_get:
                String user = prefs.getString("username", "");
                String username = "&username=" + user;
                new MyAsyncTask(this).execute("?operation=get", username);
                break;
            case R.id.menu_add:
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                EditText name = new EditText(this);
                name.setHint("Name");
                linearLayout.addView(name);
                EditText price = new EditText(this);
                price.setHint("Preis");
                price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                linearLayout.addView(price);
                EditText email = new EditText(this);
                email.setHint("Email");
                email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                linearLayout.addView(email);
                EditText phone = new EditText(this);
                phone.setHint("Telefon");
                phone.setInputType(InputType.TYPE_CLASS_PHONE);
                linearLayout.addView(phone);
                EditText lat = new EditText(this);
                lat.setHint("Latitude");
                lat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                linearLayout.addView(lat);
                EditText lng = new EditText(this);
                lng.setHint("Longitude");
                lng.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                linearLayout.addView(lng);

                new AlertDialog.Builder(this)
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
                                new MyAsyncTask(this).execute("?operation=add",
                                        sName,
                                        sPrice,
                                        sEmail,
                                        sPhone,
                                        sLat,
                                        sLng
                                );


                            } else {
                                new AlertDialog.Builder(this).setTitle("Fehler").setMessage("Mindestens ein Feld ist leer!").show();
                            }
                        })
                        .setNeutralButton("Koordinaten übernehmen", ((dialog, which) -> {
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                new MyAsyncTask(this).execute("?operation=add",
                                        sName,
                                        sPrice,
                                        sEmail,
                                        sPhone,
                                        sLat,
                                        sLng
                                );
                            } else {
                                new AlertDialog.Builder(this).setTitle("Fehler").setMessage("Mindestens ein Feld ist leer!").show();
                            }
                        }))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();


                break;

            case R.id.menu_pref:
                Intent intent = new Intent(this, MySettingsActivity.class);
                startActivityForResult(intent, RQ_PREFERENCES);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * Created by mseifriedsberger16 on 19.03.2019.
     */

    public class MyAsyncTask extends AsyncTask<String, Integer, String> {
        private ListView listView;
        private Context ctx;
        private List<Article> showArticles = new LinkedList<>();
        private final String URL = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";
        private String operation;

        public MyAsyncTask(Context ctx) {

            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            // here we could do some UI manipulation before the worker
            // thread starts
            listView = findViewById(R.id.listView);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // do some UI manipulation while progress is modified
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            //operation, username, name, price, password, email, phone, id

            Log.d("TAG", "entered doInBackground");
            operation = strings[0];


            //Log.d("TAG", "url: "+URL+username+"/repos");
            String sJson = "";
            String finalUrl = "";
            try {


                /*connection.addRequestProperty("operation", operation);
                connection.addRequestProperty("username", username);*/
                switch (operation) {

                    case "?operation=get":
                        String username = strings[1];

                        finalUrl = URL + operation +  username;

                        break;
                    case "?operation=add":
                        // sName,
                        //                                        sPrice,
                        //                                        sEmail,
                        //                                        sPhone
                        finalUrl = URL
                                + strings[0]
                                + "&username=" + prefs.getString("username", " ").trim()
                                + "&password=" + prefs.getString("password", " ").trim()
                                + "&name=" + strings[1]
                                + "&price=" + strings[2]
                                + "&email=" + strings[3]
                                + "&phone=" + strings[4]
                                + "&lat=" + strings[5]
                                + "&lon=" + strings[6];
                        break;
                    case "?operation=delete":
                        finalUrl = URL
                                + strings[0]
                                + "&username=" + prefs.getString("username", " ").trim()
                                + "&password=" + prefs.getString("password", " ").trim()
                                + "&id=" + strings[1];


                }
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(finalUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                //connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    sJson = readResponseStream(reader);
                }
                connection.disconnect();

            } catch (IOException e) {
                Log.d("TAG", e.getLocalizedMessage());
            }
            return sJson;
        }

        private String readResponseStream(BufferedReader reader) throws IOException {
            Log.d("TAG", "entered readResponseStreaulat");
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) { Log.d("TAG", "entered onPostExecute");
            // called after doInBackground finishes
            if(!s.isEmpty()) {
                try {
                    JSONObject jobject = new JSONObject(s);
                    Object data = jobject.get("data");
                    adapter.clear();
                    if(data instanceof JSONArray) {

                        JSONArray array = (JSONArray) data;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);
                            //int id, int price, int phone, String name, String username, String email, String password
                            int id = o.getInt("id");
                            int price = o.getInt("price");
                            int phone = o.getInt("phone");
                            String name = o.getString("name");
                            String username = o.getString("username");
                            String email = o.getString("email");
                            double lat = o.getDouble("lat");
                            double lng = o.getDouble("lon");

                            showArticles.add(new Article(id, price, phone, name, username, email, prefs.getString("password", ""), lat, lng));
                        }
                    }
                    //if (jobject.getInt("code") == 0) {
                        if (!operation.equals("?operation=get")) {
                            String user1 = prefs.getString("username", " ");
                            String username1 = "&username=" + user1;
                            new MyAsyncTask(ctx).execute("?operation=get", username1);
                        }
                    //}

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.addAll(showArticles);

            }




            super.onPostExecute(s);
        }
    }

}






