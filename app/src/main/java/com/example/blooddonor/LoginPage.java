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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPage extends AppCompatActivity implements Validator.ValidationListener {
    String phone, password,fullname,allResult;

    @NotEmpty
    @Pattern(regex =  "^[0-9]{11}$")
    EditText phoneE;

    @NotEmpty
    @Password
    @Length(min = 6)
    EditText passwordE;

    ProgressDialog pd;
    AlertDialog.Builder builder;

    private Validator validator;
    dbHelper dbHelper;
    Button login;
    TextView register;
    SharedPreferences LoginUserPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        LoginUserPhone = this.getSharedPreferences("LoginUserPhone", this.MODE_PRIVATE);
        createTextSpan();

        intializeComponents();

        validator = new Validator(this);
        validator.setValidationListener(this);

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

        phoneE = findViewById(R.id.phone);
        passwordE = findViewById(R.id.password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), LoginOption.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });
    }

    public void retrieveContents(){
        phone = phoneE.getText().toString();
        password = passwordE.getText().toString();
    }


    public void createTextSpan(){
        TextView forget = findViewById(R.id.forget);
        TextView register = findViewById(R.id.register);
        String forg = "Can't Login? Forget Password";
        String reg = "Don't Have An Account? Register";
        ForegroundColorSpan forcolor = new ForegroundColorSpan(Color.RED);
        SpannableString spanP;
        spanP = new SpannableString(forg);
        spanP.setSpan (forcolor,13,forg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forget.setText(spanP);

        spanP = new SpannableString(reg);
        spanP.setSpan (forcolor,23,reg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        register.setText(spanP);
    }

    public void volleyJsonArrayRequest(String url){
        if(pd.isShowing()){
            pd.cancel();
            pd.hide();
        }
        pd.show();
        String  REQUEST_TAG = "com.volley.volleyJsonArrayRequest";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()<=2){
                            displayMessage("Error: Unable To Login. \nInvalid Mobile No Or Password.\n" +
                                    "Please ReTry!!!");
                        }else{
                            allResult = response;
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
                params.put("opr", "login");
                params.put("phone", phone);
                params.put("password", password);
                return params;
            }
        };

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(loginRequest, REQUEST_TAG);
    }


    class ReadJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences.Editor editor;
            dbHelper = new dbHelper(getApplicationContext());

            try {
                JSONObject jsonobject = new JSONObject(allResult);
                fullname = jsonobject.getString("fullname");
                dbHelper.SaveUserInformation(
                        jsonobject.getString("fullname"), jsonobject.getString("phone"),
                        jsonobject.getString("email"), jsonobject.getString("gender"),
                        jsonobject.getString("bloodtype"), jsonobject.getString("userstate"),
                        jsonobject.getString("localgovt"), jsonobject.getString("address"),
                        jsonobject.getString("usertype"), jsonobject.getString("active"),
                        jsonobject.getString("dateReg")
                );
                editor = LoginUserPhone.edit();
                editor.putString("LoginUserPhone",jsonobject.getString("phone"));
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                dbColumnList.fromlogin = "login";
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
    public void onValidationSucceeded() {
        retrieveContents();
        volleyJsonArrayRequest(dbColumnList.serveraddress);
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
