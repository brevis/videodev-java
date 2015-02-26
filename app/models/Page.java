package models;

import play.data.validation.Constraints;
import play.db.*;
import play.db.ebean.*;
import javax.persistence.*;


@Entity
public class Page extends Model {

    @Id
    public String slug;

    @Constraints.Required
    public String title;

    @Constraints.Required
    public String content;

    public static Model.Finder<String, Page> find = new Model.Finder<>(String.class, Page.class);

    public Page(String slug, String title, String content) {
        this.slug = slug;
        this.title = title;
        this.content = content;
    }

}
