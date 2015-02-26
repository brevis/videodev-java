package controllers;

import models.Member;
import play.cache.Cache;
import play.mvc.*;
import services.AuthService;

public class Auth extends Controller {

    public static Result login() {
        String accessToken = request().getQueryString("accessToken");
        Member member = AuthService.loginWithAccessToken(accessToken);
        if (member != null) {
            AuthService.createAuth(member);
            return redirect("/feed");
        }
        return redirect("/");
    }

    public static Result logout() {
        AuthService.destroyAuth();
        return redirect("/");
    }
    
}
