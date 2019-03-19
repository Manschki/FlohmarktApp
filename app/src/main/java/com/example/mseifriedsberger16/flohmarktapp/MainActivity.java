package com.example.mseifriedsberger16.flohmarktapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MyAsyncTask(this).execute("get", "admin");
    }


    /**
     * Created by mseifriedsberger16 on 19.03.2019.
     */

    public class MyAsyncTask extends AsyncTask<String, Integer, String> {
        private ListView listView;
        private Context ctx;
        private final String URL = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";

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
            String operation = strings[0];
            String username = strings[1];
            //Log.d("TAG", "url: "+URL+username+"/repos");
            String sJson = "";
            try {


                /*connection.addRequestProperty("operation", operation);
                connection.addRequestProperty("username", username);*/
                switch (operation) {

                    case "get":
                        String finalUrl = URL + "?operation=" + operation + "&username=" + username;

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
                        //connection.disconnect();

                        break;
                    /*case "add":
                        connection.addRequestProperty("name", strings[2]);
                        connection.addRequestProperty("price", strings[3]);
                        connection.addRequestProperty("password", strings[4]);
                        connection.addRequestProperty("email", strings[5]);
                        connection.addRequestProperty("phone", strings[6]);
                        break;
                    case "delete":
                        connection.addRequestProperty("id", strings[7]);
                        connection.addRequestProperty("password", strings[4]);
                        break;*/
                }

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
        protected void onPostExecute(String s) {
            Log.d("TAG", "entered onPostExecute");
            // called after doInBackground finishes
            Gson gson = new Gson();

        TypeToken<List<String>> token = new TypeToken<List<String>>(){};

//                JSONArray array = new JSONArray(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray arr = jsonObject.getJSONArray("data");

                ArrayList<String> arrayList = gson.fromJson(arr, token);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringBuilder stringBuilder = new StringBuilder();



            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, arrayList);
            listView = listView.findViewById(R.id.listView);
            //listView.setAdapter(adapter);
            super.onPostExecute(s);
        }
    }

}




