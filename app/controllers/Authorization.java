package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Authorization extends Controller {

    public static Result login() {
        // redirect
        return ok(index.render("Your new application is ready."));
    }

    public static Result logout() {
        // redirect
        return ok(index.render("Your new application is ready."));
    }
    
}
