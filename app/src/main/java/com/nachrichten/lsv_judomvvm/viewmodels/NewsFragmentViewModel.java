package com.nachrichten.lsv_judomvvm.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.News;
import com.nachrichten.lsv_judomvvm.repositories.NewsRepository;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewsFragmentViewModel extends AndroidViewModel {

    private NewsRepository newsRepo;
    private MutableLiveData<ArrayList<News>> mNews;
    private Thread t;


    public NewsFragmentViewModel(@NonNull Application application) {
        super(application);
    }
    public void init(){
        newsRepo = NewsRepository.getInstance();
        mNews = newsRepo.getNews();
        startTimerTask();
    }

    public void startTimerTask(){
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> newsRepo.getNewsFromDatabase();
        ses.scheduleAtFixedRate(task, 0, 15, TimeUnit.SECONDS);
    }

    public LiveData<ArrayList<News>> getNews(){
        mNews = newsRepo.getNews();
        return mNews;
    }

    @Override
    protected void onCleared() {
        newsRepo = null;
        super.onCleared();
    }

    public void removeItemFromDatabase(News pClickedNews, Benutzer pBenutzer) {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                newsRepo.removeNewsFromDatabase(pClickedNews, pBenutzer.getAPIKey());
            }
        });
        t.start();
    }
}



