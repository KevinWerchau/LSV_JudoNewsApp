package com.nachrichten.lsv_judomvvm.views;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nachrichten.lsv_judomvvm.R;
import com.nachrichten.lsv_judomvvm.models.CustomInputFilter;
import com.nachrichten.lsv_judomvvm.models.Termin;
import com.nachrichten.lsv_judomvvm.models.TerminFormActivity_TextWatcher;
import com.nachrichten.lsv_judomvvm.viewmodels.TerminformActivityViewModel;

import java.util.Calendar;
import java.util.Date;

public class TerminformActivity extends AppCompatActivity {

    private TerminformActivityViewModel viewModel;

    private EditText Datetx, Tb, Notes;
    private Button DtWahl, CWahl, btSpeichern, OkColor;
    private Spinner Farbwahl;
    private DatePickerDialog datePickerDialog;

    private int day;
    private int month;
    private int year;
    private int enteredColor;
    private long selectedDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminform);
    }

    @Override
    protected void onStart() {
        viewModel = new ViewModelProvider(this).get(TerminformActivityViewModel.class);
        viewModel.init();
        this.setCurrentDay();
        this.GUIZuweisen();
        this.observeInputs();
        this.SpeichernOnClick();
        super.onStart();
    }


    private void setCurrentDay() {
        //Wichtige Daten
        final Calendar myCalendar = Calendar.getInstance();
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);
    }

    private void GUIZuweisen(){
        Tb = findViewById(R.id.editText_Termin);
        Tb.setFilters(new CustomInputFilter[]{new CustomInputFilter(true,true,true,true,viewModel)});
        Notes = findViewById(R.id.editText_Notes);
        Notes.setFilters(new CustomInputFilter[]{new CustomInputFilter(true,true,true,true,viewModel)});
        Datetx = findViewById(R.id.editText_Date);
        Farbwahl = findViewById(R.id.Spinner_Farbkategorien);
        btSpeichern = findViewById(R.id.Speicherung);
        btSpeichern.setEnabled(false);
        Tb.addTextChangedListener(new TerminFormActivity_TextWatcher(Tb,Notes,Datetx,Farbwahl, btSpeichern));
        Notes.addTextChangedListener(new TerminFormActivity_TextWatcher(Tb,Notes,Datetx,Farbwahl, btSpeichern));
        Datetx.addTextChangedListener(new TerminFormActivity_TextWatcher(Tb,Notes,Datetx,Farbwahl, btSpeichern));
        this.FarbwahlInit();
        this.FarbwahlOnItemSelectedListener();
        this.DatetxSetOnClick();
    }

    private void FarbwahlInit() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),R.array.Farbkategorien,R.layout.mainactivity_dialog_spinnertextview);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Farbwahl.setAdapter(adapter);
    }

    private void FarbwahlOnItemSelectedListener() {
        Farbwahl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0 && !Tb.getText().toString().equals("")
                        && !Notes.getText().toString().equals("")
                        && !Datetx.getText().toString().equals("")){
                    btSpeichern.setEnabled(true);
                }else
                    btSpeichern.setEnabled(false);
                switch(position ){
                    case 1 : enteredColor = Color.rgb(128,0,128);
                        break;
                    case 2 : enteredColor = Color.rgb(255,255,0);
                        break;
                    case 3 : enteredColor = Color.rgb(76,175,80);
                        break;
                    case 4 : enteredColor = Color.rgb(244,38,40);
                        break;
                    case 5 : enteredColor = Color.rgb(0,0,0);
                        break;
                    case 6 : enteredColor = Color.rgb(80,169,178);
                        break;
                    case 7 : enteredColor = Color.rgb(99,8,228);
                        break;
                    case 8 : enteredColor = Color.rgb(255,136,0);
                        break;
                    default:
                        int enteredColor = Color.rgb(255,136,0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                btSpeichern.setEnabled(false);
            }
        });
    }

    private void DatetxSetOnClick(){
        Datetx.setOnClickListener(view -> {
            datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            month = month + 1;
                            String date = day+"/" + month+"/"+year;
                            Datetx.setText(date);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                selectedDay = datePicker.getAutofillValue().getDateValue();
                            }else{
                                Date hDate = new Date();
                                hDate.setYear(year);
                                hDate.setMonth(month);
                                hDate.setDate(day);
                                selectedDay = hDate.getTime();

                            }
                        }
                    },year, month, day){

            };
            datePickerDialog.show();
        });
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

    private void SpeichernOnClick() {
        btSpeichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Termin n = new Termin(0, Tb.getText().toString(), Notes.getText().toString(),enteredColor, selectedDay, viewModel.getBenutzer().getValue());
                viewModel.InsertNeuenTermin(n);
                Intent i = new Intent(getApplication().getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}