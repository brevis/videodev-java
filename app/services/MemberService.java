package services;

import models.Member;
import play.mvc.Http;
import play.api.libs.json.*;

public class MemberService {

    public static void createAuth() {
        JsValue jsValue = Json.parse("{\"fbid\":\"100001381203370\"}");
        Http.Context.current().session().put("auth", Json.stringify(jsValue));
    }

    public static void destroyAuth() {
        Http.Context.current().session().put("auth", "");
    }

    public static boolean isLogged() {
        // check session for "auth" value
        JsValue authData;
        try{
            authData = Json.parse(Http.Context.current().session().get("auth"));
        } catch (Exception e) {
            authData = null;
        }
        if (authData == null) return false;

        // check authData "userId"
        String facebookId;
        try{
            facebookId = authData.$bslash("fbid").toString().replace("\"", ""); // stupid hack to delete quotes
        } catch (Exception e) {
            facebookId = "";
        }
        if (facebookId.equals("")) return false;

        // check user
        Member member = Member.find.where().eq("facebookId", facebookId).findUnique();
        return member != null;
    }

    public static boolean isLoggedAsAdmin() {
        // TODO: move fbid to config;
        return false;
        //return member != null && member.facebookId.equals("100001381203370");
    }

    public static String getUserName() {
        return "!!!FIX ME!!!";
        //return user.getFirstName() + " " + user.getLastName();
    }

    public static int getUnreadCount() {
        return 4;
    }

}
