package com.nachrichten.lsv_judomvvm.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.NotifyUtility;
import com.nachrichten.lsv_judomvvm.models.Termin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import io.paperdb.Paper;

public class TerminRepository {

    private static TerminRepository instance;
    private ArrayList<Termin> dataSet;
    private ArrayList<Termin> oldDataSet;
    private MutableLiveData<ArrayList<Termin>> liveData = new MutableLiveData<>();
    private boolean shouldNotify = true;

    private TerminRepository(){}
    public static TerminRepository getInstance(){
        if(instance == null){
            instance = new TerminRepository();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<Termin>> getTermine(){
        setTermine();
        liveData.setValue(dataSet);
        return liveData;
    }

    private void setTermine(){
        dataSet = Paper.book().read("TerminList",new ArrayList<>());
    }

    public void setTermine(ArrayList<Termin> pTermine){
        Paper.book().write("TerminList",pTermine);
        dataSet = pTermine;
        liveData.postValue(pTermine);
    }


    public void getTermineFromDatabase(){
        try {
            if(dataSet == null){
                setTermine();
            }
            String Request = "https://luenersv-judo.de/lsv_app/TerminDatenbankAuslesen.php?";
            String urlParameters;
            urlParameters = "nstring=" + Termin.buildEidString(dataSet)+
                    "&anz=" + dataSet.size();
            URL url = new URL(Request + urlParameters);

            URLConnection connection = url.openConnection();
            HttpsURLConnection http = (HttpsURLConnection) connection;

            // activate the output
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
            String substr = APIResponse;
            int oldSize = dataSet.size();

            while(substr.contains("<tr>") && !substr.equals("<tr><tr/>")){
                if(substr.lastIndexOf("<tr/>") != substr.indexOf("<tr/>")) {
                    Termin.AddTerminToList(dataSet,substr.substring(substr.indexOf("<tr>")+4,substr.indexOf("<tr/>")),shouldNotify);
                    substr = substr.substring(substr.indexOf("<tr/>") + 5);
                }else {
                    substr = substr.substring(substr.indexOf("<tr>") + 4, substr.indexOf("<tr/>"));
                    Termin.AddTerminToList(dataSet, substr,shouldNotify);
                    shouldNotify = true;
                }

            }
            if(oldSize != dataSet.size()){
                setTermine(dataSet);
            }
            if(APIResponse.contains("{1}")){
                oldDataSet = dataSet;
                if(dataSet.size() == 1){
                    NotifyUtility.getInstance().NotificationTerminDeleted(dataSet.get(0),1);
                    setTermine(new ArrayList<>());
                    oldDataSet = new ArrayList<>();
                    shouldNotify = true;
                }else{
                    dataSet = new ArrayList<>();
                    shouldNotify = false;
                }
            }
            if(dataSet != null && oldDataSet != null && dataSet.size() > 0 && oldDataSet.size() > 0){
                for(int i = 0; i < dataSet.size();i++){
                    for(int j = 0; j< oldDataSet.size(); j++){
                        if(dataSet.get(i).getEid() == oldDataSet.get(j).getEid()){
                            oldDataSet.remove(j);
                        }
                    }
                }if(oldDataSet.size()> 0) {
                    for (int i = 0; i < oldDataSet.size(); i++) {
                        Log.d("Removed Item", Termin.TerminToString(oldDataSet.get(i)));
                        NotifyUtility.getInstance().NotificationTerminDeleted(oldDataSet.get(i),oldDataSet.size());
                    }
                }
                oldDataSet = new ArrayList<>();
            }

            http.setConnectTimeout(500);
            http.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void removeTerminFromDatabase(Termin pTermin, String pAPIKey){
        try {
            shouldNotify = false;
            String Request = "https://luenersv-judo.de/lsv_app/TerminLoeschen.php?";
            String urlParameters = "Eid=" + pTermin.getEid() +
                    "&APIKey=" + URLEncoder.encode(pAPIKey);
            URL url = new URL(Request + urlParameters);

            URLConnection connection = url.openConnection();
            HttpsURLConnection http = (HttpsURLConnection) connection;

            // activate the output
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("GET");
            http.connect();

            http.getOutputStream().flush();
            http.getOutputStream().close();


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));

            bufferedReader.readLine();
            bufferedReader.close();

            if (dataSet != null) {
                for (int i = 0; i < dataSet.size(); i++) {
                    if (dataSet.get(i).getEid() == pTermin.getEid()) {
                        dataSet.remove(i);
                    }
                }
            }
            setTermine(dataSet);
        } catch (IOException ignored) {
        }
    }

    public void insertTerminIntoDatabase(Termin pTermin, Benutzer pBenutzer){
        try {
            shouldNotify =  false;
            String Request = "https://www.luenersv-judo.de/lsv_app/TerminHinzufuegen.php?";
            String urlParameters = "Titel=" + pTermin.getEventName()
                    + "&Beschreibung="+ pTermin.getBeschreibung()
                    +"&Datum="+ pTermin.getDatum()
                    +"&Farbe="+ pTermin.getFarbe()
                    + "&Uid="+ pBenutzer.getID()
                    + "&APIKey=" + URLEncoder.encode(pBenutzer.getAPIKey());

            URL url = new URL(Request+ urlParameters);
            URLConnection con = url.openConnection();
            HttpsURLConnection http = (HttpsURLConnection) con;
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("GET");
            http.connect();

            http.getOutputStream().flush();
            http.getOutputStream().close();
            http.getInputStream().close();
            http.disconnect();
        } catch (IOException ignored) {
        }
    }

}
