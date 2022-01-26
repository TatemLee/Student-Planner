package Database;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "term_course",
        foreignKeys = {
                @ForeignKey(entity = Term.class,
                        parentColumns = "termID",
                        childColumns = "termID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Course.class,
                        parentColumns = "courseID",
                        childColumns = "courseID",
                        onDelete = ForeignKey.CASCADE)
        })
public class TermCourseLink {

    //constructor
    public TermCourseLink(int termID, int courseID) {
        this.termID = termID;
        this.courseID = courseID;
    }

    //fields
    @PrimaryKey(autoGenerate = true)
    private int termCourseKey;

    private int termID;

    private int courseID;

    //getters and setters
    public int getTermCourseKey() {
        return termCourseKey;
    }

    public void setTermCourseKey(int termCourseKey) {
        this.termCourseKey = termCourseKey;
    }

    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }
}
