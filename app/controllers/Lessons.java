package controllers;

import helpers.CourseForm;
import models.*;
import play.mvc.*;
import services.CourseService;
import views.html.*;

public class Lessons extends Controller {

    public static Result view(String id) {
        return ok(views.html.lesson.view.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result my() {
        return ok(home.render());
    }

    //@Security.Authenticated(Secured.class) // TODO: remove after testing;
    public static Result add() {
        String formAction = routes.Lessons.save().absoluteURL(request());
        CourseForm form = CourseForm.getFormFromRequest(request().body().asFormUrlEncoded(), Course.CourseType.LESSON);
        return ok(views.html.lesson.edit.render(formAction, form));
    }

    //@Security.Authenticated(Secured.class) // TODO: remove after testing;
    public static Result save() {
        String formAction = routes.Lessons.save().absoluteURL(request());

        String contentType = request().body().asFormUrlEncoded().get("contentType")[0];
        Course.CourseType courseType = contentType.equals("course")
                ? Course.CourseType.COURSE
                : Course.CourseType.LESSON;

        CourseForm form = CourseForm.getFormFromRequest(request().body().asFormUrlEncoded(), courseType);
        if (form.isValid()) {
            try {
                Course course = CourseService.saveCourse(form);
                Lesson lesson = course.lessons.get(0);
                flash("Course/Lesson saved");
                return redirect(routes.Lessons.edit("" + lesson.id));
            } catch (Exception e) {
                flash("Something went wrong :(. Please try again");
            }
        }

        return badRequest(views.html.lesson.edit.render(formAction, form));
    }

    //@Security.Authenticated(Secured.class) // TODO: remove after testing;
    public static Result edit(String id) {
        String formAction = routes.Lessons.update(id).absoluteURL(request());
        CourseForm form = CourseForm.getFormFromRequest(request().body().asFormUrlEncoded(), Course.CourseType.LESSON);
        return ok(views.html.lesson.edit.render(formAction, form));
    }

    @Security.Authenticated(Secured.class)
    public static Result update(String id) {
        return ok(home.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        return ok(home.render());
    }

}
