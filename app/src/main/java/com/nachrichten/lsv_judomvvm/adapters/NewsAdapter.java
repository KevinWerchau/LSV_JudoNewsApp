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
import com.nachrichten.lsv_judomvvm.models.News;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private ArrayList<News> mNewsList;
    private OnItemClickListener mListener;

    private static Benutzer ABenutzer;



    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener= listener;
    }
    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitel;
        public TextView mBeschreibung;
        public ImageView mDeleteImage;

        public NewsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTitel = itemView.findViewById(R.id.textView);
            mBeschreibung = itemView.findViewById(R.id.textView2);
            mDeleteImage = itemView.findViewById(R.id.image_delete);
                if (ABenutzer.getAPIKey().equals("")) {
                    mDeleteImage.setVisibility(View.INVISIBLE);
                } else
                    mDeleteImage.setVisibility(View.VISIBLE);



            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                            if(listener != null){
                                int position = getAdapterPosition();
                                if(position != RecyclerView.NO_POSITION){
                                    listener.onDeleteClick(position);
                                }
                            }
                        }
            });

        }

    }
    public NewsAdapter(ArrayList<News> newsList){
        mNewsList = newsList;
    }

    public void setmNewsList(ArrayList<News> liste){
        mNewsList = liste;
    }

    public void setABenutzer(Benutzer pBenutzer) {
        ABenutzer = pBenutzer;
    }

    public void removeItem(int position) {
        if (position >= mNewsList.size()) return;

        mNewsList.remove(position);
        notifyItemRemoved(position);
    }

    public News getItem(int position){
        return mNewsList.get(position);
    }


    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_example, parent, false);
        NewsViewHolder nvh = new NewsViewHolder(v, mListener);
        return nvh;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
    News currentItem =  mNewsList.get(position);
    holder.mTitel.setText(currentItem.getNews_Name());
    holder.mBeschreibung.setText(currentItem.getNews_Text());
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }
    public ArrayList<News> getmNewsList() {
        return mNewsList;
    }
}
