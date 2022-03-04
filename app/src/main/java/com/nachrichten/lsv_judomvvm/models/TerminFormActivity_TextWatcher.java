package com.nachrichten.lsv_judomvvm.models;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class TerminFormActivity_TextWatcher implements TextWatcher {

        private EditText dasFeld, anderesFeld, drittesFeld;
        private Spinner KategorieSpinner;
        private Button speicherButton;

        public TerminFormActivity_TextWatcher(EditText pDasFeld, EditText pAnderesFeld,EditText pDrittesFeld,Spinner pKategorieSpinner, Button pSpeichernButton){
            dasFeld = pDasFeld;
            anderesFeld = pAnderesFeld;
            drittesFeld = pDrittesFeld;
            KategorieSpinner = pKategorieSpinner;
            speicherButton = pSpeichernButton;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str1 = dasFeld.getText().toString().trim();
            String str2 = anderesFeld.getText().toString().trim();
            String str3 = drittesFeld.getText().toString().trim();
            boolean shouldEnableButton = !str1.equals("")
                    && !str2.equals("")
                    && !str3.equals("")
                    && KategorieSpinner.getSelectedItemPosition() != 0;
             if(shouldEnableButton){
                speicherButton.setEnabled(true);
            }else{
                speicherButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


