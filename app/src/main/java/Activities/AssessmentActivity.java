package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.widget.Switch;
import android.widget.Toast;


import Database.Assessment;
import Database.AssessmentAlertLink;
import Database.AssessmentAlertLinkDao;
import Database.AssessmentDao;
import Adapters.AssessmentCardAdapter;
import Controllers.DateManager;
import Database.StudentDatabase;
import Fragments.CreateAssessmentFragment;
import Fragments.DetailedAssessmentFragment;
import Fragments.SetAlertAssessmentsFragment;
import Fragments.UpdateAssessmentFragment;

import com.example.studentplanner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class AssessmentActivity extends AppCompatActivity implements CreateAssessmentFragment.AssessmentFragmentListener, UpdateAssessmentFragment.UpdateAssessmentFragmentListener {

    //list of assessment titles
    List<String> title = new LinkedList<>();
    //list of assessment types
    List<String> type = new LinkedList<>();
    //list of assessment ids
    List<Integer> id = new LinkedList<>();
    //list of user selected assessment entries
    public static ArrayList<Integer> selected = new ArrayList<>();
    //list of all assessments in database
    List<Assessment> selectQueryResults = new LinkedList<>();
    //assessment object
    Assessment assessment;
    //handler to send code to main thread
    Handler mainHandler = new Handler();
    //new assessment values
    String newTitle, newStart, newEnd, newType;
    //update assessment values
    String updateTitle, updateStart, updateEnd, updateType;
    //new assessment object
    Assessment insertAssessment;
    static Assessment selectedAssessment;
    //assessment to be updated
    static Assessment updateAssessment;
    //fragment to fill scroll view
    Fragment fragment;
    //assessment toolbar
    Toolbar toolbar;



    //other
    AssessmentCardAdapter adapter;
    BottomNavigationView navBar;
    public static boolean isNew = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments);

        //implement notification channel creation
        createStartNotificationChannel();

        //scroll view that gets filled with various fragments
        ScrollView scrollView = findViewById(R.id.scroll_view);

        //create toolbar functionality
        toolbar =  findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //launch create assessment fragment
                if(item.getItemId() == findViewById(R.id.add_action).getId()) {
                    setCreateAssessmentFragment();
                }
                //launch assessment info fragment
                else if(item.getItemId() == findViewById(R.id.info_action).getId()) {
                    setInfoFragment();
                }
                else if(item.getItemId() == findViewById(R.id.alert_action).getId()) {
                    setAlertFragment();
                }
                //delete selected assessment(s)
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
        navBar.setSelectedItemId(R.id.nav_assessments);

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




        //populate assessment cards
        RecyclerView recyclerView = findViewById(R.id.assessment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AssessmentCardAdapter(id, title, type);
        recyclerView.setAdapter(adapter);

        //populate assessment list
        SelectRunnable selectRunnable = new SelectRunnable();
        new Thread(selectRunnable).start();



        //clear all user selections
        selected.clear();

        //HANDLE FRAGMENTS DURING ORIENTATION CHANGES
        //if the recycler_view didn't exist on orientation change, clear it out
        if(savedInstanceState != null) {
            if (!savedInstanceState.getBoolean("IS_DEFAULT", true)) {
                scrollView.removeView(findViewById(R.id.assessment_recyclerview));
                //populate selected values
                if(savedInstanceState.getIntegerArrayList("SELECTED") != null)
                    selected.addAll(savedInstanceState.getIntegerArrayList("SELECTED"));
                //disable icons to avoid fragment conflict
                toolbar.findViewById(R.id.add_action).setEnabled(false);
                toolbar.findViewById(R.id.info_action).setEnabled(false);
                toolbar.findViewById(R.id.delete_action).setEnabled(false);
                toolbar.findViewById(R.id.alert_action).setEnabled(false);
            }
        }
    }

    //HANDLE FRAGMENTS DURING ORIENTATION CHANGES
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //check if Scrollview contains recycle_view
        if(findViewById(R.id.assessment_recyclerview) == null) {
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
        launchAssessments();
    }

    //ACTIVITY LAUNCHERS

    //launch Term Activity
    public void launchTerms() {
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
    //launch assessments
    public void launchAssessments() {
        Intent i = new Intent(this, AssessmentActivity.class);
        startActivity(i);
    }
    //launch assessments
    public void launchAssessmentsButton(View v) {
        Intent i = new Intent(this, AssessmentActivity.class);
        startActivity(i);
    }

    //FRAGMENT LAUNCHERS

    //launch create assessment fragment
    public void setCreateAssessmentFragment() {
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
        Fragment fragment = new CreateAssessmentFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
            toolbar.findViewById(R.id.alert_action).setEnabled(false);

        }
    }

    //launch detailed assessment view fragment
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
        fragment = new DetailedAssessmentFragment();


        //fetch data and open page
        SelectByIdRunnable selectByIdRunnable = new SelectByIdRunnable();
        new Thread(selectByIdRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
            toolbar.findViewById(R.id.alert_action).setEnabled(false);

        }
    }

    //launch update assessment fragment
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
        fragment = new UpdateAssessmentFragment();

        //fetch data and open page
        SelectForUpdateRunnable selectForUpdateRunnable = new SelectForUpdateRunnable();
        new Thread(selectForUpdateRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
            toolbar.findViewById(R.id.alert_action).setEnabled(false);

        }
    }


    //create new assessment
    public void onClickEnter(View v) {

        EditText editTitle = findViewById(R.id.edit_title);
        CalendarView calendarViewStart = findViewById(R.id.calendarview_start);
        CalendarView calendarViewEnd = findViewById(R.id.calendarview_end);
        Switch typeSwitch = findViewById(R.id.type_switch);

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

        //get switch values
        if(typeSwitch.isChecked()) {
            newType = getString(R.string.Performance);
        }
        else {
            newType = getString(R.string.Objective);
        }

        //create new term object
        insertAssessment = new Assessment(newTitle, newStart, newEnd, newType);

        //insert term object
        InsertRunnable insertRunnable = new InsertRunnable();
        new Thread(insertRunnable).start();

        //refresh page
        launchAssessments();
    }

    //update assessment
    public void onClickUpdate(View v) {
        EditText editTitle = findViewById(R.id.edit_title);
        CalendarView calendarViewStart = findViewById(R.id.calendarview_start);
        CalendarView calendarViewEnd = findViewById(R.id.calendarview_end);
        Switch typeSwitch = findViewById(R.id.type_switch);

        //check for null value
        if(!isEmpty(editTitle)) {
            updateTitle = editTitle.getText().toString();
        }
        else {
            Toast.makeText(v.getContext(), "Please Enter a Title", Toast.LENGTH_LONG).show();
            return;
        }

        //if either startDate or endDate have not been selected, assign current date
        if(updateStart == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedStartDate = sdf.format(calendarViewStart.getDate());
            updateStart = DateManager.deFormatDate(formattedStartDate);
        }
        if(updateEnd == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedEndDate = sdf.format(calendarViewEnd.getDate());
            updateEnd = DateManager.deFormatDate(formattedEndDate);
        }

        //check that startDate is either equal to or before endDate
        if(!DateManager.isStartBeforeEnd(updateStart, updateEnd)) {
            Toast.makeText(v.getContext(), "Start Date cannot be after End Date", Toast.LENGTH_LONG).show();
            return;
        }

        //get switch values
        if(typeSwitch.isChecked()) {
            updateType = getString(R.string.Performance);
        }
        else {
            updateType = getString(R.string.Objective);
        }


        //create assessment object
        updateAssessment = new Assessment(updateTitle, updateStart, updateEnd, updateType);

        //insert assessment object
        UpdateRunnable updateRunnable = new UpdateRunnable();
        new Thread(updateRunnable).start();


        //refresh page
        launchAssessments();
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

        fragment = new SetAlertAssessmentsFragment();

       SetAlertsRunnable setAlertsRunnable = new SetAlertsRunnable();
       new Thread(setAlertsRunnable).start();

        //disable toolbar icons
        if(toolbar != null) {
            toolbar.findViewById(R.id.add_action).setEnabled(false);
            toolbar.findViewById(R.id.info_action).setEnabled(false);
            toolbar.findViewById(R.id.alert_action).setEnabled(false);
            toolbar.findViewById(R.id.delete_action).setEnabled(false);
        }
    }



    //check if edit text is empty
    public boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }



    //implement AssessmentFragmentListener interface to collect CalendarView values
    @Override
    public void onSelectDate(String date) {
        newStart = date;
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

    //delete every selected assessment record
    class DeleteRunnable implements Runnable {

        @Override
        public void run() {
            AssessmentDao assessmentDao = StudentDatabase.getInstance(getApplicationContext()).assessmentDao();

            //delete all selected term records
            for (int i = 0; i < selected.size(); i++) {
                assessmentDao.deleteById(selected.get(i));
            }

            //notify user of success
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "SUCCESS: Assessment(s) Deleted", Toast.LENGTH_SHORT).show();
                }
            });

            //refresh page
            launchAssessments();
        }
    }

    //update assessment record
    class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            AssessmentDao assessmentDao = StudentDatabase.getInstance(getApplicationContext()).assessmentDao();
            assessmentDao.updateById(selected.get(0), updateAssessment.getTitle(), updateAssessment.getStartDate(), updateAssessment.getEndDate(), updateAssessment.getType());
        }
    }

    //select assessment for update fragment
    class SelectForUpdateRunnable implements  Runnable {

        @Override
        public void run() {
            AssessmentDao assessmentDao = StudentDatabase.getInstance(getApplicationContext()).assessmentDao();

            Assessment altThreadAssessment = assessmentDao.selectByID(selected.get(0));
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateAssessment = altThreadAssessment;
                    getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
                    //bundle to pass data for Detailed Assessment Fragment
                    Bundle detailsBundle = new Bundle();
                    if(updateAssessment != null) {
                        detailsBundle.putInt("ID", Integer.valueOf(selected.get(0)));
                        detailsBundle.putString("TITLE", updateAssessment.getTitle());
                        detailsBundle.putString("START_DATE", updateAssessment.getStartDate());
                        detailsBundle.putString("END_DATE", updateAssessment.getEndDate());
                        detailsBundle.putString("TYPE", updateAssessment.getType());
                    }
                    fragment.setArguments(detailsBundle);
                }
            });
        }
    }

    //query for lists of enabled and disabled assessments and opens SetAlertsFragment
    class SetAlertsRunnable implements Runnable {

        @Override
        public void run() {

            AssessmentAlertLinkDao linkDao = StudentDatabase.getInstance(getApplicationContext()).assessmentAlertLinkDao();

            //list of assessments based on alert enablement
            ArrayList<Assessment> enabledAlerts = new ArrayList<>();
            ArrayList<Assessment> disabledAlerts = new ArrayList<>();
            enabledAlerts.addAll(linkDao.getAssessmentsByEnablement(true));
            disabledAlerts.addAll(linkDao.getAssessmentsByEnablement(false));

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
                            enabledID.add(enabledAlerts.get(i).getAssessmentID());
                            enabledTitle.add(enabledAlerts.get(i).getTitle());
                        }
                        alertBundle.putIntegerArrayList("ENABLED_ID", enabledID);
                        alertBundle.putStringArrayList("ENABLED_TITLE", enabledTitle);
                    }
                    if (disabledAlerts.size() != 0) {
                        ArrayList<Integer> disabledID = new ArrayList<>();
                        ArrayList<String> disabledTitle = new ArrayList<>();
                        for (int i = 0; i < disabledAlerts.size(); i++) {
                            disabledID.add(disabledAlerts.get(i).getAssessmentID());
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


    //query database for all assessments
    class SelectRunnable implements Runnable {

        @Override
        public void run() {
            AssessmentDao assessmentDao = StudentDatabase.getInstance(getApplicationContext()).assessmentDao();
            //get all assessments into container
            List<Assessment> returnedQueryList = assessmentDao.getAllAssessments();

            selectQueryResults = returnedQueryList;


            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(selectQueryResults != null ) {
                        for (int i = 0; i < selectQueryResults.size(); i++) {
                            //add term info and notify adapter
                            type.add(selectQueryResults.get(i).getType());
                            title.add(selectQueryResults.get(i).getTitle());
                            id.add(selectQueryResults.get(i).getAssessmentID());
                            adapter.notifyItemInserted(type.size()-1);
                            adapter.notifyItemInserted(title.size()-1);
                            adapter.notifyItemInserted(id.size()-1);
                        }
                    }
                }
            });
        }
    }

    //insert new assessment object
    class InsertRunnable implements Runnable {

        @Override
        public void run() {
            //insert assessment
            AssessmentDao assessmentDao = StudentDatabase.getInstance(getApplicationContext()).assessmentDao();
            assessmentDao.insertAssessment(insertAssessment);

            //add to assessment_alert table with alerts off by default
            int newestAssessmentID = assessmentDao.getLastInsertedID();
            AssessmentAlertLinkDao linkDao = StudentDatabase.getInstance(getApplicationContext()).assessmentAlertLinkDao();
            linkDao.insertNewAlert(new AssessmentAlertLink(newestAssessmentID, false));
        }
    }

    //select a single assessment by ID and apply info to details fragment and open detailed term fragment
    class SelectByIdRunnable implements Runnable {

        @Override
        public void run() {
            AssessmentDao assessmentDao = StudentDatabase.getInstance(getApplicationContext()).assessmentDao();

            //get assessment info
            Assessment altThreadAssessment = assessmentDao.selectByID(selected.get(0));

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    selectedAssessment = altThreadAssessment;
                    getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
                    //bundle to pass data for Detailed Term Fragment
                    Bundle detailsBundle = new Bundle();
                    if(selectedAssessment != null) {
                        //send term info
                        detailsBundle.putInt("ID", Integer.valueOf(selected.get(0)));
                        detailsBundle.putString("TITLE", selectedAssessment.getTitle());
                        detailsBundle.putString("START_DATE", selectedAssessment.getStartDate());
                        detailsBundle.putString("END_DATE", selectedAssessment.getEndDate());
                        detailsBundle.putString("TYPE", selectedAssessment.getType());
                    }
                    fragment.setArguments(detailsBundle);
                }
            });
        }
    }

    //create notification channel
    private void createStartNotificationChannel() {
        String channelName = "notifyStartChannel";
        String channelDescription = "Assessment Start Alert Channel";
        int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel("notifyAssessmentStartTime", channelName, channelImportance);
        notificationChannel.setDescription(channelDescription);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}





















