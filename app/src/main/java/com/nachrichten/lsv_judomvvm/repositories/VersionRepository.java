package com.nachrichten.lsv_judomvvm.repositories;

import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class VersionRepository{

    private static VersionRepository instance;
    private File versionFile;
    private MutableLiveData<Integer> downloadProgress;
    private MutableLiveData<File> fileData;


    private VersionRepository(){
        downloadProgress = new MutableLiveData<>();
        fileData = new MutableLiveData<>();
    }
    public static VersionRepository getInstance(){
        if(instance == null){
            instance = new VersionRepository();
        }
        return instance;
    }

    public MutableLiveData<File> getVersionFile() {
        fileData.setValue(versionFile);
        return fileData;
    }

    public void loadVersionFileFromServer(){
        int count;
        String fileLocationOnServer = "https://luenersv-judo.de/lsv_app/app.apk";
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/LSV_JUDO.apk");
        byte[] data = new byte[1024];
        long total = 0;
        try {
            URL url = new URL(fileLocationOnServer);
            URLConnection connection = url.openConnection();
            connection.connect();

            int lengthOfFile = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(),8192);
            OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/LSV_JUDO.apk" );

            while((count = input.read(data)) != -1){
                total += count;
                setDownloadProgress(Integer.parseInt(""+(int) ((total * 100) / lengthOfFile)));
                Log.d("calcedProgress",""+Integer.parseInt(""+(int) ((total * 100) / lengthOfFile)));
                output.write(data,0 ,count);
            }
            output.flush();
            output.close();
            input.close();
            setVersionFile(f);
        }catch (UnknownHostException | MalformedURLException e){
            setDownloadProgress(-1);
        } catch (FileNotFoundException e) {
            setDownloadProgress(-2);
        } catch (IOException e) {
            setDownloadProgress(-3);
        }
    }

    public void setVersionFile(File pVersionFile) {
        this.versionFile = pVersionFile;
    }

    public MutableLiveData<Integer> getDownloadProgress(){
        return downloadProgress;
    }

    private void setDownloadProgress(int pDownloadProgress){
        downloadProgress.postValue(pDownloadProgress);
    }
}
