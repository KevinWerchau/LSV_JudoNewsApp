package com.nachrichten.lsv_judomvvm.repositories;

import androidx.lifecycle.MutableLiveData;

import com.nachrichten.lsv_judomvvm.BuildConfig;
import com.nachrichten.lsv_judomvvm.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import io.paperdb.Paper;

public class UIStateRepository {

    private static UIStateRepository instance;
    private MutableLiveData<Date> liveDate = new MutableLiveData<>();;
    private Date storedDate;
    private MutableLiveData<Integer> liveBottomNavItemId = new MutableLiveData<>();;
    private int storedBottomNavItemId;
    private MutableLiveData<Boolean > liveCheckedVersionCode = new MutableLiveData<>();
    private boolean storedCheckedVersionCode;

    private UIStateRepository(){}
    public static UIStateRepository getInstance(){
        if(instance == null){
            instance = new UIStateRepository();
        }
        return instance;
    }

    public MutableLiveData<Date> getDate(){
        setDate();
        if(liveDate.getValue() != storedDate){
            liveDate.setValue(storedDate);
        }
        return liveDate;
    }

    private void setDate(){
        Date currentTime = new Date(System.currentTimeMillis());
        storedDate = Paper.book().read("SelectedDate",currentTime);
    }

    public void setDate(Date pDate){
        Paper.book().write("SelectedDate",pDate);
        storedDate = pDate;
        liveDate.postValue(pDate);
    }

    public MutableLiveData<Integer> getBottomNavItemId(){
        setBottomNavItemId();
        liveBottomNavItemId.setValue(storedBottomNavItemId);
        return liveBottomNavItemId;
    }

    private void setBottomNavItemId(){
        int TerminId =  R.id.Termin;
        storedBottomNavItemId = Paper.book().read("BottomNavId", TerminId);
    }

    public void setBottomNavItemId(int pInt){
        Paper.book().write("BottomNavId",pInt);
        storedBottomNavItemId = pInt;
        liveBottomNavItemId.postValue(pInt);
    }

    public MutableLiveData<Boolean> getLiveCheckedVersionCode(){
        setCheckedVersionCode();
        liveCheckedVersionCode.setValue(storedCheckedVersionCode);
        return liveCheckedVersionCode;
    }

    private void setCheckedVersionCode(){
        storedCheckedVersionCode = Paper.book().read("BoolCheckedVersionCode", true);
    }

    public void setCheckedVersionCode(boolean pChecked){
        Paper.book().write("BoolCheckedVersionCode", pChecked);
        storedCheckedVersionCode = pChecked;
        liveCheckedVersionCode.postValue(pChecked);
    }

    public void CheckVersionCode(){
        try{
            String Request = "https://luenersv-judo.de/lsv_app/VersionComparing.php?";
            String urlParameters = "versionCode="+ BuildConfig.VERSION_CODE;
            URL url = new URL(Request + urlParameters);
            URLConnection connection = url.openConnection();
            HttpsURLConnection http = (HttpsURLConnection) connection;

            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("GET");
            http.connect();

            String APIResponse = "";
            if(http.getResponseCode() == 200) {
                String line = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                while ((line = in.readLine()) != null) {
                    APIResponse = line;
                }
                in.close();
            }
            if(!APIResponse.equals(""))
            setCheckedVersionCode(Boolean.parseBoolean(APIResponse));
            http.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
