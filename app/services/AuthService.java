package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.Member;
import play.api.Play;
import play.api.libs.json.JsValue;
import play.api.libs.json.Json;
import play.cache.Cache;
import play.mvc.Http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AuthService {

    public static String getFacebookAppId() {
        try {
            return ConfigFactory.load().getString("facebook.appid");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFacebookAppSecret() {
        try {
            return ConfigFactory.load().getString("facebook.appsecret");
        } catch (Exception e) {
            return "";
        }
    }

    public static Member loginWithAccessToken(String accessToken) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String rawData = getUrlContent("https://graph.facebook.com/me?key=value&access_token=" + accessToken);
            HashMap<String, String> fbData = mapper.readValue(rawData, HashMap.class);

            String fbid = fbData.get("id");
            if (!fbid.matches("\\d+")) return null;

            Member member = Member.find.byId(fbid);
            if (member == null) {
                member = new Member(fbData.get("first_name"), fbData.get("last_name"), fbData.get("email"), fbid);
                member.registrationDate = new Date();
                member.saveMember();
            }
            return member;
        } catch (Exception e) {
            return null;
        }
    }

    public static void createAuth(Member member) {
        member.lastLoginDate = new Date();
        member.save();
        Http.Context.current().session().clear();
        String uuid = java.util.UUID.randomUUID().toString();
        Cache.set("member:" + uuid, member);
        Http.Context.current().session().put("uuid", uuid);
    }

    public static void destroyAuth() {
        try{
            Cache.remove("member:" + Http.Context.current().session().get("uuid"));
        } catch (Exception e) {}
        Http.Context.current().session().clear();
    }

    public static Member getCurrentMember() {
        Member member;
        try{
            member = (Member)Cache.get("member:" + Http.Context.current().session().get("uuid"));
        } catch (Exception e) {
            member = null;
        }
        return member;
    }

    public static boolean isLogged() {
        return getCurrentMember() != null;
    }

    public static boolean isLoggedAsAdmin() {
        Member member = getCurrentMember();
        if (member == null) return false;

        Config conf = ConfigFactory.load();
        String[] adminIds = conf.getString("application.adminFacebookIds").split(",");
        for(String id : adminIds) {
            if (id.equals(member.facebookId)) return true;
        }

        return false;
    }

    public static String getUserName() {
        Member member = getCurrentMember();
        if (member == null) return "";
        return member.firstName + " " + member.lastName;
    }

    public static int getUnreadCount() {
        return 4;
    }

    /*-------------------------------------------------------------------------*/

    private static String getUrlContent(String url) throws Exception {
        URL page = new URL(url);
        URLConnection connection = page.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null)
            content.append(line);
        in.close();
        return content.toString();
    }

}
