package controllers;

import models.Course;
import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result home() {
        return Courses.list(null ,0);
    }

}
