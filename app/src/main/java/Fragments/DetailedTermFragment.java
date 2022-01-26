package Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.studentplanner.R;

import java.util.ArrayList;

import Controllers.DateManager;


public class DetailedTermFragment extends Fragment {

    //values for fragment
    int id;
    String idString, title, start, end;
    ArrayList<String> assignedList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //catch bundle with detailed term information
        Bundle bundle = this.getArguments();
        //assign values
        if(getArguments() != null) {
            id = bundle.getInt("ID");
            idString = String.valueOf(id);
            title = bundle.getString("TITLE");
            start = bundle.getString("START_DATE");
            end = bundle.getString("END_DATE");

            if(bundle.getStringArrayList("ASSIGNED") != null) {
                assignedList = bundle.getStringArrayList("ASSIGNED");
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        View v = inflater.inflate(R.layout.fragment_detailed_term, container, false);
        if(getArguments() != null) {
            //find target views
            TextView idView = v.findViewById(R.id.id_view);
            TextView titleView = v.findViewById(R.id.title_view);
            TextView startView = v.findViewById(R.id.start_view);
            TextView endView = v.findViewById(R.id.end_view);
            Spinner spinner = v.findViewById(R.id.assigned_spinner);


            //assign values to targets
            idView.setText(idString);
            titleView.setText(title);
            startView.setText(DateManager.formatDate(start));
            endView.setText(DateManager.formatDate(end));

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






















