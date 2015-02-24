package models;

import play.db.DB;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

@Entity
public class Category extends Model {

    @Id
    public String slug;

    public String name;

    public static Finder<String, Category> find = new Finder<String, Category>(String.class, Category.class);

    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

}
