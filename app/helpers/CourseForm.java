package helpers;

import models.Category;
import models.Course;
import services.CourseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for represent course web form
 */
public class CourseForm {

    private Course.CourseType courseType;

    private static final String[] courseFields = {"category", "title", "description", "cover"};
    private static final String[] lessonFields = {"title", "playerCode"};

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
        HashMap<String, String> lesson;
        try{
            lesson = lessonsValues.get(lessonNumber);
        } catch (Exception e) {
            lesson = new HashMap<String, String>();
            lessonsValues.add(lessonNumber, lesson);
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
            return courseValues.get(key);
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
            return lesson.get(key);
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
        return lessonsValues.size();
    }

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

        String cover = this.getValue("cover");
        System.out.println("!!! CHECK COVER FILE !!!"); // TODO: check file exists;

        // Lessons data
        Integer lessonsCount = courseType == Course.CourseType.COURSE ? 1 : lessonsValues.size();
        for(int i = 0; i<lessonsCount; i++) {
            String lessonTitle = this.getLessonValue(i, "title");
            lessonTitle = lessonTitle.trim();
            if (lessonTitle.equals("")) this.addLessonError(i, "title", "required");

            String playerCode = this.getLessonValue(i, "playerCode");
            playerCode = playerCode.trim();
            if (playerCode.equals("")) {
                this.addLessonError(i, "playerCode", "required");
            } else {
                String extractedPlayerCode = CourseService.extractPlayerCode(playerCode);
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
        HashMap<String, String> lesson;
        try{
            lesson = lessonsErrors.get(lessonNumber);
        } catch (Exception e) {
            lesson = new HashMap<String, String>();
            lessonsErrors.add(lessonNumber, lesson);
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
            return courseErrors.get(key);
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
            return lesson.get(key);
        } catch (Exception e) {
            return "";
        }
    }

}
