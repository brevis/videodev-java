package controllers;

import helpers.CourseForm;
import models.*;
import play.i18n.Messages;
import play.mvc.*;
import services.AuthService;
import services.CourseService;
import views.html.*;

public class Covers extends Controller {

    public static Result get(String id) {
        return ok(home.render());
    }

    //@Security.Authenticated(Secured.class) // TODO: remove after testing;
    public static Result save() {
        return ok(home.render());
    }

    //@Security.Authenticated(Secured.class) // TODO: remove after testing;
    public static Result delete(String id) {
        return ok(home.render());
    }

}
