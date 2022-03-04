package com.nachrichten.lsv_judomvvm.models;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.nachrichten.lsv_judomvvm.viewmodels.LoginActivityViewModel;
import com.nachrichten.lsv_judomvvm.viewmodels.NewsformActivityViewModel;
import com.nachrichten.lsv_judomvvm.viewmodels.TerminformActivityViewModel;

public class CustomInputFilter implements InputFilter {

    private final boolean allowCharacters;
    private final boolean allowDigits;
    private final boolean allowSpaceChar;
    private final boolean allowSpecialChar;
    private LoginActivityViewModel LoginViewModel;
    private TerminformActivityViewModel TerminViewModel;
    private NewsformActivityViewModel NewsViewModel;

    public CustomInputFilter(boolean pAllowCharacters,
                             boolean pAllowDigits,
                             boolean pAllowSpaceChar,
                             boolean pAllowSpecialChar,
                             LoginActivityViewModel pViewModel)
    {
        allowCharacters = pAllowCharacters;
        allowDigits = pAllowDigits;
        allowSpaceChar = pAllowSpaceChar;
        allowSpecialChar = pAllowSpecialChar;
        LoginViewModel = pViewModel;
    }

    public CustomInputFilter(boolean pAllowCharacters,
                             boolean pAllowDigits,
                             boolean pAllowSpaceChar,
                             boolean pAllowSpecialChar,
                             NewsformActivityViewModel pViewModel)
    {
        allowCharacters = pAllowCharacters;
        allowDigits = pAllowDigits;
        allowSpaceChar = pAllowSpaceChar;
        allowSpecialChar = pAllowSpecialChar;
        NewsViewModel = pViewModel;
    }

    public CustomInputFilter(boolean pAllowCharacters,
                             boolean pAllowDigits,
                             boolean pAllowSpaceChar,
                             boolean pAllowSpecialChar,
                             TerminformActivityViewModel pViewModel)
    {
        allowCharacters = pAllowCharacters;
        allowDigits = pAllowDigits;
        allowSpaceChar = pAllowSpaceChar;
        allowSpecialChar = pAllowSpecialChar;
        TerminViewModel = pViewModel;
    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        boolean keepOriginal = true;
        StringBuilder sb = new StringBuilder(end - start);
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (isCharAllowed(c)) {
                sb.append(c);
            } else {
                keepOriginal = false;
            }
        }
        if (keepOriginal) {
            return null;
        } else {
            if (source instanceof Spanned) {
                SpannableString sp = new SpannableString(sb);
                TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                return sp;
            } else {
                return sb;
            }
        }



    }

    private boolean isCharAllowed(char c) {
        if(LoginViewModel != null){
            LoginActivityViewModel viewModel = LoginViewModel;
            if (Character.isLetter(c) && allowCharacters) {
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if (Character.isDigit(c) && allowDigits) {
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if (Character.isSpaceChar(c) && allowSpaceChar) {
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if(c != '/' &&!Character.isLetter(c) && !Character.isDigit(c)&& !Character.isSpaceChar(c) && allowSpecialChar){
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if(Character.isSpaceChar(c) && !allowSpaceChar){
                viewModel.setIsCharAllowed(1);
                return false;
            }
            if(c == '/'){
                viewModel.setIsCharAllowed(2);
                return false;
            }
            return false;
        }
        if(TerminViewModel != null){
            TerminformActivityViewModel viewModel = TerminViewModel;
            if (Character.isLetter(c) && allowCharacters) {
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if (Character.isDigit(c) && allowDigits) {
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if (Character.isSpaceChar(c) && allowSpaceChar) {
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if(c != '/' &&!Character.isLetter(c) && !Character.isDigit(c)&& !Character.isSpaceChar(c) && allowSpecialChar){
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if(Character.isSpaceChar(c) && !allowSpaceChar){
                viewModel.setIsCharAllowed(1);
                return false;
            }
            if(c == '/'){
                viewModel.setIsCharAllowed(2);
                return false;
            }
            return false;
        }
        if(NewsViewModel != null){
            NewsformActivityViewModel viewModel = NewsViewModel;
            if (Character.isLetter(c) && allowCharacters) {
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if (Character.isDigit(c) && allowDigits) {
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if (Character.isSpaceChar(c) && allowSpaceChar) {
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if(c != '/' &&!Character.isLetter(c) && !Character.isDigit(c)&& !Character.isSpaceChar(c) && allowSpecialChar){
                viewModel.setIsCharAllowed(0);
                return true;
            }
            if(Character.isSpaceChar(c) && !allowSpaceChar){
                viewModel.setIsCharAllowed(1);
                return false;
            }
            if(c == '/'){
                viewModel.setIsCharAllowed(2);
                return false;
            }
            return false;
        }
        return false;
    }
};


