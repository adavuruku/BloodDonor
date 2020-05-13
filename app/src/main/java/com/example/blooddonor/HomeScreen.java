package com.example.blooddonor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeScreen extends AppCompatActivity {
    private SharedPreferences LoginUserPhone;
    String loginPhone,alldonor, allusers, allbloodbank;
    dbHelper dbHelper;
    Handler mHandler;
    ProgressDialog pd;
    AlertDialog.Builder builder;
    LinearLayout myprofile;
    int alldone = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        dbHelper = new dbHelper(getApplicationContext());
        LoginUserPhone = this.getSharedPreferences("LoginUserPhone", this.MODE_PRIVATE);
        loginPhone = LoginUserPhone.getString("LoginUserPhone", "");

        mHandler = new Handler();

        initComponents();
        /* TODO
        *   1.create a cron job that loads all data from the three table
        * 2.cron job to update UI
        *
        * */
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing Request ...");
        pd.setTitle(R.string.app_name);
        pd.setIcon(R.mipmap.ic_launcher);
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        if(dbColumnList.fromlogin.equals("login")){
            pd.setMessage("Preparing Your Dashboard ...");
            if(pd.isShowing()){
                pd.cancel();
                pd.hide();
            }
            pd.show();
            makingAllRequest();
        }else{
            mStatusChecker.run();
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                makingAllRequest();
            } finally {
                mHandler.postDelayed(mStatusChecker, 60000);
            }
        }
    };

    protected void loadStatistic() {
        TextView nodonor, nohospital, nobank, norequest;
        nodonor = findViewById(R.id.nodonor);
        nohospital = findViewById(R.id.nohospital);
        nobank = findViewById(R.id.nobank);
        norequest = findViewById(R.id.norequest);

        Cursor count = dbHelper.getAllGroup("Donor");
        nodonor.setText(Integer.toString( count.getCount()));
        count.close();

        count = dbHelper.getAllGroup("Hospital");
        nohospital.setText(Integer.toString( count.getCount()));
        count.close();

        count = dbHelper.getAllGroup("Blood Bank");
        nobank.setText(Integer.toString(count.getCount()));
        count.close();

        count = dbHelper.getAllRequest();
        norequest.setText(Integer.toString( count.getCount()));
        count.close();
    }
    public void makingAllRequest(){
        volleyAllRequest(dbColumnList.serveraddress);
        volleyAllUsers(dbColumnList.serveraddress);
        volleyBankBlood(dbColumnList.serveraddress);
    }

    public void volleyAllRequest(String url){
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest AllDonoRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()>2){
                            alldonor = response;
                            new ProcessDonors().execute();
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


    public void volleyAllUsers(String url){
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest AllUsersRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()>2){
                            allusers = response;
                            new ProcessUsers().execute();
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



//AFTER DB OPERATION
class  ProcessDonors extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONArray jsonarray = new JSONArray(alldonor);
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
        if(dbColumnList.fromlogin.equals("login")){
            alldone+=1;
            if(alldone==3){
                if(pd.isShowing()){
                    pd.cancel();
                    pd.hide();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mStatusChecker.run();
                    }
                },60000);
            }
        }
        loadStatistic();
        super.onPostExecute(s);
    }
}

    class ProcessUsers extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
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
            if(dbColumnList.fromlogin.equals("login")){
                alldone+=1;
                if(alldone==3){
                    if(pd.isShowing()){
                        pd.cancel();
                        pd.hide();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStatusChecker.run();
                        }
                    },60000);
                }
            }
            loadStatistic();
            super.onPostExecute(s);
        }
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
            if(dbColumnList.fromlogin.equals("login")){
                alldone+=1;
                if(alldone==3){
                    if(pd.isShowing()){
                        pd.cancel();
                        pd.hide();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStatusChecker.run();
                        }
                    },60000);
                }
            }
            loadStatistic();
            super.onPostExecute(s);
        }
    }



//all menu click
    public void initComponents(){
        myprofile = findViewById(R.id.myprofile);
        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });
    }

    public void showProfile(){
        View snackView = getLayoutInflater().inflate(R.layout.customprofile, null);

        TextView txtUser = snackView.findViewById(R.id.txtUser);
        TextView pstate = snackView.findViewById(R.id.pstate);
        TextView plocalgov = snackView.findViewById(R.id.plocalgov);
        TextView pphone = snackView.findViewById(R.id.pphone);
        TextView pbloodtype = snackView.findViewById(R.id.pbloodtype);
        TextView pdatereg = snackView.findViewById(R.id.pdatereg);
        TextView prequest = snackView.findViewById(R.id.prequest);
        TextView pemail = snackView.findViewById(R.id.pemail);
        TextView paddress = snackView.findViewById(R.id.paddress);


        Cursor cur =dbHelper.getAUser(loginPhone);
        if(cur.getCount()>0){
            cur.moveToFirst();
            txtUser.setText(
                    cur.getString(cur.getColumnIndex(dbColumnList.usersRecord.COLUMN_FULLNAME))
            );
            pstate.setText(
                    cur.getString(cur.getColumnIndex(dbColumnList.usersRecord.COLUMN_STATE)) + " State."
            );
            plocalgov.setText(
                    cur.getString(cur.getColumnIndex(dbColumnList.usersRecord.COLUMN_LGOV)) + " Local Government."
            );
            pphone.setText(
                    cur.getString(cur.getColumnIndex(dbColumnList.usersRecord.COLUMN_PHONE))
            );
            pemail.setText(
                    cur.getString(cur.getColumnIndex(dbColumnList.usersRecord.COLUMN_EMAIL))
            );
            paddress.setText(
                    cur.getString(cur.getColumnIndex(dbColumnList.usersRecord.COLUMN_CONTACTADD))
            );
            pbloodtype.setText(
                    "Blood Type : "+cur.getString(cur.getColumnIndex(dbColumnList.usersRecord.COLUMN_BLOODTYPE))
            );
            pdatereg.setText(
                    cur.getString(cur.getColumnIndex(dbColumnList.usersRecord.COLUMN_DATEREG))
            );
        }
        cur.close();

        cur =dbHelper.getAUserRequest(loginPhone);
        prequest.setText(cur.getCount() + " Total Blood Request ");
        cur.close();

        final Dialog d = new Dialog(HomeScreen.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setCanceledOnTouchOutside(true);
        d.setContentView(snackView);
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        d.show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.close:
                SharedPreferences.Editor editor;
                editor = LoginUserPhone.edit();
                editor.putString("LoginUserPhone", "");
                editor.apply();
                intent = new Intent(getApplicationContext(), LoginOption.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
