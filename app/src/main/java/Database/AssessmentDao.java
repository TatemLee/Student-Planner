package Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface  AssessmentDao {

    @Query("SELECT * FROM assessments")
    public List<Assessment> getAllAssessments();

    //update assessment by ID
    @Query("UPDATE assessments SET title = :newTitle, start_date = :newStart, end_date = :newEnd, assessment_type = :newType WHERE assessmentID = :id")
    public void updateById(int id, String newTitle, String newStart, String newEnd, String newType);

    //get last inserted assessment
    @Query("SELECT * FROM assessments WHERE assessmentID = (SELECT MAX(assessmentID) FROM assessments) ")
    public int getLastInsertedID();

    //get assessment by ID
    @Query("SELECT * FROM assessments WHERE assessmentID = :id")
    public Assessment selectByID(int id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAssessment(Assessment assessment);

    @Update
    public void updateAssessment(Assessment assessment);

    @Delete
    public void deleteAssessment(Assessment assessment);

    //delete assessment by ID
    @Query("DELETE FROM assessments WHERE assessmentID = :id")
    public void deleteById(int id);
}
