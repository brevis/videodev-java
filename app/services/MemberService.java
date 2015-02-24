package services;

import models.Member;
import play.mvc.Http;
import play.api.libs.json.*;

public class MemberService {

    private static boolean isLogged = false;
    private static Member member = null;

    public static void createAuth() {
        JsValue jsValue = Json.parse("{\"member_id\":1}");
        Http.Context.current().session().put("auth", Json.stringify(jsValue));
    }

    public static void destroyAuth() {
        Http.Context.current().session().put("auth", "");
        isLogged = false;
        member = null;
    }

    public static boolean isLogged() {
        if (isLogged) return true;

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
            facebookId = authData.$bslash("member_id").toString();
        } catch (Exception e) {
            facebookId = "";
        }
        if (facebookId.equals("")) return false;

        // check user
        Member member = Member.find.where().eq("facebook_id", facebookId).findUnique();
        if (member != null) {
            MemberService.member = member;
            isLogged = true;
        }
        return isLogged;
    }

    public static boolean isLoggedAsAdmin() {
        // TODO: move fbid to config;
        return member != null && member.facebookId.equals("100001381203370");
    }

    public static Member getMember() {
        return member;
    }

    public static String getUserName() {
        return "!!!FIX ME!!!";
        //return user.getFirstName() + " " + user.getLastName();
    }

    public static int getUnreadCount() {
        return 4;
    }

}
