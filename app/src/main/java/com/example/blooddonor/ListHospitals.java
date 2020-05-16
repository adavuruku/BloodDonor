package com.example.blooddonor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ListHospitals extends AppCompatActivity implements  SearchView.OnQueryTextListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    List<myModels.Hospitals> contentData, newRecord;
    RecyclerView recyclerView;

    String allusers, allbloodbank, userTypeSelect;
    private hospitalAdapter hospitalAdapter;
    dbHelper dbHelper;
    SearchView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_hospitals);


        userTypeSelect = getIntent().getStringExtra("userType");

        String titlePage = userTypeSelect.equals("Hospital")? "List Of All Hospitals":"List Of All Blood Banks";
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle(titlePage);
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
                volleyBankBlood(dbColumnList.serveraddress);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new LoadLocalData().execute();
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
                params.put("type", userTypeSelect);
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

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            new LoadLocalData().execute();
            super.onPostExecute(s);
        }
    }

    public void volleyBankBlood(String url){
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest AllBloodBankRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()>2){
                            allbloodbank = response;
                            new ProcessBloodBank().execute();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opr", "loadbloodbank");
                return params;
            }
        };

        AllBloodBankRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(AllBloodBankRequest, REQUEST_TAG);
    }

    class ProcessBloodBank extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONArray jsonarray = new JSONArray(allbloodbank);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    dbHelper.saveBloodBank(
                            jsonobject.getString("id"), jsonobject.getString("bloodtype"),
                            jsonobject.getString("phone"), jsonobject.getString("qty")
                    );
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            new LoadLocalData().execute();
            super.onPostExecute(s);
        }
    }
    class LoadLocalData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                contentData.clear();
                Cursor allnews = dbHelper.getAllGroup(userTypeSelect);
                if (allnews.getCount() > 0) {
                    while (allnews.moveToNext()) {

                        String userPhone = allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_PHONE));
                        int ap=0,an=0,bp=0,bn=0,abp=0,abn=0,op=0,on=0;

                        String messageBlood = "";
                        Cursor apc = dbHelper.getABloodTypeCount(userPhone, "A+");
                        if(apc.getCount()>0){
                            apc.moveToFirst();
                            ap = Integer.parseInt(apc.getString(apc.getColumnIndex(dbColumnList.hospitalBankBlood.COLUMN_QUANTITY)));
                            messageBlood.concat(ap >0 ? "A+":"");
                        }
                        apc.close();

                        Cursor anc = dbHelper.getABloodTypeCount(userPhone, "A-");
                        if(anc.getCount()>0){
                            anc.moveToFirst();
                            an = Integer.parseInt(anc.getString(anc.getColumnIndex(dbColumnList.hospitalBankBlood.COLUMN_QUANTITY)));
                            messageBlood.concat(an >0 ? "A-":"");
                        }
                        anc.close();

                        Cursor bnc = dbHelper.getABloodTypeCount(userPhone, "B-");
                        if(bnc.getCount()>0){
                            bnc.moveToFirst();
                            bn = Integer.parseInt(bnc.getString(bnc.getColumnIndex(dbColumnList.hospitalBankBlood.COLUMN_QUANTITY)));
                            messageBlood.concat(bn >0 ? "B-":"");
                        }
                        bnc.close();

                        Cursor bpc = dbHelper.getABloodTypeCount(userPhone, "B+");
                        if(bpc.getCount()>0){
                            bpc.moveToFirst();
                            bp = Integer.parseInt(bpc.getString(bpc.getColumnIndex(dbColumnList.hospitalBankBlood.COLUMN_QUANTITY)));
                            messageBlood.concat(bp >0 ? "B+":"");
                        }
                        bpc.close();

                        Cursor abpc = dbHelper.getABloodTypeCount(userPhone, "AB+");
                        if(abpc.getCount()>0){
                            abpc.moveToFirst();
                            abp = Integer.parseInt(abpc.getString(abpc.getColumnIndex(dbColumnList.hospitalBankBlood.COLUMN_QUANTITY)));
                            messageBlood.concat(abp >0 ? "AB+":"");
                        }
                        abpc.close();

                        Cursor abnc = dbHelper.getABloodTypeCount(userPhone, "AB-");
                        if(abnc.getCount()>0){
                            abnc.moveToFirst();
                            abn = Integer.parseInt(abnc.getString(abnc.getColumnIndex(dbColumnList.hospitalBankBlood.COLUMN_QUANTITY)));
                            messageBlood.concat(abn >0 ? "AB-":"");
                        }
                        abnc.close();

                        Cursor onc = dbHelper.getABloodTypeCount(userPhone, "O-");
                        if(onc.getCount()>0){
                            onc.moveToFirst();
                            on = Integer.parseInt(onc.getString(onc.getColumnIndex(dbColumnList.hospitalBankBlood.COLUMN_QUANTITY)));
                            messageBlood.concat(on >0 ? "O-":"");
                        }
                        onc.close();

                        Cursor opc = dbHelper.getABloodTypeCount(userPhone, "O+");
                        if(opc.getCount()>0){
                            opc.moveToFirst();
                            op = Integer.parseInt(opc.getString(opc.getColumnIndex(dbColumnList.hospitalBankBlood.COLUMN_QUANTITY)));
                            messageBlood.concat(op >0 ? "O+":"");
                        }
                        opc.close();
                        myModels.Hospitals noticeList = new myModels().new Hospitals(
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_FULLNAME)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_PHONE)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_EMAIL)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_USERTYPE)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_STATE)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_LGOV)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_CONTACTADD)),
                                ap,an,bp,bn,op,on,abp,abn,messageBlood);

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
                "Type : " + newRecord.get(position).getType() +"\n" +
                "Address : " + newRecord.get(position).getAddress() +"\n\n" +
                "Blood Donor App.\n" +
                "Design By: Omoniyi Foluke Temitope.";
        return message;
    }
    public void loadData(){
        System.out.println(contentData.get(0).getFullname());
        newRecord = contentData;
        hospitalAdapter = new hospitalAdapter( newRecord, getApplicationContext(), new hospitalAdapter.OnItemClickListener() {
            @Override
            public void onCallClick(View v, int position) {
                String phoneNO =  newRecord.get(position).getPhone();
                ActivityCompat.requestPermissions(ListHospitals.this,new String[]{Manifest.permission.CALL_PHONE},24027);
                String phone_id = "tel:"+phoneNO;
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(phone_id));
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    if (ActivityCompat.checkSelfPermission(ListHospitals.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
        hospitalAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(hospitalAdapter);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {


        return true;
    }

    @Override
    public boolean onQueryTextChange(String textData) {
        ArrayList<myModels.Hospitals> newList = new ArrayList<>();
        newRecord = new ArrayList<>();
        String newText = textData.toLowerCase();
        for(myModels.Hospitals cont : contentData){
            String name_ = cont.getState().toLowerCase();
            String depart_ = cont.getLocalGovt().toLowerCase();
            String bloodtype_ = cont.getMessageBlood().toLowerCase();
            if(name_.contains(newText) || depart_.contains(newText)
                    || bloodtype_.contains(newText)){
                newList.add(cont);
            }
        }
        if(!textData.isEmpty()){
            newRecord = newList;
        }else{
            newRecord = contentData;
        }

        hospitalAdapter.setFilter(newList);
        hospitalAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(hospitalAdapter);
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
