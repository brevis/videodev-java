package models;

import play.data.validation.Constraints;
import play.db.*;
import play.db.ebean.*;
import javax.persistence.*;

@Entity
public class Cover extends Model {

    public static String[] availableContentTypes = {"image/jpeg", "image/png", "image/gif"};

    @Id
    public Integer id;

    @Lob
    @Constraints.Required
    public byte[] data;

    @Constraints.Required
    public String contentType;

    public static Model.Finder<String, Cover> find = new Model.Finder<>(String.class, Cover.class);

    public Cover(byte[] data, String contentType) {
        this.data = data;
        this.contentType = contentType;
    }

}
