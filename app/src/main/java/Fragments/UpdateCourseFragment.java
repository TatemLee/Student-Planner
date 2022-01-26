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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import Activities.CourseActivity;
import Adapters.CourseCardAdapter;
import Controllers.DateManager;
import Database.Course;
import Database.CourseDao;
import Database.StudentDatabase;


public class UpdateCourseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private UpdateCourseFragmentListener listener;
    //update course variables
    String updateTitle, updateStart, updateEnd, updateStatus;
    String updateName, updateNumber, updateEmail, updateNotes;
    EditText editTitle, editName, editNumber, editEmail, editNotes;
    Spinner statusSpinner;
    int spinnerPosition;
    CalendarView calendarViewStart, calendarViewEnd;



    //interface for calendar view selection listener
    public interface UpdateCourseFragmentListener {
        void onUpdateSelectStartDate(String startDate);
        void onUpdateSelectEndDate(String endDate);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        //get bundle values
        if(getArguments() != null) {
            updateTitle = bundle.getString("title");
            updateStatus = bundle.getString("status");
            updateStart = bundle.getString("startDate");
            updateEnd = bundle.getString("endDate");
            updateName = bundle.getString("name");
            updateNumber = bundle.getString("number");
            updateEmail = bundle.getString("email");
            updateNotes = bundle.getString("notes");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_update_course, container, false);

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
                updateStart = completeDate;
                listener.onUpdateSelectStartDate(completeDate);
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
                updateEnd = completeDate;
                listener.onUpdateSelectEndDate(completeDate);
            }
        });


        //set listener for notes character limit
        editNotes = v.findViewById(R.id.edit_note);
        editNotes.addTextChangedListener(new TextWatcher() {
            int characterCount;
            TextView countDisplay = v.findViewById(R.id.character_limit_view);
            String displayText;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                characterCount = editNotes.getText().toString().length();
                displayText = characterCount + "/85";
                countDisplay.setText(displayText);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        //status spinner
        for (int i = 0; i < 4; i++) {
            String comparisonValue;
            comparisonValue = statusSpinner.getItemAtPosition(i).toString();
            if(comparisonValue.compareTo(updateStatus) == 0) {
                statusSpinner.setSelection(i);
            }
        }

        //edit text views
        editTitle = v.findViewById(R.id.edit_title);
        editTitle.setText(updateTitle);
        editName = v.findViewById(R.id.edit_name);
        editName.setText(updateName);
        editNumber = v.findViewById(R.id.edit_phone_number);
        editNumber.setText(updateNumber);
        editEmail = v.findViewById(R.id.edit_email);
        editEmail.setText(updateEmail);
        if(updateNotes != null) {
            editNotes.setText(updateNotes);
        }

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

        if(statusSpinner.getSelectedItemPosition() != 0) {
            spinnerPosition = statusSpinner.getSelectedItemPosition();
            outState.putInt("STATUS", spinnerPosition);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null) {
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

            statusSpinner.setSelection(savedInstanceState.getInt("STATUS", 0));
        }
    }

    //methods for spinner listener in "UpdateCourseFragment"
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof UpdateCourseFragmentListener) {
            listener = (UpdateCourseFragmentListener) context;
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