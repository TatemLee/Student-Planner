package Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CourseAssessmentLinkDao {

    //get list of assessments assigned to a particular course id
    @Query("SELECT assessmentID FROM course_assessment WHERE courseID = :id")
    List<Integer> getAssignedAssessments(int id);

    @Query("DELETE FROM course_assessment WHERE courseID = :id and assessmentID = :id2")
    void deleteByID(int id, int id2);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewAssignment(CourseAssessmentLink courseAssessmentLink);


}
