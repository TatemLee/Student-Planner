package Database;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

//uses AssessmentAlertLinkDao
@Entity(tableName = "scheduled_alert_id",
        foreignKeys = {
                @ForeignKey(entity = Assessment.class,
                        parentColumns = "assessmentID",
                        childColumns = "assessmentID",
                        onDelete = ForeignKey.CASCADE)})
public class ScheduledAlertAssessments {

    //constructor
    public ScheduledAlertAssessments(int assessmentID) {
        this.assessmentID = assessmentID;
    }

    //fields
    @PrimaryKey(autoGenerate = true)
    private int AlertID;

    private int assessmentID;

    public int getAlertID() {
        return AlertID;
    }

    public void setAlertID(int alertID) {
        AlertID = alertID;
    }

    public int getAssessmentID() {
        return assessmentID;
    }

    public void setAssessmentID(int assessmentID) {
        this.assessmentID = assessmentID;
    }
}
