package com.example.blooddonor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private SharedPreferences LoginUserPhone;
    String loginPhone,alldonor, allusers, allbloodbank,bloodtype,unitsize;
    dbHelper dbHelper;
    Handler mHandler;
    ProgressDialog pd;
    AlertDialog.Builder builder;
    ArrayAdapter<CharSequence> bloodAdapter;
    DatePickerDialog.OnDateSetListener setListener;
    int year, month, day;
    LinearLayout myprofile,updateprofile,createrequest,about,calculate,updatebank,
    delete, donors,bloodbank,hospital;
    int alldone = 0;
    String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setElevation(0);
        }
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
                        }else{
                            loadStatistic();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadStatistic();
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
                        }else{
                            loadStatistic();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadStatistic();
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
                        }else{
                            loadStatistic();
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
        Cursor cur =dbHelper.getAUser(loginPhone);
        if(cur.getCount()>0){
            cur.moveToFirst();
            userType = cur.getString(cur.getColumnIndex(dbColumnList.usersRecord.COLUMN_USERTYPE));
        }




        myprofile = findViewById(R.id.myprofile);
        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });

        updateprofile = findViewById(R.id.updateprofile);
        updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userType.equals("Donor")){
                    Intent intent = new Intent(getApplication(), updateProfile.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }else{
                    Intent intent = new Intent(getApplication(), updateHospital.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }

            }
        });

        createrequest = findViewById(R.id.createrequest);
        createrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), createRequest.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });


        about = findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), about.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });


        donors = findViewById(R.id.donors);
        donors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ListDonors.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });


        bloodbank = findViewById(R.id.bloodbank);
        bloodbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ListHospitals.class);
                intent.putExtra("userType", "Blood Bank");
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        hospital = findViewById(R.id.hospital);
        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ListHospitals.class);
                intent.putExtra("userType", "Hospital");
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });


        calculate = findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showCalculator();
            }
        });

        updatebank = findViewById(R.id.updatebank);
        updatebank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankUpdate();
            }
        });
        if(userType.equals("Donor")){
            updatebank.setVisibility(View.INVISIBLE);
            updatebank.setVisibility(View.GONE);
        }

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDeleteAccountMsg();
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



    public void showCalculator(){

        View snackView = getLayoutInflater().inflate(R.layout.customcalculatedonation, null);
        final TextView calcdonorresult = snackView.findViewById(R.id.calcdonorresult);
        final Button calculatenow = snackView.findViewById(R.id.calculatenow);

        Calendar calender = Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH);
        day = calender.get(Calendar.DAY_OF_MONTH);

        calculatenow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HomeScreen.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        setListener,year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });


        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year,month,dayOfMonth);
                String DATE_FORMAT= "EEE, d MMM yyyy, HH:mm:ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                String displayDate = dateFormat.format(cal.getTime());
                calculatenow.setText(displayDate);

                cal.add(Calendar.DATE, 56);
                calcdonorresult.setText(dateFormat.format(cal.getTime()));
            }
        };


        final Dialog d = new Dialog(HomeScreen.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setCanceledOnTouchOutside(true);
        d.setContentView(snackView);
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        d.show();
    }

    public void showBankUpdate(){

        View snackView = getLayoutInflater().inflate(R.layout.customupdatebank, null);
        final Spinner bloodtypeE = snackView.findViewById(R.id.bloodtype);
        final EditText unit = snackView.findViewById(R.id.unit);
        final Button register = snackView.findViewById(R.id.register);

        bloodAdapter = ArrayAdapter.createFromResource(this,
                R.array.bloodList, android.R.layout.simple_list_item_checked);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        bloodtypeE.setAdapter(bloodAdapter);
        bloodtypeE.setOnItemSelectedListener(HomeScreen.this);

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"Welcome "+ bloodtype + " To BLOOD DONOR - MOBILE APP",Toast.LENGTH_LONG).show();
                unitsize =unit.getText().toString();
                if(Integer.parseInt(unitsize) > 0 && !bloodtype.equals("Blood Type")){
                    if(pd.isShowing()){
                        pd.cancel();
                        pd.hide();
                    }
                    pd.show();
                    volleyUpdateBankRequest(dbColumnList.serveraddress);
                }
            }
        });




        final Dialog d = new Dialog(HomeScreen.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setCanceledOnTouchOutside(true);
        d.setContentView(snackView);
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        d.show();
    }

    public void volleyUpdateBankRequest(String url){
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest UpdateBankRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if(pd.isShowing()){
                            pd.cancel();
                            pd.hide();
                        }
                        if (response.equals("Updated")){
                            displayMessage("Blood Bank Updated!!");
                        }else{
                            displayMessage("Error: Fail To Update Blood Bank \nPlease Retry!!");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(pd.isShowing()){
                            pd.cancel();
                            pd.hide();
                        }
                        displayMessage("Error: Fail To Update Blood Bank \nPlease Retry!!");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opr", "UpdateBankRequest");
                params.put("phone", loginPhone);
                params.put("unit", unitsize);
                params.put("bloodtype", bloodtype);
                return params;
            }
        };

        UpdateBankRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(UpdateBankRequest, REQUEST_TAG);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        bloodtype = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    public void displayDeleteAccountMsg(){
        String msg="Confirmation: Are You Sure You Want To Delete Your Account? " + System.getProperty("line.separator")+
                "This Process Will Delete Every Of Your Activity On This Platform." + System.getProperty("line.separator")
                + "Please Confirm !";
        builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
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
                volleyDeleteAccountRequest(dbColumnList.serveraddress);
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

    public void volleyDeleteAccountRequest(String url){
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest DeleteAccountRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if(pd.isShowing()){
                            pd.cancel();
                            pd.hide();
                        }
                        if (response.equals("Deleted")){
                            deleteLocalAccount();
                            displayMessage("Account Deleted And All Records Remove!!");
                        }else{
                            displayMessage("Error: Fail To Delete Account \nPlease Retry!!");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(pd.isShowing()){
                            pd.cancel();
                            pd.hide();
                        }
                        displayMessage("Error: Fail To Delete Account \nPlease Retry!!");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opr", "DeleteAccountRequest");
                params.put("phone", loginPhone);
                return params;
            }
        };

        DeleteAccountRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(DeleteAccountRequest, REQUEST_TAG);
    }

    public void deleteLocalAccount(){
        SharedPreferences.Editor editor;
        editor = LoginUserPhone.edit();
        editor.putString("LoginUserPhone", "");
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), LoginOption.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.close:
                deleteLocalAccount();
        }
        return super.onOptionsItemSelected(item);
    }
}
