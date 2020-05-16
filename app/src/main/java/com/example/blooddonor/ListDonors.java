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

public class ListDonors extends AppCompatActivity implements  SearchView.OnQueryTextListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    List<myModels.Donors> contentData, newRecord;
    RecyclerView recyclerView;

    String allusers;
    private donorAdapter donorAdapter;
    dbHelper dbHelper;
    SearchView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_donors);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        dbHelper = new dbHelper(getApplicationContext());
        contentData = new ArrayList<>();
        newRecord = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);


        search = findViewById(R.id.searchData);
        search.setOnQueryTextListener(this);


        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                volleyAllUsers(dbColumnList.serveraddress);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                volleyAllUsers(dbColumnList.serveraddress);
            }
        },1000);

    }


    public void volleyAllUsers(String url){
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest AllUsersRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()>2){
                            allusers = response;
                            new ReadJSON().execute();
                        }else{
                            new LoadLocalData().execute();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new LoadLocalData().execute();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opr", "loaddonorusers");
                return params;
            }
        };

        AllUsersRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(AllUsersRequest, REQUEST_TAG);
    }




    class ReadJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            contentData.clear();
            try {
                JSONArray jsonarray = new JSONArray(allusers);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    dbHelper.SaveUserInformation(
                            jsonobject.getString("fullname"), jsonobject.getString("phone"),
                            jsonobject.getString("email"), jsonobject.getString("gender"),
                            jsonobject.getString("bloodtype"), jsonobject.getString("userstate"),
                            jsonobject.getString("localgovt"), jsonobject.getString("address"),
                            jsonobject.getString("usertype"), jsonobject.getString("active"),
                            jsonobject.getString("dateReg")
                    );

                    myModels.Donors contentList = new myModels().new Donors(
                            jsonobject.getString("fullname"),
                            jsonobject.getString("phone"),
                            jsonobject.getString("email"),
                            jsonobject.getString("gender"),
                            jsonobject.getString("bloodtype"),
                            jsonobject.getString("userstate"),
                            jsonobject.getString("localgovt"),
                            jsonobject.getString("address")
                    );

                    contentData.add(contentList);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            loadData();
            super.onPostExecute(s);
        }
    }


    class LoadLocalData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                contentData.clear();
                Cursor allnews = dbHelper.getAllGroup("Donor");
                if (allnews.getCount() > 0) {
                    while (allnews.moveToNext()) {
                        myModels.Donors noticeList = new myModels().new Donors(
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_FULLNAME)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_PHONE)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_EMAIL)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_GENDER)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_BLOODTYPE)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_STATE)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_LGOV)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_CONTACTADD))
                        );

                        contentData.add(noticeList);
                    }
                }
            } finally { }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            loadData();
            super.onPostExecute(s);
        }
    }

    public String prepMessage(int position){
        String message =  "You can Contact the Information Bello\n" +
                "To Get A Blood Transfusion.\n\n"+
                "Name : " + newRecord.get(position).getFullname() +"\n" +
                "Phone : " + newRecord.get(position).getPhone() +"\n" +
                "Email : " + newRecord.get(position).getEmail() +"\n" +
                "State : " + newRecord.get(position).getState() +"\n" +
                "Local Govt : " + newRecord.get(position).getLocalGovt() +"\n" +
                "Gender : " + newRecord.get(position).getGender() +"\n" +
                "Address : " + newRecord.get(position).getAddress() +"\n\n" +
                "Blood Donor App.\n" +
                "Design By: Omoniyi Foluke Temitope.";
        return message;
    }
    public void loadData(){
        newRecord = contentData;
        donorAdapter = new donorAdapter( newRecord, getApplicationContext(), new donorAdapter.OnItemClickListener() {
            @Override
            public void onCallClick(View v, int position) {
                String phoneNO =  newRecord.get(position).getPhone();
                ActivityCompat.requestPermissions(ListDonors.this,new String[]{Manifest.permission.CALL_PHONE},24027);
                String phone_id = "tel:"+phoneNO;
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(phone_id));
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    if (ActivityCompat.checkSelfPermission(ListDonors.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                }
            }

            @Override
            public void onMessageClick(View v, int position) {
                String message =  prepMessage(position);
                String phoneNO =  newRecord.get(position).getPhone();
                Uri smsUri = Uri.parse("tel:" + phoneNO);
                Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                intent.putExtra("address", phoneNO);
                intent.putExtra("sms_body", message);
                intent.setType("vnd.android-dir/mms-sms");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }

            @Override
            public void onEmailClick(View v, int position) {
                String emailAdd =  newRecord.get(position).getEmail();
                String message =  prepMessage(position);
                Intent it = new Intent(Intent.ACTION_SEND);
                it.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAdd});
                it.putExtra(Intent.EXTRA_SUBJECT,"Blood Donor App.");
                it.putExtra(Intent.EXTRA_TEXT,message);
                it.setType("message/rfc822");
                startActivity(Intent.createChooser(it,"Choose Mail App"));
            }

            @Override
            public void onShareClick(View v, int position) {
                String message =  prepMessage(position);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Blood Donor App.");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,message );
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
            }
        });
        donorAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(donorAdapter);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {


        return true;
    }

    @Override
    public boolean onQueryTextChange(String textData) {
        ArrayList<myModels.Donors> newList = new ArrayList<>();
        newRecord = new ArrayList<>();
        String newText = textData.toLowerCase();
        for(myModels.Donors cont : contentData){
            String name_ = cont.getState().toLowerCase();
            String depart_ = cont.getLocalGovt().toLowerCase();
            String group = cont.getBloodtype().toLowerCase();
            if(name_.contains(newText) || depart_.contains(newText)|| group.contains(newText)){
                newList.add(cont);
            }
        }
        if(!textData.isEmpty()){
            newRecord = newList;
        }else{
            newRecord = contentData;
        }

        donorAdapter.setFilter(newList);
//        contentAdapter.getFilter().filter(textData);
        donorAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(donorAdapter);
        return true;
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
