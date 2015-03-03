package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import controllers.Auth;
import play.data.validation.Constraints;
import play.db.*;
import play.db.ebean.*;
import services.AuthService;

import javax.persistence.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    public List<Lesson> lessons;

    public Date updateDate;

    public Integer viewsCount;

    public static Model.Finder<String, Course> find = new Model.Finder<>(String.class, Course.class);

    private Lesson lastViewedLesson = null;

    public Course(Member member, Category category, String title, String description, CourseType type) {
        this.member = member;
        this.category = category;
        this.title = title;
        this.description = description;
        this.type = type;
        this.cover = null;
        this.viewsCount = 0;
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
        if (category != null && !category.equals("")) expr.eq("category.slug", category);
        if (member != null) expr.eq("member", member);
        return expr
                .orderBy("updateDate DESC")
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page-1);
    }

    public List<Lesson> lessons() {
        Collections.sort(lessons);
        return lessons;
    }

    @Override
    public void save() {
        super.save();
        if (lessons.size() > 0) {
            updateDate = lessons.get(lessons.size() - 1).postdate;
            super.save();
        }
    }

    @Override
    public void update() {
        super.update();
        if (lessons.size() > 0) {
            updateDate = lessons.get(lessons.size() - 1).postdate;
            super.update();
        }
    }

    @Override
    public void delete() {
        deleteViewHistory();
        super.delete();
    }

    public void deleteViewHistory() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = play.db.DB.getConnection();
            stmt = conn.createStatement();
            stmt.execute("delete from member_course where course_id = " + this.id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            conn = null;
            stmt = null;
        } finally {
            try {
                conn.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void updateViewsCount() {
        this.viewsCount++;
        this.update();
    }

    public boolean hasUnreadLessons() {
        if (!AuthService.isLogged()) return false;
        Lesson lastLesson = getLastViewedLesson();
        return lastLesson == null || lastLesson.postdate.before(this.updateDate);
    }

    public Lesson getFirstUnreadLesson() {
        if (!AuthService.isLogged()) return null;

        Lesson lastLesson = getLastViewedLesson();
        if (lastLesson == null) return null;

        Lesson firstUnreadLesson = null;
        boolean found = false;
        for (Lesson l : this.lessons()) {
            if (found) {
                firstUnreadLesson = l;
                break;
            }
            if (l.equals(lastLesson)) found = true;
        }
        return firstUnreadLesson;
    }

    public Integer getFirstUnreadLessonNumber() {
        Integer l = 1;
        Lesson firstUnread = getFirstUnreadLesson();
        if (firstUnread != null) {
            for (int i=0; i<lessons().size(); i++) {
                System.out.println("1: " + lessons().get(i).id + ", 2: " + firstUnread.id);
                if (lessons().get(i).id.equals(firstUnread.id)) {
                    l = i + 1;
                    break;
                }
            }
        }

        return l;
    }

    /*------------------------------------------------------------------------------------*/

    private Lesson getLastViewedLesson() {
        if (!AuthService.isLogged()) return null;
        if (lastViewedLesson != null) return lastViewedLesson;
        String sql  = " select lesson.id, lesson.title, lesson.player_code, lesson.postdate "
            + " from member_lesson "
            + " join lesson on member_lesson.lesson_id = lesson.id "
            + " join member on member_lesson.member_facebook_id = member.facebook_id "
            + " where lesson.course_id = " + this.id + " and member.facebook_id = '" + AuthService.getCurrentMember().facebookId +"' "
            + " order by lesson.id DESC "
            + " LIMIT 1";
        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        com.avaje.ebean.Query<Lesson> ebeanQuery = Ebean.find(Lesson.class);
        ebeanQuery.setRawSql(rawSql);
        lastViewedLesson = ebeanQuery.findUnique();
        return lastViewedLesson;
    }

}
