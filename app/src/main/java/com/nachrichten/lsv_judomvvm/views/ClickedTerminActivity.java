package com.nachrichten.lsv_judomvvm.views;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nachrichten.lsv_judomvvm.R;


public class ClickedTerminActivity extends AppCompatActivity {
    private long Datum;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_termin_activity);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView titelTxt = (TextView) findViewById(R.id.termin_adapter_Titel);
        TextView beschreibungTxt = (TextView) findViewById(R.id.Beschreibung_adapter_Textview);
        ImageView Farbanzeige = (ImageView) findViewById(R.id.termin_adapter_Farbe);
        String Titel = "";
        String Beschreibung = "";
        int Farbe = 0;
        if(getIntent().getStringExtra("Termin_Titel") != null){
            Titel = getIntent().getStringExtra("Termin_Titel");
        }
        if(getIntent().getStringExtra("Termin_Beschreibung") != null){
            Beschreibung = getIntent().getStringExtra("Termin_Beschreibung");
        }
        if(getIntent().getIntExtra("Termin_Farbe",0) != 0){
            Farbe = getIntent().getIntExtra("Termin_Farbe",0);
        }
        if(getIntent().getLongExtra("Termin_Datum",0) != 0){
            Datum = getIntent().getLongExtra("Termin_Datum",0);
        }
        titelTxt.setText(Titel);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Color PrimaryRed = Color.valueOf(getResources().getColor(R.color.PrimaryRed));
            Color Orange = Color.valueOf(getResources().getColor(R.color.Orange));
            Shader textShader = new LinearGradient(0, 0, 0, titelTxt.getLineHeight(), new int[]{PrimaryRed.toArgb(),Orange.toArgb(), PrimaryRed.toArgb()},new float[]{0,1,0 },Shader.TileMode.CLAMP);
            titelTxt.getPaint().setShader(textShader);
        }
        beschreibungTxt.setText(Beschreibung);
        Farbanzeige.setBackgroundColor(Farbe);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
