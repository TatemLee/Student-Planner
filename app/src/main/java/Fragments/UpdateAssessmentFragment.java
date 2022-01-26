package Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;

import com.example.studentplanner.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import Controllers.DateManager;

public class UpdateAssessmentFragment extends Fragment {

    private UpdateAssessmentFragmentListener listener;
    //update assessment variables
    String updateTitle, updateStart, updateEnd, updateType;
    EditText editTitle;
    Switch typeSwitch;
    CalendarView calendarViewStart, calendarViewEnd;

    //interface for calendar view selection listener
    public interface UpdateAssessmentFragmentListener {
        void onUpdateSelectStartDate(String startDate);
        void onUpdateSelectEndDate(String endDate);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get update data
        Bundle bundle = this.getArguments();

        if(getArguments() != null) {
            updateTitle = bundle.getString("TITLE");
            updateStart = bundle.getString("START_DATE");
            updateEnd = bundle.getString("END_DATE");
            updateType = bundle.getString("TYPE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_update_assessment, container, false);
        //set listeners
        calendarViewStart = v.findViewById(R.id.calendarview_start);
        calendarViewEnd = v.findViewById(R.id.calendarview_end);

        calendarViewStart.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //get date info and format to mmddyyy
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
                updateStart = completeDate;
                listener.onUpdateSelectStartDate(completeDate);
            }
        });

        calendarViewEnd.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //get date info and format to mmddyyy
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
                updateEnd = completeDate;
                listener.onUpdateSelectEndDate(completeDate);
            }
        });

        //set values

        //calendars
        try {
            calendarViewStart.setDate(new SimpleDateFormat("MM/dd/yyyy").parse(DateManager.formatDate(updateStart)).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //calendars
        try {
            calendarViewEnd.setDate(new SimpleDateFormat("MM/dd/yyyy").parse(DateManager.formatDate(updateEnd)).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //edit title text view
        editTitle = v.findViewById(R.id.edit_title);
        editTitle.setText(updateTitle);

        //type switch value
        typeSwitch = v.findViewById(R.id.type_switch);
        if(updateType.contains(getString(R.string.Performance)))
            typeSwitch.setChecked(false);
        else
            typeSwitch.setChecked(true);



        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //catch values and send to outState
        if(updateStart != null)
            outState.putString("START", updateStart);
        if(updateEnd != null)
            outState.putString("END", updateEnd);
        if(typeSwitch.isChecked())
            outState.putString("TYPE", getString(R.string.Objective));
        else
            outState.putString("TYPE", getString(R.string.Performance));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.getString("START") != null) {
                try {
                    calendarViewStart.setDate(new SimpleDateFormat("MM/dd/yyyy").parse(DateManager.formatDate(savedInstanceState.getString("START"))).getTime());
                    //set up date for next orientation change, in case user makes no new selection
                    updateStart = savedInstanceState.getString("START");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("END") != null) {
                try {
                    calendarViewEnd.setDate(new SimpleDateFormat("MM/dd/yyyy").parse(DateManager.formatDate(savedInstanceState.getString("END"))).getTime());
                    //set up date for next orientation change, in case user makes no new selection
                    updateEnd = savedInstanceState.getString("END");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(savedInstanceState.getString("TYPE") != null) {
                String typeValue = savedInstanceState.getString("TYPE");
                if(typeValue.contains(getString(R.string.Performance)))
                    typeSwitch.setChecked(false);
                else
                    typeSwitch.setChecked(true);
            }

        }
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof UpdateAssessmentFragmentListener) {
            listener = (UpdateAssessmentFragmentListener) context;
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