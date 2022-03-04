package com.nachrichten.lsv_judomvvm.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.Termin;
import com.nachrichten.lsv_judomvvm.repositories.TerminRepository;
import com.nachrichten.lsv_judomvvm.repositories.UIStateRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TerminFragmentViewModel extends AndroidViewModel {

    private TerminRepository terminRepo;
    private UIStateRepository UIStateRepo;
    private MutableLiveData<ArrayList<Termin>> mTermine;
    private MutableLiveData<Date> mSelectedMonth;
    private Thread t;

    public TerminFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        terminRepo = TerminRepository.getInstance();
        UIStateRepo = UIStateRepository.getInstance();
        mSelectedMonth = UIStateRepo.getDate();
        mTermine = terminRepo.getTermine();
        startTimerTask();
    }

    public void startTimerTask(){
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> terminRepo.getTermineFromDatabase();
        ses.scheduleAtFixedRate(task, 0, 15, TimeUnit.SECONDS);
    }

    public LiveData<ArrayList<Termin>> getTerminList(){
        return terminRepo.getTermine();
    }

    public LiveData<Date> getSelectedDate(){
        mSelectedMonth = UIStateRepo.getDate();
        return mSelectedMonth;
    }

    public void setSelectedDate(Date pDate){
        UIStateRepo.setDate(pDate);
    }

    public void removeItemFromDatabase(Termin pClickedTermin, Benutzer pBenutzer) {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                terminRepo.removeTerminFromDatabase(pClickedTermin, pBenutzer.getAPIKey());
            }
        });
        t.start();
    }

}
