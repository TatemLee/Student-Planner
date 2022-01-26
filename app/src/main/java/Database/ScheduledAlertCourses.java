package Database;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

//uses CoursesAlertLinkDao
@Entity(tableName = "scheduled_alert_courses",
        foreignKeys = {
                @ForeignKey(entity = Course.class,
                        parentColumns = "courseID",
                        childColumns = "courseID",
                        onDelete = ForeignKey.CASCADE)})
public class ScheduledAlertCourses {

    //constructor
    public ScheduledAlertCourses(int courseID) {
        this.courseID = courseID;
    }

    //fields
    @PrimaryKey(autoGenerate = true)
    private int AlertID;

    private int courseID;

    public int getAlertID() {
        return AlertID;
    }

    public void setAlertID(int alertID) {
        AlertID = alertID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }
}
