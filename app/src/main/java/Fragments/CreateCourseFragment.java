package Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentplanner.R;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import Activities.CourseActivity;
import Adapters.CourseCardAdapter;
import Controllers.DateManager;
import Database.Course;
import Database.CourseDao;
import Database.StudentDatabase;


public class CreateCourseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private CourseFragmentListener listener;

    //user input containers for saved instance
    EditText title, name, number, email, notes;
    String startDate, endDate;
    Spinner statusSpinner;
    int spinnerPosition;
    CalendarView calendarViewStart, calendarViewEnd;



    //interface for calendar view selection listener
    public interface CourseFragmentListener {
        void onSelectStartDate(String startDate);
        void onSelectEndDate(String endDate);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_course, container, false);

        //set edit text values
        title = v.findViewById(R.id.edit_title);
        name = v.findViewById(R.id.edit_name);
        number = v.findViewById(R.id.edit_phone_number);
        email = v.findViewById(R.id.edit_email);
        notes = v.findViewById(R.id.edit_note);




        //set spinner values
        statusSpinner = v.findViewById(R.id.spinner_status);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(v.getContext(), R.array.course_status, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(arrayAdapter);
        statusSpinner.setOnItemSelectedListener(this);

        //set listeners
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

        //set listener for notes character limit
        notes.addTextChangedListener(new TextWatcher() {
            int characterCount;
            TextView countDisplay = v.findViewById(R.id.character_limit_view);
            String displayText;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                characterCount = notes.getText().toString().length();
                displayText = characterCount + "/85";
                countDisplay.setText(displayText);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }

    //handle orientation changes
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //catch values and send to outState
        if(title.getText() != null)
            outState.putString("TITLE", title.getText().toString());
        if(name.getText() != null)
            outState.putString("NAME", name.getText().toString());
        if(number.getText() != null)
            outState.putString("NUMBER", number.getText().toString());
        if(email.getText() != null)
            outState.putString("EMAIL", email.getText().toString());
        if(notes.getText() != null)
            outState.putString("NOTES", notes.getText().toString());
        if(startDate != null)
            outState.putString("START", startDate);
        if(endDate != null)
            outState.putString("END", endDate);
        if(statusSpinner.getSelectedItemPosition() != 0) {
            spinnerPosition = statusSpinner.getSelectedItemPosition();
            outState.putInt("STATUS", spinnerPosition);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            //if values have been passed, assign them
            title.setText(savedInstanceState.getString("TITLE"));
            name.setText(savedInstanceState.getString("NAME"));
            number.setText(savedInstanceState.getString("NUMBER"));
            email.setText(savedInstanceState.getString("EMAIL"));
            notes.setText(savedInstanceState.getString("NOTES"));

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

            statusSpinner.setSelection(savedInstanceState.getInt("STATUS", 0));
        }
    }


    //methods for spinner listener in "CreateCourseFragment"
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof CourseFragmentListener) {
            listener = (CourseFragmentListener) context;
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