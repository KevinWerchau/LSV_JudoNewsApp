package com.nachrichten.lsv_judomvvm.models;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity_TextWatcher implements TextWatcher {

    private EditText dasFeld, anderesFeld;
    private Button anmeldeButton;

    public LoginActivity_TextWatcher(EditText pDasFeld, EditText pAnderesFeld, Button pAnmeldeButton){
        dasFeld = pDasFeld;
        anderesFeld = pAnderesFeld;
        anmeldeButton = pAnmeldeButton;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!dasFeld.getText().toString().equals("") && !anderesFeld.getText().toString().equals("")){
            anmeldeButton.setEnabled(true);
        }else{
            anmeldeButton.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
