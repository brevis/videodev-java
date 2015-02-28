package controllers;

import com.avaje.ebean.PagingList;
import helpers.CourseForm;
import javafx.scene.control.Pagination;
import models.*;
import play.i18n.Messages;
import play.mvc.*;
import services.AuthService;
import services.CourseService;
import views.html.*;

import java.util.ArrayList;

public class Courses extends Controller {

    public static Result view(String id) {
        return ok(views.html.course.view.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result my(String cslug, Integer page) {
        return ok(
                views.html.course.my.render(
                        Course.page(page, 10, cslug, AuthService.getCurrentMember()),
                        cslug
                )
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        String formAction = routes.Courses.save().absoluteURL(request());
        CourseForm form = CourseForm.getFormFromRequest(request().body().asFormUrlEncoded(), Course.CourseType.LESSON);
        return ok(views.html.course.edit.render("add", formAction, form));
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        String formAction = routes.Courses.save().absoluteURL(request());

        String contentType;
        try {
            contentType = request().body().asFormUrlEncoded().get("contentType")[0];
        } catch (Exception e) {
            contentType = "lesson";
        }
        Course.CourseType courseType = contentType.equals("course")
                ? Course.CourseType.COURSE
                : Course.CourseType.LESSON;

        CourseForm form = CourseForm.getFormFromRequest(request().body().asFormUrlEncoded(), courseType);
        if (form.isValid()) {
            try {
                Course course = CourseService.saveCourse(form);
                flash("success", Messages.get(course.type.equals(Course.CourseType.COURSE) ? "course.saved" : "lesson.saved"));
                return redirect(routes.Courses.edit("" + course.id));
            } catch (Exception e) {
                flash("danger", e.getMessage());
            }
        } else {
            flash("danger", Messages.get("error"));
        }

        return ok(views.html.course.edit.render("add", formAction, form));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(String id) {
        String formAction = routes.Courses.update(id).absoluteURL(request());

        Course course = Course.find.byId(id);
        if (course==null) {
            return notFound(errors.render(Messages.get("error.page_not_found")));
        }

        if (!AuthService.isLoggedAsAdmin() && course.member.equals(AuthService.getCurrentMember())) {
            return badRequest(errors.render(Messages.get("error.access_denied")));
        }

        CourseForm form = CourseForm.getFormFromCourse(course);
        return ok(views.html.course.edit.render("edit", formAction, form));
    }

    @Security.Authenticated(Secured.class)
    public static Result update(String id) {
        String formAction = routes.Courses.update(id).absoluteURL(request());

        Course course = Course.find.byId(id);
        if (course==null) {
            return notFound(errors.render(Messages.get("error.page_not_found")));
        }

        if (!AuthService.isLoggedAsAdmin() && course.member.equals(AuthService.getCurrentMember())) {
            return badRequest(errors.render(Messages.get("error.access_denied")));
        }

        CourseForm form = CourseForm.getFormFromRequest(request().body().asFormUrlEncoded(), course.type);
        if (form.isValid()) {
            try {
                course = CourseService.updateCourse(course, form);
                flash("success", Messages.get(course.type.equals(Course.CourseType.COURSE) ? "course.updated" : "lesson.updated"));
                return redirect(routes.Courses.edit("" + course.id));
            } catch (Exception e) {
                flash("danger", e.getMessage());
            }
        } else {
            flash("danger", Messages.get("error"));
        }

        return ok(views.html.course.edit.render("edit", formAction, form));
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        return ok(home.render());
    }

}
