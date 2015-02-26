package models;

import play.db.*;
import play.mvc.Http;

import java.util.Date;
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

    public static Model.Finder<String, Member> find = new Model.Finder<String, Member>(String.class, Member.class);

    public Member(String firstName, String lastName, String email, String facebookId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.facebookId = facebookId;
    }

    @Deprecated
    public void save() {}

    public void saveMember() throws Exception {
        if (!this.facebookId.matches("\\d+")) {
            throw new Exception("[facebookId] field empty");
        }
        super.save();
    }

}
