package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result home() {
        return ok(home.render("Your new application is ready."));
    }

    public static Result feed() {
        return ok(home.render("Your new application is ready."));
    }

    public static Result category(String slug) {
        return ok(home.render(slug));
    }

    public static Result lessons(String id) {
        return ok(lessons.render());
    }

}
