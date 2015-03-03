package services;

import helpers.CourseForm;
import models.*;
import play.api.mvc.Codec;
import play.core.j.JavaResults;
import play.mvc.Result;
import play.mvc.Results;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseService {

    /**
     * Save new Course
     *
     * @param form CourseForm
     * @return Course
     * @throws Exception
     */
    public static Course saveCourse(CourseForm form) throws Exception {
        // TODO: find how to escape from .save() for each model and use .save() only for parent model

        Category category = Category.find.byId(form.getValue("category"));
        if (category == null) throw new Exception("[category] is wrong");

        Member member = AuthService.getCurrentMember();

        Course course = new Course(member,
                category,
                form.getValue("title"),
                form.getValue("description"),
                form.getType());
        try {
            course.cover = Cover.find.byId(form.getValue("coverId"));
        } catch (Exception e) {
            course.cover = null;
        }
        course.save();

        for (int i=0; i<form.getLessonsCount(); i++) {
            Lesson lesson = new Lesson(course, form.getLessonValue(i, "lessonTitle"), form.getLessonValue(i, "playerCode"));
            lesson.save();
            course.lessons().add(lesson);
        }

        course.save(); // hack for set course.updateDate

        member.courses.add(course);
        member.update();

        member.addViewHistory(course);

        return course;
    }

    /**
     * Update existing Course
     *
     * @param course Course
     * @param form CourseForm
     * @return Course
     * @throws Exception
     */
    public static Course updateCourse(Course course, CourseForm form) throws Exception {
        Category category = Category.find.byId(form.getValue("category"));
        if (category == null) throw new Exception("[category] is wrong");

        course.category = category;
        course.title = form.getValue("title");
        course.description = form.getValue("description");
        try {
            course.cover = Cover.find.byId(form.getValue("coverId"));
        } catch (Exception e) {
            course.cover = null;
        }

        Integer courseLessonsCount = course.lessons.size();
        for (int i=0; i<form.getLessonsCount(); i++) {
            if (i < courseLessonsCount) {
                Lesson lesson = course.lessons().get(i);
                lesson.title = form.getLessonValue(i, "lessonTitle");
                lesson.playerCode = form.getLessonValue(i, "playerCode");
                lesson.update();
            } else {
                Lesson lesson = new Lesson(course, form.getLessonValue(i, "lessonTitle"), form.getLessonValue(i, "playerCode"));
                lesson.save();
                course.lessons().add(lesson);
            }
        }
        for (int i=0; i<courseLessonsCount - form.getLessonsCount(); i++) {
            Lesson lesson = course.lessons().get(form.getLessonsCount());
            course.lessons().remove(lesson);
            lesson.delete();
        }
        course.update();

        course.member.addViewHistory(course);

        return course;
    }

    public static boolean deleteCourses(String[] ids) {
        if (ids == null) return false;
        ArrayList<Integer> idsList = new ArrayList<>();
        for (String id : ids) {
            idsList.add(Integer.parseInt(id));
        }
        List<Course> courses = Course.find.where()
            .in("id", idsList)
            .eq("member", AuthService.getCurrentMember())
            .findList();
        if (courses == null || courses.size() < 1) return false;

        for (Course course : courses) {
            for(Lesson lesson : course.lessons()) {
                lesson.delete();
            }
            //course.deleteManyToManyAssociations("member");
            course.delete();
        }

        return true;
    }

    /**
     * Download image by url and save as Cover
     *
     * @param url String
     * @return Cover
     */
    public static Cover saveCover(String url) throws Exception {
        Cover cover = Cover.find.where().eq("url", url).findUnique();
        if (cover != null) return cover;

        URLConnection conn = new URL(url).openConnection();
        String contentType = conn.getHeaderField("Content-Type").toLowerCase();
        if (!Arrays.asList(Cover.availableContentTypes).contains(contentType)) throw new Exception("Incorrect Content-type");

        conn.setReadTimeout(5000);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytesRead = -1;
        byte[] buffer = new byte[1024];
        while ((bytesRead = conn.getInputStream().read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
            if (baos.size() > 1024 * 300) throw new Exception("Image size bigger than 300 kb");
        }

        if (baos.size() < 1024) throw new Exception("Image is too small");
        // TODO: check image width & height instead of size

        cover = new Cover(baos.toByteArray(), contentType, url);
        cover.save();

        return cover;
    }

    /**
     * Normalize playerCode
     *
     * @param playerCode String
     * @return String
     */
    public static String normalizePlayerCode(String playerCode) {
        Pattern pattern = Pattern.compile("src=[\"']https?://(www\\.youtube\\.com|youtu\\.be)/([a-zA-Z\\?\\.=0-9\\-/:_]+)[\"']");
        Matcher matcher = pattern.matcher(playerCode);
        if (matcher.find()) {
            return "<iframe width=\"100%\" height=\"440\" src=\"https://www.youtube.com/"
                    + matcher.group(2)
                    + "\" frameborder=\"0\" allowfullscreen></iframe>";
        } else {
            return "";
        }
    }

}
