package com.example.blooddonor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class RegisterUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        TextView login = findViewById(R.id.login);
        String forg = "Already a member? Login";
        ForegroundColorSpan forcolor = new ForegroundColorSpan(Color.RED);
        SpannableString spanP = new SpannableString(forg);
        spanP.setSpan (forcolor,18,forg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(spanP);

        Spinner state = findViewById(R.id.state);
        Spinner blood = findViewById(R.id.bloodtype);
        Spinner gender = findViewById(R.id.gender);

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.statesList, android.R.layout.simple_list_item_checked);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        state.setAdapter(stateAdapter);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.genderList, android.R.layout.simple_list_item_checked);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        gender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> bloodAdapter = ArrayAdapter.createFromResource(this,
                R.array.bloodList, android.R.layout.simple_list_item_checked);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        blood.setAdapter(bloodAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
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
}
