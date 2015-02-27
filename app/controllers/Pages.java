package controllers;

import models.Page;
import play.data.*;
import play.mvc.*;
import services.AuthService;
import views.html.*;

public class Pages extends Controller {

    public static Result page(String slug) {
        Page page = Page.find.where().eq("slug", slug).findUnique();
        if (page == null) return notFound(errors.render("Page not found"));

        return ok(views.html.page.page.render(page));
    }

    public static Result edit(String slug) {
        if (!AuthService.isLoggedAsAdmin()) return redirect("/" + slug + ".html");
        Page page = Page.find.where().eq("slug", slug).findUnique();
        if (page == null) return notFound(errors.render("Page not found"));

        Form<Page> pageForm = Form.form(Page.class).fill(page);
        return ok(views.html.page.edit.render(page, pageForm));
    }

    public static Result update(String slug) {
        if (!AuthService.isLoggedAsAdmin()) return redirect("/" + slug + ".html");
        Page page = Page.find.where().eq("slug", slug).findUnique();
        if (page == null) return notFound(errors.render("Page not found"));

        Form<Page> pageForm = Form.form(Page.class).bindFromRequest();
        if(pageForm.hasErrors()) {
            return badRequest(views.html.page.edit.render(page, pageForm));
        } else {
            page = pageForm.get();
            page.slug = slug;
            page.update();
            return redirect("/" + slug + ".html");
        }
    }

}
