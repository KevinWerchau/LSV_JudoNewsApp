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
import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.CustomInputFilter;
import com.nachrichten.lsv_judomvvm.models.LoginActivity_TextWatcher;
import com.nachrichten.lsv_judomvvm.viewmodels.LoginActivityViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText Namen_Feld, Passwort_Feld;
    private Button Anmelden_Button;

    private LoginActivityViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onDestroy() {
        viewModel.getBenutzer().removeObservers(this);
        viewModel.getIsCharAllowed().removeObservers(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        viewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);
        viewModel.init();

        this.findViews();
        this.onButtonClick();
        this.observeInputs();
        this.observeFinish();

        viewModel.getBenutzer().observe(this, new Observer<Benutzer>() {
            @Override
            public void onChanged(Benutzer benutzer) {
                Toast loginInvalid = Toast.makeText(getApplicationContext(),"Falscher Name oder Passwort",Toast.LENGTH_SHORT);
                if(Anmelden_Button.isEnabled()) {
                    if (benutzer.getAPIKey().equals("")) {
                        if (!loginInvalid.getView().isShown()) {
                            loginInvalid.show();
                        }
                    }
                    if (!benutzer.getAPIKey().equals("")) {
                        Toast loginValid = Toast.makeText(getApplicationContext(),"Erfolgreich eingeloggt," + " Hallo " + benutzer.getName(),Toast.LENGTH_SHORT);
                        if (!loginValid.getView().isShown()) {
                            loginValid.show();
                        }
                        Intent intent = new Intent(getApplication().getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        super.onStart();
    }

    private void observeFinish() {
        viewModel.shouldFinish().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case 1:
                        Toast.makeText(getApplicationContext(), "Erfolgreich Eingeloggt", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Falscher Name/Passwort", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
            }
        });
    }

    private void onButtonClick() {
        Anmelden_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.checkLoginDetails(Namen_Feld.getText().toString(),Passwort_Feld.getText().toString());
            }
        });
    }

    private void observeInputs() {
        viewModel.getIsCharAllowed().observe(this, new Observer<Integer>() {
            @SuppressLint("ShowToast")
            final Toast spaceToast = Toast.makeText(getApplicationContext(), "Leertasten sind nicht erlaubt", Toast.LENGTH_SHORT);
            @SuppressLint("ShowToast")
            final Toast specialToast = Toast.makeText(getApplicationContext(), "Folgende Eingabe ist nicht erlaubt ':'", Toast.LENGTH_SHORT);
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

    private void findViews() {
        Namen_Feld = findViewById(R.id.Benutzernamen_Feld);
        Namen_Feld.setFilters(new CustomInputFilter[]{
                new CustomInputFilter(
                        true,
                        true,
                        false,
                        true,
                        viewModel)});
        Passwort_Feld = (EditText) findViewById(R.id.Passwort_Feld);
        Passwort_Feld.setFilters(new CustomInputFilter[]{
                new CustomInputFilter(
                        true,
                        true,
                        false,
                        true,
                        viewModel)});
        Anmelden_Button = (Button) findViewById(R.id.Benutzer_Knopf);
        Anmelden_Button.setEnabled(false);
        Namen_Feld.addTextChangedListener(new LoginActivity_TextWatcher(Namen_Feld,Passwort_Feld,Anmelden_Button));
        Passwort_Feld.addTextChangedListener(new LoginActivity_TextWatcher(Namen_Feld,Passwort_Feld,Anmelden_Button));
    }
}