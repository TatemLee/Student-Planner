package Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TermDao {

    //return list of all terms
    @Query("SELECT * FROM terms")
    List<Term> getAllTerms();

    //get term by ID
    @Query("SELECT * FROM terms WHERE termID = :id")
    Term getTermByID(int id);

    //insert new term
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTerm(Term term);

    //update term by id
    @Query("UPDATE terms SET " +
            "title = :title, " +
            "start_date = :start, " +
            "end_date = :end " +
            "WHERE termID = :id")
    void updateByID(int id, String title, String start, String end);


    //delete term by ID
    @Query("DELETE FROM terms WHERE termID = :id")
    void deleteByID(int id);
}
