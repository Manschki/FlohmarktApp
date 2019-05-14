package com.example.mseifriedsberger16.flohmarktapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

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

/**
 * Created by mseifriedsberger16 on 19.03.2019.
 */

public class MyAsyncTask extends AsyncTask<String, Integer, String> {
    private final SharedPreferences prefs;
    //private ListView listView;
    private Context ctx;
    private LeftFragment leFr;
    private List<Article> showArticles = new LinkedList<>();
    private final String URL = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";
    private String operation;


    public MyAsyncTask(Context ctx, LeftFragment leftFragment) {
        this.ctx = ctx;
        this.leFr = leftFragment;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @Override
    protected void onPreExecute() {
        // here we could do some UI manipulation before the worker
        // thread starts
        //listView = findViewById(R.id.listView);
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
                showArticles.clear();
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
                    new MyAsyncTask(ctx, leFr).execute("?operation=get", username1);
                }
                //}

            } catch (JSONException e) {
                e.printStackTrace();
            }

            leFr.setData(showArticles);

        }




        super.onPostExecute(s);
    }
}



