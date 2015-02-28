package models;

import play.data.validation.Constraints;
import play.db.*;
import play.db.ebean.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course extends Model {

    public enum CourseType {
        COURSE, LESSON
    }

    @Id
    public Integer id;

    @Constraints.Required
    public CourseType type;

    @ManyToOne
    @Constraints.Required
    public Category category;

    @Constraints.Required
    public String title;

    @Constraints.Required
    @Column(columnDefinition = "TEXT")
    public String description;

    public Cover cover = null;

    @ManyToOne
    @Constraints.Required
    public Member member;

    @OneToMany
    public List<Lesson> lessons = new ArrayList<>();

    public static Model.Finder<String, Course> find = new Model.Finder<>(String.class, Course.class);

    public Course(Member member, Category category, String title, String description, CourseType type) {
        this.member = member;
        this.category = category;
        this.title = title;
        this.description = description;
        this.type = type;
    }

}
