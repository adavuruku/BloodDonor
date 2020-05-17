package com.example.blooddonor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class RequestWindow extends AppCompatActivity implements  SearchView.OnQueryTextListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    List<myModels.Request> contentData, newRecord;
    RecyclerView recyclerView;
    Boolean myRequest;
    private SharedPreferences LoginUserPhone;
    String loginPhone;
    String allusers, alldonors, userTypeSelect;
    private requestAdapter requestAdapter;
    dbHelper dbHelper;
    SearchView search;
    ProgressDialog pd;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_window);


        userTypeSelect = getIntent().getStringExtra("userType");
        String titlePage = userTypeSelect.equals("myRequest")? "List Of All Your Active Blood Request":userTypeSelect.equals("emergency")? "List Of All Active Emergency Blood Request":"List Of All Active Blood Request";

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle(titlePage);
        }

        LoginUserPhone = this.getSharedPreferences("LoginUserPhone", this.MODE_PRIVATE);
        loginPhone = LoginUserPhone.getString("LoginUserPhone", "");

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
                volleyAllRequest(dbColumnList.serveraddress);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new LoadLocalData().execute();
            }
        },1000);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing Request ...");
        pd.setTitle(R.string.app_name);
        pd.setIcon(R.mipmap.ic_launcher);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
    }

    public void displayMessage(String msg) {
        if(pd.isShowing()){
            pd.hide();
            pd.cancel();
        }
        builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alert.show();
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
                params.put("opr", "loadusers");
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

    public void volleyAllRequest(String url){
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest AllDonoRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()>2){
                            alldonors = response;
                            new ProcessDonors().execute();
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
                params.put("opr", "loadrequest");
                return params;
            }
        };

        AllDonoRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(AllDonoRequest, REQUEST_TAG);
    }

    class  ProcessDonors extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONArray jsonarray = new JSONArray(alldonors);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    dbHelper.saveRequestInformation(
                            jsonobject.getString("bloodtype"), jsonobject.getString("id"),
                            jsonobject.getString("state"), jsonobject.getString("localgovt"),
                            jsonobject.getString("phone"), jsonobject.getString("unit"),
                            jsonobject.getString("requesttype"), jsonobject.getString("address"),
                            jsonobject.getString("recorddate"), jsonobject.getString("requeststatus")
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
                Cursor allnews=null;
                myRequest = false;
                if(userTypeSelect.equals("myRequest")){
                    allnews = dbHelper.getAllMyRequest(loginPhone);
                    myRequest = true;
                }else if(userTypeSelect.equals("emergency")){
                    allnews = dbHelper.getAllEmergencyRequest();
                }else{
                    allnews = dbHelper.getAllRequest();
                }
                if (allnews.getCount() > 0) {
                    while (allnews.moveToNext()) {
//                        String requestAuthor, String dateReg, String state,
//                                String email, String localGovt, String address,
//                                String type,Boolean mytype,String phone, String bloodtype

                        String userPhone = allnews.getString(allnews.getColumnIndex(dbColumnList.usersRecord.COLUMN_PHONE));
                        String requestAuthor=null, emailAdd =null;
                        Cursor reqAuthor = dbHelper.getAUser(userPhone);
                        if(reqAuthor.getCount()>0){
                            reqAuthor.moveToFirst();
                            requestAuthor = reqAuthor.getString(reqAuthor.getColumnIndex(dbColumnList.usersRecord.COLUMN_FULLNAME));
                            emailAdd = reqAuthor.getString(reqAuthor.getColumnIndex(dbColumnList.usersRecord.COLUMN_EMAIL));
                        }
                        reqAuthor.close();

                        myModels.Request noticeList = new myModels().new Request(
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRequest.COLUMN_REQUESTID)),
                                requestAuthor,
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRequest.COLUMN_DATE)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRequest.COLUMN_STATE)),
                                emailAdd,
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRequest.COLUMN_LOCALGOV)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRequest.COLUMN_CONTACTADD)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRequest.COLUMN_REQUESTTYPE)),
                                myRequest,
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRequest.COLUMN_PHONE)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRequest.COLUMN_BLOODTYPE)),
                                allnews.getString(allnews.getColumnIndex(dbColumnList.usersRequest.COLUMN_UNIT)));

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
                "Requested By : " + newRecord.get(position).getRequestAuthor() +"\n" +
                "Phone : " + newRecord.get(position).getPhone() +"\n" +
                "Email : " + newRecord.get(position).getEmail() +"\n" +
                "State : " + newRecord.get(position).getState() +"\n" +
                "Local Govt : " + newRecord.get(position).getLocalGovt() +"\n" +
                "Address : " + newRecord.get(position).getAddress() +"\n\n" +
                "Blood Donor App.\n" +
                "Design By: Omoniyi Foluke Temitope.";
        return message;
    }
    public void loadData(){
        newRecord = contentData;
        requestAdapter = new requestAdapter( newRecord, getApplicationContext(), new requestAdapter.OnItemClickListener() {
            @Override
            public void onCallClick(View v, int position) {
                String phoneNO =  newRecord.get(position).getPhone();
                ActivityCompat.requestPermissions(RequestWindow.this,new String[]{Manifest.permission.CALL_PHONE},24027);
                String phone_id = "tel:"+phoneNO;
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(phone_id));
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    if (ActivityCompat.checkSelfPermission(RequestWindow.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

            @Override
            public void onCancelClick(View v, int position) {
                String requestid =  newRecord.get(position).getRequestID();
                String msg="Once A Blood Request Is Closed No One On This Platform Can See / Attend To The Request."+System.getProperty("line.separator")+
                        "This Process Will Close The Blood Request From The PlatForm." + System.getProperty("line.separator") +System.getProperty("line.separator") +
                        "Confirmation: Are You Sure You Want To Close The Blood Request ? " + System.getProperty("line.separator")
                        + "Please Confirm !";
                displayDeleteRequestMsg("Close",requestid,msg);
            }

            @Override
            public void onDeleteClick(View v, int position) {
                String requestid =  newRecord.get(position).getRequestID();
                String msg="This Process Will Delete This Blood Request From The PlatForm." + System.getProperty("line.separator")+System.getProperty("line.separator")+
                "Confirmation: Are You Sure You Want To Delete The Blood Request ? " + System.getProperty("line.separator")
                        + "Please Confirm !";
                displayDeleteRequestMsg("Delete",requestid,msg);
            }
        });
        requestAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(requestAdapter);
    }

    public void displayDeleteRequestMsg(final String operation, final String requestid,
                                        final String message){

        builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(pd.isShowing()){
                    pd.cancel();
                    pd.hide();
                }
                pd.show();
                volleyDeleteRequest(dbColumnList.serveraddress,operation, requestid);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alert.show();
    }

    public void volleyDeleteRequest(String url, final String operation, final String requestid){
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest AllDonoRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Deleted")){
                            dbHelper.DeleteUserRequest(requestid);
                            displayMessage("Blood Request Successfully Deleted !!!");
                            new LoadLocalData().execute();
                        }else if (response.equals("Closed")){
                            dbHelper.CancelledUserRequest(requestid);
                            displayMessage("Blood Request Successfully Closed !!!");
                            new LoadLocalData().execute();
                        }else{
                            displayMessage(operation.equals("Delete")? "Fail To Delete Blood Request\n Please Retry!!!" :
                                    "Fail To Close Blood Request\n Please Retry!!!");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(operation.equals("Delete")){
                            displayMessage("Fail To Delete Blood Request\n Please Retry!!!");
                        }else{
                            displayMessage("Fail To Close Blood Request\n Please Retry!!!");
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opr", operation);
                params.put("requestid", requestid);
                return params;
            }
        };

        AllDonoRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(AllDonoRequest, REQUEST_TAG);
    }




    @Override
    public boolean onQueryTextSubmit(String query) {


        return true;
    }

    @Override
    public boolean onQueryTextChange(String textData) {
        ArrayList<myModels.Request> newList = new ArrayList<>();
        newRecord = new ArrayList<>();
        String newText = textData.toLowerCase();
        for(myModels.Request cont : contentData){
            String name_ = cont.getState().toLowerCase();
            String depart_ = cont.getLocalGovt().toLowerCase();
            String bloodtype_ = cont.getBloodtype().toLowerCase();
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

        requestAdapter.setFilter(newList);
        requestAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(requestAdapter);
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
