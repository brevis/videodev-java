package models;

import com.avaje.ebean.ExpressionList;
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

    @ManyToOne
    @Constraints.Required
    public Member member;

    @OneToOne
    public Cover cover;

    @OneToMany
    public List<Lesson> lessons = new ArrayList<>();

    public static Model.Finder<String, Course> find = new Model.Finder<>(String.class, Course.class);

    public Course(Member member, Category category, String title, String description, CourseType type) {
        this.member = member;
        this.category = category;
        this.title = title;
        this.description = description;
        this.type = type;
        this.cover = null;
    }

    /**
     * Return a page of Course
     *
     * @param page Integer
     * @param category Filter applied on the name column
     */
    public static com.avaje.ebean.Page<Course> page(Integer page, Integer pageSize, String category, Member member) {
        if (page < 1) page = 1; // normalize page (start from 1)

        ExpressionList<Course> expr = find.where();
        if (!category.equals("")) expr.eq("category.slug", category);
        if (member != null) expr.eq("member", member);
        return expr
                .orderBy("title ASC")
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page-1);
    }

}
