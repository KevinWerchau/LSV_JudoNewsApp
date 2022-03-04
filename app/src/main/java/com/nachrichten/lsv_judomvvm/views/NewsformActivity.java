package com.nachrichten.lsv_judomvvm.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nachrichten.lsv_judomvvm.R;
import com.nachrichten.lsv_judomvvm.models.CustomInputFilter;
import com.nachrichten.lsv_judomvvm.models.LoginActivity_TextWatcher;
import com.nachrichten.lsv_judomvvm.models.News;
import com.nachrichten.lsv_judomvvm.viewmodels.NewsformActivityViewModel;

public class NewsformActivity extends AppCompatActivity {

    private NewsformActivityViewModel viewModel;

    private EditText Titel,Beschreibung;
    private Button bt;


    @Override
    protected void onStart() {
        viewModel = new ViewModelProvider(this).get(NewsformActivityViewModel.class);
        viewModel.init();
        this.GUIZuweisen();
        this.observeInputs();
        this.btOnClick();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsform);
    }

    private void GUIZuweisen() {
        Titel = findViewById(R.id.editText_Titel);
        Beschreibung = findViewById(R.id.editText_Beschreibung);
        bt = findViewById(R.id.Speicherung_News);
        bt.setEnabled(false);
        Titel.setFilters(new CustomInputFilter[]{new CustomInputFilter(true,true,true,true,viewModel)});
        Beschreibung.setFilters(new CustomInputFilter[]{new CustomInputFilter(true,true,true,true,viewModel)});
        Titel.addTextChangedListener(new LoginActivity_TextWatcher(Titel,Beschreibung,bt));
        Beschreibung.addTextChangedListener(new LoginActivity_TextWatcher(Titel,Beschreibung,bt));
    }

    private void observeInputs() {
        viewModel.getIsCharAllowed().observe(this, new Observer<Integer>() {
            @SuppressLint("ShowToast")
            final Toast spaceToast = Toast.makeText(getApplicationContext(), "Leertasten sind nicht erlaubt", Toast.LENGTH_SHORT);
            @SuppressLint("ShowToast")
            final Toast specialToast = Toast.makeText(getApplicationContext(), "Folgende Eingabe ist nicht erlaubt '/'", Toast.LENGTH_SHORT);
            @Override
            public void onChanged(Integer integer) {
                switch(integer){
                    case 1:
                        if(spaceToast != null){
                            if(!spaceToast.getView().isShown()){
                                spaceToast.show();
                            }}
                        break;
                    case 2:
                        if(specialToast != null){
                            if(!specialToast.getView().isShown()){
                                specialToast.show();
                            }}
                        break;
                    default:
                        break;
                }
            }
        });
    }
    private void btOnClick(){
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                News n = new News(0,Titel.getText().toString(),Beschreibung.getText().toString(),viewModel.getBenutzer().getValue());
                viewModel.InsertNeueNews(n);
                Intent i = new Intent(getApplication().getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}