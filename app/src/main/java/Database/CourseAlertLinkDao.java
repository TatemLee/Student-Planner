package Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CourseAlertLinkDao {
    //no delete query required in this case, courses table will cascade on course record delete

    //get list of courses based on Alert enabled or disabled
    @Query("SELECT courses.* FROM courses JOIN course_alert ON courses.courseID = course_alert.courseID WHERE course_alert.isEnabled = :enablement")
    List<Course> getCoursesByEnablement(boolean enablement);

    //change assessment enablement
    @Query("UPDATE course_alert SET isEnabled = :enablement WHERE courseID = :id")
    void updateEnablementByID(boolean enablement, int id);

    //add course to ScheduledAlertCourses
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScheduledAlert(ScheduledAlertCourses scheduledAlert);

    @Query("DELETE FROM scheduled_alert_courses WHERE courseID = :id")
    void deleteAlertByID(int id);

    //get unique ids for alert notifications
    @Query("SELECT AlertID FROM scheduled_alert_courses WHERE courseID = :courseID")
    List<Integer> getAlertIDs(int courseID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewAlert(CourseAlertLink courseAlertLink);
}
