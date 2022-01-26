package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Dao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.studentplanner.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Adapters.TermCardAdapter;
import Controllers.DateManager;
import Database.Assessment;
import Database.AssessmentDao;
import Database.Course;
import Database.CourseAssessmentLinkDao;
import Database.CourseDao;
import Database.StudentDatabase;
import Database.Term;
import Database.TermCourseLinkDao;
import Database.TermDao;
import Fragments.AssignAssessmentsFragment;
import Fragments.AssignCoursesFragment;
import Fragments.CreateCourseFragment;
import Fragments.CreateTermFragment;
import Fragments.DetailedCourseFragment;
import Fragments.DetailedTermFragment;
import Fragments.UpdateCourseFragment;
import Fragments.UpdateTermFragment;


public class TermActivity extends AppCompatActivity implements CreateTermFragment.TermFragmentListener, UpdateTermFragment.UpdateTermFragmentListener {

    //list of term titles
    List<String> title = new LinkedList<>();
    //list of term ids
    List<Integer> id = new LinkedList<>();
    //list of user selected Term entries
    public static ArrayList<Integer> selected = new ArrayList<>();
    //term variables
    String newTitle, newStart, newEnd;
    //update term variables
    String updateTitle, updateStart, updateEnd;
    //list of all courses in database
    List<Term> selectQueryResults = new LinkedList<>();
    //new term object
    Term insertTerm;
    static Term selectedTerm;
    //term to be updated
    static Term updateTerm;
    //get work to main thread
    Handler mainHandler = new Handler();
    //fragment to fill scroll view
    Fragment fragment;

    //other
    BottomNavigationView navBar;
    TermCardAdapter adapter;
    Toolbar toolbar;
    static boolean safeToDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        //scroll view that gets filled with various fragments
        ScrollView scrollView = findViewById(R.id.scroll_view);

        //create toolbar functionality
        toolbar =  findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //launch create term fragment
                if(item.getItemId() == findViewById(R.id.add_action).getId()) {
                    //launch fragment
                    setCreateTermFragment();
                }
                //launch term info fragment
                else if(item.getItemId() == findViewById(R.id.info_action).getId()) {
                    //launch fragment
                    setInfoFragment();
                }
                //launch term assignment fragment
                else if(item.getItemId() == findViewById(R.id.course_action).getId()) {
                    //launch fragment
                    setAssignFragment();
                }
                //delete all selected terms
                else if(item.getItemId() == findViewById(R.id.delete_action).getId()) {
                    //check that at least one selection has been made
                    if(selected.size() < 1) {
                        Toast.makeText(getApplicationContext(), "Select Item(s) to Delete", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    //check if courses are assigned to any selected term
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TermCourseLinkDao linkDao = StudentDatabase.getInstance(getApplicationContext()).termCourseLinkDao();
                            safeToDelete = true;
                            ArrayList<Integer> listOfCourse = new ArrayList<>();
                            for (int i = 0; i < selected.size(); i++) {
                                //get list of associated courses
                                listOfCourse.addAll(linkDao.getAssignedCourses(selected.get(i)));
                                //if there are courses
                                if(listOfCourse.size() > 0) {
                                         mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                new AlertDialog.Builder(toolbar.getContext())
                                                        .setTitle("Unable to Complete Request")
                                                        .setMessage("One or more of the selected Terms has Courses assigned to it. " +
                                                             "Please Un-Assign all Courses in a term to delete them")
                                                        //button to close dialog
                                                        .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.cancel();
                                                             }
                                                        }).setIcon(android.R.drawable.ic_dialog_info).show();
                                            }
                                    });
                                    //notify app that it is not safe to delete selected terms
                                    safeToDelete = false;
                                    break;
                                }

                                }
                            //if there are not assigned courses among selected terms
                            if(safeToDelete) {
                                //ask user to confirm delete action
                                new AlertDialog.Builder(toolbar.getContext())
                                        .setTitle("Delete?")
                                        .setMessage("Are you sure you want to delete all selected entries?")
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                DeleteRunnable deleteRunnable = new DeleteRunnable();
                                                new Thread(deleteRunnable).start();
                                                dialogInterface.cancel();
                                            }
                                        })
                                        //set cancel button
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
                            }
                        }
                    });
                }
                //reset default;
                safeToDelete = true;
                return true;
            }
        });


        //Nav bar assignment
        navBar = findViewById(R.id.bottom_nav_bar);

        //Set selected Nav Bar item
        navBar.setSelectedItemId(R.id.nav_term);

        //create nav bar listener
        navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_home:
                        //Launch HomeActivity
                        launchHome();
                        return true;
                    case R.id.nav_term:
                        //Launch TermActivity
                        launchTerms();
                        return true;
                    case R.id.nav_course:
                        //Launch CourseActivity
                        launchCourses();
                        return true;
                    case R.id.nav_assessments:
                        //Launch AssessmentsActivity
                        launchAssessments();
                        return true;


                }
                return true;
            }
        });

        //populate term cards
        RecyclerView recyclerView = findViewById(R.id.term_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TermCardAdapter(id, title);
        recyclerView.setAdapter(adapter);

        //populate list
        SelectRunnable selectRunnable = new SelectRunnable();
        new Thread(selectRunnable).start();

        //clear all user selections
        selected.clear();

        //HANDLE FRAGMENTS DURING ORIENTATION CHANGES
        //if the recycler_view didn't exist on orientation change, clear it out
        if(savedInstanceState != null) {
            if (!savedInstanceState.getBoolean("IS_DEFAULT", true)) {
                scrollView.removeView(findViewById(R.id.term_recyclerview));
                //populate selected values
                if(savedInstanceState.getIntegerArrayList("SELECTED") != null)
                    selected.addAll(savedInstanceState.getIntegerArrayList("SELECTED"));
                //disable icons to avoid fragment conflict
                toolbar.findViewById(R.id.add_action).setEnabled(false);
                toolbar.findViewById(R.id.info_action).setEnabled(false);
                toolbar.findViewById(R.id.course_action).setEnabled(false);
                toolbar.findViewById(R.id.delete_action).setEnabled(false);
            }
        }
    }


    //HANDLE FRAGMENTS DURING ORIENTATION CHANGES
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //check if Scrollview contains recycle_view
        if(findViewById(R.id.term_recyclerview) == null) {
            outState.putBoolean("IS_DEFAULT", false);
            if(selected.size() != 0) {
                ArrayList<Integer> outStateSelected = new ArrayList<>();
                outStateSelected.addAll(selected);
                outState.putIntegerArrayList("SELECTED", outStateSelected);
            }

        }
    }

    //reload page on back press

    @Override
    public void onBackPressed() {
       launchTerms();
    }


    //ACTIVITY LAUNCHERS

    //launch Term Activity
    public void launchTerms() {
        Intent i = new Intent(this, TermActivity.class);
        startActivity(i);
    }

    public void launchTermsButton(View v) {
        Intent i = new Intent(this, TermActivity.class);
        startActivity(i);
    }

    //launch Home Activity
    public void launchHome() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    //launch Course Activity
    public void launchCourses() {
        Intent i = new Intent(this, CourseActivity.class);
        startActivity(i);
    }
    //launch Assessment Activity
    public void launchAssessments() {
        Intent i = new Intent(this, AssessmentActivity.class);
        startActivity(i);
    }

    //launch create term fragment
    public void setCreateTermFragment() {
        //clear user selections
        selected.clear();
        //find destination scroll view
        ScrollView scrollview_destination = findViewById(R.id.scroll_view);
        try {
            scrollview_destination.removeAllViews();
        }
        catch (Exception e) {
            //nothing
        }
        Fragment fragment = new CreateTermFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.course_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }



    //launch detailed term view fragment
    public void setInfoFragment() {
        //check if list of selected items is null/empty
        if(selected == null || selected.size() == 0) {
            Toast.makeText(this, "Please Make a Selection", Toast.LENGTH_SHORT).show();
            return;
        }
        //check if list contains more than one item
        if(selected.size() > 1) {
            Toast.makeText(this, "Select ONLY ONE item", Toast.LENGTH_SHORT).show();
            return;
        }

        ScrollView scrollview_replace = findViewById(R.id.scroll_view);
        try {
            scrollview_replace.removeAllViews();
        }
        catch (Exception e) {
            //nothing
        }
        fragment = new DetailedTermFragment();


        //fetch data and open page
        SelectByIdRunnable selectByIdRunnable = new SelectByIdRunnable();
        new Thread(selectByIdRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.course_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }

    //launch fragment for editing assigned courses
    public void setAssignFragment() {
        if(selected == null || selected.size() == 0) {
            Toast.makeText(this, "Please Make a Selection", Toast.LENGTH_SHORT).show();
            return;
        }
        //check if list contains more than one item
        if(selected.size() > 1) {
            Toast.makeText(this, "Select ONLY ONE item", Toast.LENGTH_SHORT).show();
            return;
        }
        ScrollView scrollview_replace = findViewById(R.id.scroll_view);
        try {
            scrollview_replace.removeAllViews();
        }
        catch (Exception e) {
            //nothing
        }

        fragment = new AssignCoursesFragment();

        AssignedCourseRunnable assignedCourseRunnable = new AssignedCourseRunnable();
        new Thread(assignedCourseRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.course_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }

    //launch update term fragment
    public void setUpdateFragment(View v) {
        if(selected == null || selected.size() == 0) {
            Toast.makeText(this, "Please Make a Selection", Toast.LENGTH_SHORT).show();
            return;
        }
        //check if list contains more than one item
        if(selected.size() > 1) {
            Toast.makeText(this, "Select ONLY ONE item", Toast.LENGTH_SHORT).show();
            return;
        }
        //prepare destination
        ScrollView scrollview_destination = findViewById(R.id.scroll_view);
        try {
            scrollview_destination.removeAllViews();
        }
        catch (Exception e) {
            //nothing
        }
        fragment = new UpdateTermFragment();

        //fetch data and open page
        SelectForUpdateRunnable selectForUpdateRunnable = new SelectForUpdateRunnable();
        new Thread(selectForUpdateRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.course_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }

    //create new term
    public void onClickEnter(View v) {

        EditText editTitle = findViewById(R.id.edit_title);
        CalendarView calendarViewStart = findViewById(R.id.calendarview_start);
        CalendarView calendarViewEnd = findViewById(R.id.calendarview_end);

        //check for null value
        if(!isEmpty(editTitle)) {
            newTitle = editTitle.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Enter a Title", Toast.LENGTH_LONG).show();
            return;
        }

        //if either startDate or endDate have not been selected, assign current date
        if(newStart == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedStartDate = sdf.format(calendarViewStart.getDate());
            newStart = DateManager.deFormatDate(formattedStartDate);
        }
        if(newEnd == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedEndDate = sdf.format(calendarViewEnd.getDate());
            newEnd = DateManager.deFormatDate(formattedEndDate);
        }

        //check that startDate is either equal to or before endDate
        if(!DateManager.isStartBeforeEnd(newStart, newEnd)) {
            Toast.makeText(v.getContext(), "Start Date cannot be after End Date", Toast.LENGTH_LONG).show();
            return;
        }

        //create new term object
        insertTerm = new Term(newTitle, newStart, newEnd);

        //insert term object
        InsertRunnable insertRunnable = new InsertRunnable();
        new Thread(insertRunnable).start();

        //refresh page
        launchTerms();
    }

    //update term
    public void onClickUpdate(View v) {

        //compile all course values into updated course
        EditText editTitle = findViewById(R.id.edit_title);
        CalendarView calendarViewStart = findViewById(R.id.calendarview_start);
        CalendarView calendarViewEnd = findViewById(R.id.calendarview_end);

        //check for null values and assign values not handled by listeners
        if(!isEmpty(editTitle)) {
            updateTitle = editTitle.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Complete All Fields", Toast.LENGTH_LONG).show();
            return;
        }

        //if either startDate or endDate have not been selected, assign current date
        if(updateStart == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedEndDate = sdf.format(calendarViewStart.getDate());
            updateStart = DateManager.deFormatDate(formattedEndDate);
        }
        if(updateEnd == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedEndDate = sdf.format(calendarViewEnd.getDate());
            updateEnd = DateManager.deFormatDate(formattedEndDate);
        }

        //check for null values
        if(updateTitle == null) {
            Toast.makeText(v.getContext(), "Please Enter a Title", Toast.LENGTH_LONG).show();
            return;
        }

        //check that startDate is either equal to or before endDate
        if(!DateManager.isStartBeforeEnd(updateStart, updateEnd)) {
            Toast.makeText(v.getContext(), "Start Date cannot be after End Date", Toast.LENGTH_LONG).show();
            return;
        }



        //create term object
        updateTerm = new Term(updateTitle, updateStart, updateEnd);

        //insert assessment object
        UpdateRunnable updateRunnable = new UpdateRunnable();
        new Thread(updateRunnable).start();


        //refresh page
        launchTerms();
    }


    //check if edit text is empty
    public boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onSelectStartDate(String startDate) {
        newStart = startDate;
    }

    @Override
    public void onSelectEndDate(String endDate) {
        newEnd = endDate;
    }

    @Override
    public void onUpdateSelectStartDate(String startDate) {
        updateStart = startDate;
    }

    @Override
    public void onUpdateSelectEndDate(String endDate) {
        updateEnd = endDate;
    }




    //RUNNABLES

    //delete every selected term record
    class DeleteRunnable implements Runnable {

        @Override
        public void run() {
            TermDao termDao = StudentDatabase.getInstance(getApplicationContext()).termDao();

            //delete all selected term records
            for (int i = 0; i < selected.size(); i++) {
                termDao.deleteByID(selected.get(i));
            }

            //notify user of success
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "SUCCESS: Term(s) Deleted", Toast.LENGTH_SHORT).show();
                }
            });

            //refresh page
            launchTerms();
        }
    }



    //update term record
    class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            TermDao termDao = StudentDatabase.getInstance(getApplicationContext()).termDao();
            termDao.updateByID(selected.get(0), updateTerm.getTitle(), updateTerm.getStartDate(), updateTerm.getEndDate());
        }
    }

    //select term for update fragment
    class SelectForUpdateRunnable implements  Runnable {

        @Override
        public void run() {
            TermDao termDao = StudentDatabase.getInstance(getApplicationContext()).termDao();

            Term altThreadCourse = termDao.getTermByID(selected.get(0));
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateTerm = altThreadCourse;
                    getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
                    //bundle to pass data for Detailed Term Fragment
                    Bundle detailsBundle = new Bundle();
                    if(updateTerm != null) {
                        detailsBundle.putInt("id", Integer.valueOf(selected.get(0)));
                        detailsBundle.putString("title", updateTerm.getTitle().toString());
                        detailsBundle.putString("startDate", updateTerm.getStartDate());
                        detailsBundle.putString("endDate", updateTerm.getEndDate());

                        fragment.setArguments(detailsBundle);
                    }
                }
            });
        }
    }

    //queries for a list of assessments associated with an course id and opens the assessment assignment fragment
    class AssignedCourseRunnable implements Runnable {

        @Override
        public void run() {
            TermCourseLinkDao linkDao = StudentDatabase.getInstance(getApplicationContext()).termCourseLinkDao();
            TermDao termDao = StudentDatabase.getInstance(getApplicationContext()).termDao();
            CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();

            //list of assigned courses based on term ID
            ArrayList<Integer> assignedIdList = new ArrayList<>();
            assignedIdList.addAll(linkDao.getAssignedCourses(selected.get(0)));
            //list of every course
            ArrayList<Course> allCourses = new ArrayList<>();
            allCourses.addAll(courseDao.getAllCourses());

            //lists to pass to fragment
            ArrayList<Integer> bundleAssignedID = new ArrayList<>();
            ArrayList<String> bundleAssignedTitle = new ArrayList<>();
            ArrayList<Integer> bundleUnAssignedID = new ArrayList<>();
            ArrayList<String> bundleUnAssignedTitle = new ArrayList<>();

            //if neither query comes back empty
            if(assignedIdList.size() != 0 && allCourses.size() != 0) {
                int compID;
                //create assigned bundles
                for (int i = 0; i < allCourses.size(); i++) {
                    compID = allCourses.get(i).getCourseID();
                    if (assignedIdList.contains(compID)) {
                        //add to bundle of assigned list quantities
                        bundleAssignedID.add(allCourses.get(i).getCourseID());
                        bundleAssignedTitle.add(allCourses.get(i).getTitle());
                    }
                    else {
                        //add to bundle of unassigned list quantities
                        bundleUnAssignedID.add(allCourses.get(i).getCourseID());
                        bundleUnAssignedTitle.add(allCourses.get(i).getTitle());
                    }
                }
            }
            //if assigned query comes back empty
            else if(assignedIdList.size() == 0 && allCourses.size() != 0) {
                for (int i = 0; i < allCourses.size(); i++) {
                    //add everything to unassigned
                    bundleUnAssignedID.add(allCourses.get(i).getCourseID());
                    bundleUnAssignedTitle.add(allCourses.get(i).getTitle());
                }
            }

            //get term info
            Term selectedTerm = termDao.getTermByID(selected.get(0));


            //send work to main thread
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
                    //bundle lists and send to fragment
                    //add course information
                    Bundle assignmentBundle = new Bundle();
                    if(bundleAssignedID.size() != 0) {
                        assignmentBundle.putIntegerArrayList("ASSIGNED_ID_LIST", bundleAssignedID);
                        assignmentBundle.putStringArrayList("ASSIGNED_TITLE_LIST", bundleAssignedTitle);
                    }
                    if(bundleUnAssignedID.size() != 0) {
                        assignmentBundle.putIntegerArrayList("UNASSIGNED_ID_LIST", bundleUnAssignedID);
                        assignmentBundle.putStringArrayList("UNASSIGNED_TITLE_LIST", bundleUnAssignedTitle);
                    }
                    //add term information
                    assignmentBundle.putString("ID", String.valueOf(selectedTerm.getTermID()));
                    assignmentBundle.putString("TITLE", selectedTerm.getTitle());

                    fragment.setArguments(assignmentBundle);
                }
            });
        }
    }

    //select a single term by ID and apply info to details fragment and open detailed term fragment
    class SelectByIdRunnable implements Runnable {

        @Override
        public void run() {
            CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();
            TermCourseLinkDao linkDao = StudentDatabase.getInstance(getApplicationContext()).termCourseLinkDao();
            TermDao termDao = StudentDatabase.getInstance(getApplicationContext()).termDao();

            //get course info
            Term altThreadTerm = termDao.getTermByID(selected.get(0));

            //get list of assigned courses
            ArrayList<String> returnBundle = new ArrayList<>();
            ArrayList<Integer> assigned = new ArrayList<>();
            assigned.addAll(linkDao.getAssignedCourses(selected.get(0)));

            //get list of all courses
            ArrayList<Course> allCourses = new ArrayList<>();
            allCourses.addAll(courseDao.getAllCourses());

            for (int i = 0; i < allCourses.size(); i++) {
                if(assigned.contains(allCourses.get(i).getCourseID())) {
                    returnBundle.add(allCourses.get(i).getTitle());
                }
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    selectedTerm = altThreadTerm;
                    getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
                    //bundle to pass data for Detailed Term Fragment
                    Bundle detailsBundle = new Bundle();
                    if(selectedTerm != null) {
                        //send term info
                        detailsBundle.putInt("ID", Integer.valueOf(selected.get(0)));
                        detailsBundle.putString("TITLE", selectedTerm.getTitle().toString());
                        detailsBundle.putString("START_DATE", selectedTerm.getStartDate());
                        detailsBundle.putString("END_DATE", selectedTerm.getEndDate());

                        //send assigned course titles
                        if(returnBundle.size() > 0) {
                            detailsBundle.putStringArrayList("ASSIGNED", returnBundle);
                        }
                        fragment.setArguments(detailsBundle);
                    }
                }
            });
        }
    }


    //query database for all terms
    class SelectRunnable implements Runnable {

        @Override
        public void run() {
            TermDao termDao = StudentDatabase.getInstance(getApplicationContext()).termDao();

            //get all terms into container
            List<Term> returnedQueryList = termDao.getAllTerms();

            selectQueryResults = returnedQueryList;


            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(selectQueryResults != null ) {
                        for (int i = 0; i < selectQueryResults.size(); i++) {
                            //add term info and notify adapter
                            title.add(selectQueryResults.get(i).getTitle());
                            id.add(selectQueryResults.get(i).getTermID());
                            adapter.notifyItemInserted(title.size()-1);
                            adapter.notifyItemInserted(id.size()-1);
                        }
                    }
                }
            });
        }
    }

    //insert new term object
    class InsertRunnable implements Runnable {

        @Override
        public void run() {
            TermDao termDao = StudentDatabase.getInstance(getApplicationContext()).termDao();
            termDao.insertTerm(insertTerm);
        }
    }
}