package com.nachrichten.lsv_judomvvm.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class News implements Parcelable, Comparable<News>{

    private int NID;
    private String News_Name;
    private String News_Text;
    private Benutzer User;

    public News(int id,String Name, String text, Benutzer benutzer){
        NID = id;
        News_Name = Name;
        News_Text = text;
        User = benutzer;
    }

    public static String buildNidString(ArrayList<News> pNewsList){
        StringBuilder NidString = new StringBuilder();
        for(int i = 0; i< pNewsList.size(); i++){
            if(i == pNewsList.size()-1){
                NidString.append(pNewsList.get(i).getNID());
            }else{
                NidString.append(pNewsList.get(i).getNID()).append(",");
            }
        }
        return NidString.toString();
    }

    public static void AddNewsToList(ArrayList<News> pNewsList, String pSubstring,boolean pShouldNotify){
        News Hilfsnews = News.NewsFromString(pSubstring);
        boolean Einfuegen = true;
        if(Hilfsnews != null){
            for(News pNews: pNewsList){
                if (pNews.getNID() == Hilfsnews.getNID()) {
                    Einfuegen = false;
                    break;
                }
            }
            if(Einfuegen){
                pNewsList.add(Hilfsnews);}
                if(pShouldNotify) {
                    NotifyUtility.getInstance().NotificationNewsAdded(Hilfsnews, pNewsList.size());
                    Log.d("News", "Neue News");
                }


        }
    }

    public static String NewsToString(News pNews){
        return "<tr>"+pNews.getNID()
                +"/"+pNews.getNews_Name()
                +"/"+pNews.getNews_Text()
                +"/"+pNews.getUser().getID()+"<tr/>";
    }

    public static News NewsFromString(String pDaten){
        int size = 4;
        String[] results = new String[size];
        String substr;
        for(int i = 0; i < size; i++){
            if(i == size -1){
                substr = pDaten;
            }else
                substr = pDaten.substring(0, pDaten.indexOf("/"));
            if(!substr.equals("")){
                results[i] = substr;
            }
            pDaten = pDaten.substring(pDaten.indexOf("/")+1);
        }
        if(results[0] != null && results[1] != null && results[2] != null && results[3] != null)
        return new News(Integer.parseInt(results[0]), results[1], results[2], new Benutzer(Integer.parseInt(results[3]),"füllung", ""));

        return null;
    }
    protected News(Parcel in){
        NID = in.readInt();
        News_Name = in.readString();
        News_Text = in.readString();
        User = (Benutzer) in.readValue(getClass().getClassLoader());
    }
    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };


    public String getNews_Text() {
        return News_Text;
    }

    public void setNews_Text(String news_Text) {
        News_Text = news_Text;
    }

    public String getNews_Name() {
        return News_Name;
    }

    public int getNID(){
        return NID;
    }

    public void setNID(int id){
        NID = id;
    }

    public void setNews_Name(String news_Name) {
        News_Name = news_Name;
    }

    public Benutzer getUser(){
        return User;
    }

    public void setUser(Benutzer pBenutzer){
        User = pBenutzer;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(NID);
        parcel.writeString(News_Name);
        parcel.writeString(News_Text);
        parcel.writeValue(User);

    }

    @Override
    public int compareTo(News news) {
            if(this.getNID() > news.getNID())
                return -1;
            if (this.getNID() < news.getNID())
                return 1;
            else
                return 0;

    }
}
