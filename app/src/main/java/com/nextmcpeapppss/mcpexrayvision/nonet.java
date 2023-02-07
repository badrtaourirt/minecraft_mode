package com.nextmcpeapppss.mcpexrayvision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


public class nonet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonet);
    }
    public void tryagianbtn(){
        Intent i = new Intent(nonet.this, SplashActivity.class);
        startActivity(i);
    }
}