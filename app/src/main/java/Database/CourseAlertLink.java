package Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "course_alert",
        foreignKeys = {
                @ForeignKey(entity = Course.class,
                        parentColumns = "courseID",
                        childColumns = "courseID",
                        onDelete = ForeignKey.CASCADE)})
public class CourseAlertLink {

    //constructor
    public CourseAlertLink(int courseID, boolean isEnabled) {
        this.courseID = courseID;
        this.isEnabled = isEnabled;
    }

    //fields
    @PrimaryKey(autoGenerate = true)
    private int courseAlertKey;

    private int courseID;

    private boolean isEnabled;

    public int getCourseAlertKey() {
        return courseAlertKey;
    }

    public void setCourseAlertKey(int courseAlertKey) {
        this.courseAlertKey = courseAlertKey;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
