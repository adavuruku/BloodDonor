package com.example.blooddonor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        TextView forget = findViewById(R.id.forget);
        TextView register = findViewById(R.id.register);
        String forg = "Can't Login? Forget Password";
        String reg = "Don't have an account? Register";
        ForegroundColorSpan forcolor = new ForegroundColorSpan(Color.RED);
        SpannableString spanP;
        spanP = new SpannableString(forg);
        spanP.setSpan (forcolor,13,forg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forget.setText(spanP);

        spanP = new SpannableString(reg);
        spanP.setSpan (forcolor,23,reg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        register.setText(spanP);
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
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
