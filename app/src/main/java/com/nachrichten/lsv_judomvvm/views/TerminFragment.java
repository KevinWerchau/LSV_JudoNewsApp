package com.nachrichten.lsv_judomvvm.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.nachrichten.lsv_judomvvm.R;
import com.nachrichten.lsv_judomvvm.adapters.TerminAdapter;
import com.nachrichten.lsv_judomvvm.models.Benutzer;
import com.nachrichten.lsv_judomvvm.models.NotifyUtility;
import com.nachrichten.lsv_judomvvm.models.Termin;
import com.nachrichten.lsv_judomvvm.viewmodels.MainAcitivityViewModel;
import com.nachrichten.lsv_judomvvm.viewmodels.TerminFragmentViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TerminFragment extends Fragment {

    private View MainView;
    private MainAcitivityViewModel mainAcitivityViewModel;
    private TerminFragmentViewModel terminFragmentViewModel;
    private TextView TermineInDiesemMonat;
    private CompactCalendarView compactCalendarView;
    private TerminAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TerminAdapter.RecyclerViewClickListener listener;
    private ImageView Iv_Colors;

    @Override
    public void onStart() {
        mainAcitivityViewModel = new ViewModelProvider(requireActivity()).get(MainAcitivityViewModel.class);
        mainAcitivityViewModel.init();
        terminFragmentViewModel = new ViewModelProvider(requireActivity()).get(TerminFragmentViewModel.class);
        terminFragmentViewModel.init();
        this.setTerminOnClickListener();
        this.GUIZuweisen();
        this.RecyclerViewStarten();
        this.Observer();
        this.IV_Colors_OnClickListener();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.setTextviewShaders();
        }
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setTextviewShaders() {
        Color PrimaryRed = Color.valueOf(getResources().getColor(R.color.PrimaryRed));
        Color Orange = Color.valueOf(getResources().getColor(R.color.Orange));
        Shader textShader = new LinearGradient(0, 0, 0, TermineInDiesemMonat.getLineHeight(), new int[]{PrimaryRed.toArgb(),Orange.toArgb(), PrimaryRed.toArgb()},new float[]{0,1,0 },Shader.TileMode.CLAMP);
        TermineInDiesemMonat.getPaint().setShader(textShader);
    }

    private void setTerminOnClickListener() {
        listener = (v, position) -> startClickedTerminActivity(mAdapter.getItem(position));
    }

    private void startClickedTerminActivity(Termin pTermin){
        Intent intent = new Intent(getActivity(), ClickedTerminActivity.class);
        intent.putExtra("Termin_Titel", pTermin.getEventName());
        intent.putExtra("Termin_Beschreibung", pTermin.getBeschreibung());
        intent.putExtra("Termin_Farbe", pTermin.getFarbe());
        intent.putExtra("Termin_Datum", pTermin.getDatum());
        getActivity().startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainView = inflater.inflate(R.layout.fragment_termin,container,false);
        NotifyUtility.getInstance().setContext(getContext());
        return MainView;
    }

    private void GUIZuweisen(){
        compactCalendarView = MainView.findViewById(R.id.compactCalendarView);
        TermineInDiesemMonat = MainView.findViewById(R.id.AllEvents);
        Iv_Colors = MainView.findViewById(R.id.IV_Color);
        this.KalenderInit();
    }
    private void KalenderInit() {
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        compactCalendarView.setCurrentDate(terminFragmentViewModel.getSelectedDate().getValue());
        this.KalenderListener();
    }
    private void KalenderListener(){
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked){
                if(compactCalendarView.getEvents(dateClicked).size() == 1){
                    ArrayList<Event> hilfslist = (ArrayList<Event>) compactCalendarView.getEvents(dateClicked);
                    Termin clickedTermin = (Termin) hilfslist.get(0).getData();
                    if(clickedTermin != null) {
                        startClickedTerminActivity(clickedTermin);
                }
                }
                if(compactCalendarView.getEvents(dateClicked).size() > 1){
                    Toast.makeText(getContext(), "Mehr als ein Termin an diesem Tag", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        terminFragmentViewModel.setSelectedDate(firstDayOfNewMonth);
                        ArrayList<Termin> termins = new ArrayList<>();
                        for (int i = 0; i < compactCalendarView.getEventsForMonth(firstDayOfNewMonth).size(); i++) {
                            termins.add(0,(Termin) compactCalendarView.getEventsForMonth(firstDayOfNewMonth).get(i).getData());
                        }
                        Collections.sort(termins);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.clearItems();
                                mAdapter.setmTerminList(termins);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                t.start();

            }
        });
    }

    private void Observer(){
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
        terminFragmentViewModel.getTerminList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Termin>>() {
            @Override
            public void onChanged(ArrayList<Termin> termine) {
                compactCalendarView.removeAllEvents();
                for (int i = 0; i < termine.size(); i++) {
                    Termin HilfsTermin = termine.get(i);
                    compactCalendarView.addEvent((new Event(HilfsTermin.getFarbe(), HilfsTermin.getDatum(), HilfsTermin)));
                }
                List<Event> CalendarList = compactCalendarView.getEventsForMonth(compactCalendarView.getFirstDayOfCurrentMonth());
                mAdapter.clearItems();
                for(int i = 0; i < CalendarList.size();i++){
                    mAdapter.addItem((Termin) CalendarList.get(i).getData(),0);
                    mAdapter.notifyItemInserted(0);
                }
            }
        });
    }

    private void RecyclerViewStarten() {
        mRecyclerView = MainView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(MainView.getContext());
        mAdapter = new TerminAdapter(new ArrayList<>(), listener);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setABenutzer(mainAcitivityViewModel.getBenutzer().getValue());
        for (int i = 0; i < compactCalendarView.getEventsForMonth(compactCalendarView.getFirstDayOfCurrentMonth()).size(); i++) {
            mAdapter.addItem((Termin) compactCalendarView.getEventsForMonth(compactCalendarView.getFirstDayOfCurrentMonth()).get(i).getData(),0);
            mAdapter.notifyItemInserted(0);
        }
        setOnItemClickListener();
    }

    private void setOnItemClickListener() {
        mAdapter.setOnItemClickListener(new TerminAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                terminFragmentViewModel.removeItemFromDatabase(mAdapter.getItem(position), mainAcitivityViewModel.getBenutzer().getValue());
                mAdapter.removeItem(position);
                mAdapter.notifyItemRemoved(position);
            }
        });
    }

    private void IV_Colors_OnClickListener() {
        Iv_Colors.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), FarbHilfeActivity.class);
            getActivity().startActivity(i);
        });
    }
}
