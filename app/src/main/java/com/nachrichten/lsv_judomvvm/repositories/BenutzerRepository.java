package com.nachrichten.lsv_judomvvm.repositories;

import androidx.lifecycle.MutableLiveData;

import com.nachrichten.lsv_judomvvm.models.Benutzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import io.paperdb.Paper;

public class BenutzerRepository {

    private static BenutzerRepository instance;
    private Benutzer benutzerData;
    private MutableLiveData<Benutzer> liveData = new MutableLiveData<>();

    private BenutzerRepository(){
        setBenutzer();
        liveData.setValue(benutzerData);
    }
    public static BenutzerRepository getInstance(){
        if(instance == null){
            instance = new BenutzerRepository();
        }
        return instance;
    }

    public MutableLiveData<Benutzer> getBenutzer(){
        setBenutzer();
        if(liveData.getValue().getID() != benutzerData.getID()){
            liveData.setValue(benutzerData);
        }
        return liveData;
    }

    private void setBenutzer(){
        benutzerData = Paper.book().read("AktBenutzer", new Benutzer(0,"Default",""));
    }

    public void setBenutzer(Benutzer pBenutzer){
        Paper.book().write("AktBenutzer",pBenutzer);
        benutzerData = pBenutzer;
        liveData.postValue(pBenutzer);
    }

    public void getBenutzerFromDatabase(String pName, String pPassword) {
        if (benutzerData.getAPIKey().equals("")) {
            String APIResponse = "";
            try {
                String Request = "https://www.luenersv-judo.de/lsv_app/LoginReader.php?";
                String urlParameters = "name=" + pName + "&pass=" + pPassword;

                URL url = new URL(Request + urlParameters);

                URLConnection connection = url.openConnection();
                HttpsURLConnection http = (HttpsURLConnection) connection;

                // activate the output + input
                http.setDoOutput(true);
                http.setDoInput(true);
                http.setRequestMethod("GET");
                http.connect();
                //Outputstream flushen und schließßen
                http.getOutputStream().flush();
                http.getOutputStream().close();
                //Inputstream empfangen
                if (http.getResponseCode() == 200) {
                    String line;
                    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));

                    while ((line = in.readLine()) != null) {
                        APIResponse = line;
                    }
                    in.close();
                }
                http.disconnect();


            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!APIResponse.equals("")) {
                if (Benutzer.BenutzerFromString(APIResponse) != null) {
                    setBenutzer(Benutzer.BenutzerFromString(APIResponse));
                    liveData.postValue(Benutzer.BenutzerFromString(APIResponse));
                }
            }else{
                setBenutzer(benutzerData);
            }
        }
    }
}
