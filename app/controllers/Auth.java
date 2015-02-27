package controllers;

import models.Member;
import play.cache.Cache;
import play.mvc.*;
import services.AuthService;

public class Auth extends Controller {

    public static Result login() {
        Member member = AuthService.loginWithAccessToken(request().getQueryString("accessToken"));
        if (member != null) AuthService.createAuth(member);
        return redirect("/");
    }

    public static Result logout() {
        AuthService.destroyAuth();
        return redirect("/");
    }
    
}
