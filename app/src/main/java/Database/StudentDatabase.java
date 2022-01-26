package Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Assessment.class, Course.class, CourseAssessmentLink.class, Term.class, TermCourseLink.class, AssessmentAlertLink.class, ScheduledAlertAssessments.class, CourseAlertLink.class, ScheduledAlertCourses.class}, version = 1, exportSchema = false)
public abstract class StudentDatabase extends RoomDatabase {

    //Database name
    private static final String DATABASE_NAME = "student_database";

    private static StudentDatabase studentDatabase;

    //singleton
    public static StudentDatabase getInstance(Context context) {
        if (studentDatabase == null) {
            studentDatabase = Room.databaseBuilder(context, StudentDatabase.class,
                    DATABASE_NAME).allowMainThreadQueries().build();
        }
        return studentDatabase;
    }

    //member DAOs

    public abstract AssessmentDao assessmentDao();
    public abstract CourseDao courseDao();
    public abstract TermDao termDao();
    public abstract CourseAssessmentLinkDao courseAssessmentLinkDao();
    public abstract TermCourseLinkDao termCourseLinkDao();
    public abstract AssessmentAlertLinkDao assessmentAlertLinkDao();
    public abstract CourseAlertLinkDao  courseAlertLinkDao();
}
