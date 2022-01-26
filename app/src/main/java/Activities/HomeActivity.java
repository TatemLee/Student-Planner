package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.icu.lang.UScript;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ScrollView;

import com.example.studentplanner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import Fragments.TutorialFragment;


public class HomeActivity extends AppCompatActivity {



   //Instantiate navigation bar
    BottomNavigationView navBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*
        //populate legend/tutorial fragment
        ScrollView scrollView = findViewById(R.id.scroll_view);

        Fragment fragment = new TutorialFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.scroll_view, fragment).commit();
         */
        //set toolbar title
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Academic Planner");


        //Nav bar assignment
        navBar = findViewById(R.id.bottom_nav_bar);

        //Set selected Nav Bar item
        navBar.setSelectedItemId(R.id.nav_home);

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
    }



    @Override
    public void onBackPressed() {
        launchHome();
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
    public void launchAssessments() {
        Intent i = new Intent(this, AssessmentActivity.class);
        startActivity(i);
    }

}
