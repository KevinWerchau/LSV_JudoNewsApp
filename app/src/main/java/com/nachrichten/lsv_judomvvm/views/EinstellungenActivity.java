package com.nachrichten.lsv_judomvvm.views;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nachrichten.lsv_judomvvm.R;
import com.nachrichten.lsv_judomvvm.viewmodels.EinstellungenActivityViewModel;

import java.io.File;

public class EinstellungenActivity extends AppCompatActivity {

    private EinstellungenActivityViewModel viewModel;
    private Button uButton;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);

        viewModel = new ViewModelProvider(this).get(EinstellungenActivityViewModel.class);
        viewModel.init();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        uButton = findViewById(R.id.Update_Button);
        this.setUButtonListener();
    }

    private LifecycleOwner getLifecycleOwner(){
        return this;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Datei wird heruntergeladen. Bitte warten...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(viewModel.getDownloadProgress() != null && viewModel.getDownloadProgress().getValue() == 100)
                    uButton.callOnClick();
                }
            });
            pDialog.show();
            return pDialog;
        }
        return null;
    }


    private void setUButtonListener() {
        uButton.setOnClickListener(v -> {
            viewModel.getVersionFile().observe(getLifecycleOwner(), new Observer<File>() {
                @Override
                public void onChanged(File file) {
                    if(file != null && file.exists()){
                        viewModel.installVersion();
                    }else{
                        showDialog(0);
                        viewModel.downloadVersion();
                    }
                }
            });

            final Toast[] model = {new Toast(getApplicationContext())};
            viewModel.getDownloadProgress().observe(getLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if(pDialog != null && integer != null){
                        pDialog.setProgress(integer);
                        Log.d("integer", "" + integer);
                        switch (integer){
                            case 100:
                                pDialog.dismiss();
                                if(model[0].getView() != null) {
                                    if (model[0].getView().isShown()) {
                                        model[0] = Toast.makeText(getApplicationContext(), "Erfolgreich heruntergeladen", Toast.LENGTH_SHORT);
                                        model[0].show();
                                    }
                                }

                                break;
                            case -1:
                                pDialog.dismiss();
                                if(model[0].getView() != null) {
                                    if (model[0].getView().isShown()) {
                                        model[0] = Toast.makeText(getApplicationContext(), "Keine Internetverbindung", Toast.LENGTH_SHORT);
                                        model[0].show();
                                    }
                                }
                                break;
                            case -2:
                                pDialog.dismiss();
                                if(model[0].getView() != null) {
                                    if (model[0].getView().isShown()) {
                                        model[0] = Toast.makeText(getApplicationContext(), "Server Offline", Toast.LENGTH_SHORT);
                                        model[0].show();
                                    } }
                                break;
                            case -3:
                                pDialog.dismiss();
                                if(model[0].getView() != null) {
                                    if (model[0].getView().isShown()) {
                                        model[0] = Toast.makeText(getApplicationContext(), "Probleme beim Speichern der Datei", Toast.LENGTH_SHORT);
                                        model[0].show();
                                    } }
                                break;
                        }
                    }else
                        Log.d("integer", "" + integer);
                }});



        });
    }
}