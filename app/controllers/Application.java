package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result home() {
        return ok(home.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result feed() {
        return ok(home.render());
    }

    public static Result category(String slug) {
        return ok(home.render());
    }

}
