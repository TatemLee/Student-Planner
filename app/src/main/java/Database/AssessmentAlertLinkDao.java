package Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AssessmentAlertLinkDao {

    //no delete query required in this case, assessments table will cascade on assessment record delete

    //get list of assessments based on Alert enabled or disabled
    @Query("SELECT assessments.* FROM assessments JOIN assessment_alert ON assessments.assessmentID = assessment_alert.assessmentID WHERE assessment_alert.isEnabled = :enablement")
    List<Assessment> getAssessmentsByEnablement(boolean enablement);

    //change assessment enablement
    @Query("UPDATE assessment_alert SET isEnabled = :enablement WHERE assessmentID = :id")
    void updateEnablementByID(boolean enablement, int id);

    //add assessment to ScheduledAlertAssessments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScheduledAlert(ScheduledAlertAssessments scheduledAlert);

    @Query("DELETE FROM scheduled_alert_id WHERE assessmentID = :id")
    void deleteAlertByID(int id);

    //get unique ids for alert notifications
    @Query("SELECT AlertID FROM scheduled_alert_id WHERE assessmentID = :assessmentID")
    List<Integer> getAlertIDs(int assessmentID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewAlert(AssessmentAlertLink assessmentAlertLink);
}
