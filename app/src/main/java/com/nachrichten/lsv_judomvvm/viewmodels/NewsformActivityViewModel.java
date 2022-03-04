package com.nachrichten.lsv_judomvvm.viewmodels;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.News;
import com.nachrichten.lsv_judomvvm.models.Termin;
import com.nachrichten.lsv_judomvvm.repositories.BenutzerRepository;
import com.nachrichten.lsv_judomvvm.repositories.NewsRepository;
import com.nachrichten.lsv_judomvvm.views.MainActivity;

public class NewsformActivityViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> isCharAllowed = new MutableLiveData<>();
    private MutableLiveData<Benutzer> mBenutzer;
    private BenutzerRepository benutzerRepo;
    private NewsRepository newsRepo;

    public NewsformActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        benutzerRepo = BenutzerRepository.getInstance();
        newsRepo = NewsRepository.getInstance();
        mBenutzer = benutzerRepo.getBenutzer();
    }



    public LiveData<Benutzer> getBenutzer(){
        mBenutzer = benutzerRepo.getBenutzer();
        return mBenutzer;
    }

    public void InsertNeueNews(News pNews){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                newsRepo.insertNewsIntoDatabase(pNews,benutzerRepo.getBenutzer().getValue());
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
