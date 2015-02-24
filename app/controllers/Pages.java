package controllers;

import models.Page;
import play.data.Form;
import play.mvc.*;
import services.MemberService;
import views.html.*;
import views.html.staticpage;

public class Pages extends Controller {

    public static Result staticpage(String slug) {
        Page page = Page.find.where().eq("slug", slug).findUnique();
        if (page == null) return notFound();
        return ok(staticpage.render(page));
    }

    public static Result editpage(String slug) {
        if (!MemberService.isLoggedAsAdmin()) return redirect("/" + slug + ".html");
        Page page = Page.find.where().eq("slug", slug).findUnique();
        if (page == null) return notFound();

        Form<Page> pageForm = Form.form(Page.class).fill(page);
        //return ok(pageForm.toString());
        return ok(editpage.render(page, pageForm));
    }

    public static Result savepage(String slug) {
        if (!MemberService.isLoggedAsAdmin()) return redirect("/" + slug + ".html");
        Page page = Page.find.where().eq("slug", slug).findUnique();
        if (page == null) return notFound();

        Form<Page> pageForm = Form.form(Page.class).bindFromRequest();
        if(pageForm.hasErrors()) {
            return badRequest();
        } else {
            Page updatedPage = pageForm.get();
            updatedPage.slug = page.slug;
            updatedPage.save();
            return redirect("/" + slug + ".html");
        }

    }


}
