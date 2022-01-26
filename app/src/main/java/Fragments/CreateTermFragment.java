package Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;

import com.example.studentplanner.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import Controllers.DateManager;


public class CreateTermFragment extends Fragment {

    private TermFragmentListener listener;

    //user input containers for saved instance
    EditText title;
    CalendarView  calendarViewStart, calendarViewEnd;
    String startDate, endDate;


    //interface for calendar view selection listener
    public interface TermFragmentListener {
        void onSelectStartDate(String startDate);
        void onSelectEndDate(String endDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       View v = inflater.inflate(R.layout.fragment_create_term, container, false);

        //set calendar listeners
        calendarViewStart = v.findViewById(R.id.calendarview_start);
        calendarViewEnd = v.findViewById(R.id.calendarview_end);

        calendarViewStart.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //get dat info and format to mmddyyy
                String selectedDay = String.valueOf(dayOfMonth);
                if(selectedDay.length() == 1)
                    selectedDay = "0" + selectedDay;
                String selectedYear = String.valueOf(year);
                String selectedMonth = String.valueOf(month + 1);
                if(selectedMonth.length() == 1)
                    selectedMonth = "0" + selectedMonth;

                //compile values into one string
                String completeDate = selectedMonth + selectedDay + selectedYear;
                //send values to the implementing activity
                startDate = completeDate;
                listener.onSelectStartDate(completeDate);
            }
        });

        calendarViewEnd.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //get dat info and format to mmddyyy
                String selectedDay = String.valueOf(dayOfMonth);
                if(selectedDay.length() == 1)
                    selectedDay = "0" + selectedDay;
                String selectedYear = String.valueOf(year);
                String selectedMonth = String.valueOf(month + 1);
                if(selectedMonth.length() == 1)
                    selectedMonth = "0" + selectedMonth;

                //compile values into one string
                String completeDate = selectedMonth + selectedDay + selectedYear;
                //send values to the implementing activity
                endDate = completeDate;
                listener.onSelectEndDate(completeDate);
            }
        });

       return v;
    }

    //handle orientation changes
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(startDate != null)
            outState.putString("START", startDate);
        if(endDate != null)
            outState.putString("END", endDate);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("START") != null) {
                try {
                    calendarViewStart.setDate(new SimpleDateFormat("MM/dd/yyyy").parse(DateManager.formatDate(savedInstanceState.getString("START"))).getTime());
                    //set up date for next orientation change, in case user makes no new selection
                    startDate = savedInstanceState.getString("START");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("END") != null) {
                try {
                    calendarViewEnd.setDate(new SimpleDateFormat("MM/dd/yyyy").parse(DateManager.formatDate(savedInstanceState.getString("END"))).getTime());
                    //set up date for next orientation change, in case user makes no new selection
                    endDate = savedInstanceState.getString("END");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof TermFragmentListener) {
            listener = (TermFragmentListener) context;
        }
        else {
            //nothing
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }



}

























