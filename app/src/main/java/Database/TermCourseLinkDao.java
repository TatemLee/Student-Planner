package Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TermCourseLinkDao {

    //get list of courses assigned to a particular assessment id
    @Query("SELECT courseID FROM term_course WHERE termID = :id")
    List<Integer> getAssignedCourses(int id);


    //delete assignment by ID
    @Query("DELETE FROM term_course WHERE termID = :id and courseID = :id2")
    void deleteByID(int id, int id2);

    //insert new course
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewCourse(TermCourseLink termCourseLink);
}
