package com.nachrichten.lsv_judomvvm.viewmodels;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.repositories.BenutzerRepository;
import com.nachrichten.lsv_judomvvm.views.MainActivity;


public class LoginActivityViewModel extends AndroidViewModel{

    private BenutzerRepository benutzerRepo;
    private final MutableLiveData<Integer> isCharAllowed = new MutableLiveData<>();
    private MutableLiveData<Integer> shouldFinish = new MutableLiveData<>();
    private MutableLiveData<Benutzer> mBenutzer;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        benutzerRepo = BenutzerRepository.getInstance();
        mBenutzer = benutzerRepo.getBenutzer();
    }


    public LiveData<Integer> shouldFinish(){
        return shouldFinish;
    }

    public LiveData<Benutzer> getBenutzer(){
        return benutzerRepo.getBenutzer();
    }

    public LiveData<Integer> getIsCharAllowed(){
        return isCharAllowed;
    }

    public void setIsCharAllowed(int pIsAllowed){
        isCharAllowed.postValue(pIsAllowed);
    }

    public void checkLoginDetails(String pName, String pPassword){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                benutzerRepo.getBenutzerFromDatabase(pName, pPassword);
            }
        });
        t.start();
    }

}
