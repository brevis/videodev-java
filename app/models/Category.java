package models;

import play.db.*;
import play.mvc.Http;

import javax.persistence.*;
import play.db.ebean.*;

import java.util.List;

@Entity
public class Category extends Model {

    @Id
    public String slug;

    public String name;

    public static Model.Finder<String, Category> find = new Model.Finder<>(String.class, Category.class);

    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public static List<Category> getActiveCategories() {
        List<Category> categories = find.findList();
        return categories;

        /*
        return find.where()
                .eq("lessons_cont > 0", false)
                .findList();
        */
    }

}
