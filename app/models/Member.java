package models;

import play.db.*;
import play.mvc.Http;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;

@Entity
public class Member extends Model {

    @Id
    public String facebookId;
    public String firstName;
    public String lastName;
    public String email;
    public Date registrationDate;
    public Date lastLoginDate;

    public static Finder<String, Member> find = new Finder<String, Member>(String.class, Member.class);

    public Member(String firstName, String lastName, String email, String facebookId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.facebookId = facebookId;
    }

    /*
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    */

    /*
    public static User getUserById(int userId) {
        User user = null;
        try {
            Connection conn = DB.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"User\" WHERE id = " + userId + " LIMIT 1");
            if (rs.next()) {
                user = new User(rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("facebookId"));
                //user.id = rs.getInt("id");
                user.registrationDate = rs.getDate("registrationDate");
                user.lastLoginDate = rs.getDate("lastLoginDate");
            }
        } catch (Exception e) {
            user = null;
        }

        return user;
    }
    */

}
