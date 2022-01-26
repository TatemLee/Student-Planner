package Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studentplanner.R;


public class CreateAssessmentFragment extends Fragment {

    private AssessmentFragmentListener listener;


    //user input containers for saved instance
    CalendarView  calendarViewStart, calendarViewEnd;
    String startDate, endDate;


    //interface for calendar view selection listener
    public interface AssessmentFragmentListener {
        void onSelectDate(String date);
        void onSelectEndDate(String endDate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_assessment, container, false);


        calendarViewStart = v.findViewById(R.id.calendarview_start);
        calendarViewEnd = v.findViewById(R.id.calendarview_end);

        //set listeners
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
                listener.onSelectDate(completeDate);
            }
        });

        calendarViewEnd.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //get date info and format to mmddyyyy
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


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //check if activity implements interface
        if(context instanceof AssessmentFragmentListener) {
            listener = (AssessmentFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + "implement AssessmentFragmentListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
