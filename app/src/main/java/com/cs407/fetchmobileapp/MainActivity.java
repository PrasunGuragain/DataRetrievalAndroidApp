package com.cs407.fetchmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public Map<Integer, List<Integer>> groupByListId(String jsonString) throws JSONException {
        // Display all the items grouped by "listId"
        Map<Integer, List<Integer>> groupedData = new HashMap<>();
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i  < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int listId = (int) jsonObject.get("listId");
            String element = jsonObject.getString("name");

            // Filter out any items where "name" is blank or null
            if (!element.equals("null") && !element.isEmpty()) {
                int itemNum = Integer.parseInt(element.substring(5, element.length()));

                if (!groupedData.containsKey(listId)) {
                    groupedData.put(listId, new ArrayList<>());
                }

                groupedData.get(listId).add(itemNum);
            }
        }

        return groupedData;
    }

    public Map<Integer, List<Integer>> sortByListIDAndName(Map<Integer, List<Integer>> groupedData){
        // Sort the results first by "listId" then by "name" when displaying
        for (Integer listId: groupedData.keySet()) {
            List<Integer> elements = groupedData.get(listId);

            Collections.sort(elements);

            groupedData.put(listId, elements);
        }

        return groupedData;
    }

    public void retrieveData(View view){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                //Background work
                HttpURLConnection urlConnection = null;

                String urlString = "https://fetch-hiring.s3.amazonaws.com/hiring.json";
                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                br.close();

                String jsonString = sb.toString();

                handler.post(() -> {
                    //UI Thread work
                    Map<Integer, List<Integer>> groupedData = null;
                    try {
                        groupedData = groupByListId(jsonString);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Map<Integer, List<Integer>> sortedData = sortByListIDAndName(groupedData);

                    // display to user
                    StringBuilder builder = new StringBuilder();
                    builder.append("\n");
                    builder.append("----------------- LIST -----------------\n");

                    for (Integer listId: sortedData.keySet()) {
                        builder.append("\n");
                        builder.append("----------- LIST ID: " + listId + " -----------\n");
                        List<Integer> elements = sortedData.get(listId);
                        for (int e : elements) {
                            builder.append(e + "\n");
                        }
                    }

                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setMovementMethod(new ScrollingMovementMethod());
                    textView.setText(builder.toString());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
























