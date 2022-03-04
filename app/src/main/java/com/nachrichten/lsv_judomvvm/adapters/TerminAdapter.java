package com.nachrichten.lsv_judomvvm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nachrichten.lsv_judomvvm.R;
import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.Termin;

import java.util.ArrayList;
import java.util.List;

public class TerminAdapter extends RecyclerView.Adapter<TerminAdapter.TerminViewHolder>{
    private ArrayList<Termin> mTerminList;
    private static Benutzer Abenutzer;
    private OnItemClickListener mListener;
    private final RecyclerViewClickListener Listener;
    public static final int lila = -8388480;
    public static final int gelb = -256;
    public static final int gruen = -11751600;
    public static final int rot = -776664;
    public static final int schwarz = -16777216;
    public static final int tuerkis = -11490894;
    public static final int blau = -10286876;
    public static final int orange = -30720;




    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }
    public interface RecyclerViewClickListener{
        void onClick(View v,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener= listener;
    }

    public class TerminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitel;
        public TextView mBeschreibung;
        public ImageView mDeleteImage;
        public RelativeLayout mRelLay;

        public TerminViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTitel = itemView.findViewById(R.id.Titel_Termin);
            mBeschreibung = itemView.findViewById(R.id.Info_Termin);
            mDeleteImage = itemView.findViewById(R.id.Termin_delete);
            mRelLay = itemView.findViewById(R.id.RelativeLayout_exampleTermin);
            if (Abenutzer != null && !Abenutzer.getAPIKey().equals("")) {
                mDeleteImage.setVisibility(View.VISIBLE);
            } else
                mDeleteImage.setVisibility(View.INVISIBLE);


            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);


                        }
                    }
                }
            });

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Listener.onClick(view, getAdapterPosition());
        }
    }
    public TerminAdapter(ArrayList<Termin> terminList, RecyclerViewClickListener listener){
        mTerminList = terminList;
        this.Listener = listener;
    }

    public void setItems(List<Termin> newItems)
    {
        clearItems();
        addItems(newItems);
    }

    public void setmTerminList(ArrayList<Termin> liste){
        mTerminList = liste;
    }

    public ArrayList<Termin>getmTerminList(){
        return mTerminList;
    }

    public void addItem(Termin item, int position)
    {
        if (position > mTerminList.size()) return;

        mTerminList.add(item);
        notifyItemInserted(position);
    }

    public void addMoreItems(List<Termin> newItems, int position)
    {

        newItems.addAll(newItems);
        notifyItemChanged(position, newItems);
    }

    public void addItems(List<Termin> newItems)
    {
        mTerminList.addAll(newItems);
        notifyDataSetChanged();
    }

    public void clearItems()
    {
        mTerminList.clear();
        notifyDataSetChanged();
    }


    public void setABenutzer(Benutzer benutzer){
        Abenutzer = benutzer;
    }

    public Benutzer getABenutzer(){
        return Abenutzer;
    }

    public void addLoader()
    {
        mTerminList.add(null);
        notifyItemInserted(mTerminList.size() - 1);
    }


    public void removeLoader()
    {
        mTerminList.remove(mTerminList.size() - 1);
        notifyItemRemoved(mTerminList.size());
    }

    public void removeItem(int position)
    {
        if (position >= mTerminList.size()) return;

        mTerminList.remove(position);
        notifyItemRemoved(position);
    }

    public Termin getItem(int position){
        return mTerminList.get(position);
    }

    public void swapItems(int positionA, int positionB)
    {
        if (positionA > mTerminList.size()) return;
        if (positionB > mTerminList.size()) return;

        Termin firstItem = mTerminList.get(positionA);

        mTerminList.set(positionA, mTerminList.get(positionB));
        mTerminList.set(positionB, firstItem);

        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public TerminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.termin_example, parent, false);
        TerminViewHolder nvh = new TerminViewHolder(v,mListener);
        return nvh;
    }


    @Override
    public void onBindViewHolder(@NonNull TerminViewHolder holder, int position) {
        Termin currentItem =  mTerminList.get(position);
        holder.mTitel.setText(currentItem.getEventName());
        holder.mBeschreibung.setText(currentItem.getBeschreibung());
        switch(currentItem.getFarbe()){
            case lila:
                holder.mRelLay.setBackgroundResource(R.drawable.ic_turniere_grafik);
                    break;
            case gelb:
                holder.mRelLay.setBackgroundResource(R.drawable.ic_meisterschaft_grafik);
                    break;
            case gruen:
                holder.mRelLay.setBackgroundResource(R.drawable.ic_nwdk_grafik);
                    break;
            case rot:
                holder.mRelLay.setBackgroundResource(R.drawable.ic_nwjv_grafik);
                    break;
            case schwarz:
                holder.mRelLay.setBackgroundResource(R.drawable.ic_djb_grafik);
                    break;
            case tuerkis:
                holder.mRelLay.setBackgroundResource(R.drawable.ic_lehrgang_grafik);
                    break;
            case blau:
                holder.mRelLay.setBackgroundResource(R.drawable.ic_vorstand_grafik);
                    break;
            default:
                    holder.mRelLay.setBackgroundResource(R.drawable.ic_veranstaltung_grafik);

        }
    }

    @Override
    public int getItemCount() {
        return mTerminList.size();
    }

}


