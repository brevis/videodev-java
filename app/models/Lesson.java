package models;

import play.data.validation.Constraints;
import play.db.*;
import play.db.ebean.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Entity
public class Lesson extends Model {

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

    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<Member> viewHistory = new ArrayList<>();

    public static Model.Finder<String, Lesson> find = new Model.Finder<>(String.class, Lesson.class);

    public Lesson(Course course, String title, String playerCode) {
        this.course = course;
        this.title = title;
        this.playerCode = playerCode;
        this.postdate = new Date();
    }

}
