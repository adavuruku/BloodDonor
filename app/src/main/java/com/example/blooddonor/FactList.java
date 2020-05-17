package com.example.blooddonor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactList extends AppCompatActivity {

    List<myModels.Facts> contentData;
    RecyclerView recyclerView;
    private factsAdapter factsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_list);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }


        contentData = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new LoadLocalData().execute();
            }
        },100);

    }




    class LoadLocalData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                contentData.clear();
                int counter = 0;
                while (counter < dbColumnList.allfacts.length){
                    myModels.Facts noticeList = new myModels().new Facts(
                            dbColumnList.allfacts[counter]
                    );
                    contentData.add(noticeList);
                    counter++;
                }
            } finally { }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            factsAdapter = new factsAdapter(contentData, getApplicationContext(), new factsAdapter.OnItemClickListener() {
            });
            factsAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(factsAdapter);
            super.onPostExecute(s);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
