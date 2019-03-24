package com.example.mseifriedsberger16.flohmarktapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
    List<Article> articles = new LinkedList<>();
    private ListView listView;
    private ArrayAdapter<Article> adapter;
    private SharedPreferences prefs;

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
        Log.d("TAG", "onOptionsItemSelected: " + id );
        switch (id) {
            case R.id.menu_get:
                String user = prefs.getString("username", " ");
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

                new AlertDialog.Builder(this)
                        .setTitle("Artikel als " + prefs.getString("username", "") + " hinzufügen")
                        .setView(linearLayout)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            String sEmail = email.getText().toString().trim();
                            String sPhone = phone.getText().toString().trim();
                            String sName = name.getText().toString().trim();
                            String sPrice = price.getText().toString().trim();
                            if (!sEmail.isEmpty() && !sPhone.isEmpty() && !sName.isEmpty() && !sPrice.isEmpty()) {
                                new MyAsyncTask(this).execute("?operation=add",
                                        sName,
                                        sPrice,
                                        sEmail,
                                        sPhone
                                       );


                            } else {
                                new AlertDialog.Builder(this).setTitle("Fehler").setMessage("Mindestens ein Feld ist leer!").show();
                            }
                        })
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
                        finalUrl = URL + strings[0] +  strings[1];
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
                                + "&phone=" + strings[4];
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

                            showArticles.add(new Article(id, price, phone, name, username, email, prefs.getString("password", "")));
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




