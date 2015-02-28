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
        Cover cover = Cover.find.byId(id);
        if (cover == null) return notFound(errors.render(Messages.get("error.page_not_found")));
        return ok(cover.data).as(cover.contentType);
    }

}
