package Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "assessment_alert",
        foreignKeys = {
                @ForeignKey(entity = Assessment.class,
                        parentColumns = "assessmentID",
                        childColumns = "assessmentID",
                        onDelete = ForeignKey.CASCADE)})
public class AssessmentAlertLink {

    //constructor
    public AssessmentAlertLink(int assessmentID, boolean isEnabled) {
        this.assessmentID = assessmentID;
        this.isEnabled = isEnabled;
    }

    //fields
    @PrimaryKey(autoGenerate = true)
    private int assessmentAlertKey;

    private int assessmentID;

    private boolean isEnabled;

    public int getAssessmentAlertKey() {
        return assessmentAlertKey;
    }

    public void setAssessmentAlertKey(int assessmentAlertKey) {
        this.assessmentAlertKey = assessmentAlertKey;
    }

    public int getAssessmentID() {
        return assessmentID;
    }

    public void setAssessmentID(int assessmentID) {
        this.assessmentID = assessmentID;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
