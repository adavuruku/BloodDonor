package com.example.blooddonor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class HospitalReg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_reg);

        TextView login = findViewById(R.id.login);
        String forg = "Already a member? Login";
        ForegroundColorSpan forcolor = new ForegroundColorSpan(Color.RED);
        SpannableString spanP = new SpannableString(forg);
        spanP.setSpan (forcolor,18,forg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(spanP);
    }
}
