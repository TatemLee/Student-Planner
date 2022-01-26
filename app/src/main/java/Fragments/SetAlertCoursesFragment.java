package Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.studentplanner.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import Controllers.AlertBroadcastCourse;
import Database.Course;
import Database.CourseAlertLinkDao;
import Database.CourseDao;
import Database.ScheduledAlertCourses;
import Database.StudentDatabase;


public class SetAlertCoursesFragment extends Fragment {

    static int courseID;
    static boolean enablement;
    Handler mainHandler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_alert_courses, container, false);


        Spinner enabledSpinner = v.findViewById(R.id.enabled_title_spinner);
        Spinner enabledSpinnerID = v.findViewById(R.id.enabled_id_spinner);
        Spinner disabledSpinner = v.findViewById(R.id.disabled_title_spinner);
        Spinner disabledSpinnerID = v.findViewById(R.id.disabled_id_spinner);

        ArrayList<Integer> enabledID = new ArrayList<>();
        ArrayList<String> enabledTitle = new ArrayList<>();
        ArrayList<Integer> disabledID = new ArrayList<>();
        ArrayList<String> disabledTitle = new ArrayList<>();

        //get bundle
        Bundle bundle = this.getArguments();
        //set adapters for spinners
        ArrayAdapter<Integer> enabledIDAdapter = new ArrayAdapter<Integer>(v.getContext(),
                android.R.layout.simple_spinner_item);
        enabledIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        enabledSpinnerID.setAdapter(enabledIDAdapter);
        if (bundle.getIntegerArrayList("ENABLED_ID") != null) {
            enabledID = bundle.getIntegerArrayList("ENABLED_ID");
            for (int i = 0; i < enabledID.size(); i++) {
                enabledIDAdapter.add(enabledID.get(i));
            }
        }

        ArrayAdapter<String> enabledTitleAdapter = new ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_spinner_item);
        enabledIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        enabledSpinner.setAdapter(enabledTitleAdapter);

        if (bundle.getStringArrayList("ENABLED_TITLE") != null) {
            enabledTitle = bundle.getStringArrayList("ENABLED_TITLE");

            for (int i = 0; i < enabledTitle.size(); i++) {
                enabledTitleAdapter.add(enabledTitle.get(i));
                enabledTitleAdapter.notifyDataSetChanged();
            }
        }

        ArrayAdapter<Integer> disabledIDAdapter = new ArrayAdapter<Integer>(v.getContext(),
                android.R.layout.simple_spinner_item);
        disabledIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disabledSpinnerID.setAdapter(disabledIDAdapter);
        if (bundle.getIntegerArrayList("DISABLED_ID") != null) {
            disabledID = bundle.getIntegerArrayList("DISABLED_ID");
            for (int i = 0; i < disabledID.size(); i++) {
                disabledIDAdapter.add(disabledID.get(i));
            }
        }

        ArrayAdapter<String> disabledTitleAdapter = new ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_spinner_item);
        disabledIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disabledSpinner.setAdapter(disabledTitleAdapter);

        if (bundle.getStringArrayList("DISABLED_TITLE") != null) {
            disabledTitle = bundle.getStringArrayList("DISABLED_TITLE");

            for (int i = 0; i < disabledTitle.size(); i++) {
                disabledTitleAdapter.add(disabledTitle.get(i));
                disabledTitleAdapter.notifyDataSetChanged();
            }
        }

        //set spinner listeners
        enabledSpinnerID.setEnabled(false);
        disabledSpinnerID.setEnabled(false);

        enabledSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedPosition = enabledSpinner.getSelectedItemPosition();
                enabledSpinnerID.setSelection(selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        disabledSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedPosition = disabledSpinner.getSelectedItemPosition();
                disabledSpinnerID.setSelection(selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //set button listeners
        Button assignButton = v.findViewById(R.id.bt_assign);
        Button removeButton = v.findViewById(R.id.bt_remove);
        Button startOnlyButton = v.findViewById(R.id.bt_assign_start);
        Button endOnlyButton = v.findViewById(R.id.bt_assign_end);


        //enable alert button
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //perform enable alert
                //get course ID
                courseID = Integer.parseInt(disabledSpinnerID.getSelectedItem().toString());
                enablement = true;
                ChangeEnablementRunnable changeEnablementRunnable = new ChangeEnablementRunnable();
                new Thread(changeEnablementRunnable).start();

                //perform UI changes
                //swap items to enable an alert
                enabledIDAdapter.add(Integer.parseInt(disabledSpinnerID.getSelectedItem().toString()));
                enabledIDAdapter.notifyDataSetChanged();
                enabledTitleAdapter.add(disabledSpinner.getSelectedItem().toString());
                enabledTitleAdapter.notifyDataSetChanged();
                //remove items from unassigned adapter
                disabledIDAdapter.remove(Integer.parseInt(disabledSpinnerID.getSelectedItem().toString()));
                disabledTitleAdapter.remove(disabledSpinner.getSelectedItem().toString());
            }
        });

        //enable alert button
        startOnlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //perform enable alert
                //get course ID
                courseID = Integer.parseInt(disabledSpinnerID.getSelectedItem().toString());
                enablement = true;
                StartChangeEnablementRunnable changeEnablementRunnable = new StartChangeEnablementRunnable();
                new Thread(changeEnablementRunnable).start();

                //perform UI changes
                //swap items to enable an alert
                enabledIDAdapter.add(Integer.parseInt(disabledSpinnerID.getSelectedItem().toString()));
                enabledIDAdapter.notifyDataSetChanged();
                enabledTitleAdapter.add(disabledSpinner.getSelectedItem().toString());
                enabledTitleAdapter.notifyDataSetChanged();
                //remove items from unassigned adapter
                disabledIDAdapter.remove(Integer.parseInt(disabledSpinnerID.getSelectedItem().toString()));
                disabledTitleAdapter.remove(disabledSpinner.getSelectedItem().toString());
            }
        });

        //enable alert button
        endOnlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //perform enable alert
                //get course ID
                courseID = Integer.parseInt(disabledSpinnerID.getSelectedItem().toString());
                enablement = true;
                EndChangeEnablementRunnable changeEnablementRunnable = new EndChangeEnablementRunnable();
                new Thread(changeEnablementRunnable).start();

                //perform UI changes
                //swap items to enable an alert
                enabledIDAdapter.add(Integer.parseInt(disabledSpinnerID.getSelectedItem().toString()));
                enabledIDAdapter.notifyDataSetChanged();
                enabledTitleAdapter.add(disabledSpinner.getSelectedItem().toString());
                enabledTitleAdapter.notifyDataSetChanged();
                //remove items from unassigned adapter
                disabledIDAdapter.remove(Integer.parseInt(disabledSpinnerID.getSelectedItem().toString()));
                disabledTitleAdapter.remove(disabledSpinner.getSelectedItem().toString());
            }
        });

        //disable alert button
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //perform disable alert
                //get course ID
                courseID = Integer.parseInt(enabledSpinnerID.getSelectedItem().toString());
                enablement = false;
                ChangeEnablementRunnable changeEnablementRunnable = new ChangeEnablementRunnable();
                new Thread(changeEnablementRunnable).start();

                //perform UI changes
                //swap items to disable an alert
                disabledIDAdapter.add(Integer.parseInt(enabledSpinnerID.getSelectedItem().toString()));
                disabledIDAdapter.notifyDataSetChanged();
                disabledTitleAdapter.add(enabledSpinner.getSelectedItem().toString());
                disabledTitleAdapter.notifyDataSetChanged();
                //remove items from assigned adapter
                enabledIDAdapter.remove(Integer.parseInt(enabledSpinnerID.getSelectedItem().toString()));
                enabledTitleAdapter.remove(enabledSpinner.getSelectedItem().toString());
            }
        });
        return v;
    }

    //update alert record
    class ChangeEnablementRunnable implements Runnable {

        @Override
        public void run() {
            CourseAlertLinkDao linkDao = StudentDatabase.getInstance(getActivity().getApplicationContext()).courseAlertLinkDao();
            CourseDao courseDao = StudentDatabase.getInstance(getActivity().getApplicationContext()).courseDao();
            linkDao.updateEnablementByID(enablement, courseID);

            if (enablement) {
                //insert for start alert ID
                linkDao.insertScheduledAlert(new ScheduledAlertCourses(courseID));
                //insert for end alert ID
                linkDao.insertScheduledAlert(new ScheduledAlertCourses(courseID));

                Course course = courseDao.getCourseByID(courseID);

                //list of unique ids for alert
                List<Integer> alertIDs = linkDao.getAlertIDs(courseID);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //create start notification

                        //set values for notification
                        //get id for start notification

                        String text = "Course: " + course.getTitle() + " begins today";

                        Intent intentStart = new Intent(getActivity().getApplicationContext(), AlertBroadcastCourse.class);
                        intentStart.putExtra("TEXT", text);
                        intentStart.putExtra("ID", alertIDs.get(0));
                        PendingIntent pendingIntentStart = PendingIntent.getBroadcast(getActivity().getApplicationContext(), alertIDs.get(0), intentStart, PendingIntent.FLAG_MUTABLE);

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                        //get date of start notification
                        String start = course.getStartDate();
                        String startMonth = String.valueOf(start.charAt(0)) + String.valueOf(start.charAt(1));
                        String startDay = String.valueOf(start.charAt(2)) + String.valueOf(start.charAt(3));
                        String startYear = String.valueOf(start.charAt(4)) + String.valueOf(start.charAt(5)) + String.valueOf(start.charAt(6)) + String.valueOf(start.charAt(7));
                        int intMonth = Integer.parseInt(startMonth);
                        int intDay = Integer.parseInt(startDay);
                        int intYear = Integer.parseInt(startYear);


                        Calendar startAlarmDate = Calendar.getInstance();
                        startAlarmDate.setTimeInMillis(System.currentTimeMillis());
                        startAlarmDate.clear();
                        startAlarmDate.set(intYear, intMonth - 1, intDay, 0, 0);
                        //set alert
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startAlarmDate.getTimeInMillis(), pendingIntentStart);


                        //create end notification
                        String textEnd = "Course: "  + course.getTitle() + " ends today";

                        Intent intentEnd = new Intent(getActivity().getApplicationContext(), AlertBroadcastCourse.class);
                        intentEnd.putExtra("TEXT", textEnd);
                        intentEnd.putExtra("ID", alertIDs.get(1));

                        PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(getActivity().getApplicationContext(), alertIDs.get(1), intentEnd, PendingIntent.FLAG_MUTABLE);

                        AlarmManager alarmManagerEnd = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                        //get date of end notification
                        String end = course.getEndDate();
                        String endMonth = String.valueOf(end.charAt(0)) + String.valueOf(end.charAt(1));
                        String endDay = String.valueOf(end.charAt(2)) + String.valueOf(end.charAt(3));
                        String endYear = String.valueOf(end.charAt(4)) + String.valueOf(end.charAt(5)) + String.valueOf(end.charAt(6)) + String.valueOf(end.charAt(7));
                        int intMonthEnd = Integer.parseInt(endMonth);
                        int intDayEnd = Integer.parseInt(endDay);
                        int intYearEnd = Integer.parseInt(endYear);

                        Calendar endAlarmDate = Calendar.getInstance();
                        endAlarmDate.setTimeInMillis(System.currentTimeMillis());
                        endAlarmDate.clear();
                        endAlarmDate.set(intYearEnd, intMonthEnd - 1, intDayEnd, 0, 0);

                        alarmManagerEnd.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endAlarmDate.getTimeInMillis(), pendingIntentEnd);
                    }
                });

            } else if (!enablement) {
                //list of unique ids for alert
                List<Integer> alertIDs = linkDao.getAlertIDs(courseID);
                Course course = courseDao.getCourseByID(courseID);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //cancel alerts

                        //start
                        String text = "Course # " + alertIDs.get(0) + " " + course.getTitle() + " begins today";
                        Intent intentStart = new Intent(getActivity().getApplicationContext(), AlertBroadcastCourse.class);
                        intentStart.putExtra("TEXT", text);
                        intentStart.putExtra("ID", alertIDs.get(0));
                        PendingIntent pendingIntentStart = PendingIntent.getBroadcast(getActivity().getApplicationContext(), alertIDs.get(0), intentStart, PendingIntent.FLAG_MUTABLE);
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntentStart);
                        //end
                        String textEnd = "Course # " + alertIDs.get(1) + " " + course.getTitle() + " ends today";
                        Intent intentEnd = new Intent(getActivity().getApplicationContext(), AlertBroadcastCourse.class);
                        intentEnd.putExtra("TEXT", textEnd);
                        intentEnd.putExtra("ID", alertIDs.get(1));
                        PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(getActivity().getApplicationContext(), alertIDs.get(1), intentEnd, PendingIntent.FLAG_MUTABLE);
                        AlarmManager alarmManagerEnd = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        alarmManagerEnd.cancel(pendingIntentEnd);

                    }
                });
                //remove all notifications for selected item
                linkDao.deleteAlertByID(courseID);
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (enablement)
                        Toast.makeText(getActivity().getApplicationContext(), "Alert Enabled for Course " + courseID, Toast.LENGTH_SHORT).show();
                    else if (!enablement) {
                        Toast.makeText(getActivity().getApplicationContext(), "Alert Disabled for Course " + courseID, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    //update start alert record
    class StartChangeEnablementRunnable implements Runnable {

        @Override
        public void run() {
            CourseAlertLinkDao linkDao = StudentDatabase.getInstance(getActivity().getApplicationContext()).courseAlertLinkDao();
            CourseDao courseDao = StudentDatabase.getInstance(getActivity().getApplicationContext()).courseDao();
            linkDao.updateEnablementByID(enablement, courseID);

            if (enablement) {
                //insert for start alert ID
                linkDao.insertScheduledAlert(new ScheduledAlertCourses(courseID));
                //insert for end alert ID
                linkDao.insertScheduledAlert(new ScheduledAlertCourses(courseID));

                Course course = courseDao.getCourseByID(courseID);

                //list of unique ids for alert
                List<Integer> alertIDs = linkDao.getAlertIDs(courseID);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //create start notification

                        //set values for notification
                        //get id for start notification

                        String text = "Course: " + course.getTitle() + " begins today";

                        Intent intentStart = new Intent(getActivity().getApplicationContext(), AlertBroadcastCourse.class);
                        intentStart.putExtra("TEXT", text);
                        intentStart.putExtra("ID", alertIDs.get(0));
                        PendingIntent pendingIntentStart = PendingIntent.getBroadcast(getActivity().getApplicationContext(), alertIDs.get(0), intentStart, PendingIntent.FLAG_MUTABLE);

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                        //get date of start notification
                        String start = course.getStartDate();
                        String startMonth = String.valueOf(start.charAt(0)) + String.valueOf(start.charAt(1));
                        String startDay = String.valueOf(start.charAt(2)) + String.valueOf(start.charAt(3));
                        String startYear = String.valueOf(start.charAt(4)) + String.valueOf(start.charAt(5)) + String.valueOf(start.charAt(6)) + String.valueOf(start.charAt(7));
                        int intMonth = Integer.parseInt(startMonth);
                        int intDay = Integer.parseInt(startDay);
                        int intYear = Integer.parseInt(startYear);


                        Calendar startAlarmDate = Calendar.getInstance();
                        startAlarmDate.setTimeInMillis(System.currentTimeMillis());
                        startAlarmDate.clear();
                        startAlarmDate.set(intYear, intMonth - 1, intDay, 0, 0);
                        //set alert
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startAlarmDate.getTimeInMillis(), pendingIntentStart);

                    }
                });

            } else if (!enablement) {
                //list of unique ids for alert
                List<Integer> alertIDs = linkDao.getAlertIDs(courseID);
                Course course = courseDao.getCourseByID(courseID);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //cancel alerts

                        //start
                        String text = "Course # " + alertIDs.get(0) + " " + course.getTitle() + " begins today";
                        Intent intentStart = new Intent(getActivity().getApplicationContext(), AlertBroadcastCourse.class);
                        intentStart.putExtra("TEXT", text);
                        intentStart.putExtra("ID", alertIDs.get(0));
                        PendingIntent pendingIntentStart = PendingIntent.getBroadcast(getActivity().getApplicationContext(), alertIDs.get(0), intentStart, PendingIntent.FLAG_MUTABLE);
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntentStart);
                    }
                });
                //remove all notifications for selected item
                linkDao.deleteAlertByID(courseID);
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (enablement)
                        Toast.makeText(getActivity().getApplicationContext(), "End Date Alert Enabled for Course " + courseID, Toast.LENGTH_SHORT).show();
                    else if (!enablement) {
                        Toast.makeText(getActivity().getApplicationContext(), "End Date Alert Disabled for Course " + courseID, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    //update alert record
    class EndChangeEnablementRunnable implements Runnable {

        @Override
        public void run() {
            CourseAlertLinkDao linkDao = StudentDatabase.getInstance(getActivity().getApplicationContext()).courseAlertLinkDao();
            CourseDao courseDao = StudentDatabase.getInstance(getActivity().getApplicationContext()).courseDao();
            linkDao.updateEnablementByID(enablement, courseID);

            if (enablement) {
                //insert for start alert ID
                linkDao.insertScheduledAlert(new ScheduledAlertCourses(courseID));
                //insert for end alert ID
                linkDao.insertScheduledAlert(new ScheduledAlertCourses(courseID));

                Course course = courseDao.getCourseByID(courseID);

                //list of unique ids for alert
                List<Integer> alertIDs = linkDao.getAlertIDs(courseID);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //create start notification

                        //set values for notification

                        //create end notification
                        String textEnd = "Course: "  + course.getTitle() + " ends today";

                        Intent intentEnd = new Intent(getActivity().getApplicationContext(), AlertBroadcastCourse.class);
                        intentEnd.putExtra("TEXT", textEnd);
                        intentEnd.putExtra("ID", alertIDs.get(1));

                        PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(getActivity().getApplicationContext(), alertIDs.get(1), intentEnd, PendingIntent.FLAG_MUTABLE);

                        AlarmManager alarmManagerEnd = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                        //get date of end notification
                        String end = course.getEndDate();
                        String endMonth = String.valueOf(end.charAt(0)) + String.valueOf(end.charAt(1));
                        String endDay = String.valueOf(end.charAt(2)) + String.valueOf(end.charAt(3));
                        String endYear = String.valueOf(end.charAt(4)) + String.valueOf(end.charAt(5)) + String.valueOf(end.charAt(6)) + String.valueOf(end.charAt(7));
                        int intMonthEnd = Integer.parseInt(endMonth);
                        int intDayEnd = Integer.parseInt(endDay);
                        int intYearEnd = Integer.parseInt(endYear);

                        Calendar endAlarmDate = Calendar.getInstance();
                        endAlarmDate.setTimeInMillis(System.currentTimeMillis());
                        endAlarmDate.clear();
                        endAlarmDate.set(intYearEnd, intMonthEnd - 1, intDayEnd, 0, 0);

                        alarmManagerEnd.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endAlarmDate.getTimeInMillis(), pendingIntentEnd);
                    }
                });

            } else if (!enablement) {
                //list of unique ids for alert
                List<Integer> alertIDs = linkDao.getAlertIDs(courseID);
                Course course = courseDao.getCourseByID(courseID);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //cancel alert

                        //end
                        String textEnd = "Course # " + alertIDs.get(1) + " " + course.getTitle() + " ends today";
                        Intent intentEnd = new Intent(getActivity().getApplicationContext(), AlertBroadcastCourse.class);
                        intentEnd.putExtra("TEXT", textEnd);
                        intentEnd.putExtra("ID", alertIDs.get(1));
                        PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(getActivity().getApplicationContext(), alertIDs.get(1), intentEnd, PendingIntent.FLAG_MUTABLE);
                        AlarmManager alarmManagerEnd = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        alarmManagerEnd.cancel(pendingIntentEnd);

                    }
                });
                //remove all notifications for selected item
                linkDao.deleteAlertByID(courseID);
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (enablement)
                        Toast.makeText(getActivity().getApplicationContext(), "End Date Alert Enabled for Course " + courseID, Toast.LENGTH_SHORT).show();
                    else if (!enablement) {
                        Toast.makeText(getActivity().getApplicationContext(), "End Date Alert Disabled for Course " + courseID, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }




}