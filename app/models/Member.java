package models;

import play.db.*;
import play.mvc.Http;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Member extends Model {

    @Id
    public String facebookId;
    public String firstName;
    public String lastName;
    public String email;
    public Date registrationDate;
    public Date lastLoginDate;

    @OneToMany
    public List<Course> courses = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    public Set<Lesson> lessonsViewHistory = new HashSet<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    public Set<Course> coursesViewHistory = new HashSet<>();

    public static Model.Finder<String, Member> find = new Model.Finder<>(String.class, Member.class);

    public Member(String firstName, String lastName, String email, String facebookId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.facebookId = facebookId;
    }

    @Deprecated
    public void save() {}


    public void saveMember() throws Exception {
        if (!this.facebookId.matches("^\\d+$")) {
            throw new Exception("[facebookId] field empty");
        }
        super.save();
    }

    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public void addViewHistory(Lesson lesson) {
        this.lessonsViewHistory.add(lesson);
        this.coursesViewHistory.add(lesson.course);
        this.update();
    }

    public void addViewHistory(Course course) {
        addViewHistory(course.lessons().get(course.lessons().size() - 1));
    }

}
