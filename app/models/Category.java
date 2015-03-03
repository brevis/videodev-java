package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import play.db.*;
import play.mvc.Http;

import javax.persistence.*;
import play.db.ebean.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Category extends Model {

    @Id
    public String slug;

    public String name;

    @OneToMany
    public List<Course> courses;

    public static Model.Finder<String, Category> find = new Model.Finder<>(String.class, Category.class);

    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public static List<Category> getNonEmptyCategories(Member member) {
        String sql  = " select category.slug, category.name "
                    + " from course "
                    + " join category on category.slug = course.category_slug "
                    + " where category.slug is not null ";
        if (member != null) sql += " and course.member_facebook_id = '" + member.facebookId + "' ";
        sql += " group by category.slug ";

        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        com.avaje.ebean.Query<Category> ebeanQuery = Ebean.find(Category.class);
        ebeanQuery.setRawSql(rawSql);
        return ebeanQuery.findList();
    }

}
