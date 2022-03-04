package com.nachrichten.lsv_judomvvm.repositories;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.News;
import com.nachrichten.lsv_judomvvm.models.NotifyUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import io.paperdb.Paper;

public class NewsRepository {

    private static NewsRepository instance;
    private ArrayList<News> dataSet;
    private ArrayList<News> oldDataSet;
    private MutableLiveData<ArrayList<News>> liveData = new MutableLiveData<>();;
    private boolean shouldNotify = true;


    private NewsRepository(){}
    public static NewsRepository getInstance(){
        if(instance == null){
            instance = new NewsRepository();

        }

        return instance;
    }

    public MutableLiveData<ArrayList<News>> getNews(){
        setNews();
        liveData.setValue(dataSet);
        return liveData;
    }

    private void setNews(){
        dataSet = Paper.book().read("NewsList",new ArrayList<>());
        Collections.sort(Objects.requireNonNull(dataSet));
    }
    public void setNews(ArrayList<News> pNews){
        Paper.book().write("NewsList",pNews);
        dataSet = pNews;
        ArrayList<News> newData = pNews;
        Collections.sort(newData);
        liveData.postValue(newData);
    }


    public void getNewsFromDatabase(){
        try {
            if(dataSet == null){
                setNews();
            }
            String Request = "https://luenersv-judo.de/lsv_app/NewsDatenbankAuslesen.php?";
            String urlParameters;
            urlParameters = "nstring=" + News.buildNidString(dataSet)+
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
                        News.AddNewsToList(dataSet,substr.substring(substr.indexOf("<tr>")+4,substr.indexOf("<tr/>")),shouldNotify);
                        substr = substr.substring(substr.indexOf("<tr/>") + 5);
                    }else {
                        substr = substr.substring(substr.indexOf("<tr>") + 4, substr.indexOf("<tr/>"));
                        News.AddNewsToList(dataSet, substr,shouldNotify);
                        shouldNotify = true;
                    }
                }
                if(oldSize != dataSet.size()){
                    setNews(dataSet);
                }
                if(APIResponse.contains("{1}")){
                    oldDataSet = dataSet;
                    if(dataSet.size() == 1){
                        NotifyUtility.getInstance().NotificationNewsDeleted(dataSet.get(0),1);
                        setNews(new ArrayList<>());
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
                                if(dataSet.get(i).getNID() == oldDataSet.get(j).getNID()){
                                    oldDataSet.remove(j);
                                }
                            }
                        }if(oldDataSet.size()> 0) {
                            for (int i = 0; i < oldDataSet.size(); i++) {
                                Log.d("Removed Item", News.NewsToString(oldDataSet.get(i)));
                                NotifyUtility.getInstance().NotificationNewsDeleted(oldDataSet.get(i),oldDataSet.size());
                            }
                        }
                        oldDataSet = new ArrayList<>();
                    }
            http.disconnect();
        } catch (IllegalStateException exception){
            exception.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeNewsFromDatabase(News pNews,String pAPIKey){
        try {
            shouldNotify =  false;
            String Request = "https://luenersv-judo.de/lsv_app/NewsLoeschen.php?";
            String urlParameters = "Nid=" + pNews.getNID() +
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
                    if (dataSet.get(i).getNID() == pNews.getNID()) {
                        dataSet.remove(i);
                    }
                }
            }
            setNews(dataSet);
        } catch (IOException ignored) {
        }
    }

    public void insertNewsIntoDatabase(News pNews, Benutzer pBenutzer) {
        try {
            shouldNotify =  false;
            String Request = "https://www.luenersv-judo.de/lsv_app/NewsHinzufuegen.php?";
            String urlParameters = "Titel=" + pNews.getNews_Name()
                    + "&Beschreibung="+ pNews.getNews_Text()
                    + "&Uid=" + pBenutzer.getID()
                    + "&APIKey="+ URLEncoder.encode(pBenutzer.getAPIKey());

            URL url = new URL(Request + urlParameters);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
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
