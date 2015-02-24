package controllers;

import play.mvc.*;
import services.MemberService;

public class Authorization extends Controller {

    public static Result login() {
        MemberService.createAuth();
        return redirect("/feed");
    }

    public static Result logout() {
        MemberService.destroyAuth();
        return redirect("/");
    }
    
}
