package Database;

import android.app.Application;

import androidx.room.Room;


public class StudentApplication extends Application {

    StudentDatabase studentDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        String NAME = "studentDatabase";

        studentDatabase = Room.databaseBuilder(this, StudentDatabase.class, NAME).fallbackToDestructiveMigration().build();
    }

    public StudentDatabase getStudentDatabase;
}
