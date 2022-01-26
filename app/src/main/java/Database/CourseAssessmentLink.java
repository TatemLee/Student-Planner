package Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "course_assessment",
        foreignKeys = {
                @ForeignKey(entity = Course.class,
                        parentColumns = "courseID",
                        childColumns = "courseID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Assessment.class,
                        parentColumns = "assessmentID",
                        childColumns = "assessmentID",
                        onDelete = ForeignKey.CASCADE)
                     })
public class CourseAssessmentLink {


    public CourseAssessmentLink(int courseID, int assessmentID) {
        this.courseID = courseID;
        this.assessmentID = assessmentID;
    }

    //fields
    @PrimaryKey(autoGenerate = true)
    private int courseAssessmentKey;

    private int courseID;

    private int assessmentID;

    private String title;

    //getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCourseAssessmentKey() {
        return courseAssessmentKey;
    }

    public void setCourseAssessmentKey(int courseAssessmentKey) {
        this.courseAssessmentKey = courseAssessmentKey;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getAssessmentID() {
        return assessmentID;
    }

    public void setAssessmentID(int assessmentID) {
        this.assessmentID = assessmentID;
    }
}
