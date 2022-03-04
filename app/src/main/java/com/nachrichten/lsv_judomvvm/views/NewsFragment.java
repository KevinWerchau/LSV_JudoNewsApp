package com.nachrichten.lsv_judomvvm.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nachrichten.lsv_judomvvm.R;
import com.nachrichten.lsv_judomvvm.adapters.NewsAdapter;
import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.News;
import com.nachrichten.lsv_judomvvm.viewmodels.MainAcitivityViewModel;
import com.nachrichten.lsv_judomvvm.viewmodels.NewsFragmentViewModel;

import java.util.ArrayList;

public class NewsFragment extends Fragment {

    private View MainView;
    private MainAcitivityViewModel mainAcitivityViewModel;
    private NewsFragmentViewModel newsFragmentViewModel;

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        mainAcitivityViewModel = new ViewModelProvider(requireActivity()).get(MainAcitivityViewModel.class);
        newsFragmentViewModel = new ViewModelProvider(requireActivity()).get(NewsFragmentViewModel.class);
        mainAcitivityViewModel.init();
        newsFragmentViewModel.init();
        this.RecyclerViewStarten();
        this.Observer();
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        mainAcitivityViewModel.getBenutzer().removeObservers(getViewLifecycleOwner());
        newsFragmentViewModel.getNews().removeObservers(getViewLifecycleOwner());
        super.onDestroyView();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainView = inflater.inflate(R.layout.fragment_news,container,false);
        return MainView;
    }

    private void Observer(){
        newsFragmentViewModel.getNews().observe(getViewLifecycleOwner(), new Observer<ArrayList<News>>() {
            @Override
            public void onChanged(ArrayList<News> news) {
                mAdapter.setmNewsList(news);
                mAdapter.notifyDataSetChanged();
            }
        });
        mainAcitivityViewModel.getBenutzer().observe(getViewLifecycleOwner(), new Observer<Benutzer>() {
            @Override
            public void onChanged(Benutzer benutzer) {
                if(benutzer != null) {
                    mRecyclerView.removeAllViews();
                    mLayoutManager.removeAllViews();
                    RecyclerViewStarten();
                }
            }
        });
    }


    private void RecyclerViewStarten(){
        mRecyclerView = MainView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(MainView.getContext());
        mAdapter = new NewsAdapter(newsFragmentViewModel.getNews().getValue());
        mAdapter.setABenutzer(mainAcitivityViewModel.getBenutzer().getValue());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        this.setOnItemClickListener();
    }

    private void setOnItemClickListener() {
        mAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                    newsFragmentViewModel.removeItemFromDatabase(mAdapter.getItem(position), mainAcitivityViewModel.getBenutzer().getValue());
                    mAdapter.removeItem(position);
                    mAdapter.notifyItemRemoved(position);
            }
        });
    }
}
