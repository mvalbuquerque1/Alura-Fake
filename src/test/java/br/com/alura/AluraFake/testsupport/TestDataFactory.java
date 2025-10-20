package br.com.alura.AluraFake.testsupport;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.OpenTextTask;
import br.com.alura.AluraFake.task.Option;
import br.com.alura.AluraFake.task.OptionRequest;
import br.com.alura.AluraFake.task.SingleChoiceTaskRequest;
import br.com.alura.AluraFake.task.OpenTextTaskRequest;
import br.com.alura.AluraFake.task.MultipleChoiceTaskRequest;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;

import java.util.List;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static User instructor() {
        return new User("Instructor Name", "inst@example.com", Role.INSTRUCTOR);
    }

    public static Course courseWithInstructor() {
        return new Course("Course Title", "Course Desc", instructor());
    }

    public static OpenTextTask validOpenTextTask(int order) {
        return new OpenTextTask("A valid open text statement", order);
    }

    public static OpenTextTaskRequest validOpenTextTaskRequest(long courseId, int order) {
        return new OpenTextTaskRequest(courseId, "A valid open text statement", order);
    }

    public static Option option(String text, boolean correct) {
        return new Option(text, correct);
    }

    public static OptionRequest optionRequest(String text, boolean correct) {
        return new OptionRequest(text, correct);
    }

    public static SingleChoiceTaskRequest singleChoiceRequest(long courseId, int order) {
        var options = List.of(optionRequest("Option A", false), optionRequest("Option B", true));
        return new SingleChoiceTaskRequest(courseId, "A single choice question", order, options);
    }

    public static MultipleChoiceTaskRequest multipleChoiceRequest(long courseId, int order) {
        var options = List.of(optionRequest("Option A", true), optionRequest("Option B", true), optionRequest("Option C", false));
        return new MultipleChoiceTaskRequest(courseId, "A multiple choice question", order, options);
    }
}

