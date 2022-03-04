package com.nachrichten.lsv_judomvvm.viewmodels;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.nachrichten.lsv_judomvvm.repositories.VersionRepository;

import java.io.File;
import java.util.List;

public class EinstellungenActivityViewModel extends AndroidViewModel{

    private MutableLiveData<File> versionFile = new MutableLiveData<>();
    private MutableLiveData<Integer> downloadProgress = new MutableLiveData<>();
    private VersionRepository versionRepo;

    public EinstellungenActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
            versionRepo = VersionRepository.getInstance();
            versionFile = versionRepo.getVersionFile();
            downloadProgress = versionRepo.getDownloadProgress();
            getDownloadProgress().observeForever(new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if(integer != null && integer == 100){
                        installVersion();
                    }
                }
            });
    }
    public LiveData<File> getVersionFile() {
        versionFile = versionRepo.getVersionFile();
        return versionFile;
    }

    public void downloadVersion(){
        Thread t = new Thread(() -> versionRepo.loadVersionFileFromServer());
        t.start();

    }

    public LiveData<Integer> getDownloadProgress(){
        downloadProgress = versionRepo.getDownloadProgress();
        return downloadProgress;
    }

    public void installVersion(){
        if(versionFile.getValue() != null){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if(Build.VERSION.SDK_INT >= 24){
                Uri downloaded_apk = FileProvider.getUriForFile(getApplication().getApplicationContext(), getApplication().getApplicationContext().getPackageName() + ".provider", versionFile.getValue());
                intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
                List<ResolveInfo> resInfoList = getApplication().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {getApplication().grantUriPermission(getApplication().getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getApplication().startActivity(intent);
            }else {
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setDataAndType(Uri.fromFile(versionFile.getValue()), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(intent);
            }
            downloadProgress.postValue(0);
        }

    }

}
