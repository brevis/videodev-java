package services;

import helpers.CourseForm;
import models.Category;
import models.Course;
import models.Lesson;
import models.Member;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseService {

    public static Course saveCourse(CourseForm form) {
        // TODO: find how to escape from .save() for each model and use .save() only for parent model

        Category category = Category.find.byId(form.getValue("category"));
        if (category == null) return null;

        Member member = AuthService.getCurrentMember();

        Course course = new Course(member,
                category,
                form.getValue("title"),
                form.getValue("description"),
                form.getValue("cover"),
                form.getType());
        course.save();

        for (int i=0; i<form.getLessonsCount(); i++) {
            Lesson lesson = new Lesson(course, form.getLessonValue(i, "title"), form.getLessonValue(i, "playerCode"));
            lesson.save();
            course.lessons.add(lesson);
        }

        member.courses.add(course);
        member.update();

        return course;
    }

    public static String extractPlayerCode(String playerCode) {
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
