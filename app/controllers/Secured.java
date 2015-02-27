package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        try{
            Member member = (Member)Cache.get("member:" + ctx.session().get("uuid"));
            return member.facebookId;
        } catch (Exception e){}
        return null;
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect("/");
    }
}