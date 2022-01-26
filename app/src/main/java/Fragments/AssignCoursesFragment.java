package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentplanner.R;

import java.util.ArrayList;

import Database.CourseAssessmentLink;
import Database.CourseAssessmentLinkDao;
import Database.StudentDatabase;
import Database.TermCourseLink;
import Database.TermCourseLinkDao;

public class AssignCoursesFragment extends Fragment {

    static int termID;
    static int courseID;
    TermCourseLink termCourseLink;
    Handler mainHandler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_assign_courses, container, false);

        Spinner assignedSpinner = v.findViewById(R.id.assigned_spinner);
        Spinner assignedSpinnerID = v.findViewById(R.id.assigned_id_spinner);
        Spinner unassignedSpinner = v.findViewById(R.id.unassigned_spinner);
        Spinner unassignedSpinnerID = v.findViewById(R.id.unassigned_id_spinner);

        ArrayList<Integer> bundleAssignedID = new ArrayList<>();
        ArrayList<String> bundleAssignedTitle = new ArrayList<>();
        ArrayList<Integer> bundleUnAssignedID = new ArrayList<>();
        ArrayList<String> bundleUnAssignedTitle = new ArrayList<>();

        //get bundle
        Bundle bundle = this.getArguments();

        //populate term values
        TextView termTitle = v.findViewById(R.id.term_title_view);
        TextView termID = v.findViewById(R.id.term_id_view);
        if (bundle != null) {
            termTitle.setText(bundle.getString("TITLE"));
            termID.setText(bundle.getString("ID"));
        }

        AssignCoursesFragment.termID = Integer.parseInt(bundle.getString("ID"));

        //set adapters for spinners
        ArrayAdapter<Integer> assignedIDAdapter = new ArrayAdapter<Integer>(v.getContext(),
                android.R.layout.simple_spinner_item);
        assignedIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignedSpinnerID.setAdapter(assignedIDAdapter);
        if (bundle.getIntegerArrayList("ASSIGNED_ID_LIST") != null) {
            bundleAssignedID = bundle.getIntegerArrayList("ASSIGNED_ID_LIST");
            for (int i = 0; i < bundleAssignedID.size(); i++) {
                assignedIDAdapter.add(bundleAssignedID.get(i));
            }
        }

        ArrayAdapter<String> assignedTitleAdapter = new ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_spinner_item);
        assignedIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignedSpinner.setAdapter(assignedTitleAdapter);

        if (bundle.getStringArrayList("ASSIGNED_TITLE_LIST") != null) {
            bundleAssignedTitle = bundle.getStringArrayList("ASSIGNED_TITLE_LIST");

            for (int i = 0; i < bundleAssignedTitle.size(); i++) {
                assignedTitleAdapter.add(bundleAssignedTitle.get(i));
                assignedTitleAdapter.notifyDataSetChanged();
            }
        }

        ArrayAdapter<Integer> unassignedIDAdapter = new ArrayAdapter<Integer>(v.getContext(),
                android.R.layout.simple_spinner_item);
        unassignedIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unassignedSpinnerID.setAdapter(unassignedIDAdapter);
        if (bundle.getIntegerArrayList("UNASSIGNED_ID_LIST") != null) {
            bundleUnAssignedID = bundle.getIntegerArrayList("UNASSIGNED_ID_LIST");
            for (int i = 0; i < bundleUnAssignedID.size(); i++) {
                unassignedIDAdapter.add(bundleUnAssignedID.get(i));
            }
        }

        ArrayAdapter<String> unassignedTitleAdapter = new ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_spinner_item);
        unassignedIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unassignedSpinner.setAdapter(unassignedTitleAdapter);

        if (bundle.getStringArrayList("UNASSIGNED_TITLE_LIST") != null) {
            bundleUnAssignedTitle = bundle.getStringArrayList("UNASSIGNED_TITLE_LIST");

            for (int i = 0; i < bundleUnAssignedTitle.size(); i++) {
                unassignedTitleAdapter.add(bundleUnAssignedTitle.get(i));
                unassignedTitleAdapter.notifyDataSetChanged();
            }
        }

        //set spinner listeners
        assignedSpinnerID.setEnabled(false);
        unassignedSpinnerID.setEnabled(false);

        assignedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedPosition = assignedSpinner.getSelectedItemPosition();
                assignedSpinnerID.setSelection(selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        unassignedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedPosition = unassignedSpinner.getSelectedItemPosition();
                unassignedSpinnerID.setSelection(selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //set button listeners
        Button assignButton = v.findViewById(R.id.bt_assign);
        Button removeButton = v.findViewById(R.id.bt_remove);


        TermCourseLinkDao linkDao = StudentDatabase.getInstance(v.getContext()).termCourseLinkDao();

        //assign course button
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //perform insert query
                termCourseLink = new TermCourseLink(AssignCoursesFragment.termID, Integer.parseInt(unassignedSpinnerID.getSelectedItem().toString()));
                InsertRunnable insertRunnable = new InsertRunnable();
                new Thread(insertRunnable).start();

                //perform UI changes
                //swap items to assign a course
                assignedIDAdapter.add(Integer.parseInt(unassignedSpinnerID.getSelectedItem().toString()));
                assignedIDAdapter.notifyDataSetChanged();
                assignedTitleAdapter.add(unassignedSpinner.getSelectedItem().toString());
                assignedTitleAdapter.notifyDataSetChanged();
                //remove items from unassigned adapter
                unassignedIDAdapter.remove(Integer.parseInt(unassignedSpinnerID.getSelectedItem().toString()));
                unassignedTitleAdapter.remove(unassignedSpinner.getSelectedItem().toString());
            }
        });

        //remove courses/unassign courses button
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //perform delete query
                //get course ID
                courseID = Integer.parseInt(assignedSpinnerID.getSelectedItem().toString());
                DeleteRunnable deleteRunnable = new DeleteRunnable();
                new Thread(deleteRunnable).start();

                //perform UI changes
                //swap items to unassign a course
                unassignedIDAdapter.add(Integer.parseInt(assignedSpinnerID.getSelectedItem().toString()));
                unassignedIDAdapter.notifyDataSetChanged();
                unassignedTitleAdapter.add(assignedSpinner.getSelectedItem().toString());
                unassignedTitleAdapter.notifyDataSetChanged();
                //remove items from assigned adapter
                assignedIDAdapter.remove(Integer.parseInt(assignedSpinnerID.getSelectedItem().toString()));
                assignedTitleAdapter.remove(assignedSpinner.getSelectedItem().toString());
            }
        });

        return v;
    }

    //delete an assignment record
    class DeleteRunnable implements Runnable {

        @Override
        public void run() {
            TermCourseLinkDao linkDao = StudentDatabase.getInstance(getActivity().getApplicationContext()).termCourseLinkDao();
            linkDao.deleteByID(termID, courseID);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), "Course Unassigned", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //insert an assignment record
    class InsertRunnable implements Runnable {

        @Override
        public void run() {
            TermCourseLinkDao linkDao = StudentDatabase.getInstance(getActivity().getApplicationContext()).termCourseLinkDao();
            linkDao.insertNewCourse(termCourseLink);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), "Course Assigned", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}