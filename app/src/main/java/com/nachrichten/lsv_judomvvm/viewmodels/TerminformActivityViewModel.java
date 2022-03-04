package com.nachrichten.lsv_judomvvm.viewmodels;

import android.app.Application;
import android.content.Intent;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.Termin;
import com.nachrichten.lsv_judomvvm.repositories.BenutzerRepository;
import com.nachrichten.lsv_judomvvm.repositories.TerminRepository;
import com.nachrichten.lsv_judomvvm.views.MainActivity;

import java.util.concurrent.ThreadPoolExecutor;

public class TerminformActivityViewModel extends AndroidViewModel{

    private MutableLiveData<Integer> isCharAllowed = new MutableLiveData<>();
    private MutableLiveData<Benutzer> mBenutzer;
    private BenutzerRepository benutzerRepo;
    private TerminRepository terminRepo;

    public TerminformActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        benutzerRepo = BenutzerRepository.getInstance();
        mBenutzer = benutzerRepo.getBenutzer();
        terminRepo = TerminRepository.getInstance();
    }

    public LiveData<Benutzer> getBenutzer(){
        mBenutzer = benutzerRepo.getBenutzer();
        return mBenutzer;
    }

    public void InsertNeuenTermin(Termin pTermin){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                terminRepo.insertTerminIntoDatabase(pTermin,benutzerRepo.getBenutzer().getValue());
            }
        });
        t.start();
    }

    public LiveData<Integer> getIsCharAllowed(){
        return isCharAllowed;
    }

    public void setIsCharAllowed(int pIsAllowed){
        isCharAllowed.postValue(pIsAllowed);
    }
}
