package Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentplanner.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Activities.CourseActivity;
import Controllers.DateManager;
import Database.Course;
import Database.CourseDao;
import Database.StudentDatabase;

public class DetailedCourseFragment extends Fragment {
    //values for fragment
    int id;
    String idString, title, status, start, end, name, number, email, notes;
    ArrayList<String> assignedList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //catch bundle with detailed course information
        Bundle bundle = this.getArguments();
        //assign values
        if(getArguments() != null) {
            id = bundle.getInt("id");
            idString = String.valueOf(id);
            title = bundle.getString("title");
            status = bundle.getString("status");
            start = bundle.getString("startDate");
            end = bundle.getString("endDate");
            name = bundle.getString("name");
            number = bundle.getString("number");
            email = bundle.getString("email");
            notes = bundle.getString("notes");
            if(bundle.getStringArrayList("ASSIGNED") != null) {
                assignedList = bundle.getStringArrayList("ASSIGNED");
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailed_course, container, false);

        if(getArguments() != null) {
            //find target views
            TextView idView = v.findViewById(R.id.id_view);
            TextView titleView = v.findViewById(R.id.title_view);
            TextView statusView = v.findViewById(R.id.status_view);
            TextView startView = v.findViewById(R.id.start_view);
            TextView endView = v.findViewById(R.id.end_view);
            TextView nameView = v.findViewById(R.id.name_view);
            TextView numberView = v.findViewById(R.id.number_view);
            TextView emailView = v.findViewById(R.id.email_view);
            TextView notesView = v.findViewById(R.id.note_view);
            Spinner spinner = v.findViewById(R.id.assigned_spinner);

            //assign values to targets
            idView.setText(idString);
            titleView.setText(title);
            statusView.setText(status);
            startView.setText(DateManager.formatDate(start));
            endView.setText(DateManager.formatDate(end));
            nameView.setText(name);
            numberView.setText(number);
            emailView.setText(email);
            if(notes != null) {
                notesView.setText(notes);
            }

            //create adapter for spinner
            ArrayAdapter<String> assignedAdapter = new ArrayAdapter<String>(v.getContext(),
                                    android.R.layout.simple_spinner_item);
            assignedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(assignedAdapter);

            if(assignedList != null) {
                for (int i = 0; i < assignedList.size(); i++) {
                    assignedAdapter.add(assignedList.get(i));
                    assignedAdapter.notifyDataSetChanged();

                }
            }
            else {
                assignedAdapter.add("No Assigned Assessments");
                assignedAdapter.notifyDataSetChanged();
            }

        }
        return v;
    }
















}