package com.example.blooddonor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class RegisterHospital extends AppCompatActivity implements AdapterView.OnItemSelectedListener,Validator.ValidationListener {
    String fullname, email, phone, contactAddress, password, confirmPassword, gender,bloodtype, state,type, lgov;
    @NotEmpty
    EditText fullnameE;

    @NotEmpty
    @Email
    EditText emailE;

    @NotEmpty
    @Pattern(regex =  "^[0-9]{11}$")
    EditText phoneE;

    @NotEmpty
    EditText contactAddressE;

    @NotEmpty
    @Password
    @Length(min = 6)
    EditText passwordE;

    @NotEmpty
    @ConfirmPassword
    EditText confirmPasswordE;

    @NotEmpty
    EditText  lgovE;

    Spinner stateE;

    ProgressDialog pd;
    AlertDialog.Builder builder;

    private Validator validator;
    dbHelper dbHelper;
    Button submit;
    TextView login;
    SharedPreferences LoginUserPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_reg);
        type = "Hospital";
        LoginUserPhone = this.getSharedPreferences("LoginUserPhone", this.MODE_PRIVATE);
        createTextSpan();

        intializeComponents();

        validator = new Validator(this);
        validator.setValidationListener(this);


        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.statesList, android.R.layout.simple_list_item_checked);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        stateE.setAdapter(stateAdapter);
        stateE.setOnItemSelectedListener(this);

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

        fullnameE = findViewById(R.id.fullname);
        emailE = findViewById(R.id.email);
        phoneE = findViewById(R.id.phone);
        contactAddressE = findViewById(R.id.contactAddress);
        passwordE = findViewById(R.id.password);
        confirmPasswordE = findViewById(R.id.confirmPassword);
        lgovE = findViewById(R.id.lgov);
        submit = findViewById(R.id.register);
        login = findViewById(R.id.login);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), LoginPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });
    }

    public void retrieveContents(){
        fullname = fullnameE.getText().toString();
        email = emailE.getText().toString();
        phone = phoneE.getText().toString();
        contactAddress = contactAddressE.getText().toString();
        password = passwordE.getText().toString();
        confirmPassword = confirmPasswordE.getText().toString();
        lgov = lgovE.getText().toString();
    }
    public void createTextSpan(){
        TextView login = findViewById(R.id.login);
        String forg = "Already a member? Login";
        ForegroundColorSpan forcolor = new ForegroundColorSpan(Color.RED);
        SpannableString spanP = new SpannableString(forg);
        spanP.setSpan (forcolor,18,forg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(spanP);
    }

    public void volleyJsonArrayRequest(String url){
        if(pd.isShowing()){
            pd.cancel();
            pd.hide();
        }
        pd.show();
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest registerHospitalRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("Account Created Successfully !!!")){
                            displayMessage("Error: Unable To Create Account \n" +
                                    "Please ReTry!!!");
                        }else{
//                            allResult = response;
                            new ReadJSON().execute();
//                            displayMessage(response);
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
                params.put("opr", "registerother");
                params.put("fullname", fullname);
                params.put("email", email);
                params.put("phone", phone);
                params.put("contactAddress", contactAddress);
                params.put("password", password);
                params.put("state", state);
                params.put("lgov", lgov);
                params.put("type", type);
                return params;
            }
        };

        registerHospitalRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(registerHospitalRequest, REQUEST_TAG);
    }


    class ReadJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences.Editor editor;
            dbHelper = new dbHelper(getApplicationContext());

            dbHelper.SaveUserInformation(fullname,phone,email,gender,bloodtype,
                    state,lgov,contactAddress,type
            );
            editor = LoginUserPhone.edit();
            editor.putString("LoginUserPhone",phone);
            editor.apply();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if(pd.isShowing()){
                    pd.cancel();
                    pd.hide();
                }
                Toast.makeText(getApplicationContext(),"Welcome "+ fullname + " To BLOOD DONOR - MOBILE APP",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplication(), HomeScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                displayMessage("Error: No Internet Connection !!!");
            }

            super.onPostExecute(s);
        }
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.gender:
                gender = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + gender, Toast.LENGTH_LONG).show();
                break;
            case R.id.bloodtype:
                bloodtype = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + bloodtype, Toast.LENGTH_LONG).show();
                break;
            case R.id.state:
                state = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + state, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onValidationSucceeded() {
        if(state.isEmpty() || state.equalsIgnoreCase("State") ){
            displayMessage("Either Gender, State or Blood Type Is Not Selected");
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
}
