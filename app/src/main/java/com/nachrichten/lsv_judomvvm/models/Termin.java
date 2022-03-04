package com.nachrichten.lsv_judomvvm.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Termin implements Parcelable , Comparable<Termin>
{
    private int Eid;
    private String EventName;
    private String Beschreibung;
    private int Farbe;
    private long Datum;
    private Benutzer Ersteller;


    public Termin(int pEid, String PEventName, String PBeschreibung, int PFarbe, long PDatum, Benutzer pErsteller){
        Eid = pEid;
        EventName = PEventName;
        Beschreibung = PBeschreibung;
        Farbe = PFarbe;
        Datum = PDatum;
        Ersteller = pErsteller;
    }


    protected Termin(Parcel in) {
        Eid = in.readInt();
        Ersteller = (Benutzer) in.readValue(getClass().getClassLoader());
        EventName = in.readString();
        Beschreibung = in.readString();
        Farbe = in.readInt();
        Datum = in.readLong();
    }

    public static String buildEidString(ArrayList<Termin> pTerminList){
        StringBuilder NidString = new StringBuilder();
        for(int i = 0; i< pTerminList.size(); i++){
            if(i == pTerminList.size()-1){
                NidString.append(pTerminList.get(i).getEid());
            }else{
                NidString.append(pTerminList.get(i).getEid()).append(",");
            }
        }
        return NidString.toString();
    }

    public static void AddTerminToList(ArrayList<Termin> pTerminList, String pSubstring, boolean pShouldNotify){
        Termin HilfsTermin = Termin.TerminFromString(pSubstring);
        boolean Einfuegen = true;
        if(HilfsTermin != null){
            for(Termin pTermin: pTerminList){
                if (pTermin.getEid() == HilfsTermin.getEid()) {
                    Einfuegen = false;
                    break;
                }
            }
            if(Einfuegen){
                pTerminList.add(HilfsTermin);
                if(pShouldNotify){
                    NotifyUtility.getInstance().NotificationTerminAdded(HilfsTermin,pTerminList.size());NotifyUtility.getInstance().NotificationTerminAdded(HilfsTermin,pTerminList.size());
                    Log.d("Termin", "Neuer Termin");
                }

            }
        }
    }

    public static Termin TerminFromString(String pDaten){
        int size = 6;
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
        boolean isArraySet =
                results[0] != null
                && results[1] != null
                && results[2] != null
                && results[3] != null
                && results[5] != null;
        if(isArraySet){
          return new Termin(Integer.parseInt(results[0]),results[2],results[4],Integer.parseInt(results[3]),Long.parseLong(results[1]),new Benutzer(Integer.parseInt(results[5]),"fill",""));
        }
        return null;
    }

    public static String TerminToString(Termin pTermin){
        return "<tr>"+pTermin.getEid()
                +"/"+pTermin.getEventName()
                +"/"+pTermin.getBeschreibung()
                +"/"+pTermin.getFarbe()
                +"/"+pTermin.getDatum()
                +"/"+pTermin.getErsteller()+"<tr/>";
    }



    public static final Creator<Termin> CREATOR = new Creator<Termin>() {
        @Override
        public Termin createFromParcel(Parcel in) {
            return new Termin(in);
        }

        @Override
        public Termin[] newArray(int size) {
            return new Termin[size];
        }
    };

    public String getEventName() {
        return EventName;
    }

    public String getBeschreibung() {
        return Beschreibung;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public void setBeschreibung(String beschreibung) {
        Beschreibung = beschreibung;
    }

    public int getFarbe() {
        return Farbe;
    }

    public void setFarbe(int farbe) {
        Farbe = farbe;
    }

    public long getDatum() {
        return Datum;
    }

    public void setDatum(long datum) {
        Datum = datum;
    }

    public int getEid(){
        return Eid;
    }

    public void setEid(int pEid){
        Eid = pEid;
    }

    public Benutzer getErsteller(){
        return Ersteller;
    }

    public void setErsteller(Benutzer pBenutzer){
        Ersteller = pBenutzer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Eid);
        parcel.writeValue(Ersteller);
        parcel.writeString(EventName);
        parcel.writeString(Beschreibung);
        parcel.writeInt(Farbe);
        parcel.writeLong(Datum);
    }

    @Override
    public int compareTo(Termin termin) {
        if(this.getDatum() < termin.getDatum())
            return -1;
        if (this.getDatum()> termin.getDatum())
            return 1;
        else
            return 0;
    }
}
