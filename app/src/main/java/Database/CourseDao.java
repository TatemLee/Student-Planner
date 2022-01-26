package Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CourseDao {


    //return list of all courses
    @Query("SELECT * FROM courses")
    public List<Course> getAllCourses();

    //get course note by ID
    @Query("SELECT course_note FROM courses WHERE courseID = :id")
    public String getCourseNote(int id);

    //get course by ID
    @Query("SELECT * FROM courses WHERE courseID = :id")
    public Course getCourseByID(int id);

    //get last inserted course
    @Query("SELECT * FROM courses WHERE courseID = (SELECT MAX(courseID) FROM courses) ")
    public int getLastInsertedID();

    //insert new course
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCourse(Course course);

    //update course by id
    @Query("UPDATE courses SET " +
            "title = :title, " +
            "start_date = :start, " +
            "end_date = :end, " +
            "status = :status, " +
            "instructor_name = :name, " +
            "instructor_phone_number = :number, " +
            "instructor_email = :email, " +
            "course_note = :notes " +
            "WHERE courseID = :id")
    public void updateByID(int id, String title, String start, String end, String status, String name, String number, String email, String notes);

    //delete course by ID
    @Query("DELETE FROM courses WHERE courseID = :id")
    public void deleteByID(int id);
}
