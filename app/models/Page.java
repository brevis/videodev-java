package models;

import play.data.validation.Constraints;
import play.db.DB;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Entity
public class Page extends Model {

    @Id
    public String slug;

    @Constraints.Required
    public String title;

    @Constraints.Required
    public String content;

    public static Finder<String, Page> find = new Finder<String, Page>(String.class, Page.class);

    public Page(String slug, String title, String content) {
        this.slug = slug;
        this.title = title;
        this.content = content;
    }

}
