package com.nachrichten.lsv_judomvvm.views;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nachrichten.lsv_judomvvm.R;
import com.nachrichten.lsv_judomvvm.Service.AlarmReceiver;
import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.NotifyUtility;
import com.nachrichten.lsv_judomvvm.repositories.BenutzerRepository;
import com.nachrichten.lsv_judomvvm.viewmodels.EinstellungenActivityViewModel;
import com.nachrichten.lsv_judomvvm.viewmodels.MainAcitivityViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private MainAcitivityViewModel viewModel;
    private EinstellungenActivityViewModel versionViewModel;


    private NewsFragment newsFragment = new NewsFragment();
    private TerminFragment terminFragment = new TerminFragment();
    private BottomNavigationView bNavView;
    private NotifyUtility notifyUtility;
    private Dialog mDialog;
    private FloatingActionButton fab_form;
    private ActionBar mainActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Paper.init(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActionBar = getSupportActionBar();
    }

    @Override
    protected void onStop() {
        AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 3*1000, alarmIntent);
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        viewModel.CheckVersionCode();
        AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);
        super.onPostResume();
    }

    @Override
    protected void onStart() {

        BenutzerRepository.getInstance();

        AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);

        viewModel = new ViewModelProvider(this).get(MainAcitivityViewModel.class);
        viewModel.init();
        versionViewModel = new ViewModelProvider(this).get(EinstellungenActivityViewModel.class);
        versionViewModel.init();

        viewModel.CheckVersionCode();
        notifyUtility = NotifyUtility.getInstance();
        notifyUtility.setContext(getApplicationContext());
        bNavView = findViewById(R.id.bottom_navigation);

        mDialog = new Dialog(this);
        fab_form = findViewById(R.id.Form_Fab);
        this.fabOnClick();
        this.AddFragments();
        this.NavListener();
        this.Berechtigungen();
        this.Observer();
        super.onStart();
    }

    private void fabOnClick(){
        fab_form.setOnClickListener(v -> {
            if(terminFragment.isVisible()){
                Intent intent = new Intent(this, TerminformActivity.class);
                startActivity(intent);
            }else if(newsFragment.isVisible()) {
                Intent intent = new Intent(this, NewsformActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Observer() {
        viewModel.getCheckedVersionCode().observe(this, aBoolean -> {
            if(!aBoolean){
                mDialog.setContentView(R.layout.activity_main_version_alertdialog);
                mDialog.setCancelable(false);
                this.DialogButtonOnClickListener();
                if(!mDialog.isShowing()){
                    mDialog.show();
                }
            }
        });
        viewModel.getBenutzer().observe(this, new Observer<Benutzer>() {
            @Override
            public void onChanged(Benutzer benutzer) {
                if (!benutzer.getAPIKey().equals("")) {
                    fab_form.setVisibility(View.VISIBLE);
                }else
                    fab_form.setVisibility(View.INVISIBLE);
            }
        });
        viewModel.getCurrentDate().observe(this, new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                if(date.getYear() != 1970){
                    SimpleDateFormat datumsFormatierung = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());
                    getSupportActionBar().setTitle("Kalender / " +datumsFormatierung.format(date));
                }
            }
        });
    }

    private void DialogButtonOnClickListener() {
        Button dialogButton = mDialog.findViewById(R.id.alertDialog_button);
        dialogButton.setOnClickListener(v -> {
            final boolean[] CheckClicked = {false};
            versionViewModel.getVersionFile().observe(this, new Observer<File>() {
                @Override
                public void onChanged(File file) {
                    if(file != null && file.exists() && CheckClicked[0] && versionViewModel.getDownloadProgress().getValue() == 100){
                        versionViewModel.installVersion();
                        CheckClicked[0] = false;
                    }else{
                        versionViewModel.downloadVersion();
                        CheckClicked[0] = true;
                    }
                }
            });

            final Toast[] model = {new Toast(getApplicationContext())};
            versionViewModel.getDownloadProgress().observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if(integer != null){
                        Log.d("integer", "" + integer);
                        switch (integer){
                            case 100:
                                if(model[0].getView() != null) {
                                    if (model[0].getView().isShown()) {
                                        model[0] = Toast.makeText(getApplicationContext(), "Erfolgreich heruntergeladen", Toast.LENGTH_SHORT);
                                        model[0].show();
                                    }
                                }

                                break;
                            case -1:
                                if(model[0].getView() != null) {
                                    if (model[0].getView().isShown()) {
                                        model[0] = Toast.makeText(getApplicationContext(), "Keine Internetverbindung", Toast.LENGTH_SHORT);
                                        model[0].show();
                                    }
                                }
                                break;
                            case -2:
                                if(model[0].getView() != null) {
                                    if (model[0].getView().isShown()) {
                                        model[0] = Toast.makeText(getApplicationContext(), "Server Offline", Toast.LENGTH_SHORT);
                                        model[0].show();
                                    } }
                                break;
                            case -3:
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dropdown_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        MenuItem ausloggenItem = menu.findItem(R.id.Ausloggen);
        MenuItem einloggenItem = menu.findItem(R.id.Login);
        if (viewModel.getBenutzer().getValue().getAPIKey().equals("")) {
            ausloggenItem.setEnabled(false);
            einloggenItem.setEnabled(true);
        } else {
            ausloggenItem.setEnabled(true);
            einloggenItem.setEnabled(false);
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        viewModel.setOptionsItemId(item.getItemId());
        Intent intent;
        switch(item.getItemId()){
            case R.id.Login:
                intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Bitte Einloggen", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Einstellungen:
                intent = new Intent(getApplication(), EinstellungenActivity.class);
                startActivity(intent);
                return true;
            case R.id.Ausloggen:
                viewModel.BenutzerAusloggen();
                Toast.makeText(getApplicationContext(),"Erfolgreich ausgeloggt", Toast.LENGTH_SHORT).show();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void NavListener() {
        bNavView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.Termin || item.getItemId() == R.id.News) {
                    viewModel.setBottomNavItemId(item.getItemId());
                    return true;
                }
                return false;
            }
        });
        FragmentManager ft = getSupportFragmentManager();
        viewModel.getBottomNavItemId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == R.id.Termin) {
                    ft.beginTransaction().hide(newsFragment).commit();
                    ft.beginTransaction().show(terminFragment).commit();
                    bNavView.setSelectedItemId(R.id.Termin);
                    SimpleDateFormat datumsFormatierung = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());
                    getSupportActionBar().setTitle("Kalender / " +datumsFormatierung.format(viewModel.getCurrentDate().getValue()));
                }else{
                    ft.beginTransaction().hide(terminFragment).commit();
                    ft.beginTransaction().show(newsFragment).commit();
                    bNavView.setSelectedItemId(R.id.News);
                    mainActionBar.setTitle("Nachrichten");
                }
            }
        });
    }

    private void AddFragments() {
        FragmentManager ft = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(false);
        FragmentTransaction fragmentTransaction = ft.beginTransaction();
        if(getSupportFragmentManager().findFragmentByTag("TerminFragment") == null &&getSupportFragmentManager().findFragmentByTag("NewsFragment") == null) {
            fragmentTransaction.add(R.id.fragment_container, newsFragment, "NewsFragment");
            fragmentTransaction.add(R.id.fragment_container2, terminFragment, "TerminFragment").commit();
        }
    }

    private void Berechtigungen() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED
                }, 1);
    }

}