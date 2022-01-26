package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Dao;


import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ArrayAdapter;
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


import Adapters.CourseCardAdapter;
import Controllers.DateManager;
import Database.Assessment;
import Database.AssessmentAlertLink;
import Database.AssessmentAlertLinkDao;
import Database.AssessmentDao;
import Database.Course;
import Database.CourseAlertLink;
import Database.CourseAlertLinkDao;
import Database.CourseAssessmentLink;
import Database.CourseAssessmentLinkDao;
import Database.CourseDao;
import Database.StudentDatabase;
import Database.TermCourseLinkDao;
import Database.TermDao;
import Fragments.AssignAssessmentsFragment;
import Fragments.CreateCourseFragment;
import Fragments.DetailedCourseFragment;
import Fragments.SetAlertAssessmentsFragment;
import Fragments.SetAlertCoursesFragment;
import Fragments.UpdateCourseFragment;

public class CourseActivity extends AppCompatActivity implements CreateCourseFragment.CourseFragmentListener, UpdateCourseFragment.UpdateCourseFragmentListener {

    //list of course titles
    List<String> title = new LinkedList<>();
    //list of course types
    List<String> status = new LinkedList<>();
    //list of course ids
    List<Integer> id = new LinkedList<>();
    //List of user selected Course entries
    public static ArrayList<Integer> selected = new ArrayList<>();
    //list of all courses in database
    List<Course> selectQueryResults = new LinkedList<>();
    //course to be inserted into database
    static Course insertCourse;
    static Course selectedCourse;
    //course to be updated
    static Course updateCourse;
    //new course variables
    int newID;
    String newTitle, newStartDate, newEndDate, newStatus, newName, newNumber, newEmail, newNotes;
    //updated course variables
    int updateID;
    String updateTitle, updateStartDate, updateEndDate, updateStatus, updateName, updateNumber, updateEmail, updateNotes;
    //get work to main thread
    Handler mainHandler = new Handler();
    //fragment to fill scroll view
    Fragment fragment;

    //other
    BottomNavigationView navBar;
    CourseCardAdapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        //implement notification channel creation
        createStartNotificationChannel();

        //scroll view that gets filled with various fragments
        ScrollView scrollView = findViewById(R.id.scroll_view);

        //create toolbar functionality
        toolbar =  findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //launch create course fragment
                if(item.getItemId() == findViewById(R.id.add_action).getId()) {
                    //launch fragment
                    setCourseFragment();
                }
                //launch course info fragment
                else if(item.getItemId() == findViewById(R.id.info_action).getId()) {
                    //launch fragment
                    setInfoFragment();
                }
                //launch assessment assignment fragment
                else if(item.getItemId() == findViewById(R.id.assessment_action).getId()) {
                    setAssignFragment();
                }
                else if(item.getItemId() == findViewById(R.id.alert_action).getId()) {
                    setAlertFragment();
                }
                //share Course Notes
                else if(item.getItemId() == findViewById(R.id.share_action).getId()) {
                    if(selected == null || selected.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Please Make a Selection", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    //check if list contains more than one item
                    if(selected.size() > 1) {
                        Toast.makeText(getApplicationContext(), "Select ONLY ONE item", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    ShareRunnable shareRunnable = new ShareRunnable();
                    new Thread(shareRunnable).start();
                }
                //delete selected course
                else if(item.getItemId() == findViewById(R.id.delete_action).getId()) {

                    //check that at least one selection has been made
                    if(selected.size() < 1) {
                        Toast.makeText(getApplicationContext(), "Select Item(s) to Delete", Toast.LENGTH_SHORT).show();
                        return false;
                    }
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

                return true;
            }
        });


        //Nav bar assignment
        navBar = findViewById(R.id.bottom_nav_bar);

        //Set selected Nav Bar item
        navBar.setSelectedItemId(R.id.nav_course);

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

        //POPULATE COURSE CARDS
        RecyclerView recyclerView = findViewById(R.id.course_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseCardAdapter(id, title, status);
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
                scrollView.removeView(findViewById(R.id.course_recyclerview));
                //populate selected values
                if(savedInstanceState.getIntegerArrayList("SELECTED") != null)
                    selected.addAll(savedInstanceState.getIntegerArrayList("SELECTED"));
                //disable all icons
                toolbar.findViewById(R.id.add_action).setEnabled(false);
                toolbar.findViewById(R.id.info_action).setEnabled(false);
                toolbar.findViewById(R.id.assessment_action).setEnabled(false);
                toolbar.findViewById(R.id.share_action).setEnabled(false);
                toolbar.findViewById(R.id.alert_action).setEnabled(false);
                toolbar.findViewById(R.id.delete_action).setEnabled(false);
            }
        }

    }



    //HANDLE FRAGMENTS DURING ORIENTATION CHANGES
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //check if Scrollview contains recycle_view
        if(findViewById(R.id.course_recyclerview) == null) {
            outState.putBoolean("IS_DEFAULT", false);
            if(selected.size() != 0) {
                ArrayList<Integer> outStateSelected = new ArrayList<>();
                outStateSelected.addAll(selected);
                outState.putIntegerArrayList("SELECTED", outStateSelected);
            }

        }
    }

    @Override
    public void onBackPressed() {
        launchCourses();
    }


    //ACTIVITY LAUNCHERS

    //Launch Term Activity
    public void launchTerms() {
        Intent i = new Intent(this, TermActivity.class);
        startActivity(i);
    }

    //Launch Home Activity
    public void launchHome() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    //Launch Course Activity
    public void launchCourses() {
        Intent i = new Intent(this, CourseActivity.class);
        startActivity(i);
    }

    //Launch Course Activity
    public void launchCoursesButton(View v) {
        Intent i = new Intent(this, CourseActivity.class);
        startActivity(i);
    }

    //launch Assessments activity
    public void launchAssessments() {
        Intent i = new Intent(this, AssessmentActivity.class);
        startActivity(i);
    }


    //launch create course fragment
    public void setCourseFragment() {
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
        Fragment fragment = new CreateCourseFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.assessment_action).setEnabled(false);
            toolbar.findViewById(R.id.share_action).setEnabled(false);
            toolbar.findViewById(R.id.alert_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }

    //launch fragment for editing assigned assessments
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

        fragment = new AssignAssessmentsFragment();

        AssignedAssessmentRunnable assignedAssessmentRunnable = new AssignedAssessmentRunnable();
        new Thread(assignedAssessmentRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.assessment_action).setEnabled(false);
            toolbar.findViewById(R.id.share_action).setEnabled(false);
            toolbar.findViewById(R.id.alert_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }


    //launch course info fragment
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
        fragment = new DetailedCourseFragment();

        //fetch data and open page
        SelectByIdRunnable selectByIdRunnable = new SelectByIdRunnable();
        new Thread(selectByIdRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.assessment_action).setEnabled(false);
            toolbar.findViewById(R.id.share_action).setEnabled(false);
            toolbar.findViewById(R.id.alert_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }


    //launch update course fragment
    public void setUpdateFragment(View v) {
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

        //prepare destination
        ScrollView scrollview_destination = findViewById(R.id.scroll_view);
        try {
            scrollview_destination.removeAllViews();
        }
        catch (Exception e) {
            //nothing
        }
        fragment = new UpdateCourseFragment();

        //fetch data and open page
        SelectForUpdateRunnable selectForUpdateRunnable = new SelectForUpdateRunnable();
        new Thread(selectForUpdateRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.assessment_action).setEnabled(false);
            toolbar.findViewById(R.id.share_action).setEnabled(false);
            toolbar.findViewById(R.id.alert_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }

    //launch fragment for enabling/disabling alerts
    public void setAlertFragment() {

        ScrollView scrollview_replace = findViewById(R.id.scroll_view);
        try {
            scrollview_replace.removeAllViews();
        }
        catch (Exception e) {
            //nothing
        }

        fragment = new SetAlertCoursesFragment();

        SetAlertsRunnable setAlertsRunnable = new SetAlertsRunnable();
        new Thread(setAlertsRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.assessment_action).setEnabled(false);
            toolbar.findViewById(R.id.share_action).setEnabled(false);
            toolbar.findViewById(R.id.alert_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }

    public void onClickEnter(View v) {

        //compile all course values into new course
        EditText editTitle = findViewById(R.id.edit_title);
        EditText editName = findViewById(R.id.edit_name);
        EditText editNumber = findViewById(R.id.edit_phone_number);
        EditText editEmail = findViewById(R.id.edit_email);
        EditText editNote = findViewById(R.id.edit_note);
        Spinner statusSpinner = findViewById(R.id.spinner_status);
        CalendarView calendarViewStart = findViewById(R.id.calendarview_start);
        CalendarView calendarViewEnd = findViewById(R.id.calendarview_end);

        //check for null values and assign values not handled by listeners
        if(!isEmpty(editTitle)) {
            newTitle = editTitle.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Complete All Fields", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isEmpty(editName)) {
            newName = editName.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Complete All Fields", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isEmpty(editNumber)) {
            newNumber = editNumber.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Complete All Fields", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isEmpty(editEmail)) {
            newEmail = editEmail.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Complete All Fields", Toast.LENGTH_LONG).show();
            return;
        }
        if(editNote.getText() != null) {
            newNotes = editNote.getText().toString();
        }

        int spinnerPosition = statusSpinner.getSelectedItemPosition();
        newStatus = statusSpinner.getItemAtPosition(spinnerPosition).toString();

        //if either startDate or endDate have not been selected, assign current date
        if(newStartDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedStartDate = sdf.format(calendarViewStart.getDate());
            newStartDate = DateManager.deFormatDate(formattedStartDate);
        }
        if(newEndDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedEndDate = sdf.format(calendarViewEnd.getDate());
            newEndDate = DateManager.deFormatDate(formattedEndDate);
        }

        //check that startDate is either equal to or before endDate
        if(!DateManager.isStartBeforeEnd(newStartDate, newEndDate)) {
            Toast.makeText(v.getContext(), "Start Date cannot be after End Date", Toast.LENGTH_LONG).show();
            return;
        }

        if(newNotes != null) {
            if (newNotes.length() > 85) {
                Toast.makeText(this, "Entered notes surpass 85 Character Limit", Toast.LENGTH_LONG).show();
                return;
            }
        }

        //create course object
        insertCourse = new Course(newTitle, newStartDate, newEndDate, newStatus, newName, newNumber, newEmail, newNotes);

        //insert course object
        InsertRunnable insertRunnable = new InsertRunnable();
        new Thread(insertRunnable).start();

        //refresh page
        launchCourses();
    }

    public void onClickUpdate(View v) {

        //compile all course values into updated course
        EditText editTitle = findViewById(R.id.edit_title);
        EditText editName = findViewById(R.id.edit_name);
        EditText editNumber = findViewById(R.id.edit_phone_number);
        EditText editEmail = findViewById(R.id.edit_email);
        EditText editNote = findViewById(R.id.edit_note);
        Spinner statusSpinner = findViewById(R.id.spinner_status);
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
        if(!isEmpty(editName)) {
            updateName = editName.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Complete All Fields", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isEmpty(editNumber)) {
            updateNumber = editNumber.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Complete All Fields", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isEmpty(editEmail)) {
            updateEmail = editEmail.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Complete All Fields", Toast.LENGTH_LONG).show();
            return;
        }
        if(editNote.getText() != null) {
            updateNotes = editNote.getText().toString();
        }
        int spinnerPosition = statusSpinner.getSelectedItemPosition();
        updateStatus = statusSpinner.getItemAtPosition(spinnerPosition).toString();


        //if either startDate or endDate have not been selected, assign current date
        if(updateStartDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedEndDate = sdf.format(calendarViewStart.getDate());
            updateStartDate = DateManager.deFormatDate(formattedEndDate);
        }
        if(updateEndDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedEndDate = sdf.format(calendarViewEnd.getDate());
            updateEndDate = DateManager.deFormatDate(formattedEndDate);
        }

        //check for null values
        if(updateTitle == null|| updateName == null || updateNumber == null || updateEmail == null || updateStatus == null) {
            Toast.makeText(v.getContext(), "Please Complete All Fields", Toast.LENGTH_LONG).show();
            return;
        }

        //check that startDate is either equal to or before endDate
        if(!DateManager.isStartBeforeEnd(updateStartDate, updateEndDate)) {
            Toast.makeText(v.getContext(), "Start Date cannot be after End Date", Toast.LENGTH_LONG).show();
            return;
        }

        if(updateNotes != null) {
            if(updateNotes.length() > 85) {
                Toast.makeText(this, "Entered notes surpass 85 Character Limit", Toast.LENGTH_LONG).show();
                return;
            }
        }

        //create course object
        updateCourse = new Course(updateTitle, updateStartDate, updateEndDate, updateStatus, updateName, updateNumber, updateEmail, updateNotes);

        //insert course object

        UpdateRunnable updateRunnable = new UpdateRunnable();
        new Thread(updateRunnable).start();

        //refresh page
        launchCourses();
    }

    //check if edit text is empty
    public boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }


    //implement listeners for calendars
    @Override
    public void onSelectStartDate(String startDate) {
            newStartDate = startDate;
    }

    @Override
    public void onSelectEndDate(String endDate) {
        newEndDate = endDate;
    }

    @Override
    public void onUpdateSelectStartDate(String startDate) {
        updateStartDate = startDate;
    }

    @Override
    public void onUpdateSelectEndDate(String endDate) {
        updateStartDate = endDate;
    }


    //RUNNABLE(S)/QUERIES

    //query for lists of enabled and disabled assessments and opens SetAlertCoursesFragment
    class SetAlertsRunnable implements Runnable {

        @Override
        public void run() {

            CourseAlertLinkDao linkDao = StudentDatabase.getInstance(getApplicationContext()).courseAlertLinkDao();

            //list of courses based on alert enablement
            ArrayList<Course> enabledAlerts = new ArrayList<>();
            ArrayList<Course> disabledAlerts = new ArrayList<>();
            enabledAlerts.addAll(linkDao.getCoursesByEnablement(true));
            disabledAlerts.addAll(linkDao.getCoursesByEnablement(false));

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
                    //bundle lists and send to fragment
                    Bundle alertBundle = new Bundle();
                    if (enabledAlerts.size() != 0) {
                        ArrayList<Integer> enabledID = new ArrayList<>();
                        ArrayList<String> enabledTitle = new ArrayList<>();
                        for (int i = 0; i < enabledAlerts.size(); i++) {
                            enabledID.add(enabledAlerts.get(i).getCourseID());
                            enabledTitle.add(enabledAlerts.get(i).getTitle());
                        }
                        alertBundle.putIntegerArrayList("ENABLED_ID", enabledID);
                        alertBundle.putStringArrayList("ENABLED_TITLE", enabledTitle);
                    }
                    if (disabledAlerts.size() != 0) {
                        ArrayList<Integer> disabledID = new ArrayList<>();
                        ArrayList<String> disabledTitle = new ArrayList<>();
                        for (int i = 0; i < disabledAlerts.size(); i++) {
                            disabledID.add(disabledAlerts.get(i).getCourseID());
                            disabledTitle.add(disabledAlerts.get(i).getTitle());
                        }
                        alertBundle.putIntegerArrayList("DISABLED_ID", disabledID);
                        alertBundle.putStringArrayList("DISABLED_TITLE", disabledTitle);
                    }
                    fragment.setArguments(alertBundle);
                }
            });
        }
    }


    //queries for a list of assessments associated with an course id and opens the assessment assignment fragment
    class AssignedAssessmentRunnable implements Runnable {

        @Override
        public void run() {
            CourseAssessmentLinkDao linkDao = StudentDatabase.getInstance(getApplicationContext()).courseAssessmentLinkDao();
            AssessmentDao assessmentDao = StudentDatabase.getInstance(getApplicationContext()).assessmentDao();
            CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();

            //list of assigned assessments based on course ID
            ArrayList<Integer> assignedIdList = new ArrayList<>();
            assignedIdList.addAll(linkDao.getAssignedAssessments(selected.get(0)));
            //list of every assessment
            ArrayList<Assessment> allAssessments = new ArrayList<>();
            allAssessments.addAll(assessmentDao.getAllAssessments());

            //lists to pass to fragment
            ArrayList<Integer> bundleAssignedID = new ArrayList<>();
            ArrayList<String> bundleAssignedTitle = new ArrayList<>();
            ArrayList<Integer> bundleUnAssignedID = new ArrayList<>();
            ArrayList<String> bundleUnAssignedTitle = new ArrayList<>();

            //if neither query comes back empty
            if(assignedIdList.size() != 0 && allAssessments.size() != 0) {
                int compID;
                //create assigned bundles
                for (int i = 0; i < allAssessments.size(); i++) {
                    compID = allAssessments.get(i).getAssessmentID();
                    if (assignedIdList.contains(compID)) {
                        //add to bundle of assigned list quantities
                        bundleAssignedID.add(allAssessments.get(i).getAssessmentID());
                        bundleAssignedTitle.add(allAssessments.get(i).getTitle());
                    }
                    else {
                        //add to bundle of unassigned list quantities
                        bundleUnAssignedID.add(allAssessments.get(i).getAssessmentID());
                        bundleUnAssignedTitle.add(allAssessments.get(i).getTitle());
                    }
                }
            }
            //if assigned query comes back empty
            else if(assignedIdList.size() == 0 && allAssessments.size() != 0) {
                for (int i = 0; i < allAssessments.size(); i++) {
                    //add everything to unassigned
                    bundleUnAssignedID.add(allAssessments.get(i).getAssessmentID());
                    bundleUnAssignedTitle.add(allAssessments.get(i).getTitle());
                }
            }

            //get course info
            Course selectedCourse = courseDao.getCourseByID(selected.get(0));


            //send work to main thread
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
                    //bundle lists and send to fragment
                    //add assessment information
                    Bundle assignmentBundle = new Bundle();
                    if(bundleAssignedID.size() != 0) {
                        assignmentBundle.putIntegerArrayList("ASSIGNED_ID_LIST", bundleAssignedID);
                        assignmentBundle.putStringArrayList("ASSIGNED_TITLE_LIST", bundleAssignedTitle);
                    }
                    if(bundleUnAssignedID.size() != 0) {
                        assignmentBundle.putIntegerArrayList("UNASSIGNED_ID_LIST", bundleUnAssignedID);
                        assignmentBundle.putStringArrayList("UNASSIGNED_TITLE_LIST", bundleUnAssignedTitle);
                    }
                    //add course information
                    assignmentBundle.putString("ID", String.valueOf(selectedCourse.getCourseID()));
                    assignmentBundle.putString("TITLE", selectedCourse.getTitle());

                    fragment.setArguments(assignmentBundle);
                }
            });
        }
    }


   //query for note to share
   class ShareRunnable implements Runnable {

       @Override
       public void run() {
           CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();
           //query for note to be shared
           String note = courseDao.getCourseNote(selected.get(0));
                //if there is a note to share
                if (note != null) {
                    Log.d("not null", "not null");
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent noteIntent = new Intent();
                            noteIntent.setAction(Intent.ACTION_SEND);
                            noteIntent.putExtra(Intent.EXTRA_TEXT, note);
                            noteIntent.setType("text/plain");

                            Intent shareNote = Intent.createChooser(noteIntent, "Share Course Note");
                            startActivity(shareNote);
                        }
                    });
                }
                //if there is not a note to share
                else {
                    Log.d("null", "null");
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No Note to Share", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
       }

    //query database for all courses
    class SelectRunnable implements Runnable {

        @Override
        public void run() {
            CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();

            //get all courses into container
            List<Course> returnedQueryList = courseDao.getAllCourses();

            selectQueryResults = returnedQueryList;


            mainHandler.post(new Runnable() {
                @Override
                public void run() {


                    if(selectQueryResults != null ) {
                        for (int i = 0; i < selectQueryResults.size(); i++) {
                            //add course info and notify adapter
                            title.add(selectQueryResults.get(i).getTitle());
                            status.add(selectQueryResults.get(i).getStatus());
                            id.add(selectQueryResults.get(i).getCourseID());
                            adapter.notifyItemInserted(title.size()-1);
                            adapter.notifyItemInserted(status.size()-1);
                            adapter.notifyItemInserted(id.size()-1);
                        }
                    }
                }
            });
        }
    }

    //select course for update fragment
    class SelectForUpdateRunnable implements  Runnable {

        @Override
        public void run() {
            CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();


            Course altThreadCourse = courseDao.getCourseByID(selected.get(0));
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateCourse = altThreadCourse;
                    getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
                    //bundle to pass data for Detailed Course Fragment
                    Bundle detailsBundle = new Bundle();
                    if(updateCourse != null) {
                        //detailsBundle.putInt("id", Integer.valueOf(selected.get(0)));
                        detailsBundle.putString("title", updateCourse.getTitle().toString());
                        detailsBundle.putString("startDate", updateCourse.getStartDate());
                        detailsBundle.putString("endDate", updateCourse.getEndDate());
                        detailsBundle.putString("status", updateCourse.getStatus());
                        detailsBundle.putString("number", updateCourse.getInstructorNumber());
                        detailsBundle.putString("name", updateCourse.getInstructorName());
                        detailsBundle.putString("email", updateCourse.getInstructorEmail());

                        //notes are optional
                        if(updateCourse.getCourseNotes() != null) {
                            detailsBundle.putString("notes", updateCourse.getCourseNotes());
                        }
                        fragment.setArguments(detailsBundle);
                    }
                }
            });
        }
    }

    class DeleteRunnable implements Runnable {

        @Override
        public void run() {
            CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();


            //delete all selected term records
            for (int i = 0; i < selected.size(); i++) {
                courseDao.deleteByID(selected.get(i));
            }

            //notify user of success
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "SUCCESS: Course Deleted", Toast.LENGTH_SHORT).show();
                }
            });

            //refresh page
            launchCourses();
            }
        }


    //select a single Course by ID and apply info to details fragment and open detailed course fragment - only to be called from setCreateFragment
    class SelectByIdRunnable implements Runnable {

        @Override
        public void run() {
            CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();
            CourseAssessmentLinkDao linkDao = StudentDatabase.getInstance(getApplicationContext()).courseAssessmentLinkDao();
            AssessmentDao assessmentDao = StudentDatabase.getInstance(getApplicationContext()).assessmentDao();

            //get course info
            Course altThreadCourse = courseDao.getCourseByID(selected.get(0));

            //get list of assigned assessments
            ArrayList<String> returnBundle = new ArrayList<>();
            ArrayList<Integer> assigned = new ArrayList<>();
            assigned.addAll(linkDao.getAssignedAssessments(selected.get(0)));

            //get list of all assessments
            ArrayList<Assessment> allAssessments = new ArrayList<>();
            allAssessments.addAll(assessmentDao.getAllAssessments());

            for (int i = 0; i < allAssessments.size(); i++) {
                if(assigned.contains(allAssessments.get(i).getAssessmentID())) {
                    returnBundle.add(allAssessments.get(i).getTitle());
                }
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    selectedCourse = altThreadCourse;
                    getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
                    //bundle to pass data for Detailed Course Fragment
                    Bundle detailsBundle = new Bundle();
                    if(selectedCourse != null) {
                        //send course info
                        detailsBundle.putInt("id", Integer.valueOf(selected.get(0)));
                        detailsBundle.putString("title", selectedCourse.getTitle().toString());
                        detailsBundle.putString("startDate", selectedCourse.getStartDate());
                        detailsBundle.putString("endDate", selectedCourse.getEndDate());
                        detailsBundle.putString("status", selectedCourse.getStatus());
                        detailsBundle.putString("name", selectedCourse.getInstructorName());
                        detailsBundle.putString("number", selectedCourse.getInstructorNumber());
                        detailsBundle.putString("email", selectedCourse.getInstructorEmail());
                        //notes are optional
                        if(selectedCourse.getCourseNotes() != null) {
                            detailsBundle.putString("notes", selectedCourse.getCourseNotes());
                        }
                        //send assigned assessment titles
                        if(returnBundle.size() > 0) {
                            detailsBundle.putStringArrayList("ASSIGNED", returnBundle);
                        }
                        fragment.setArguments(detailsBundle);
                    }
                }
            });
        }
    }

    //insert new course objects
    class InsertRunnable implements Runnable {

        @Override
        public void run() {
            CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();
            courseDao.insertCourse(insertCourse);

            //add to assessment_alert table with alerts off by default
            int newestCourseID = courseDao.getLastInsertedID();
            CourseAlertLinkDao linkDao = StudentDatabase.getInstance(getApplicationContext()).courseAlertLinkDao();
            linkDao.insertNewAlert(new CourseAlertLink(newestCourseID, false));
        }
    }

    //update course record
    class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            CourseDao courseDao = StudentDatabase.getInstance(getApplicationContext()).courseDao();
            courseDao.updateByID(selected.get(0), updateCourse.getTitle(), updateCourse.getStartDate(), updateCourse.getEndDate(), updateCourse.getStatus(), updateCourse.getInstructorName(), updateCourse.getInstructorNumber(), updateCourse.getInstructorEmail(), updateCourse.getCourseNotes());
        }
    }
    //create notification channel
    private void createStartNotificationChannel() {
        String channelName = "notifyCourseChannel";
        String channelDescription = "Course Start Alert Channel";
        int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel("notifyCourseStartTime", channelName, channelImportance);
        notificationChannel.setDescription(channelDescription);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

}











