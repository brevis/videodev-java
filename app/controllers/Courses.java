package controllers;

import com.avaje.ebean.PagingList;
import helpers.CourseForm;
import models.*;
import play.i18n.Messages;
import play.mvc.*;
import services.AuthService;
import services.CourseService;
import views.html.*;

import java.util.ArrayList;
import java.util.List;

public class Courses extends Controller {

    public static Result view(String id, Integer l) {
        Course course = Course.find.byId(id);
        if (course==null) return notFound(errors.render(Messages.get("error.page_not_found")));

        // update viewCount
        course.updateViewsCount();

        // get first unread lesson number
        if (AuthService.isLogged() && request().queryString().containsKey("new")) {
            l = course.getFirstUnreadLessonNumber();
            return redirect(routes.Courses.view(id, l) + "#lesson" + l);
        }

        // add course to member's view history
        if (AuthService.isLogged()) {
            AuthService.getCurrentMember().addViewHistory(course);
        }

        // check lesson number and add lesson to member's view history
        if (l < 1 || l > course.lessons().size()) {
            l = 0;
        }

        return ok(views.html.course.view.render(course, l));
    }

    public static Result list(String slug, Integer page) {
        String siteTitle = Messages.get("default.siteTitle");
        Category category = null;
        if (slug != null) {
            try {
                category = Category.find.byId(slug);
            } catch (Exception e) {
                category = null;
            }
            if (category == null) return notFound(errors.render(Messages.get("error.page_not_found")));
        }
        if (category != null) {
            siteTitle = category.name;
        }
        return ok(
                views.html.course.list.render(Course.page(page, 10, slug, null), slug, siteTitle)
        );
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

        if (!course.member.equals(AuthService.getCurrentMember()) && !AuthService.isLoggedAsAdmin() ) {
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

        if (!course.member.equals(AuthService.getCurrentMember()) && !AuthService.isLoggedAsAdmin() ) {
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
    public static Result deleteone(String id) {
        return deleteCourses(new String[]{id});
    }

    @Security.Authenticated(Secured.class)
    public static Result delete() {
        String[] ids;
        try{
            ids = request().body().asFormUrlEncoded().get("id");
        } catch (Exception e) {
            ids = null;
        }
        return deleteCourses(ids);
    }

    /*--------------------------------------------------------------------------------*/

    private static Result deleteCourses(String[] ids) {
        if (CourseService.deleteCourses(ids)) {
            flash("success", Messages.get("course.deleted"));
        } else {
            flash("danger", Messages.get("error.unknown"));
        }

        String cslug;
        try {
            cslug = request().getQueryString("cslug");
        } catch (Exception e) {
            cslug = "";
        }

        return redirect(routes.Courses.my(cslug, 0));
    }

}
