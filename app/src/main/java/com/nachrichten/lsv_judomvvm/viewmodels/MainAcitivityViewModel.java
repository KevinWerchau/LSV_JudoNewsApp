package com.nachrichten.lsv_judomvvm.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.repositories.BenutzerRepository;
import com.nachrichten.lsv_judomvvm.repositories.UIStateRepository;

import java.io.File;
import java.util.Date;

public class MainAcitivityViewModel extends AndroidViewModel{

    private BenutzerRepository benutzerRepo;
    private UIStateRepository UIStateRepo;
    private MutableLiveData<Benutzer> mBenutzer;
    private MutableLiveData<Integer> bottomNavItemId = new MutableLiveData<>();
    private MutableLiveData<Integer> OptionsItemId = new MutableLiveData<>();
    private File oldInstallationPackage = new File("Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+\"/LSV_JUDO.apk\"");
    private MutableLiveData<Boolean> CheckedVersionCode = new MutableLiveData<>();


    public MainAcitivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        benutzerRepo = BenutzerRepository.getInstance();
        UIStateRepo = UIStateRepository.getInstance();
        mBenutzer = benutzerRepo.getBenutzer();
        oldInstallationPackage.delete();
    }

    public LiveData<Benutzer> getBenutzer(){
        mBenutzer = benutzerRepo.getBenutzer();
        return mBenutzer;
    }

    public LiveData<Date> getCurrentDate(){
        return UIStateRepo.getDate();
    }

    public void CheckVersionCode(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UIStateRepo.CheckVersionCode();
            }
        });
        t.start();
    }


    private void setBenutzer(Benutzer pBenutzer){
        benutzerRepo.getBenutzer().setValue(pBenutzer);
    }

    public void BenutzerAusloggen(){
        Benutzer newData = new Benutzer(0,"Default", "");
        this.setBenutzer(newData);
        benutzerRepo.setBenutzer(newData);
    }

    public void setBottomNavItemId(Integer itemId){
        UIStateRepo.setBottomNavItemId(itemId);
    }
    public LiveData<Integer> getBottomNavItemId(){
        bottomNavItemId = UIStateRepo.getBottomNavItemId();
        return bottomNavItemId;
    }

    public void setOptionsItemId(Integer itemId){
        OptionsItemId.setValue(itemId);
    }

    public LiveData<Boolean> getCheckedVersionCode(){
        CheckedVersionCode = UIStateRepo.getLiveCheckedVersionCode();
        return CheckedVersionCode;
    }

}

