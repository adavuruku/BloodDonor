package com.example.blooddonor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class createRequest extends AppCompatActivity implements AdapterView.OnItemSelectedListener,Validator.ValidationListener {
    String unit, email, phone, contactAddress, bloodtype, state,type, lgov, emergency;
    String loginPhone;
    @NotEmpty
    EditText unitE;

    @NotEmpty
    EditText contactAddressE;

    @NotEmpty
    EditText  lgovE;

    Spinner bloodtypeE, stateE;
    CheckBox emergencyE;

    ProgressDialog pd;
    AlertDialog.Builder builder;

    private Validator validator;
    dbHelper dbHelper;
    Button submit;
    SharedPreferences LoginUserPhone;
    ArrayAdapter<CharSequence> bloodAdapter,stateAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        type = "Donor";
        LoginUserPhone = this.getSharedPreferences("LoginUserPhone", this.MODE_PRIVATE);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }



        dbHelper = new dbHelper(getApplicationContext());
        LoginUserPhone = this.getSharedPreferences("LoginUserPhone", this.MODE_PRIVATE);
        loginPhone = LoginUserPhone.getString("LoginUserPhone", "");

        stateE = findViewById(R.id.state);
        bloodtypeE = findViewById(R.id.bloodtype);


        validator = new Validator(this);
        validator.setValidationListener(this);
        stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.statesList, android.R.layout.simple_list_item_checked);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        stateE.setAdapter(stateAdapter);
        stateE.setOnItemSelectedListener(this);


        bloodAdapter = ArrayAdapter.createFromResource(this,
                R.array.bloodList, android.R.layout.simple_list_item_checked);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        bloodtypeE.setAdapter(bloodAdapter);
        bloodtypeE.setOnItemSelectedListener(this);

        intializeComponents();

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

    public void intializeComponents(){
        stateE = findViewById(R.id.state);
        bloodtypeE = findViewById(R.id.bloodtype);
        emergencyE = findViewById(R.id.emergency);

        unitE = findViewById(R.id.unit);
        contactAddressE = findViewById(R.id.contactAddress);
        lgovE = findViewById(R.id.lgov);

        submit = findViewById(R.id.register);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
    }

    public void retrieveContents(){
        unit = unitE.getText().toString();
        contactAddress = contactAddressE.getText().toString();
        lgov = lgovE.getText().toString();
        emergency = emergencyE.isChecked()? "0":"1";
    }


    public void volleyJsonArrayRequest(String url){
        if(pd.isShowing()){
            pd.cancel();
            pd.hide();
        }
        pd.show();
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest registerDonorRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("Request Created Successfully !!!")){
                            displayMessage("Error: Unable To Create Request \n" +
                                    "Please ReTry!!!");
                        }else{
                            GoToHomeScreen();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(pd.isShowing()){
                            pd.hide();
                        }
                        displayMessage("Error: No Internet Connection !!!");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opr", "newrequest");
                params.put("unit", unit);
                params.put("phone", loginPhone);
                params.put("contactAddress", contactAddress);
                params.put("bloodtype", bloodtype);
                params.put("state", state);
                params.put("lgov", lgov);
                params.put("emergency", emergency);
                return params;
            }
        };

        registerDonorRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(registerDonorRequest, REQUEST_TAG);
    }


    public void GoToHomeScreen(){
        if(pd.isShowing()){
            pd.cancel();
            pd.hide();
        }
        displayMessage("Request Successfully Created !!");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.bloodtype:
                bloodtype = parent.getItemAtPosition(position).toString();
                break;
            case R.id.state:
                state = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onValidationSucceeded() {
        if(bloodtype.isEmpty() || bloodtype.equalsIgnoreCase("Blood Type") ||
                state.isEmpty() || state.equalsIgnoreCase("State") ){
            displayMessage("Either State or Blood Type Is Not Selected");
        }else{
            retrieveContents();
            volleyJsonArrayRequest(dbColumnList.serveraddress);
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                displayMessage(message);
            }
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
