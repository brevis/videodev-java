package helpers;

import models.Category;
import models.Course;
import models.Cover;
import models.Lesson;
import play.i18n.Messages;
import services.CourseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for represent course web form
 */
public class CourseForm {

    private Course.CourseType courseType;

    private static final String[] courseFields = {"category", "title", "description", "coverId", "coverUrl"};
    private static final String[] lessonFields = {"lessonTitle", "playerCode"};

    private HashMap<String, String> courseValues = new HashMap<>();
    private ArrayList<HashMap<String, String>> lessonsValues = new ArrayList<>();

    private HashMap<String, String> courseErrors = new HashMap<>();
    private ArrayList<HashMap<String, String>> lessonsErrors = new ArrayList<>();

    /**
     * Build CourseForm based on request fields and return it
     *
     * @param request Map<String, String[]>
     * @return CourseForm
     */
    public static CourseForm getFormFromRequest(Map<String, String[]> request, Course.CourseType courseType) {
        CourseForm form = new CourseForm(courseType);

        String baseFieldName = "lesson";
        if (courseType == Course.CourseType.COURSE) baseFieldName = "course";

        // course info
        for(String field : courseFields) {
            String value;
            try{
                value = request.get(baseFieldName + "[" + field + "]")[0];
            } catch (Exception e) {
                value = "";
            }
            form.addValue(field, value);
        }

        // lessons info
        Integer lessonsCount;
        try{
            lessonsCount = Integer.parseInt(request.get("lessonsCount")[0]);
        } catch (Exception e) {
            lessonsCount = 1;
        }
        if (lessonsCount < 1) lessonsCount = 1;

        for(int i=0; i<lessonsCount; i++) {
            for(String field : lessonFields) {
                String value;
                try{
                    value = request.get(baseFieldName + "[" + field + "]")[i];
                } catch (Exception e) {
                    value = "";
                }
                form.addLessonValue(i, field, value);
            }
        }

        if (courseType.equals(Course.CourseType.LESSON)) {
            form.addLessonValue(0, "lessonTitle", form.getValue("title"));
        }

        return form;
    }

    /**
     * Build CourseForm based on Course object
     *
     * @param course Course
     * @return CourseForm
     */
    public static CourseForm getFormFromCourse(Course course) {
        CourseForm form = new CourseForm(course.type);
        form.addValue("category", course.category.slug);
        form.addValue("title", course.title);
        form.addValue("description", course.description);
        Cover cover = course.cover;
        form.addValue("coverId", cover == null ? "" : course.cover.id.toString());
        form.addValue("coverUrl", "");

        int i = 0;
        for(Lesson lesson : course.lessons) {
            form.addLessonValue(i, "lessonTitle", lesson.title);
            form.addLessonValue(i, "playerCode", lesson.playerCode);
            i++;
        }

        return form;
    }

    private CourseForm(Course.CourseType courseType) {
        this.courseType = courseType;
    }

    public Course.CourseType getType() {
        return courseType;
    }

    /**
     * Save course fields value
     *
     * @param key String
     * @param value String
     */
    public void addValue(String key, String value) {
        courseValues.put(key, value);
    }

    /**
     * Save lesson fields value
     *
     * @param lessonNumber Integer
     * @param key String
     * @param value String
     */
    public void addLessonValue(Integer lessonNumber, String key, String value) {
        HashMap<String, String> lesson = new HashMap<>();
        try{
            lesson = lessonsValues.get(lessonNumber);
        } catch (Exception e) {
            for(int i=lessonsValues.size(); i<lessonNumber+1; i++) {
                lesson = new HashMap<>();
                lessonsValues.add(i, lesson);
            }
        }
        lesson.put(key, value);
    }

    /**
     * Return course field value
     *
     * @param key String
     * @return String
     */
    public String getValue(String key) {
        try {
            return courseValues.get(key) != null ? courseValues.get(key) : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Return lesson field value
     *
     * @param lessonNumber Integer
     * @param key String
     * @return String
     */
    public String getLessonValue(Integer lessonNumber, String key) {
        HashMap<String, String> lesson;
        try{
            lesson = lessonsValues.get(lessonNumber);
            return lesson.get(key) != null ? lesson.get(key) : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Return lessons count
     *
     * @return Integer
     */
    public Integer getLessonsCount() {
        Integer count = courseType == Course.CourseType.LESSON ? 1 : lessonsValues.size();
        if (count < 1) count = 1;
        return count;
    }

    /**
     * Validate form
     *
     * @return boolean
     */
    public boolean isValid() {
        courseErrors = new HashMap<>();
        lessonsErrors = new ArrayList<>();

        // Course data
        Category category = Category.find.byId(this.getValue("category"));
        if (category == null) this.addError("category", "required");

        String title = this.getValue("title");
        title = title.trim();
        if (title.equals("")) this.addError("title", "required");

        String description = this.getValue("description");
        description = description.trim();
        if (description.equals("")) this.addError("description", "required");

        // TODO: refactor next block
        Cover cover;
        String coverId = this.getValue("coverId");
        String coverUrl = this.getValue("coverUrl").trim();
        this.addValue("coverId", coverId);
        this.addValue("coverUrl", coverUrl);
        if (coverUrl.equals("")) {
            if (!coverId.equals("")) {
                try {
                    cover = Cover.find.byId(coverId);
                } catch (Exception e) {
                    cover = null;
                }
                if (cover == null) this.addValue("coverId", "");
            }
        } else if (coverUrl.matches("^https?://.+")) {
            try {
                cover = CourseService.saveCover(coverUrl);
                if (cover != null) {
                    this.addValue("coverId", cover.id.toString());
                    this.addValue("coverUrl", "");
                }
            } catch (Exception e) {
                this.addError("cover", "incorrect.image");
            }
        } else {
            this.addError("cover", "incorrect");
        }


        // Lessons data
        for(int i = 0; i<getLessonsCount(); i++) {
            String lessonTitle = this.getLessonValue(i, "lessonTitle");
            lessonTitle = lessonTitle.trim();
            if (lessonTitle.equals("")) this.addLessonError(i, "lessonTitle", "required");

            String playerCode = this.getLessonValue(i, "playerCode");
            playerCode = playerCode.trim();
            if (playerCode.equals("")) {
                this.addLessonError(i, "playerCode", "required");
            } else {
                String extractedPlayerCode = CourseService.normalizePlayerCode(playerCode);
                if (extractedPlayerCode.equals("")) {
                    this.addLessonError(i, "playerCode", "incorrect");
                } else {
                    playerCode = extractedPlayerCode;
                }
                this.addLessonValue(i, "playerCode", playerCode);
            }
        }

        return courseErrors.size() + lessonsErrors.size() == 0;
    }

    /**
     * Save course error
     *
     * @param key String
     * @param value String
     */
    public void addError(String key, String value) {
        courseErrors.put(key, value);
    }

    /**
     * Save lesson error
     *
     * @param lessonNumber Integer
     * @param key String
     * @param value String
     */
    public void addLessonError(Integer lessonNumber, String key, String value) {
        HashMap<String, String> lesson = new HashMap<>();
        try{
            lesson = lessonsErrors.get(lessonNumber);
        } catch (Exception e) {
            for(int i=lessonsErrors.size(); i<lessonNumber+1; i++) {
                lesson = new HashMap<>();
                lessonsErrors.add(i, lesson);
            }
        }
        lesson.put(key, value);
    }

    /**
     * Check course error
     *
     * @param key String
     * @return boolean
     */
    public boolean hasError(String key) {
        try {
            return courseErrors.get(key) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return course error
     *
     * @param key String
     * @return String
     */
    public String getError(String key) {
        try {
            String error = courseErrors.get(key);
            return error != null ? Messages.get("error.form." + error) : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check lesson error
     *
     * @param lessonNumber Integer
     * @param key String
     * @return boolean
     */
    public boolean hasLessonError(Integer lessonNumber, String key) {
        HashMap<String, String> lesson;
        try{
            lesson = lessonsErrors.get(lessonNumber);
            return lesson.get(key) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return lesson error
     *
     * @param lessonNumber Integer
     * @param key String
     * @return String
     */
    public String getLessonError(Integer lessonNumber, String key) {
        HashMap<String, String> lesson;
        try{
            lesson = lessonsErrors.get(lessonNumber);
            String error = lesson.get(key);
            return error != null ? Messages.get("error.form." + error) : "";
        } catch (Exception e) {
            return "";
        }
    }

}
