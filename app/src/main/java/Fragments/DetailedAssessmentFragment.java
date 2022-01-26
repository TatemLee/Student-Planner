package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.studentplanner.R;

import Activities.CourseActivity;
import Controllers.DateManager;

public class DetailedAssessmentFragment extends Fragment {
    //values for fragment
    int id;
    String idString, title, start, end, type;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //catch bundle with detailed assessment information
        Bundle bundle = this.getArguments();
        //assign values
        if(getArguments() != null) {
            id = bundle.getInt("ID");
            idString = String.valueOf(id);
            title = bundle.getString("TITLE");
            start = bundle.getString("START_DATE");
            end = bundle.getString("END_DATE");
            type = bundle.getString("TYPE");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailed_assessment, container, false);
        if(getArguments() != null) {
            //find target views
            TextView idView = v.findViewById(R.id.id_view);
            TextView titleView = v.findViewById(R.id.title_view);
            TextView startView = v.findViewById(R.id.start_view);
            TextView endView = v.findViewById(R.id.end_view);
            TextView typeView = v.findViewById(R.id.type_view);


            //assign values to targets
            idView.setText(idString);
            titleView.setText(title);
            startView.setText(DateManager.formatDate(start));
            endView.setText(DateManager.formatDate(end));
            typeView.setText(type);
        }
        return v;
    }
}