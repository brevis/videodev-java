package models;

import play.data.validation.Constraints;
import play.db.*;
import play.db.ebean.*;
import javax.persistence.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

@Entity
public class Lesson extends Model implements Comparable<Lesson>, Comparator<Lesson> {

    @Id
    public Integer id;

    @Constraints.Required
    public String title;

    @Constraints.Required
    @Column(columnDefinition = "TEXT")
    public String playerCode;

    public Date postdate;

    @ManyToOne
    public Course course;

    public static Model.Finder<String, Lesson> find = new Model.Finder<>(String.class, Lesson.class);

    public Lesson(Course course, String title, String playerCode) {
        this.course = course;
        this.title = title;
        this.playerCode = playerCode;
        this.postdate = new Date();
    }

    public int compareTo(Lesson l) {
        return id.compareTo(l.id);
    }

    public int compare(Lesson l1, Lesson l2) {
        return l1.id - l2.id;
    }

    public void save() {
        super.save();

        if (course != null) {
            course.deleteViewHistory();
        }
    }

    @Override
    public void delete() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = play.db.DB.getConnection();
            stmt = conn.createStatement();
            stmt.execute("delete from member_lesson where lesson_id = " + this.id);
        } catch (Exception e) {
            conn = null;
            stmt = null;
        } finally {
            try {
                conn.close();
                stmt.close();
            } catch (Exception e) {

            }
        }

        super.delete();
    }

}
