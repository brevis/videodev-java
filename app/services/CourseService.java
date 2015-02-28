package services;

import helpers.CourseForm;
import models.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseService {

    /**
     * Save new Course
     *
     * @param form CourseForm
     * @return Course
     */
    public static Course saveCourse(CourseForm form) {
        // TODO: find how to escape from .save() for each model and use .save() only for parent model

        Category category = Category.find.byId(form.getValue("category"));
        if (category == null) return null;

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
            course.lessons.add(lesson);
        }

        member.courses.add(course);
        member.update();

        return course;
    }

    /**
     * Update existing Course
     *
     * @param course Course
     * @param form CourseForm
     * @return Course
     */
    public static Course updateCourse(Course course, CourseForm form) {
        Category category = Category.find.byId(form.getValue("category"));
        if (category == null) return null;

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
                Lesson lesson = course.lessons.get(i);
                lesson.title = form.getLessonValue(i, "lessonTitle");
                lesson.playerCode = form.getLessonValue(i, "playerCode");
                lesson.update();
            } else {
                Lesson lesson = new Lesson(course, form.getLessonValue(i, "lessonTitle"), form.getLessonValue(i, "playerCode"));
                lesson.save();
                course.lessons.add(lesson);
            }
        }
        for (int i=0; i<courseLessonsCount - form.getLessonsCount(); i++) {
            Lesson lesson = course.lessons.get((int)form.getLessonsCount());
            course.lessons.remove(lesson);
            lesson.delete();
        }
        course.update();

        return course;
    }

    /**
     * Download image by url and save as Cover
     *
     * @param url String
     * @return Cover
     */
    public static Cover saveCover(String url) {
        return null;
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
